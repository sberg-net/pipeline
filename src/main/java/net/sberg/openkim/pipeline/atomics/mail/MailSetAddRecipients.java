package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.Map;

/**
 * Atomic MailSetAddRecipients will add or set recipients of MimeMessage.
 * The recipient map contains tupels of recipient type and mail addressList (comma seperated).
 * In default set will be used and replaces all addresses in recipient type.
 * The addresses will be added with MAIL_SETRECIPIENTS_ADD = true.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input  key: {@code MAIL_MIMEMESSAGE} <br>
 *         value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *         key: {@code MAIL_SETRECIPIENTS}<br>
 *         value: SetRecipientList [{@code Map<String,Message.RecipientType>}]
 *         key: {@code MAIL_SETRECIPIENTS_ADD}<br>
 *         value: AddRecipientsFlag [{@code boolean}]
 *         optional: default -> false
 * @Output all Inputs
 */

public class MailSetAddRecipients extends MailKeys implements PipelineOp {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_SETADDRECIPIENTS) == null)
            throw new AtomicInputException("MAIL_SETRECIPIENTS not exist or is null!");
        if (!(input.get(MAIL_SETADDRECIPIENTS) instanceof Map<?,?>))
            throw new AtomicInputException("MAIL_SETRECIPIENTS is not instance of Map");

        ((Map)input.get(MAIL_SETADDRECIPIENTS)).forEach((k, v) -> {
            if (! (k instanceof Message.RecipientType) || ! (v instanceof String))
                throw new AtomicInputException("Map is not of type Map<Message.RecipientType, String>!");
        });

        Map<Message.RecipientType,String> recipients = (Map<Message.RecipientType, String>) input.get(MAIL_SETADDRECIPIENTS);
        boolean add = false;
        if (input.containsKey(MAIL_SETADDRECIPIENTS_ADD))
            add = (boolean) input.get(MAIL_SETADDRECIPIENTS_ADD);

        for (Map.Entry<Message.RecipientType,String> entry : recipients.entrySet()) {
            if (add)
                message.addRecipients(entry.getKey(), InternetAddress.parse(entry.getValue()));
            else
                message.setRecipients(entry.getKey(), InternetAddress.parse(entry.getValue()));
        }
        return input;
    }
}
