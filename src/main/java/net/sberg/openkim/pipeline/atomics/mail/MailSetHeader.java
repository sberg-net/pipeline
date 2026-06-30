package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Atomic MailSetHeader set Header to MimeMessage. Will replace an existing header value.
 * If the value is null, the header will be removed.
 * @Input   key: {@code MAIL_HEADER}
 *          value: MailHeader [{@code List<jakarta.mail.Header>}] and
 *          key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 * @Output  key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 */

public class MailSetHeader extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailSetHeader.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (input.get(MAIL_HEADER) == null)
            throw new AtomicInputException("MAIL_HEADER not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (!(input.get(MAIL_HEADER) instanceof List<?>))
            throw new AtomicInputException("MAIL_HEADER is not instance of List!");

        List<Header> headerList = (List<Header>) input.get(MAIL_HEADER);
        for (Header header : headerList) {
            if (header.getValue() == null) {
                logger.debug("Remove header {} from message", header.getName());
                message.removeHeader(header.getName());
            }
            else {
                logger.debug("Set header {} value {} to message", header.getName(), header.getValue());
                message.setHeader(header.getName(), header.getValue());
            }
        }
        return input;
    }
}
