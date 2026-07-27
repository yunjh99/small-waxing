package com.example.waxing.notice.controller;

import com.example.waxing.global.common.SuccessResponse;
import com.example.waxing.global.security.Login;
import com.example.waxing.notice.domain.NoticeStatus;
import com.example.waxing.notice.dto.NoticeCreateRequest;
import com.example.waxing.notice.dto.NoticeDetailDto;
import com.example.waxing.notice.service.NoticeService;
import com.example.waxing.user.dto.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/notices")
public class AdminNoticeController {

    private static final String NOTICE_LIST_VIEW = "pages/community/notice/list";
    private static final String NOTICE_DETAIL_VIEW = "pages/community/notice/detail";
    private static final String NOTICE_FORM_VIEW = "pages/community/notice/form";
    private static final int PAGE_SIZE = 8;

    private final NoticeService noticeService;

    // 조회 화면

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "all") String viewType,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        addNoticeListAttributes(model, viewType, pageable);
        return NOTICE_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "all") String viewType,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        model.addAttribute("notice", noticeService.getAdminNotice(id));
        model.addAttribute("currentId", id);
        addNoticeListAttributes(model, viewType, pageable);

        return NOTICE_DETAIL_VIEW;
    }

    // 생성 및 수정

    @GetMapping("/form")
    public String createForm() {
        return NOTICE_FORM_VIEW;
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute NoticeCreateRequest request,
            BindingResult bindingResult,
            @Login LoginUser loginUser
    ) {
        if (bindingResult.hasErrors()) {
            return NOTICE_FORM_VIEW;
        }

        Long id = noticeService.createNotice(loginUser, request);
        return redirectToDetail(id);
    }

    @GetMapping("/{id}/form")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getAdminNotice(id));
        return NOTICE_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute NoticeCreateRequest request,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "false") boolean deleteBodyImage,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            // 수정 화면에 기존 이미지 정보를 다시 표시한다.
            NoticeDetailDto notice = noticeService.getAdminNotice(id);
            model.addAttribute("notice", notice);
            return NOTICE_FORM_VIEW;
        }

        noticeService.updateNotice(id, request, deleteBodyImage);
        return redirectToDetail(id);
    }

    // 삭제 및 복구

    @PostMapping("/{id}/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<Void> delete(
            @PathVariable Long id,
            @Login LoginUser loginUser
    ) {
        noticeService.deleteNotice(id, loginUser);
        return successResponse("공지사항 삭제 성공");
    }

    @PostMapping("/{id}/restore")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<Void> restore(@PathVariable Long id) {
        noticeService.restoreNotice(id);
        return successResponse("공지사항 복구 성공");
    }

    // 공통 처리

    private void addNoticeListAttributes(Model model, String viewType, Pageable pageable) {
        String normalizedViewType = normalizeViewType(viewType);
        NoticeStatus status = resolveStatus(normalizedViewType);

        model.addAttribute("notices", noticeService.getAdminNotices(status, pageable));
        model.addAttribute("viewType", normalizedViewType);
        model.addAttribute("adminView", true);
    }

    private String normalizeViewType(String viewType) {
        if (viewType == null) {
            return "all";
        }

        return viewType.trim().toLowerCase(Locale.ROOT);
    }

    private NoticeStatus resolveStatus(String viewType) {
        return switch (viewType) {
            case "active" -> NoticeStatus.ACTIVE;
            case "deleted" -> NoticeStatus.DELETED;
            default -> NoticeStatus.ALL;
        };
    }

    private String redirectToDetail(Long id) {
        return "redirect:/admin/notices/" + id;
    }

    private SuccessResponse<Void> successResponse(String message) {
        return SuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .build();
    }
}
