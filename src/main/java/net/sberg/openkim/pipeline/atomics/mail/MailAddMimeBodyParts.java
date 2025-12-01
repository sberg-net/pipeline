package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailGetMimeMessage create a MimeMessage from mail File, mail InputStream or new empty MimeMessage.
 * If a Session is set, it will be added to MimeMessage otherwise an empty Session will be set.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_FILE}<br>
 *          value: File [{@code java.io.File}] <br>
 *          optional: default -> null<br>
 *          key: [{@code  MAIL_STREAM}]<br>
 *          value: InputStream [{@code java.io.InputStream}]<br>
 *          optional: default -> null<br>
 *          key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *          optional: default -> EmptySession
 * @Output  key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 */

public class MailAddMimeBodyParts extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailAddMimeBodyParts.class);

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input)
            throws FileNotFoundException, MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_ADDMIMEBODYPARTS) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_ADDMIMEBODYPARTS) instanceof List<?> parts))
            throw new AtomicInputException("MAIL_ADDMIMEBODYPARTS is not instance of List!");
        if (parts.isEmpty())
            throw new AtomicInputException("MAIL_ADDMIMEBODYPARTS is an empty list!");

        Multipart multipart = new MimeMultipart();

        for(Object part : parts) {
            if (part instanceof File partFile){
                logger.debug("Create MimeBodyPart from File: {}", partFile.getName());
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(partFile);
                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(partFile.getName());
                multipart.addBodyPart(mimeBodyPart);
            }
            else if (part instanceof String partText){
                logger.debug("Create MimeBodyPart from String (Text)");
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setText(partText);
                multipart.addBodyPart(mimeBodyPart);
            }
            else {
                throw new AtomicInputException(part.getClass() + " is not an supported type!");
            }
        }
        message.setContent(multipart);
        return input;
    }
}
