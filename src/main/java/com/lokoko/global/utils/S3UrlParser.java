package com.lokoko.global.utils;

import com.lokoko.domain.productReview.exception.PresignedUrlParsingException;
import com.lokoko.global.common.entity.MediaFile;
import java.net.URI;
import java.nio.file.Paths;

public class S3UrlParser {

    public static MediaFile parsePresignedUrl(String presignedUrl) {
        try {
            URI uri = new URI(presignedUrl);
            String path = uri.getPath();
            String fileName = Paths.get(path).getFileName().toString();
            String fileUrl = uri.getScheme() + "://" + uri.getHost() + path;

            return MediaFile.of(fileName, fileUrl);
        } catch (Exception e) {
            throw new PresignedUrlParsingException();
        }
    }
}
