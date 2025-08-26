package com.lokoko.global.common.exception.translation;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MigrationFailedException extends BaseException {
    public MigrationFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.MIGRATION_FAILED.getMessage());
    }
}
