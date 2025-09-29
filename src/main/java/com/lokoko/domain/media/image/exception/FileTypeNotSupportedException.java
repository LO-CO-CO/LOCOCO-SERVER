package com.lokoko.domain.media.image.exception;

import static com.lokoko.domain.media.image.exception.ErrorMessage.FILE_TYPE_NOT_SUPPORTED;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;


public class FileTypeNotSupportedException extends BaseException {
    public FileTypeNotSupportedException() {
        super(HttpStatus.BAD_REQUEST, FILE_TYPE_NOT_SUPPORTED.getMessage());
    }
}
