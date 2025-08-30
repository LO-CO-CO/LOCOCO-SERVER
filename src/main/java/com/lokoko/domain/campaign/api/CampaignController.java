package com.lokoko.domain.campaign.api;

import com.lokoko.domain.campaign.application.service.CampaignReadService;
import com.lokoko.domain.campaign.application.service.CampaignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CAMPAIGN")
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignReadService campaignReadService;

}
