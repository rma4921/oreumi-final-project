package com.estsoft.finalproject.userTest;

import com.estsoft.finalproject.user.jwt.JwtAuthenticationFilter;
import com.estsoft.finalproject.user.jwt.JwtUtil;
import com.estsoft.finalproject.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

class TestableJwtAuthenticationFilter extends JwtAuthenticationFilter {
    public TestableJwtAuthenticationFilter(JwtUtil jwtUtil, UsersRepository usersRepository) {
        super(jwtUtil, usersRepository);
    }

    public void testDoFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        doFilterInternal(request, response, chain);
    }
}