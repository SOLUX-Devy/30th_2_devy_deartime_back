package com.project.deartime.app.auth.Service;

import com.project.deartime.app.auth.dto.SignUpRequest;
import com.project.deartime.app.auth.repository.UserRepository;
import com.project.deartime.app.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User signUp(String providerId, String email, SignUpRequest request) {

        System.out.println("=== 회원가입 시작 ===");
        System.out.println("providerId: " + providerId);
        System.out.println("email: " + email);
        System.out.println("nickname: " + request.getNickname());

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .providerId(providerId)
                .email(email)
                .nickname(request.getNickname())
                .birthDate(request.getBirthDate())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .build();

        User savedUser = userRepository.save(user);
        System.out.println("저장된 User ID: " + savedUser.getId());
        System.out.println("=== 회원가입 완료 ===");

        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
