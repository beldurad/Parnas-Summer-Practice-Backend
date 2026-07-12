package org.example.parnasservice.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.parnasservice.dto.response.PayoutDetail;
import org.example.parnasservice.dto.response.PayoutDistributionListResponse;
import org.example.parnasservice.service.PayoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campaigns/{campaignId}/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @GetMapping("/{payoutId}")
    public ResponseEntity<PayoutDetail> getPayout(
            @PathVariable UUID campaignId,
            @PathVariable UUID payoutId) {
        return ResponseEntity.ok(payoutService.getPayout(campaignId, payoutId));
    }

    @GetMapping("/{payoutId}/distributions")
    public ResponseEntity<PayoutDistributionListResponse> getDistributions(
            @PathVariable UUID campaignId,
            @PathVariable UUID payoutId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(
            payoutService.getPayoutDistributions(campaignId, payoutId, page, pageSize));
    }
}
