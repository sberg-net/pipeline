package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailRemovePersonalFromRecipients will remove personal (description) from recipients.
 * Recipient types to be removed can be set via MAIL_RECIPIENTS_TYPES.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input  key: {@code MAIL_MIMEMESSAGE} <br>
 *         value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *         key: {@code MAIL_RECIPIENTS_TYPES}<br>
 *         value: MailRecipientList [{@code List<Message.RecipientType>}] <br>
 * @Output all Inputs and<br>
 *          key: {@code MAIL_REPLACERECIPIENTS_COUNT}<br>
 *          value: replacementCount [{@code Integer}]
 */

public class MailRemovePersonalFromRecipients extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailRemovePersonalFromRecipients.class);

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_RECIPIENTS_TYPES) == null)
            throw new AtomicInputException("MAIL_RECIPIENTS_TYPES not exist or is null!");
        if (! (input.get(MAIL_RECIPIENTS_TYPES) instanceof List<?>))
            throw new AtomicInputException("MAIL_RECIPIENTS_TYPES is not instance of List!");

        List<Message.RecipientType> types = (List<Message.RecipientType>) input.get(MAIL_RECIPIENTS_TYPES);

        for (Message.RecipientType type : types) {
                if (message.getRecipients(type) != null) {
                    List<Address> addresses = new ArrayList<>(List.of(message.getRecipients(type)));
                    addresses.replaceAll(address -> {
                            try {
                                InternetAddress ia = new InternetAddress(address.toString());
                                if (ia.getPersonal() != null)
                                        logger.info("Removing personal from ({}) address {}",type.toString(), address);
                                address = new InternetAddress(ia.getAddress());
                            }
                            catch (AddressException e) { throw new RuntimeException(e); }
                        return address;
                    });
                    message.setRecipients(type, addresses.toArray(new Address[0]));
                }
        }
        return input;
    }
}
