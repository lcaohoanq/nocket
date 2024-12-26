package com.lcaohoanq.nocket.constant

class EmailSubject {
    companion object {
        fun subjectGreeting(name: String?): String {
            return """
                nocket Corporation - Welcome $name, thanks for joining us!
            """.trimIndent()
        }

        fun subjectRequestUpdateRole(): String {
            return """
                nocket Corporation - New Request to update role
            """.trimIndent()
        }

        fun subjectForgotPassword(name: String?): String {
            return """
                nocket Corporation - Hi $name, we're here to help you get the password back!
            """.trimIndent()
        }

        fun subjectRunningApp(): String {
            return """
                nocket Corporation - Your app is running, Happy Coding!
            """.trimIndent()
        }

        fun subjectBlockEmail(name: String?): String {
            return """
                nocket Corporation - $name, your account has been blocked!
            """.trimIndent()
        }
    }
}
