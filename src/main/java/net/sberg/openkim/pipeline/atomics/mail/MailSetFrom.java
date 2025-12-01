package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.Map;

/**
 * Atomic MailSetFrom set From Address in MimeMessage. Will replace an existing header value.
 * If value is null header will be removed. Given mail address will be syntax validated.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_FROM}
 *          value: MailFrom [{@code String}] and
 *          key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 * @Output  key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 */

public class MailSetFrom extends MailKeys implements PipelineOp {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (input.get(MAIL_FROM) == null)
            throw new AtomicInputException("MAIL_FROM not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage mimeMessage))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (!(input.get(MAIL_FROM) instanceof String))
            throw new AtomicInputException("MAIL_FROM is not instance of String!");

        InternetAddress address = new InternetAddress((String) input.get(MAIL_FROM), true);
        mimeMessage.setFrom(address);
        return input;
    }
}
