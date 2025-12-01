package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.Map;

/**
 * Atomic MailAddHeader adds Header to MimeMessage.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_HEADER}
 *          value: MailHeader [{@code Map<String, String>}] and
 *          key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 * @Output  key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 */

public class MailAddHeader extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (input.get(MAIL_HEADER) == null)
            throw new AtomicInputException("MAIL_SUBJECT not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (!(input.get(MAIL_HEADER) instanceof Map<?,?>))
            throw new AtomicInputException("MAIL_HEADER is not instance of Map!");

        Map<String,String> headerMap = (Map<String,String>) input.get(MAIL_HEADER);
        for (Map.Entry<String, String> header : headerMap.entrySet()) {
            message.addHeader(header.getKey(), header.getValue());
        }
        return input;
    }
}
