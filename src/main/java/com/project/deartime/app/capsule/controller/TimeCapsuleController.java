package com.project.deartime.app.capsule.controller;

import com.project.deartime.app.capsule.dto.CapsuleResponse;
import com.project.deartime.app.capsule.dto.CapsuleType;
import com.project.deartime.app.capsule.dto.CreateCapsuleRequest;
import com.project.deartime.app.capsule.service.TimeCapsuleService;
import com.project.deartime.app.auth.repository.UserRepository;
import com.project.deartime.global.dto.ApiResponseTemplete;
import com.project.deartime.global.dto.PageResponse;
import com.project.deartime.global.exception.CoreApiException;
import com.project.deartime.global.exception.ErrorCode;
import com.project.deartime.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/capsules")
@RequiredArgsConstructor
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;
    private final UserRepository userRepository;

    /**
     * Authentication에서 사용자 ID를 안전하게 파싱
     */
    private Long parseUserId(Authentication authentication) {
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new CoreApiException(ErrorCode.INVALID_ID_EXCEPTION, "사용자 ID 파싱에 실패했습니다.");
        }
    }

    /**
     * 타임캡슐 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponseTemplete<CapsuleResponse>> createCapsule(
            @Valid @RequestPart(value = "request") CreateCapsuleRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication) {

        Long userId = parseUserId(authentication);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.USER_NOT_FOUND));

        CapsuleResponse response = timeCapsuleService.createCapsule(userId, request, imageFile, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseTemplete.<CapsuleResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .success(true)
                        .message(SuccessCode.CAPSULE_CREATE_SUCCESS.getMessage())
                        .data(response)
                        .build());
    }

    /**
     * 타임캡슐 목록 조회 (필터링)
     */
    @GetMapping
    public ResponseEntity<ApiResponseTemplete<PageResponse<CapsuleResponse>>> getCapsules(
            @RequestParam(required = false) CapsuleType type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        try {
            Page<CapsuleResponse> page = timeCapsuleService.getCapsulesByType(userId, type, pageable);
            PageResponse<CapsuleResponse> pageResponse = PageResponse.from(page);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponseTemplete.<PageResponse<CapsuleResponse>>builder()
                            .status(HttpStatus.OK.value())
                            .success(true)
                            .message(SuccessCode.CAPSULE_LIST_SUCCESS.getMessage())
                            .data(pageResponse)
                            .build());
        } catch (CoreApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[CAPSULE] 캡슐 목록 조회 실패", e);
            throw new CoreApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 타임캡슐 상세 조회
     */
    @GetMapping("/{capsuleId}")
    public ResponseEntity<ApiResponseTemplete<CapsuleResponse>> getCapsule(
            @PathVariable Long capsuleId,
            Authentication authentication) {

        Long userId = parseUserId(authentication);

        CapsuleResponse response = timeCapsuleService.getCapsule(capsuleId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseTemplete.<CapsuleResponse>builder()
                        .status(HttpStatus.OK.value())
                        .success(true)
                        .message(SuccessCode.CAPSULE_GET_SUCCESS.getMessage())
                        .data(response)
                        .build());
    }
}

