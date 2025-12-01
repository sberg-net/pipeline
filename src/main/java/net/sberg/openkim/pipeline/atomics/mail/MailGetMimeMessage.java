package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

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

public class MailGetMimeMessage extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailGetMimeMessage.class);

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input)
            throws FileNotFoundException, MessagingException, AtomicInputException {

        if (input.get(MAIL_FILE) != null && !(input.get(MAIL_FILE) instanceof File))
            throw new AtomicInputException("MAIL_FILE is not instance of File!");
        if (input.get(MAIL_STREAM) != null && !(input.get(MAIL_STREAM) instanceof InputStream))
            throw new AtomicInputException("MAIL_STREAM is not instance of InputStream!");
        if (input.get(MAIL_SESSION) != null && !(input.get(MAIL_SESSION) instanceof Session))
            throw new AtomicInputException("MAIL_SESSION is not instance of Session!");

        MimeMessage message;
        Session session = (input.containsKey(MAIL_SESSION))
                ? (Session) input.get(MAIL_SESSION)
                : Session.getInstance(new Properties());

        File mailFile = (input.containsKey(MAIL_FILE))
                ? (File) input.get(MAIL_FILE)
                : null;

        InputStream mailInputStream = (input.containsKey(MAIL_STREAM))
                ? (InputStream) input.get(MAIL_STREAM)
                : null;

        if (System.getProperty("mail.mime.contenttypehandler") != null) {
            logger.debug("Use custom contentTypeHandler: {}", System.getProperty("mail.mime.contenttypehandler"));
        }

        if ( mailFile != null ) {
            logger.debug("Create MimeMessage from File: {}", ((File) input.get(MAIL_FILE)).getName());
            message = new MimeMessage(session, new FileInputStream(mailFile));
        } else if ( mailInputStream != null ) {
            logger.debug("Create MimeMessage from InputStream");
            message = new MimeMessage(session, mailInputStream);
        } else {
            logger.debug("Create empty MimeMessage");
            message = new MimeMessage(session);
        }
        input.put(MAIL_MIMEMESSAGE, message);
        return input;
    }
}
