package com.estsoft.finalproject.user.controller;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.dto.UsersResponse;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.estsoft.finalproject.user.repository.UsersRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UsersController {
    private final UsersRepository usersRepository;

    @GetMapping("/api/users")
    public ResponseEntity<UsersResponse> getUserInfo(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUsersDetails)) {
            log.info("인증되지 않은 사용자 접근 시도: authentication = {}", authentication);
            return ResponseEntity.status(401).build();
        }

        CustomUsersDetails userDetails = (CustomUsersDetails) authentication.getPrincipal();

        Users users = usersRepository.findByProviderAndEmail(
                        userDetails.getUsers().getProvider(),
                        userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        log.info("인증된 사용자 정보 조회 성공: 이메일 = {}, 프로바이더 = {}", userDetails.getUsername(), userDetails.getUsers().getProvider());

        return ResponseEntity.ok(new UsersResponse(users));
    }
}
