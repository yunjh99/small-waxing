package com.example.waxing.event.repository;

import com.example.waxing.event.domain.EventImageType;
import com.example.waxing.event.domain.EventStatus;
import com.example.waxing.event.domain.QEventImage;
import com.example.waxing.event.dto.EventDetailDto;
import com.example.waxing.event.dto.EventListDto;
import com.example.waxing.file.domain.QUploadFile;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.waxing.event.domain.QEvent.event;
import static com.example.waxing.event.domain.QEventImage.eventImage;
import static com.example.waxing.file.domain.QUploadFile.uploadFile;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private static final String FILE_URL_PREFIX = "/files/";

    // QueryDSL 쿼리를 생성하고 실행하는 객체
    private final JPAQueryFactory queryFactory;

    /**
     * 삭제되지 않았고 현재 진행 중인 이벤트 목록을 페이지 단위로 조회한다.
     */
    @Override
    public Page<EventListDto> findActiveEvents(Pageable pageable) {
        LocalDate today = LocalDate.now();

        BooleanExpression activeCondition = event.deletedAt.isNull()
                .and(event.startDate.loe(today))
                .and(event.endDate.goe(today));

        return findEvents(activeCondition, pageable);
    }

    /**
     * 관리자가 선택한 상태에 해당하는 이벤트 목록을 조회한다.
     * ALL은 삭제된 이벤트를 포함해 아무 상태 조건도 적용하지 않는다.
     */
    @Override
    public Page<EventListDto> findAdminEvents(EventStatus status, Pageable pageable) {
        return findEvents(adminStatusCondition(status), pageable);
    }

    /**
     * 관리자 목록 탭에 대응하는 조회 조건을 만든다.
     */
    private BooleanExpression adminStatusCondition(EventStatus status) {
        LocalDate today = LocalDate.now();

        return switch (status) {
            case UPCOMING -> event.deletedAt.isNull()
                    .and(event.startDate.gt(today));
            case ACTIVE -> event.deletedAt.isNull()
                    .and(event.startDate.loe(today))
                    .and(event.endDate.goe(today));
            case ENDED -> event.deletedAt.isNull()
                    .and(event.endDate.lt(today));
            case DELETED -> event.deletedAt.isNotNull();
            case ALL -> null;
        };
    }

    /**
     * 목록 DTO projection, 썸네일 조인, 정렬과 페이징을 공통 처리한다.
     */
    private Page<EventListDto> findEvents(Predicate condition, Pageable pageable) {
        List<EventListDto> content = queryFactory
                // 엔티티 전체가 아니라 목록 화면에 필요한 값만 DTO로 조회한다.
                .select(Projections.constructor(
                        EventListDto.class,
                        event.id,
                        event.title,
                        event.startDate,
                        event.endDate,
                        fileUrl(uploadFile)
                ))
                .from(event)
                // 썸네일이 없는 이벤트도 조회되도록 LEFT JOIN을 사용한다.
                .leftJoin(eventImage).on(
                        eventImage.event.eq(event),
                        eventImage.type.eq(EventImageType.THUMBNAIL)
                )
                .leftJoin(eventImage.uploadFile, uploadFile)
                .where(condition)
                .orderBy(event.startDate.desc(), event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page의 전체 페이지 수를 계산하기 위해 같은 조건의 전체 개수를 조회한다.
        Long total = queryFactory
                .select(event.id.count())
                .from(event)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    /**
     * 삭제되지 않았고 현재 진행 중인 이벤트 한 건의 상세 정보를 조회한다.
     */
    @Override
    public Optional<EventDetailDto> findActiveDetailById(Long id) {
        LocalDate today = LocalDate.now();

        return findDetail(
                event.id.eq(id),
                event.deletedAt.isNull(),
                event.startDate.loe(today),
                event.endDate.goe(today)
        );
    }

    /**
     * 관리자는 삭제 여부와 진행 기간에 관계없이 이벤트를 조회한다.
     */
    @Override
    public Optional<EventDetailDto> findDetailById(Long id) {
        return findDetail(event.id.eq(id));
    }

    /**
     * 상세 DTO 조회에 공통으로 필요한 projection과 이미지 조인을 구성한다.
     * 호출하는 공개 메서드가 사용자 유형에 맞는 조회 조건만 전달한다.
     */
    private Optional<EventDetailDto> findDetail(Predicate... predicates) {
        // 같은 테이블을 썸네일과 본문 이미지로 두 번 조인하므로 별칭을 각각 만든다.
        QEventImage thumbnailImage = new QEventImage("thumbnailImage");
        QEventImage bodyImage = new QEventImage("bodyImage");
        QUploadFile thumbnailFile = new QUploadFile("thumbnailFile");
        QUploadFile bodyFile = new QUploadFile("bodyFile");

        EventDetailDto result = queryFactory
                .select(Projections.constructor(
                        EventDetailDto.class,
                        event.id,
                        event.title,
                        event.content,
                        event.startDate,
                        event.endDate,
                        event.viewCount,
                        event.createdAt,
                        event.deletedAt,
                        fileUrl(thumbnailFile),
                        thumbnailFile.originalName,
                        fileUrl(bodyFile),
                        bodyFile.originalName
                ))
                .from(event)
                .leftJoin(thumbnailImage).on(
                        thumbnailImage.event.eq(event),
                        thumbnailImage.type.eq(EventImageType.THUMBNAIL)
                )
                .leftJoin(thumbnailImage.uploadFile, thumbnailFile)
                .leftJoin(bodyImage).on(
                        bodyImage.event.eq(event),
                        bodyImage.type.eq(EventImageType.BODY)
                )
                .leftJoin(bodyImage.uploadFile, bodyFile)
                .where(predicates)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 파일이 있으면 "/files/{id}" 형태의 URL을 만들고,
     * 파일이 없으면 null을 반환한다.
     */
    private Expression<String> fileUrl(QUploadFile file) {
        return ExpressionUtils.template(
                String.class,
                "case when {0} is null then null else concat({1}, cast({0} as string)) end",
                file.id,
                FILE_URL_PREFIX
        );
    }
}
