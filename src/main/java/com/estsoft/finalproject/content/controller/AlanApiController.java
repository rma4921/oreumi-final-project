package com.estsoft.finalproject.content.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.estsoft.finalproject.content.services.AlanCommunicationService;

import jakarta.servlet.http.HttpSession;

@RestController
public class AlanApiController {
    private final AlanCommunicationService alanCommunicationService;

    public AlanApiController(AlanCommunicationService alanCommunicationService) {
        this.alanCommunicationService = alanCommunicationService;
    }

    @GetMapping("/api/v1/ask-alan")
    public final ResponseEntity<AlanResponseDto> askAlan(HttpSession session, 
            @RequestParam(name = "question") String question) {
        AlanResponseDto alanResponseDto = alanCommunicationService.getResultFromAlan(question);
        return ResponseEntity.status(alanResponseDto.getResponseCode()).body(alanResponseDto);
    }

    @GetMapping("/api/v1/translate-korean-to-english")
    public final ResponseEntity<AlanResponseDto> translateTextKoreanToEnglish(HttpSession session, 
            @RequestParam(name = "text") String text) {
        AlanResponseDto alanResponseDto = alanCommunicationService.translateTextKoreanToEnglish(text);
        return ResponseEntity.status(alanResponseDto.getResponseCode()).body(alanResponseDto);
    }

    @GetMapping("/api/v1/translate-english-to-korean")
    public final ResponseEntity<AlanResponseDto> translateTextEnglishToKorean(HttpSession session, 
            @RequestParam(name = "text") String text) {
        AlanResponseDto alanResponseDto = alanCommunicationService.translateTextEnglishToKorean(text);
        return ResponseEntity.status(alanResponseDto.getResponseCode()).body(alanResponseDto);
    }
    
}
