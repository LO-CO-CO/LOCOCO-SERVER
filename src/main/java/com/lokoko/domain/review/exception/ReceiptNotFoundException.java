package com.lokoko.domain.review.exception;


import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReceiptNotFoundException extends BaseException {
    public ReceiptNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.RECEIPT_IMAGE_NOT_FOUND.getMessage());
    }
}
