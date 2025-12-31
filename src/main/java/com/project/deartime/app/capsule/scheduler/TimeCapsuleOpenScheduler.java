package com.project.deartime.app.capsule.scheduler;

import com.project.deartime.app.capsule.repository.TimeCapsuleRepository;
import com.project.deartime.app.domain.TimeCapsule;
import com.project.deartime.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 타임캡슐 오픈 시간 도래 시 알림을 발송하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimeCapsuleOpenScheduler {

    private final TimeCapsuleRepository timeCapsuleRepository;
    private final NotificationService notificationService;
    private final TransactionTemplate transactionTemplate;

    /**
     * 매 분마다 실행하여 오픈 시간이 된 캡슐을 확인하고 알림 발송
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 * * * * *") // 매 분 0초에 실행
    public void checkAndNotifyOpenedCapsules() {
        LocalDateTime now = LocalDateTime.now();

        List<TimeCapsule> capsulesReadyToOpen = timeCapsuleRepository.findCapsulesReadyToOpen(now);

        if (capsulesReadyToOpen.isEmpty()) {
            return;
        }

        log.info("[SCHEDULER] 오픈 대기 중인 캡슐 {} 개 발견", capsulesReadyToOpen.size());

        for (TimeCapsule capsule : capsulesReadyToOpen) {
            processIndividualCapsule(capsule.getId());
        }
    }

    /**
     * 개별 캡슐 알림 처리 - 각 캡슐별로 독립적인 트랜잭션으로 처리
     * 하나의 실패가 전체 알림 발송에 영향을 주지 않음
     */
    private void processIndividualCapsule(Long capsuleId) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                TimeCapsule capsule = timeCapsuleRepository.findById(capsuleId)
                        .orElse(null);

                if (capsule == null || capsule.getIsNotified()) {
                    return;
                }

                // 수신자에게 캡슐 오픈 알림 발송
                notificationService.notifyCapsuleOpened(
                        capsule.getReceiver(),
                        capsule.getId(),
                        capsule.getSender().getNickname(),
                        capsule.getTitle()
                );

                // 알림 발송 완료 표시
                capsule.markAsNotified();
                timeCapsuleRepository.save(capsule);

                log.info("[SCHEDULER] 캡슐 오픈 알림 발송 완료. capsuleId={}, receiverId={}",
                        capsule.getId(), capsule.getReceiver().getId());
            } catch (Exception e) {
                log.error("[SCHEDULER] 캡슐 오픈 알림 발송 실패. capsuleId={}", capsuleId, e);
                status.setRollbackOnly();
            }
        });
    }
}

