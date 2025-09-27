package com.lokoko.domain.creatorCampaign.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.creator.exception.CreatorCampaignNotFoundException;
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


    public CreatorCampaign findParticipation(Long campaignId, Long creatorId) {

        return creatorCampaignRepository
                .findByCampaignIdAndCreatorId(campaignId, creatorId)
                .orElseThrow(CreatorCampaignNotFoundException::new);
    }

    /**
     * 특정 크리에이터가 지정된 캠페인에 이미 참여했는지 조회하는 메서드 - 이미 참여했다면 예외 발생
     *
     * @param campaignId 검증할 캠페인의 고유 ID
     * @param creatorId  검증할 크리에이터의 고유 ID
     * @throws AlreadyParticipatedCampaignException 이미 해당 캠페인에 참여한 경우 발생
     */
    public void findExistingParticipation(Long campaignId, Long creatorId) {
        if (creatorCampaignRepository.existsByCampaignIdAndCreatorId(campaignId, creatorId)) {
            throw new AlreadyParticipatedCampaignException();
        }
    }


    /**
     * 특정 크리에이터가 참여한 캠페인 목록을 페이징 방식으로 조회하는 메서드 - 조회 시 {CreatorCampaign} 엔티티와 연관된 {Campaign} 엔티티를 함께 조회
     *
     * @param creatorId 조회할 크리에이터의 고유 ID
     * @param page      요청할 페이지 번호 (0부터 시작)
     * @param size      한 페이지당 조회할 데이터 개수
     * @return 크리에이터가 참여한 캠페인 목록을 담은 {@link Slice} 객체
     */
    public Slice<CreatorCampaign> findMyCampaigns(Long creatorId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return creatorCampaignRepository.findSliceWithCampaignByCreator(creatorId, pageable);
    }

    /**
     * 크리에이터가 현재 리뷰 작성이 가능한 캠페인 참여 이력들을 조회하는 메서드 - 리뷰 작성이 가능한 상태 (배송지가 확정된 상태 : 1차 리뷰 가능) 또는 (1차 리뷰를 완료한 상태 : 2차 리뷰 가능)
     *
     * @param creatorId 조회할 크리에이터의 고유 ID
     * @return 리뷰 작성 자격이 있는 {@link CreatorCampaign} 리스트 (최신순으로 정렬)
     */
    public List<CreatorCampaign> findReviewable(Long creatorId) {
        return creatorCampaignRepository.findReviewablesInReview(
                creatorId,
                CampaignStatus.IN_REVIEW,
                List.of(
                        ParticipationStatus.APPROVED_ADDRESS_CONFIRMED,
                        ParticipationStatus.APPROVED_FIRST_REVIEW_DONE,
                        ParticipationStatus.APPROVED_REVISION_REQUESTED,
                        ParticipationStatus.APPROVED_REVISION_CONFIRMED
                )
        );
    }

    /**
     * (campaign, creatorId) 조합으로 CreatorCampaign 단건을 조회 메서드 - 존재하지 않을 경우 예외 발생
     *
     * @param campaign  캠페인 엔티티 (도메인 인자 전달)
     * @param creatorId 크리에이터 고유 ID
     * @return CreatorCampaign
     */
    public CreatorCampaign getByCampaignAndCreatorId(Campaign campaign, Long creatorId) {
        return creatorCampaignRepository.findByCampaignAndCreator_Id(campaign, creatorId)
                .orElseThrow(CreatorCampaignNotFoundException::new);
    }
}
