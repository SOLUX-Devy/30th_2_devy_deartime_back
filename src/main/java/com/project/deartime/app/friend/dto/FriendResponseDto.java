package com.project.deartime.app.friend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.deartime.app.domain.Friend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDto {

    private Long userId;
    private Long friendId;
    private String friendNickname;
    private String friendProfileImageUrl;
    private String friendBio;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;

    public static FriendResponseDto of(Friend friend, Long currentUserId) {
        boolean isRequesterMe = friend.getUser().getId().equals(currentUserId);
        var friendUser = isRequesterMe ? friend.getFriend() : friend.getUser();

        return FriendResponseDto.builder()
                .userId(currentUserId)
                .friendId(friendUser.getId())
                .friendNickname(friendUser.getNickname())
                .friendProfileImageUrl(friendUser.getProfileImageUrl())
                .friendBio(friendUser.getBio())
                .status(friend.getStatus())
                .requestedAt(friend.getRequestedAt())
                .build();
    }

    public static FriendResponseDto from(Friend friend) {
        return of(friend, friend.getUser().getId());
    }
}