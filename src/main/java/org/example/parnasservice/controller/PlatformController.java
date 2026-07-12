package org.example.parnasservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.parnasservice.dto.response.HomePageResponse;
import org.example.parnasservice.service.PlatformService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping("/home")
    public ResponseEntity<HomePageResponse> getHomePage(
            @RequestParam(defaultValue = "6") int popularLimit,
            @RequestParam(defaultValue = "12") int activeLimit) {
        return ResponseEntity.ok(platformService.getHomePage(popularLimit, activeLimit));
    }
}
