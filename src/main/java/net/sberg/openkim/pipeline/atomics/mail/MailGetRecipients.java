package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailGetRecipients get all sender addresses mime message.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input  key: {@code MAIL_MIMEMESSAGE} <br>
 *         value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *         key: {@code MAIL_RECIPIENTS_TYPES}<br>
 *         value: MailRecipientList [{@code List<Message.RecipientType>}] <br>
 *         optional: default -> TO
 * @Output all Inputs and<br>
 *          key: {@code MAIL_RECIPIENTS}<br>
 *          value: MailAddresses [{@code List<String>}] empty if no sender address is found
 */

public class MailGetRecipients extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_RECIPIENTS_TYPES) != null && ! (input.get(MAIL_RECIPIENTS_TYPES) instanceof List<?>))
            throw new AtomicInputException("MAIL_RECIPIENTS_TYPES is not instance of List!");

        if (input.get(MAIL_RECIPIENTS_TYPES) == null || ((List<Message.RecipientType>) input.get(MAIL_RECIPIENTS_TYPES)).isEmpty())
            input.put(MAIL_RECIPIENTS_TYPES, new ArrayList<>() {{ add(Message.RecipientType.TO); }});

        input.put(MAIL_RECIPIENTS, new ArrayList<String>());
        List<Message.RecipientType> types = (List<Message.RecipientType>) input.get(MAIL_RECIPIENTS_TYPES);

        for (Message.RecipientType type : types) {
            if (message.getRecipients(type) != null) {
                Arrays.stream(message.getRecipients(type)).iterator().forEachRemaining(to ->
                        ((List<String>) input.get(MAIL_RECIPIENTS)).add(to.toString()));
            }
        }
        return input;
    }
}
