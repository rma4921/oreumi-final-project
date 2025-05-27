package com.estsoft.finalproject.user.repository;

import com.estsoft.finalproject.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndEmail(String provider, String email);
}
