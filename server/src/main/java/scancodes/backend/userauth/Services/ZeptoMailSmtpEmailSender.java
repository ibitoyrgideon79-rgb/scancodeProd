package scancodes.backend.userauth.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import scancodes.backend.config.ZeptoMailProperties;

@Service
@ConditionalOnExpression("!'${spring.mail.host:}'.isBlank()")
public class ZeptoMailSmtpEmailSender implements EmailSender {

  private static final Logger log = LoggerFactory.getLogger(ZeptoMailSmtpEmailSender.class);

  private final JavaMailSender mailSender;
  private final ZeptoMailProperties properties;

  public ZeptoMailSmtpEmailSender(JavaMailSender mailSender, ZeptoMailProperties properties) {
    this.mailSender = mailSender;
    this.properties = properties;
  }

  @Override
  public void send(String to, String subject, String body) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(properties.getFromAddress(), properties.getFromName());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
    } catch (Exception ex) {
      log.error("ZeptoMail SMTP send failed for {}", to, ex);
      throw new IllegalStateException("Failed to send email. Please try again later.");
    }
  }
}
