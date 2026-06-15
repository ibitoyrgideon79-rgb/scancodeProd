package scancodes.backend.userauth.Services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnMissingBean(EmailSender.class)
public class ConsoleEmailSender implements EmailSender {
    @Override
    public void send(String to, String subject, String body) {
        System.out.println("--- DEBUG EMAIL (ZeptoMail not configured — set ZEPTOMAIL_SMTP_* in .env) ---");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("--------------------------------------------------------------------------------");
    }
}
