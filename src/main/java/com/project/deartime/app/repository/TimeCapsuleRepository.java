package com.project.deartime.app.repository;

import com.project.deartime.app.domain.TimeCapsule;
import com.project.deartime.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long> {
    // 전체 조회 (보내고 받은 것 모두)
    List<TimeCapsule> findAllBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);

    // 받은 캡슐 모아보기
    List<TimeCapsule> findAllByReceiverOrderByCreatedAtDesc(User receiver);

    // 내가 보낸 캡슐
    List<TimeCapsule> findAllBySenderOrderByCreatedAtDesc(User sender);
}