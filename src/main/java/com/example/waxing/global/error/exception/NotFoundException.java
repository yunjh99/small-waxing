package com.example.waxing.global.error.exception;

import com.example.waxing.global.error.domain.DomainType;

public class NotFoundException extends ApiException {

    public NotFoundException(DomainType type){
        super(type.notFoundException());
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
