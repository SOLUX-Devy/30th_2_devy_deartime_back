package com.project.deartime.app.gallery.dto.albums;

import com.project.deartime.app.domain.Album;
import java.time.LocalDateTime;

public record AlbumListResponse(
        Long albumId,
        Long userId,
        String title,
        String coverImageUrl,
        LocalDateTime createdAt,
        int photoCount
) {
    public static AlbumListResponse of(Album album, String coverImageUrl, int photoCount) {
        return new AlbumListResponse(
                album.getId(),
                album.getUser().getId(),
                album.getTitle(),
                coverImageUrl,
                album.getCreatedAt(),
                photoCount
        );
    }
}
