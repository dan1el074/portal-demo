package br.com.metaro.portal.util.email;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailLogService emailLogService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void sendHtmlEmail(String para, String assunto, String html, String module) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(EmailCssInliner.inline(html), true);
            helper.setFrom(new InternetAddress(mailFrom, "Portal Metaro"));

            mailSender.send(message);
        } catch (Exception exception) {
            emailLogService.record(para, assunto, module, EmailStatus.ERROR, exception);
            throw exception;
        }
        emailLogService.record(para, assunto, module, EmailStatus.SENT, null);
    }
}
