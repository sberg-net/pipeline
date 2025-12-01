package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailRemoveMimeBodyParts If MimeBodyPartList is empty, no parts will be removed.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *          key: {@code MAIL_MIMEBODYPARTS}<br>
 *          value: MimeBodyPartList [{@code List<jakarta.mail.internet.MimeBodyPart>}]
 * @Output  all input values
 */

public class MailRemoveMimeBodyParts extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailRemoveMimeBodyParts.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input)
            throws IOException, MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_MIMEBODYPARTS) == null)
            throw new AtomicInputException("MAIL_MIMEBODYPARTS not exist or is null!");
        if (!(input.get(MAIL_MIMEBODYPARTS) instanceof List<?>))
            throw new AtomicInputException("MAIL_MIMEBODYPARTS is not instance of List!");

        ((List) input.get(MAIL_MIMEBODYPARTS)).forEach(object -> {
            if (! (object instanceof MimeBodyPart) ) {
                throw new AtomicInputException("MAIL_MIMEBODYPARTS elements not instance of MimeBodyPart!");
            }
        });

        if (message.getContentType().contains("multipart")) {
            List<MimeBodyPart> parts = (List<MimeBodyPart>) input.get(MAIL_MIMEBODYPARTS);
            Multipart multiPartMessage = (Multipart) message.getContent();

            logger.debug("Removing {} MimeBodyParts", parts.size());

            for (MimeBodyPart part : parts) {
                multiPartMessage.removeBodyPart(part);
            }
        }
        else
            logger.debug("Message content is not Multipart, deleting parts is not supported");

        return input;
    }
}
