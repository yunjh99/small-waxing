package com.example.waxing.event.controller;

import com.example.waxing.event.domain.EventStatus;
import com.example.waxing.event.dto.EventCreateRequest;
import com.example.waxing.event.dto.EventDetailDto;
import com.example.waxing.event.service.EventService;
import com.example.waxing.global.common.SuccessResponse;
import com.example.waxing.global.security.Login;
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
@RequestMapping("/admin/events")
public class AdminEventController {

    private static final String EVENT_LIST_VIEW = "pages/community/event/list";
    private static final String EVENT_DETAIL_VIEW = "pages/community/event/detail";
    private static final String EVENT_FORM_VIEW = "pages/community/event/form";
    private static final int PAGE_SIZE = 8;

    private final EventService eventService;

    // 조회 화면

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "all") String viewType,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        addEventListAttributes(model, viewType, pageable);
        return EVENT_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "all") String viewType,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model
    ) {
        model.addAttribute("event", eventService.getAdminEvent(id));
        model.addAttribute("currentId", id);
        addEventListAttributes(model, viewType, pageable);

        return EVENT_DETAIL_VIEW;
    }

    // 생성 및 수정

    @GetMapping("/form")
    public String createForm() {
        return EVENT_FORM_VIEW;
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute EventCreateRequest request,
            BindingResult bindingResult,
            @Login LoginUser loginUser
    ) {
        if (bindingResult.hasErrors()) {
            return EVENT_FORM_VIEW;
        }

        Long id = eventService.createEvent(loginUser, request);
        return redirectToDetail(id);
    }

    @GetMapping("/{id}/form")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getAdminEvent(id));
        return EVENT_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute EventCreateRequest request,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "false") boolean deleteThumbnail,
            @RequestParam(defaultValue = "false") boolean deleteBodyImage,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            // 수정 화면에 기존 이미지 정보를 다시 표시한다.
            EventDetailDto event = eventService.getAdminEvent(id);
            model.addAttribute("event", event);
            return EVENT_FORM_VIEW;
        }

        eventService.updateEvent(
                id,
                request,
                request.thumbnail(),
                request.bodyImage(),
                deleteThumbnail,
                deleteBodyImage
        );

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
        eventService.deleteEvent(id, loginUser);
        return successResponse("이벤트 삭제 성공");
    }

    @PostMapping("/{id}/restore")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<Void> restore(@PathVariable Long id) {
        eventService.restoreEvent(id);
        return successResponse("이벤트 복구 성공");
    }

    // 공통 처리

    private void addEventListAttributes(Model model, String viewType, Pageable pageable) {
        String normalizedViewType = normalizeViewType(viewType);
        EventStatus status = resolveStatus(normalizedViewType);

        model.addAttribute("events", eventService.getAdminEvents(status, pageable));
        model.addAttribute("viewType", normalizedViewType);
        model.addAttribute("adminView", true);
    }

    private String normalizeViewType(String viewType) {
        if (viewType == null) {
            return "all";
        }

        return viewType.trim().toLowerCase(Locale.ROOT);
    }

    private EventStatus resolveStatus(String viewType) {
        return switch (viewType) {
            case "upcoming" -> EventStatus.UPCOMING;
            case "active" -> EventStatus.ACTIVE;
            case "ended" -> EventStatus.ENDED;
            case "deleted" -> EventStatus.DELETED;
            default -> EventStatus.ALL;
        };
    }

    private String redirectToDetail(Long id) {
        return "redirect:/admin/events/" + id;
    }

    private SuccessResponse<Void> successResponse(String message) {
        return SuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .build();
    }
}
