package com.project.deartime.app.dto;

import java.time.LocalDateTime;

public record TimeCapsuleRequest(
        Long senderId, //임시용!!
        Long receiverId,
        String title,
        String content,
        String theme,
        LocalDateTime openAt,
        String imageUrl
) {}