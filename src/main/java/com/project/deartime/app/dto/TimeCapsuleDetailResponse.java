package com.project.deartime.app.dto;


import com.project.deartime.app.domain.TimeCapsule;

// Detail Response: 상세 조회 시 (내용 포함, 개봉 여부 체크)
public record TimeCapsuleDetailResponse(String content, String imageUrl, String theme) {
    public static TimeCapsuleDetailResponse from(TimeCapsule capsule) {
        return new TimeCapsuleDetailResponse(
                capsule.isOpened() ? capsule.getContent() : "아직 개봉할 수 없습니다.",
                capsule.isOpened() ? capsule.getImageUrl() : null,
                capsule.getTheme()
        );
    }
}