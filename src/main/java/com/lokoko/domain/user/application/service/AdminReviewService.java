package com.lokoko.domain.user.application.service;

import com.lokoko.domain.image.domain.repository.ReceiptImageRepository;
import com.lokoko.domain.image.domain.repository.ReviewImageRepository;
import com.lokoko.domain.productReview.domain.entity.Review;
import com.lokoko.domain.productReview.domain.repository.ReviewRepository;
import com.lokoko.domain.productReview.exception.ReviewNotFoundException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.video.domain.repository.ReviewVideoRepository;
import com.lokoko.global.utils.AdminValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReceiptImageRepository receiptImageRepository;

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        AdminValidator.validateUserRole(user);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        deleteAllMediaOfReview(review);
        reviewRepository.delete(review);
    }

    /**
     * Todo: 리뷰에 연결된 모든 미디어(영수증, 사진, 비디오)를 삭제하는 메소드 추후 유틸 클래스로 분리 예정
     */
    public void deleteAllMediaOfReview(Review review) {
        receiptImageRepository.deleteAllByReview(review);
        reviewImageRepository.deleteAllByReview(review);
        reviewVideoRepository.deleteAllByReview(review);
    }
}
