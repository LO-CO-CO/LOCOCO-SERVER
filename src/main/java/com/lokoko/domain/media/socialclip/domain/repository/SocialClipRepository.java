package com.lokoko.domain.media.socialclip.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.media.socialclip.domain.SocialClip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialClipRepository extends JpaRepository<SocialClip, Long> {
    Optional<SocialClip> findByCampaignReview(CampaignReview campaignReview);
}
