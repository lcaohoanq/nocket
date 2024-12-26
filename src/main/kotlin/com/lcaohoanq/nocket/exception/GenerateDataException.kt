package com.lcaohoanq.nocket.exception

import lombok.NoArgsConstructor

@NoArgsConstructor
class GenerateDataException(message: String?) : RuntimeException(message)
