package com.project.deartime.app.capsule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "타임캡슐 생성 요청 데이터")
public class CreateCapsuleRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Schema(description = "타임캡슐 제목", example = "2년 뒤의 나에게", maxLength = 255)
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "타임캡슐 내용", example = "2년 뒤의 나는 어떤 모습일까? 이루고 싶은 꿈들을 다시 생각해보자.", maxLength = 5000)
    private String content;

    @Schema(description = "타임캡슐 테마 (선택사항)", example = "graduation", maxLength = 50)
    private String theme;

    @NotNull(message = "받는 사람 ID는 필수입니다.")
    @Schema(description = "캡슐을 받을 친구의 ID (반드시 친구 관계여야 함)", example = "2")
    private Long receiverId;

    @NotNull(message = "개봉 일시는 필수입니다.")
    @Schema(description = "타임캡슐 공개 날짜 및 시간 (ISO 8601 형식)", example = "2027-01-20T15:30:00")
    private LocalDateTime openAt;
}

