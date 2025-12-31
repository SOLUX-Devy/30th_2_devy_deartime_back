package com.project.deartime.app.capsule.repository;

import com.project.deartime.app.domain.TimeCapsule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long> {

    Page<TimeCapsule> findBySenderId(Long senderId, Pageable pageable);

    Page<TimeCapsule> findByReceiverId(Long receiverId, Pageable pageable);

    /**
     * 모든 캡슐 (보낸 것 + 받은 것)
     */
    @Query("SELECT tc FROM TimeCapsule tc WHERE tc.sender.id = :userId OR tc.receiver.id = :userId ORDER BY tc.createdAt DESC")
    Page<TimeCapsule> findAllCapsules(@Param("userId") Long userId, Pageable pageable);

    /**
     * 개봉된 캡슐만
     */
    @Query("SELECT tc FROM TimeCapsule tc WHERE tc.openAt <= CURRENT_TIMESTAMP AND (tc.sender.id = :userId OR tc.receiver.id = :userId) ORDER BY tc.createdAt DESC")
    Page<TimeCapsule> findOpenedCapsules(@Param("userId") Long userId, Pageable pageable);

    /**
     * 오픈 시간이 지났지만 알림이 발송되지 않은 캡슐 조회 (스케줄러용)
     */
    @Query("SELECT tc FROM TimeCapsule tc WHERE tc.openAt <= :now AND tc.isNotified = false")
    List<TimeCapsule> findCapsulesReadyToOpen(@Param("now") LocalDateTime now);
}


