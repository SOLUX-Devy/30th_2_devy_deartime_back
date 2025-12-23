package com.project.deartime.app.dto;

import com.project.deartime.app.domain.TimeCapsule;

import java.time.LocalDateTime;

public record TimeCapsuleResponse(
        Long id,
        String senderNickname,
        String receiverNickname,
        String title,
        String content,
        String theme,
        LocalDateTime openAt,
        boolean isOpened,
        String imageUrl
) {
    public static TimeCapsuleResponse from(TimeCapsule capsule) {
        boolean isOpened = capsule.getOpenAt().isBefore(java.time.LocalDateTime.now());
        return new TimeCapsuleResponse(
                capsule.getId(),
                capsule.getSender().getNickname(),
                capsule.getReceiver().getNickname(),
                capsule.getTitle(),
                isOpened ? capsule.getContent() : "아직 열어볼 수 없습니다.",
                capsule.getTheme(),
                capsule.getOpenAt(),
                isOpened,
                capsule.getTheme() // 기존 엔티티에 imageUrl이 없을 경우 대체
        );
    }
}