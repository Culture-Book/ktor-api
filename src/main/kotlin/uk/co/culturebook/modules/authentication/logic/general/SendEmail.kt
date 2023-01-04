package uk.co.culturebook.modules.authentication.logic.general

import org.apache.commons.mail.SimpleEmail

internal fun sendEmail(
    subjectContent: String, message: String, email: String,
    host: String, smtp: Int, account: String, password: String
) = SimpleEmail().apply {
    hostName = host
    setSmtpPort(smtp)
    isSSLOnConnect = true
    isStartTLSEnabled = true
    setAuthentication(account, password)
    setFrom(account)
    subject = subjectContent
    setContent(message, "text/html; charset=utf-8")
    addTo(email)
    send()
}
