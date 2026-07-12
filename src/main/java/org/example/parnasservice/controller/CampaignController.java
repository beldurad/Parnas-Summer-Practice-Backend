package org.example.parnasservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.parnasservice.dto.request.CreateCampaignRequest;
import org.example.parnasservice.dto.response.CampaignDetail;
import org.example.parnasservice.dto.response.CampaignListResponse;
import org.example.parnasservice.dto.response.CampaignManagementView;
import org.example.parnasservice.dto.response.ContributorListResponse;
import org.example.parnasservice.dto.response.ContributionListResponse;
import org.example.parnasservice.dto.response.PayoutListResponse;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.service.CampaignService;
import org.example.parnasservice.service.ContributionService;
import org.example.parnasservice.service.PayoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final ContributionService contributionService;
    private final PayoutService payoutService;

    @GetMapping
    public ResponseEntity<CampaignListResponse> listCampaigns(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<CampaignStatus> status,
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(defaultValue = "CREATED_AT_DESC") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(campaignService.listCampaigns(q, status, creatorId, sort, page, pageSize));
    }

    @PostMapping
    public ResponseEntity<CampaignDetail> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request,
            @AuthenticationPrincipal User currentUser) {
        var campaign = campaignService.createCampaign(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(campaignService.getCampaign(campaign.getId(), false));
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignDetail> getCampaign(
            @PathVariable UUID campaignId,
            @RequestParam(defaultValue = "true") boolean includeRecentTransactions) {
        return ResponseEntity.ok(campaignService.getCampaign(campaignId, includeRecentTransactions));
    }

    @GetMapping("/{campaignId}/management")
    public ResponseEntity<CampaignManagementView> getManagement(
            @PathVariable UUID campaignId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(campaignService.getManagement(campaignId, currentUser));
    }

    @GetMapping("/{campaignId}/contributors")
    public ResponseEntity<ContributorListResponse> getContributors(
            @PathVariable UUID campaignId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(campaignService.getContributors(campaignId, page, pageSize));
    }

    @GetMapping("/{campaignId}/contributions")
    public ResponseEntity<ContributionListResponse> getContributions(
            @PathVariable UUID campaignId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(contributionService.getCampaignContributions(campaignId, page, pageSize));
    }

    @GetMapping("/{campaignId}/payouts")
    public ResponseEntity<PayoutListResponse> getPayouts(
            @PathVariable UUID campaignId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(payoutService.getCampaignPayouts(campaignId, page, pageSize));
    }
}
