package com.lcaohoanq.nocket.component

import com.lcaohoanq.nocket.util.WebUtil
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver

@Component
open class LocalizationUtils(
    private val messageSource: MessageSource,
    private val customLocaleResolver: LocaleResolver
) {

    open fun getLocalizedMessage(messageKey: String, vararg params: Any?): String {
        val request = WebUtil.getCurrentRequest()
        val locale = customLocaleResolver!!.resolveLocale(request)
        return messageSource!!.getMessage(messageKey, params, locale)
    }
}
