package com.lokoko.domain.media.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class MediaFile {

    private String fileName;

    @Column(length = 1000)
    private String fileUrl;

    public static MediaFile of(String fileName, String fileUrl) {
        return new MediaFile(fileName, fileUrl);
    }
}
