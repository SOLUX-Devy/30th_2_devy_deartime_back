package com.project.deartime.app.capsule.dto;

import com.project.deartime.app.domain.TimeCapsule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "타임캡슐 응답 데이터")
public class CapsuleResponse {

    @Schema(description = "타임캡슐 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "2년 뒤의 나에게")
    private String title;

    @Schema(description = "내용 (접근 권한 없으면 null)", example = "2년 뒤의 나는 어떤 모습일까?")
    private String content;

    @Schema(description = "테마", example = "graduation")
    private String theme;

    @Schema(description = "이미지 URL (S3)", example = "https://s3.amazonaws.com/bucket/image.jpg")
    private String imageUrl;

    @Schema(description = "공개 날짜/시간", example = "2027-01-20T15:30:00")
    private LocalDateTime openAt;

    @Schema(description = "알림 발송 여부", example = "true")
    private Boolean isNotified;

    @Schema(description = "보낸 사람 ID", example = "1")
    private Long senderId;

    @Schema(description = "보낸 사람 닉네임", example = "user1")
    private String senderNickname;

    @Schema(description = "보낸 사람 프로필 이미지 URL", example = "https://s3.amazonaws.com/bucket/profile1.jpg")
    private String senderProfileImageUrl;

    @Schema(description = "받는 사람 ID", example = "2")
    private Long receiverId;

    @Schema(description = "받는 사람 닉네임", example = "user2")
    private String receiverNickname;

    @Schema(description = "받는 사람 프로필 이미지 URL", example = "https://s3.amazonaws.com/bucket/profile2.jpg")
    private String receiverProfileImageUrl;

    @Schema(description = "생성 날짜/시간", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "공개 여부 (현재 시간 > openAt)", example = "false")
    private boolean isOpened;

    @Schema(description = "현재 사용자의 접근 가능 여부", example = "false")
    private boolean canAccess;

    public static CapsuleResponse from(TimeCapsule capsule, boolean canAccess) {
        boolean isOpened = capsule.getOpenAt().isBefore(LocalDateTime.now());

        return CapsuleResponse.builder()
                .id(capsule.getId())
                .title(capsule.getTitle())
                .content(isOpened || canAccess ? capsule.getContent() : null)
                .theme(capsule.getTheme())
                .imageUrl(capsule.getImageUrl())
                .openAt(capsule.getOpenAt())
                .isNotified(capsule.getIsNotified())
                .senderId(capsule.getSender().getId())
                .senderNickname(capsule.getSender().getNickname())
                .senderProfileImageUrl(capsule.getSender().getProfileImageUrl())
                .receiverId(capsule.getReceiver().getId())
                .receiverNickname(capsule.getReceiver().getNickname())
                .receiverProfileImageUrl(capsule.getReceiver().getProfileImageUrl())
                .createdAt(capsule.getCreatedAt())
                .isOpened(isOpened)
                .canAccess(canAccess)
                .build();
    }
}

