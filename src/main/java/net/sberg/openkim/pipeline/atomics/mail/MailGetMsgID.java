package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Atomic MailGetMsgId gets the MessageID and can generate one if not exist.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input  key: {@code MAIL_MIMEMESSAGE} <br>
 *         value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}] <br>
 *         key: {@code MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS} <br>
 *         value: MimeMessageIdCreateIfNotExist [{@code boolean}] <br>
 *         optional: default -> false
 *
 * @Output all Inputs and
 *          key: {@code MAIL_MAIL_MESSAGEID}
 *          value: MessageId [{@code String}]
 */

public class MailGetMsgID extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailGetMsgID.class);

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.containsKey(MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS) &&
                !(input.get(MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS) instanceof Boolean))
            throw new AtomicInputException("MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS is not instance of boolean!");

        boolean createIfNotExists = input.containsKey(MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS)
                && (Boolean) input.get(MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS);

        if ( (message.getMessageID() == null || message.getMessageID().isEmpty()) && createIfNotExists ) {
            message.saveChanges();
            input.put(MAIL_MESSAGEID, message.getMessageID());
            logger.info("MimeMessage has no MessageID, new MessageID: {} generated", message.getMessageID());
        }
        else {
            input.put(MAIL_MESSAGEID, message.getMessageID());
            logger.info("Message ID: {}", message.getMessageID());
        }
        return input;
    }
}
