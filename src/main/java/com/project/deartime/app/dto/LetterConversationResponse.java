package com.project.deartime.app.dto;

import com.project.deartime.app.domain.Letter;

import java.time.LocalDateTime;

public record LetterConversationResponse(
        Long letterId,
        String senderNickname,
        String receiverNickname,
        String title,
        String content,
        String themeCode,
        LocalDateTime sentAt,
        boolean isRead,
        boolean isBookmarked
) {
    public static LetterConversationResponse fromEntity(Letter letter, boolean isBookmarked) {
        String themeCode = letter.getTheme() != null ? letter.getTheme().getCode() : null;

        return new LetterConversationResponse(
                letter.getId(),
                letter.getSender().getNickname(),
                letter.getReceiver().getNickname(),
                letter.getTitle(),
                letter.getContent(),
                themeCode,
                letter.getCreatedAt(),
                letter.getIsRead(),
                isBookmarked
        );
    }
}
