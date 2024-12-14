package com.lcaohoanq.nocket.exception;

import com.lcaohoanq.nocket.base.exception.DataAlreadyExistException;

public class RoleAlreadyExistException extends DataAlreadyExistException {

    public RoleAlreadyExistException(String message) {
        super(message);
    }

}
