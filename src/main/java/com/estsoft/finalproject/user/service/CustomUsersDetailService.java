package com.estsoft.finalproject.user.service;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.repository.UsersRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUsersDetailService implements UserDetailsService {
    private final UsersRepository usersRepository;

    // 이메일로 유저 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 유저가 없음 : " + email));

        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                "", // OAuth 로그인이라 비밀번호가 없을 수 있음, 빈 문자열로 처리
                List.of(new SimpleGrantedAuthority(users.getRole().name()))
        );
    }
}
