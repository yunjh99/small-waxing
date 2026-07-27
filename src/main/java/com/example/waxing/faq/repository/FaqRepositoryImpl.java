package com.example.waxing.faq.repository;

import com.example.waxing.faq.domain.FaqType;
import com.example.waxing.faq.dto.FaqListDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.waxing.faq.domain.QFaq.faq;

@RequiredArgsConstructor
public class FaqRepositoryImpl implements FaqRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FaqListDto> findActiveFaqs(Pageable pageable) {
        List<FaqListDto> content = queryFactory
                .select(Projections.constructor(
                        FaqListDto.class,
                        faq.id,
                        faq.type,
                        faq.title,
                        faq.content,
                        faq.createdAt
                ))
                .from(faq)
                .where(faq.deletedAt.isNull())
                .orderBy(faq.createdAt.desc(), faq.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(faq.count())
                .from(faq)
                .where(faq.deletedAt.isNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    @Override
    public Page<FaqListDto> findActiveFaqsByType(FaqType type, Pageable pageable) {
        List<FaqListDto> content = queryFactory
                .select(Projections.constructor(
                        FaqListDto.class,
                        faq.id,
                        faq.type,
                        faq.title,
                        faq.content,
                        faq.createdAt
                ))
                .from(faq)
                .where(
                        faq.deletedAt.isNull(),
                        eqType(type)
                )
                .orderBy(faq.createdAt.desc(), faq.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(faq.count())
                .from(faq)
                .where(
                        faq.deletedAt.isNull(),
                        eqType(type)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    @Override
    public Optional<FaqListDto> findFaqById(Long id) {
        FaqListDto result = queryFactory
                .select(Projections.constructor(FaqListDto.class,
                        faq.id,
                        faq.type,
                        faq.title,
                        faq.content,
                        faq.createdAt
                ))
                .from(faq)
                .where(faq.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }


    @Override
    public Page<FaqListDto> findDeletedFaqs(Pageable pageable) {
        List<FaqListDto> content = queryFactory
                .select(Projections.constructor(
                        FaqListDto.class,
                        faq.id,
                        faq.type,
                        faq.title,
                        faq.content,
                        faq.createdAt
                ))
                .from(faq)
                .where(faq.deletedAt.isNotNull())
                .orderBy(faq.deletedAt.desc(), faq.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(faq.count())
                .from(faq)
                .where(faq.deletedAt.isNotNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    private BooleanExpression eqType(FaqType type) {
        return type == null ? null : faq.type.eq(type);
    }
}