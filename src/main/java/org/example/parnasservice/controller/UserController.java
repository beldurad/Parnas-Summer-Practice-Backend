package org.example.parnasservice.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.parnasservice.dto.response.CampaignListResponse;
import org.example.parnasservice.dto.response.DashboardResponse;
import org.example.parnasservice.dto.response.MyContributionListResponse;
import org.example.parnasservice.dto.response.PublicUserProfile;
import org.example.parnasservice.dto.response.UserProfileResponse;
import org.example.parnasservice.entity.User;
import org.example.parnasservice.entity.enums.CampaignStatus;
import org.example.parnasservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<PublicUserProfile> getUserProfile(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(userService.getPublicUserProfile(userId, page, pageSize));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getCurrentUser(currentUser.getId()));
    }

    @GetMapping("/me/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "5") int itemsLimit) {
        return ResponseEntity.ok(userService.getDashboard(currentUser.getId(), itemsLimit));
    }

    @GetMapping("/me/contributions")
    public ResponseEntity<MyContributionListResponse> getMyContributions(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(userService.getMyContributions(currentUser.getId(), page, pageSize));
    }

    @GetMapping("/me/campaigns")
    public ResponseEntity<CampaignListResponse> getMyCampaigns(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) List<CampaignStatus> status,
            @RequestParam(defaultValue = "CREATED_AT_DESC") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(userService.getMyCampaigns(currentUser.getId(), status, sort, page, pageSize));
    }
}
