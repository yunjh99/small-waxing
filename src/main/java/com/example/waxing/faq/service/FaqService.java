package com.example.waxing.faq.service;

import com.example.waxing.faq.domain.Faq;
import com.example.waxing.faq.domain.FaqType;
import com.example.waxing.faq.dto.FaqCreateRequest;
import com.example.waxing.faq.dto.FaqListDto;
import com.example.waxing.faq.repository.FaqRepository;
import com.example.waxing.global.error.exception.FaqAlreadyDeletedException;
import com.example.waxing.global.error.exception.FaqNotFoundException;
import com.example.waxing.global.error.exception.UserNotFoundException;
import com.example.waxing.user.domain.User;
import com.example.waxing.user.dto.LoginUser;
import com.example.waxing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository faqRepository;
    private final UserRepository userRepository;

    // 전체 또는 Type 조회
    public Page<FaqListDto> getActiveFaqs(String type, Pageable pageable) {
        if (type == null || "all".equalsIgnoreCase(type)) {
            return faqRepository.findActiveFaqs(pageable);
        }
        FaqType faqType = FaqType.ofValue(type);
        return faqRepository.findActiveFaqsByType(faqType, pageable);
    }

    // 삭제된 FAQ 조회 (목록용 DTO)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<FaqListDto> getDeletedFaqs(Pageable pageable) {
        return faqRepository.findDeletedFaqs(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteFaq(Long id, LoginUser loginUser) {
        Faq faq = getFaq(id); // FAQ 존재 여부 조회 (없으면 예외 발생)

        if (faq.getDeletedAt() != null) { // 이미 삭제된 FAQ라면 중복 삭제 방지
            throw new FaqAlreadyDeletedException();
        }

        User user = userRepository.getReferenceById(loginUser.getUserId()); // 삭제 처리할 관리자 엔티티 조회

        faq.delete(user);// 삭제 처리 (deletedAt, deletedBy 기록)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createFaq(LoginUser loginUser, FaqCreateRequest request){

        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Faq faq = Faq.create(
                user,
                request.title(),
                request.content(),
                request.type()
        );

        faqRepository.save(faq);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public FaqListDto getFaqForAdmin(Long id) {
        return faqRepository.findFaqById(id)
                .orElseThrow(FaqNotFoundException::new);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateFaq(Long id,
                          FaqCreateRequest req){

        Faq faq = faqRepository.findById(id)
                .orElseThrow(FaqNotFoundException::new);

        faq.update(req.title(), req.content(), req.type());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restoreFaq(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(FaqNotFoundException::new);

        faq.restore();
    }


    //FAQ 존재 여부 예외 처리
    private Faq getFaq(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(FaqNotFoundException::new);
    }
}
