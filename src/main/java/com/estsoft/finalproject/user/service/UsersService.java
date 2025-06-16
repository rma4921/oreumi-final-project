package com.estsoft.finalproject.user.service;

import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.repository.UsersRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public String getOrCreateNickname(String provider, String email) {
        Users users = usersRepository.findByProviderAndEmail(provider, email)
            .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        if (users.getNickname() == null || users.getNickname().isBlank()) {
            String randomStr = UUID.randomUUID().toString().substring(0, 8);
            String newNickname = "user_" + randomStr;
            users.setNickname(newNickname);
            usersRepository.save(users);
            return newNickname;
        }
        return users.getNickname();
    }
}
