package com.project.deartime.app.dto.photos;

public record PhotoUploadRequest (
        String caption,
        Long albumId
){
}
