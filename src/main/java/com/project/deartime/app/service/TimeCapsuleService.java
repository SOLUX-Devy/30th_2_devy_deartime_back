package com.project.deartime.app.service;

import com.project.deartime.app.domain.TimeCapsule;
import com.project.deartime.app.domain.User;
import com.project.deartime.app.dto.TimeCapsuleDetailResponse;
import com.project.deartime.app.dto.TimeCapsuleRequest;
import com.project.deartime.app.dto.TimeCapsuleResponse;
import com.project.deartime.app.repository.TimeCapsuleRepository;
import com.project.deartime.app.repository.UserRepository;
import com.project.deartime.global.exception.CoreApiException;
import com.project.deartime.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeCapsuleService {
    private final TimeCapsuleRepository timeCapsuleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createCapsule(String email, TimeCapsuleRequest request) {

        /* 이메일로 나(sender)를 조회
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreApiException(ErrorCode.NOT_FOUND_ID_EXCEPTION));
         */

        //임시테스트용
        User sender = userRepository.findById(request.senderId())
                .orElseThrow(() -> new CoreApiException(ErrorCode.NOT_FOUND_ID_EXCEPTION));
        // 수신자(receiver) 조회
        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new CoreApiException(ErrorCode.NOT_FOUND_ID_EXCEPTION));

        TimeCapsule capsule = TimeCapsule.builder()
                .sender(sender)
                .receiver(receiver)
                .title(request.title())
                .content(request.content())
                .theme(request.theme())
                .openAt(request.openAt())
                .imageUrl(request.imageUrl())
                .build();
        timeCapsuleRepository.save(capsule);
    }

    // 전체 조회
    public List<TimeCapsuleResponse> getAllCapsules(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreApiException(ErrorCode.NOT_FOUND_ID_EXCEPTION));

        // 내가 보낸 것과 받은 것 모두 조회
        return timeCapsuleRepository.findAllBySenderOrReceiverOrderByCreatedAtDesc(user, user)
                .stream()
                .map(TimeCapsuleResponse::from)
                .toList();
    }

    // 받은 것만 조회
    public List<TimeCapsuleResponse> getReceivedCapsules(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreApiException(ErrorCode.NOT_FOUND_ID_EXCEPTION));

        return timeCapsuleRepository.findAllByReceiverOrderByCreatedAtDesc(user)
                .stream()
                .map(TimeCapsuleResponse::from)
                .toList();
    }

    // 상세 확인 (보안 로직 포함)
    public TimeCapsuleDetailResponse getCapsuleDetail(String email, Long capsuleId) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoreApiException(ErrorCode.NOT_FOUND_ID_EXCEPTION));


        TimeCapsule capsule = timeCapsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new CoreApiException(ErrorCode.TIMECAPSULE_NOT_FOUND));


        if (!capsule.getSender().equals(currentUser) && !capsule.getReceiver().equals(currentUser)) {
            throw new CoreApiException(ErrorCode.TIMECAPSULE_ACCESS_DENIED);
        }

        return TimeCapsuleDetailResponse.from(capsule);
    }
}