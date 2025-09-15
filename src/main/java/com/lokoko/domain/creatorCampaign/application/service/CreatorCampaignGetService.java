package com.lokoko.domain.creatorCampaign.application.service;

import static java.util.List.of;

import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.creatorCampaign.exception.AlreadyParticipatedCampaignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorCampaignGetService {

    private final CreatorCampaignRepository creatorCampaignRepository;

    public void findExistingParticipation(Long campaignId, Long creatorId) {
        if (creatorCampaignRepository.existsByCampaignIdAndCreatorId(campaignId, creatorId)) {
            throw new AlreadyParticipatedCampaignException();
        }
    }

    public Slice<CreatorCampaign> findMyCampaigns(Long creatorId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return creatorCampaignRepository.findSliceWithCampaignByCreator(creatorId, pageable);
    }

    /**
     * 크리에이터가 현재 리뷰 작성이 가능한 캠페인 참여 이력들을 조회하는 메서드 리뷰 작성이 가능한 상태 (배송지가 확정된 상태 : 1차 리뷰 가능) 또는 (1차 리뷰를 완료한 상태 : 2차 리뷰 가능)
     *
     * @param creatorId 조회할 크리에이터의 고유 ID
     * @return 리뷰 작성 자격이 있는 {@link CreatorCampaign} 리스트 (최신순으로 정렬)
     */
    public List<CreatorCampaign> findReviewAble(Long creatorId) {
        return creatorCampaignRepository.findAllByCreatorAndStatuses(
                creatorId,
                of(ParticipationStatus.APPROVED_ADDRESS_CONFIRMED, ParticipationStatus.APPROVED_FIRST_REVIEW_DONE)
        );
    }
}
