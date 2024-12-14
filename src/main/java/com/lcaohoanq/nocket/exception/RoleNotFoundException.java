package com.lcaohoanq.nocket.exception;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;

public class RoleNotFoundException extends DataNotFoundException {

    public RoleNotFoundException(String message) {
        super(message);
    }

}
