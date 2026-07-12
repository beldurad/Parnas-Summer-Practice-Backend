package org.example.parnasservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.parnasservice.dto.response.FinalizeExpiredCampaignsResponse;
import org.example.parnasservice.service.CampaignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {

    private final CampaignService campaignService;

    @PostMapping("/campaigns/finalize-expired")
    public ResponseEntity<FinalizeExpiredCampaignsResponse> finalizeExpired(
            @RequestParam(required = false) Integer batchSize,
            @RequestParam(defaultValue = "false") boolean dryRun) {
        return ResponseEntity.accepted().body(
            campaignService.finalizeExpired(batchSize, dryRun));
    }
}
