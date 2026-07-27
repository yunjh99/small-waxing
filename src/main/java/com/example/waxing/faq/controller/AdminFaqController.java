package com.example.waxing.faq.controller;

import com.example.waxing.faq.domain.FaqType;
import com.example.waxing.faq.dto.FaqCreateRequest;
import com.example.waxing.faq.dto.FaqListDto;
import com.example.waxing.faq.service.FaqService;
import com.example.waxing.global.common.SuccessResponse;
import com.example.waxing.global.security.Login;
import com.example.waxing.user.dto.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/faqs")
public class AdminFaqController {

    private static final String FAQ_LIST_VIEW = "pages/community/faq/list";
    private static final String FAQ_FORM_VIEW = "pages/community/faq/form";
    private static final int PAGE_SIZE = 8;

    private final FaqService faqService;

    // 조회 화면

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "active") String viewType,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        String normalizedViewType = normalizeViewType(viewType);
        Page<FaqListDto> faqs = getFaqs(type, normalizedViewType, pageable);

        model.addAttribute("faqs", faqs);
        model.addAttribute("selectedType", type);
        model.addAttribute("viewType", normalizedViewType);
        model.addAttribute("adminView", true);

        return FAQ_LIST_VIEW;
    }

    // 생성 및 수정

    @GetMapping("/form")
    public String createForm(Model model) {
        model.addAttribute("faq", new FaqCreateRequest(null, null, null));
        addFaqTypes(model);
        return FAQ_FORM_VIEW;
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("faq") FaqCreateRequest request,
            BindingResult bindingResult,
            @Login LoginUser loginUser,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addFaqTypes(model);
            return FAQ_FORM_VIEW;
        }

        faqService.createFaq(loginUser, request);
        return redirectToList();
    }

    @GetMapping("/{id}/form")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("faq", faqService.getFaqForAdmin(id));
        model.addAttribute("faqId", id);
        addFaqTypes(model);

        return FAQ_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("faq") FaqCreateRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("faqId", id);
            addFaqTypes(model);
            return FAQ_FORM_VIEW;
        }

        faqService.updateFaq(id, request);
        return redirectToList();
    }

    // 삭제 및 복구

    @PostMapping("/{id}/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<Void> delete(
            @PathVariable Long id,
            @Login LoginUser loginUser
    ) {
        faqService.deleteFaq(id, loginUser);
        return successResponse("FAQ 삭제 성공");
    }

    @PostMapping("/{id}/restore")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<Void> restore(@PathVariable Long id) {
        faqService.restoreFaq(id);
        return successResponse("FAQ 복구 성공");
    }

    // 공통 처리

    private Page<FaqListDto> getFaqs(
            String type,
            String viewType,
            Pageable pageable
    ) {
        if ("deleted".equals(viewType)) {
            return faqService.getDeletedFaqs(pageable);
        }

        return faqService.getActiveFaqs(type, pageable);
    }

    private String normalizeViewType(String viewType) {
        if (viewType == null) {
            return "active";
        }

        return viewType.trim().toLowerCase(Locale.ROOT);
    }

    private void addFaqTypes(Model model) {
        model.addAttribute("types", FaqType.values());
    }

    private String redirectToList() {
        return "redirect:/admin/faqs";
    }

    private SuccessResponse<Void> successResponse(String message) {
        return SuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .build();
    }
}
