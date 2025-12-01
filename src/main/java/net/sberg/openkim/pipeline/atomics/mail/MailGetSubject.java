package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.Map;

public class MailGetSubject extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException {
        String subject = ((MimeMessage) input.get(MAIL_MIMEMESSAGE)).getSubject();
        input.put(MAIL_SUBJECT, subject);
        return input;
    }
}
