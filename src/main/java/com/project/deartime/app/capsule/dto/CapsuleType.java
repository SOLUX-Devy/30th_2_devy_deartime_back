package com.project.deartime.app.capsule.dto;

public enum CapsuleType {
    ALL,      // 모든 캡슐 (보낸 것 + 받은 것)
    RECEIVED, // 받은 캡슐만
    SENT,     // 보낸 캡슐만
    OPENED    // 개봉된 캡슐만
}

