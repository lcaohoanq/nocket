package com.lcaohoanq.nocket.exception;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;

public class MemberNotFoundException extends DataNotFoundException {

    public MemberNotFoundException(String message) {
        super(message);
    }

}
