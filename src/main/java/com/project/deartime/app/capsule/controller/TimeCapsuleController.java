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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/capsules")
@RequiredArgsConstructor
@Tag(name = "TimeCapsule", description = "타임캡슐 관련 API")
@SecurityRequirement(name = "bearer_token")
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;
    private final UserRepository userRepository;

    /**
     * 타임캡슐 생성
     */
    @PostMapping
    @Operation(
            summary = "타임캡슐 생성",
            description = "새로운 타임캡슐을 생성합니다. 이미지는 선택사항입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "캡슐 생성 성공",
                    content = @Content(schema = @Schema(implementation = CapsuleResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 오류 또는 친구가 아님"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 받는 사람을 찾을 수 없음"
            )
    })
    public ResponseEntity<ApiResponseTemplete<CapsuleResponse>> createCapsule(
            @Valid @RequestPart(value = "request")
            @Schema(description = "캡슐 생성 요청 정보")
            CreateCapsuleRequest request,
            @RequestPart(value = "imageFile", required = false)
            @Parameter(description = "캡슐 이미지 파일 (선택사항)")
            MultipartFile imageFile,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
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
    @Operation(
            summary = "타임캡슐 목록 조회",
            description = "필터링 및 페이징을 통해 타임캡슐 목록을 조회합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 캡슐 타입"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    public ResponseEntity<ApiResponseTemplete<PageResponse<CapsuleResponse>>> getCapsules(
            @Parameter(
                    description = "캡슐 필터 타입 (ALL: 모든 캡슐, RECEIVED: 받은 캡슐, SENT: 보낸 캡슐, OPENED: 개봉된 캡슐)",
                    example = "ALL"
            )
            @RequestParam(required = false)
            CapsuleType type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
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
    @Operation(
            summary = "타임캡슐 상세 조회",
            description = "특정 타임캡슐의 상세 정보를 조회합니다. 공개되지 않은 캡슐은 내용이 표시되지 않습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CapsuleResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음 또는 아직 열어볼 수 없는 캡슐"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "캡슐을 찾을 수 없음"
            )
    })
    public ResponseEntity<ApiResponseTemplete<CapsuleResponse>> getCapsule(
            @Parameter(description = "조회할 캡슐 ID", example = "1")
            @PathVariable Long capsuleId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

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

