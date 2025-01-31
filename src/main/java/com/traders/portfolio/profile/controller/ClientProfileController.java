package com.traders.portfolio.profile.controller;

import com.traders.portfolio.profile.dto.ExposureConfigurationDTO;
import com.traders.portfolio.profile.service.ClientProfileService;
import com.traders.portfolio.utils.UserIdSupplier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientProfileController {
    private final ClientProfileService clientProfileService;

    public ClientProfileController(ClientProfileService clientProfileService) {
        this.clientProfileService = clientProfileService;
    }

    @GetMapping("/portfolio/exposure")
    public ResponseEntity<List<ExposureConfigurationDTO>> getClientExposure() {
        return ResponseEntity.ok(
                clientProfileService.getClientExposure(UserIdSupplier.getUserId())
        );
    }
}
