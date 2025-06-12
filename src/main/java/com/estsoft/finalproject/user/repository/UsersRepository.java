package com.estsoft.finalproject.user.repository;

import com.estsoft.finalproject.user.domain.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByProviderAndEmail(String provider, String email);
    boolean existsByNickname(String nickname);
}
