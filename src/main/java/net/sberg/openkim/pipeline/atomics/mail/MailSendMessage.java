package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Atomic MailSendMessages send as a message via smtp. The MimeMessage must have a proper configured smtp session.
 * If a user or password isn't set, a message will be sent unauthorized (without authentication). Is AddressToSendMessage
 * is given it will be used as sendTo Addresses, if not MimeMessage recipients will be used.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}], must have a proper configured session<br>
 *          key: {@code MAIL_SENDMESSAGE_ADDRESSES}<br>
 *          value: AddressToSendMessage [{@code jakarta.mail.Address[]}]<br>
 *          optional: if not set recipients of MimeMessage will be used<br>
 *          key: {@code MAIL_SENDMESSAGE_USER}<br>
 *          value: smtpUser [{@code String}]<br>
 *          optional: an if not set message will be sent without smtp authentication<br>
 *          key: {@code MAIL_SENDMESSAGE_PASSWORD}<br>
 *          value: smtpPassword [{@code String}]<br>
 *          optional: an if not set message will be sent without smtp authentication<br>
 * @Output  all Inputs
 */

public class MailSendMessage extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailSendMessage.class);

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws AtomicInputException, IOException, MessagingException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.containsKey(MAIL_SENDMESSAGE_ADDRESSES)) {
            if (!(input.get(MAIL_SENDMESSAGE_ADDRESSES) instanceof Address[]))
                throw new AtomicInputException("MAIL_SENDMESSAGE_ADDRESSES is not instance of Address[]!");
            if (((Address[]) input.get(MAIL_SENDMESSAGE_ADDRESSES)).length == 0)
                throw new AtomicInputException("MAIL_SENDMESSAGE_ADDRESSES are empty!");
        }

        if (message.getSession() == null)
            throw new AtomicInputException("Session in MimeMessage is null but is needed in this atomic!");
        if (message.getSession().getProperty("mail.smtp.host") == null)
            throw new AtomicInputException("mail.smtp.host not set in MimeMessage Session!");

        String user = (String) input.get(MAIL_SENDMESSAGE_USER);
        String password = (String) input.get(MAIL_SENDMESSAGE_PASSWORD);
        Address[] addresses = input.containsKey(MAIL_SENDMESSAGE_ADDRESSES)
                ? (Address[]) input.get(MAIL_SENDMESSAGE_ADDRESSES)
                : null;

        if (user == null || password == null || user.isBlank() || password.isBlank()) {
            logger.debug("Sending message without user and password!");
            if (addresses != null) {
                logger.info("Sending Message {} to {}", message.getMessageID(), addresses);
                Transport.send(message, addresses);
            }
            else {
                logger.info("Sending Message {}", message.getMessageID());
                Transport.send(message);
            }
        } else {
            logger.debug("Sending message with user and password!");
            if (addresses != null){
                logger.info("Sending Message {} via account {} to {}", message.getMessageID(), user, addresses);
                Transport.send(message, addresses, user, password);
            }
            else {
                logger.info("Sending Message {} via account {}", message.getMessageID(), user);
                Transport.send(message, user, password);
            }
        }
        return input;
    }
}
