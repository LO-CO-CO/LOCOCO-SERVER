package com.lokoko.domain.socialclip.domain;

import com.lokoko.domain.socialclip.domain.entity.enums.SocialClipContent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialClip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_clip_id")
    private Long id;

    @Column
    Long instaPlays;

    @Column
    Long instaLikes;

    @Column
    Long instaComments;

    @Column
    Long instaShares;

    @Column
    Long youtubePlays;

    @Column
    Long youtubeLikes;

    @Column
    Long youtubeComments;

    @Column
    Long youtubeShares;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialClipContent socialClipContent;
}
