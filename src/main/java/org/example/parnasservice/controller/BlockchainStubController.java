package org.example.parnasservice.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainStubController {

    @PostMapping("/campaigns/{campaignId}/deployment-confirmations")
    public ResponseEntity<Map<String, Object>> confirmDeployment(@PathVariable String campaignId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(Map.of("status", 501, "code", "NOT_IMPLEMENTED",
                "detail", "Блокчейн-функциональность ещё не реализована."));
    }

    @PostMapping("/campaigns/{campaignId}/contributions/prepare")
    public ResponseEntity<Map<String, Object>> prepareContribution(@PathVariable String campaignId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(Map.of("status", 501, "code", "NOT_IMPLEMENTED",
                "detail", "Блокчейн-функциональность ещё не реализована."));
    }

    @PostMapping("/campaigns/{campaignId}/contributions/confirm")
    public ResponseEntity<Map<String, Object>> confirmContribution(@PathVariable String campaignId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(Map.of("status", 501, "code", "NOT_IMPLEMENTED",
                "detail", "Блокчейн-функциональность ещё не реализована."));
    }

    @PostMapping("/campaigns/{campaignId}/payouts/prepare")
    public ResponseEntity<Map<String, Object>> preparePayout(@PathVariable String campaignId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(Map.of("status", 501, "code", "NOT_IMPLEMENTED",
                "detail", "Блокчейн-функциональность ещё не реализована."));
    }

    @PostMapping("/campaigns/{campaignId}/payouts/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayout(@PathVariable String campaignId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(Map.of("status", 501, "code", "NOT_IMPLEMENTED",
                "detail", "Блокчейн-функциональность ещё не реализована."));
    }

    @PostMapping("/service/blockchain/events")
    public ResponseEntity<Map<String, Object>> acceptBlockchainEvents() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(Map.of("status", 501, "code", "NOT_IMPLEMENTED",
                "detail", "Блокчейн-функциональность ещё не реализована."));
    }
}
