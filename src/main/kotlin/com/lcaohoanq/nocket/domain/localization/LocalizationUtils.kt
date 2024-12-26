package com.lcaohoanq.nocket.domain.localization

import com.lcaohoanq.nocket.util.WebUtil
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver

@Component
class LocalizationUtils(
    private val messageSource: MessageSource,
    private val customLocaleResolver: LocaleResolver
) {

    fun getLocalizedMessage(messageKey: String, vararg params: Any?): String {
        val request = WebUtil.getCurrentRequest()
        val locale = customLocaleResolver!!.resolveLocale(request)
        return messageSource!!.getMessage(messageKey, params, locale)
    }
}
