package com.project.deartime.app.controller;

import com.project.deartime.app.dto.TimeCapsuleRequest;
import com.project.deartime.app.dto.TimeCapsuleDetailResponse;
import com.project.deartime.app.dto.TimeCapsuleResponse;
import com.project.deartime.app.service.TimeCapsuleService;
import com.project.deartime.global.dto.ApiResponseTemplete;
import com.project.deartime.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/capsules")
@RequiredArgsConstructor
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @PostMapping
    public ResponseEntity<ApiResponseTemplete<Void>> createCapsule(
            Principal principal,
            @RequestBody TimeCapsuleRequest request
    ) {
        // principal.getName()은 로그인한 유저의 이메일을 반환합니다.
        timeCapsuleService.createCapsule(principal.getName(), request);
        return ApiResponseTemplete.success(SuccessCode.CREATE_TIMECAPSULE_SUCCESS, null);
    }

    @GetMapping
    public ResponseEntity<ApiResponseTemplete<List<TimeCapsuleResponse>>> getAllCapsules(Principal principal) {
        var response = timeCapsuleService.getAllCapsules(principal.getName());
        return ApiResponseTemplete.success(SuccessCode.GET_TIMECAPSULE_SUCCESS, response);
    }

    @GetMapping("/{capsuleId}")
    public ResponseEntity<ApiResponseTemplete<TimeCapsuleDetailResponse>> getCapsuleDetail(
            Principal principal,
            @PathVariable Long capsuleId
    ) {
        var response = timeCapsuleService.getCapsuleDetail(principal.getName(), capsuleId);
        return ApiResponseTemplete.success(SuccessCode.GET_TIMECAPSULE_SUCCESS, response);
    }
}