package com.example.waxing.pricing.service;

import com.example.waxing.pricing.domain.Pricing;
import com.example.waxing.pricing.domain.PricingCategory;
import com.example.waxing.pricing.domain.PricingGender;
import com.example.waxing.pricing.dto.PricingCreateRequest;
import com.example.waxing.pricing.dto.PricingListDto;
import com.example.waxing.pricing.repository.PricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingService {

    private final PricingRepository pricingRepository;

    public List<PricingListDto> getActivePricings(PricingGender gender) {
        return pricingRepository.findAllByGenderAndActiveTrueOrderByDisplayOrderAscIdAsc(gender)
                .stream()
                .map(PricingListDto::from)
                .toList();
    }

    @Transactional
    public void createPricing(PricingCreateRequest request) {
        pricingRepository.save(Pricing.create(
                request.gender(),
                request.category(),
                request.name(),
                request.price(),
                request.displayOrder()
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PricingCreateRequest getForEdit(Long id) {
        return PricingCreateRequest.from(getPricing(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updatePricing(Long id, PricingCreateRequest request) {
        Pricing pricing = getPricing(id);
        pricing.update(
                request.gender(),
                request.category(),
                request.name(),
                request.price(),
                request.displayOrder()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePricing(Long id) {
        getPricing(id).deactivate();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void reorder(PricingGender gender, PricingCategory category, List<Long> pricingIds) {
        List<Pricing> pricings = pricingRepository.findAllById(pricingIds);
        if (pricings.size() != pricingIds.size()) {
            throw new IllegalArgumentException("순서를 변경할 가격 메뉴를 찾을 수 없습니다.");
        }

        for (int index = 0; index < pricingIds.size(); index++) {
            Long pricingId = pricingIds.get(index);
            Pricing pricing = pricings.stream()
                    .filter(item -> item.getId().equals(pricingId))
                    .findFirst()
                    .orElseThrow();

            if (!pricing.isActive()
                    || pricing.getGender() != gender
                    || pricing.getCategory() != category) {
                throw new IllegalArgumentException("같은 성별과 구분의 메뉴만 정렬할 수 있습니다.");
            }
            pricing.changeDisplayOrder(index);
        }
    }

    private Pricing getPricing(Long id) {
        return pricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("가격 메뉴를 찾을 수 없습니다."));
    }
}
