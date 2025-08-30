package com.lokoko.domain.campagin.application.service;

import com.lokoko.domain.campagin.domain.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignReadService {

    private final CampaignRepository campaignRepository;

}
