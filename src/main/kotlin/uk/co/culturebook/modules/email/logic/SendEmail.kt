package uk.co.culturebook.modules.email.logic

import org.apache.commons.mail.SimpleEmail
import uk.co.culturebook.config.AppConfig
import uk.co.culturebook.config.getProperty

internal fun sendEmail(subjectContent: String, message: String, email: String) = SimpleEmail().apply {
    hostName = AppConfig.EmailConfig.Host.getProperty()
    setSmtpPort(AppConfig.EmailConfig.SmtpPort.getProperty().toInt())
    isSSLOnConnect = true
    isStartTLSEnabled = true
    setAuthentication(AppConfig.EmailConfig.Account.getProperty(), AppConfig.EmailConfig.Password.getProperty())
    setFrom(AppConfig.EmailConfig.Account.getProperty())
    subject = subjectContent
    setContent(message, "text/html; charset=utf-8")
    addTo(email)
    send()
}
