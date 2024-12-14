package com.lcaohoanq.nocket.exception;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;

public class CategoryNotFoundException extends DataNotFoundException {

    public CategoryNotFoundException(String message) {
        super(message);
    }

}
