package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.*;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Atomic MailGetSession from provider.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_SESSION_PROPS}
 *          value: SessionProperties [{@code Map<String, Object>}] and
 * @Output  key: {@code MAIL_SESSION}
 *          value: Session [{@code jakarta.mail.Session}]
 */

public class MailGetSession extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        Logger logger = LoggerFactory.getLogger(MailGetSession.class);

        if (input.get(MAIL_SESSION_PROPS) == null)
            throw new AtomicInputException("MAIL_SESSION_PROPS not exist or is null!");
        if (!(input.get(MAIL_SESSION_PROPS) instanceof Map<?,?>))
            throw new AtomicInputException("MAIL_SESSION_PROPS is not instance of Map!");

        Map<String,Object> sessionProps = (Map<String, Object>) input.get(MAIL_SESSION_PROPS);
        Properties props = new Properties(){{ putAll(sessionProps); }};

        logger.debug("Properties of Session: {}", props);

        input.put(MAIL_SESSION, Session.getInstance(props));
        return input;
    }
}
