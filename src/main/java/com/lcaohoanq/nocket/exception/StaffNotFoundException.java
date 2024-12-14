package com.lcaohoanq.nocket.exception;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;

public class StaffNotFoundException extends DataNotFoundException {

    public StaffNotFoundException(String message) {
        super(message);
    }

}
