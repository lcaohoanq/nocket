package com.lcaohoanq.nocket.base.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OutOfStockException extends RuntimeException{

    public OutOfStockException(String message) {
        super(message);
    }

}
