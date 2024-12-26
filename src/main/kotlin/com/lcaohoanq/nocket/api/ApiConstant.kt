package com.lcaohoanq.nocket.api

import org.springframework.beans.factory.annotation.Value

object ApiConstant {
    
    @Value("\${api.prefix}")
    const val API_PREFIX: String = "/api/v1"

}