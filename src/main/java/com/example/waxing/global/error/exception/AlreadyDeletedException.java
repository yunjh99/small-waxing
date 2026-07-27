package com.example.waxing.global.error.exception;

import com.example.waxing.global.error.domain.DomainType;

public class AlreadyDeletedException extends ApiException {

    public AlreadyDeletedException(DomainType type) {
        super(type.alreadyDeletedMessage());
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}