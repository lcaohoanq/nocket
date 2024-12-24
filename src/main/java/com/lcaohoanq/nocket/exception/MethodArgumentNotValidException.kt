package com.lcaohoanq.nocket.exception

import lombok.Getter
import org.springframework.validation.BindingResult

class MethodArgumentNotValidException(val bindingResult: BindingResult) :
    RuntimeException("Validation failed")
