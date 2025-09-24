package com.lokoko.domain.creatorSocialStats.domain.entity;

import com.lokoko.domain.creator.domain.entity.Creator;
import jakarta.persistence.*;
import lombok.*;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(name = "creator_social_stats")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreatorSocialStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "creator_id")
    private Creator creator;

    private Integer instagramFollower;

    private Integer instagramFollowing;

    private Integer tiktokFollower;

    private Integer tiktokFollowing;
}
