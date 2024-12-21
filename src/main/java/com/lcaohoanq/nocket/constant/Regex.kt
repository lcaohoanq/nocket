package com.lcaohoanq.nocket.constant

object Regex {
    const val PASSWORD_REGEX: String =
        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"
    const val PHONE_NUMBER_REGEX: String = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$"
}
