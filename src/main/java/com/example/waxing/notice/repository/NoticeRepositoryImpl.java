package com.example.waxing.notice.repository;

import com.example.waxing.file.domain.QUploadFile;
import com.example.waxing.notice.domain.NoticeStatus;
import com.example.waxing.notice.domain.QNoticeImage;
import com.example.waxing.notice.dto.NoticeDetailDto;
import com.example.waxing.notice.dto.NoticeListDto;
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

import java.util.List;
import java.util.Optional;

import static com.example.waxing.notice.domain.QNotice.notice;
import static com.example.waxing.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private static final String FILE_URL_PREFIX = "/files/";

    private final JPAQueryFactory queryFactory;

    /** 삭제되지 않은 공지사항 목록을 조회한다. */
    @Override
    public Page<NoticeListDto> findActiveNotices(Pageable pageable) {
        return findNotices(notice.deletedAt.isNull(), pageable);
    }

    /** 관리자가 선택한 상태에 해당하는 공지사항 목록을 조회한다. */
    @Override
    public Page<NoticeListDto> findAdminNotices(NoticeStatus status, Pageable pageable) {
        return findNotices(statusCondition(status), pageable);
    }

    /** 삭제되지 않은 공지사항 상세 정보를 조회한다. */
    @Override
    public Optional<NoticeDetailDto> findActiveDetailById(Long id) {
        return findDetail(
                notice.id.eq(id),
                notice.deletedAt.isNull()
        );
    }

    /** 삭제 여부와 관계없이 공지사항 상세 정보를 조회한다. */
    @Override
    public Optional<NoticeDetailDto> findDetailById(Long id) {
        return findDetail(notice.id.eq(id));
    }

    private Page<NoticeListDto> findNotices(
            Predicate condition,
            Pageable pageable
    ) {
        List<NoticeListDto> content = queryFactory
                .select(Projections.constructor(
                        NoticeListDto.class,
                        notice.id,
                        notice.title,
                        notice.viewCount,
                        notice.createdAt,
                        user.name
                ))
                .from(notice)
                .leftJoin(notice.user, user)
                .where(condition)
                .orderBy(notice.createdAt.desc(), notice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(notice.id.count())
                .from(notice)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    private Optional<NoticeDetailDto> findDetail(Predicate... conditions) {
        QNoticeImage bodyImage = new QNoticeImage("bodyImage");
        QUploadFile bodyFile = new QUploadFile("bodyFile");

        NoticeDetailDto detail = queryFactory
                .select(Projections.constructor(
                        NoticeDetailDto.class,
                        notice.id,
                        notice.title,
                        notice.content,
                        notice.viewCount,
                        notice.createdAt,
                        notice.deletedAt,
                        fileUrl(bodyFile),
                        bodyFile.originalName
                ))
                .from(notice)
                .leftJoin(bodyImage).on(bodyImage.notice.eq(notice))
                .leftJoin(bodyImage.uploadFile, bodyFile)
                .where(conditions)
                .fetchOne();

        return Optional.ofNullable(detail);
    }

    private BooleanExpression statusCondition(NoticeStatus status) {
        if (status == null || status == NoticeStatus.ALL) {
            return null;
        }

        return switch (status) {
            case ACTIVE -> notice.deletedAt.isNull();
            case DELETED -> notice.deletedAt.isNotNull();
            case ALL -> null;
        };
    }

    private Expression<String> fileUrl(QUploadFile file) {
        return ExpressionUtils.template(
                String.class,
                "case when {0} is null then null else concat({1}, cast({0} as string)) end",
                file.id,
                FILE_URL_PREFIX
        );
    }
}
