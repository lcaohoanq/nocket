package com.lcaohoanq.nocket.exception;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;

public class TokenNotFoundException extends DataNotFoundException {

    public TokenNotFoundException(String message) {
        super(message);
    }

}
