package com.project.deartime.app.controller;

import com.project.deartime.app.dto.*;
import com.project.deartime.app.service.LetterService;
import com.project.deartime.global.dto.ApiResponseTemplete;
import com.project.deartime.global.dto.PageResponse;
import com.project.deartime.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/letters")
@RequiredArgsConstructor
public class LetterController {

    private final LetterService letterService;

    private Long getCurrentUserId(Long principalId) {
        if (principalId == null) {
            throw new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return principalId;
    }

    // 편지 전송
    @PostMapping
    public ResponseEntity<ApiResponseTemplete<LetterSendResponse>> sendLetter(
            @RequestBody @Valid LetterSendRequest request,
            @AuthenticationPrincipal Long principalId
            ) {
        Long senderId = getCurrentUserId(principalId);

        LetterSendRequest finalRequest = new LetterSendRequest(
                senderId,
                request.receiverId(),
                request.theme(),
                request.title(),
                request.content(),
                request.sentAt()
        );

        LetterSendResponse response = letterService.sendLetter(finalRequest);

        return ApiResponseTemplete.success(SuccessCode.LETTER_SEND_SUCCESS, response);
    }

    // 받은 편지 모아보기
    @GetMapping("/received")
    public ResponseEntity<ApiResponseTemplete<PageResponse<LetterListResponse>>> getReceivedLetters(
            @AuthenticationPrincipal Long principalId,
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = getCurrentUserId(principalId);
        PageResponse<LetterListResponse> response = letterService.getReceivedLetters(userId, pageable);

        SuccessCode successCode = response.totalElements() == 0
                ? SuccessCode.GET_LETTER_EMPTY
                : SuccessCode.GET_LETTER_SUCCESS;

        return ApiResponseTemplete.success(successCode, response);
    }

    // 보낸 편지 모아보기
    @GetMapping("/sent")
    public ResponseEntity<ApiResponseTemplete<PageResponse<LetterListResponse>>> getSentLetter(
            @AuthenticationPrincipal Long principalId,
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = getCurrentUserId(principalId);
        PageResponse<LetterListResponse> response = letterService.getSentLetters(userId, pageable);

        SuccessCode successCode = response.totalElements() == 0
                ? SuccessCode.GET_LETTER_EMPTY
                : SuccessCode.GET_LETTER_SUCCESS;

        return ApiResponseTemplete.success(successCode, response);
    }

    // 즐겨찾기 한 편지 모아보기
    @GetMapping("/bookmarked")
    public ResponseEntity<ApiResponseTemplete<PageResponse<LetterListResponse>>> getBookmarkedLetters(
            @AuthenticationPrincipal Long principalId,
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = getCurrentUserId(principalId);
        PageResponse<LetterListResponse> response = letterService.getBookmarkedLetters(userId, pageable);

        SuccessCode successCode = response.totalElements() == 0
                ? SuccessCode.GET_LETTER_EMPTY
                : SuccessCode.GET_LETTER_SUCCESS;

        return ApiResponseTemplete.success(successCode, response);
    }

    // 우리의 우체통
    @GetMapping("/conversation/{targetId}")
    public ResponseEntity<ApiResponseTemplete<PageResponse<LetterListResponse>>> getConversationLetters(
            @AuthenticationPrincipal Long principalId,
            @PathVariable Long targetId,
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = getCurrentUserId(principalId);
        PageResponse<LetterListResponse> response = letterService.getConversationLetters(userId, targetId, pageable);

        SuccessCode successCode = response.data().isEmpty()
                ? SuccessCode.CONVERSATION_EMPTY
                : SuccessCode.CONVERSATION_FETCH_SUCCESS;

        return ApiResponseTemplete.success(
                successCode,
                response
        );
    }

    // 편지 상세 확인
    @GetMapping("/{letterId}")
    public ResponseEntity<ApiResponseTemplete<LetterDetailResponse>> getLetterDetail(
            @PathVariable Long letterId,
            @AuthenticationPrincipal Long principalId
    ) {
        Long userId = getCurrentUserId(principalId);
        LetterDetailResponse response = letterService.getLetterDetail(letterId, userId);

        return ApiResponseTemplete.success(SuccessCode.GET_LETTER_SUCCESS, response);
    }

    // 편지 즐겨찾기/취소
    @PutMapping("/{letterId}/bookmark")
    public ResponseEntity<ApiResponseTemplete<String>> toggleBookmark(
            @PathVariable Long letterId,
            @AuthenticationPrincipal Long principalId
    ) {
        Long userId = getCurrentUserId(principalId);
        boolean isBookmarked = letterService.toggleBookmark(letterId, userId);

        String message = isBookmarked ? "편지가 즐겨찾기에 추가되었습니다." : "편지가 즐겨찾기에서 제거되었습니다.";

        return ApiResponseTemplete.success(HttpStatus.OK.value(), true, message, null);
    }

    @DeleteMapping("/{letterId}")
    public ResponseEntity<ApiResponseTemplete<String>> deleteLetter(
            @PathVariable Long letterId,
            @AuthenticationPrincipal Long principalId
    ) {
        Long userId = getCurrentUserId(principalId);

        letterService.softDeleteOrPermanentlyDelete(letterId, userId);

        return ApiResponseTemplete.success(
                SuccessCode.DELETE_LETTER_SUCCESS,
                "편지 삭제 요청이 성공적으로 처리되었습니다."
        );
    }
}
