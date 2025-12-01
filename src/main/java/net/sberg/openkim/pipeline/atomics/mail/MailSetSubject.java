package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.Map;

/**
 * Atomic MailGetMimeMessage create a MimeMessage from mail File, mail InputStream or new empty MimeMessage.
 * If a Session is set, it will be added to MimeMessage otherwise an empty Session will be set.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_SUBJECT}<br>
 *          value: Subject [{@code String}] <br>
 *          key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 * @Output  all inputs
 */

public class MailSetSubject extends MailKeys implements PipelineOp {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_SUBJECT) == null)
            throw new AtomicInputException("MAIL_SETSUBJECT not exist or is null!");
        if (!(input.get(MAIL_SUBJECT) instanceof String))
            throw new AtomicInputException("MAIL_SETSUBJECT is not instance of String!");

        message.setSubject((String) input.get(MAIL_SUBJECT));

        return input;
    }
}
