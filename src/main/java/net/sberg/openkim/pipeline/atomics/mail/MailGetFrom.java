package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.*;

/**
 * Atomic MailGetSender get all sender addresses mime message.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input  key: {@code MAIL_MIMEMESSAGE}
 *         value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 * @Output all Inputs and
 *         key: {@code MAIL_SENDER}
 *         value: MailAddresses [{@code List<String>}] empty if no sender address is found
 */

public class MailGetFrom extends MailKeys implements PipelineOp {
    @SuppressWarnings("unchecked")

    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");

        input.put(MAIL_FROM, new ArrayList<String>());
        Arrays.stream(message.getFrom()).iterator().forEachRemaining(from ->
                ((List<String>) input.get(MAIL_FROM)).add(from.toString()));
        return input;
    }
}
