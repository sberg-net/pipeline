package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.eclipse.angus.mail.pop3.POP3Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Atomic MailCheckConnection to check connection by giving properties and credentials.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_SESSION_PROPS}
 *          value: SessionProperties [{@code Map<String, Object>}] and
 *          key: {@code MAIL_AUTH_USER}<br>
 *          value: Username [{@code String}] <br>
 *          key: {@code MAIL_AUTH_PASSWORD}<br>
 *          value: Password [{@code String}] <br>
 * @Output  all inputs
 */

public class MailCheckConnection extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException {

        if (input.get(MAIL_SESSION) == null)
            throw new AtomicInputException("MAIL_SESSION not exist or is null!");
        if (!(input.get(MAIL_SESSION) instanceof Session session))
            throw new AtomicInputException("MAIL_SESSION is not instance of Session!");

        Optional<String> username = input.containsKey(MAIL_AUTH_USER)
                ? Optional.of((String) input.get(MAIL_AUTH_USER))
                : Optional.empty();

        Optional<String> password = input.containsKey(MAIL_AUTH_PASSWORD)
                ? Optional.of((String) input.get(MAIL_AUTH_PASSWORD))
                : Optional.empty();

        Optional<String> protocol = session.getProperty("mail.transport.protocol") != null
                ? Optional.ofNullable(session.getProperty("mail.transport.protocol"))
                : Optional.ofNullable(session.getProperty("mail.store.protocol"));


        Transport transport = session.getTransport(protocol.orElseThrow(()
                -> new AtomicInputException("No mail.transport.protocol or mail.store.protocol found in Session!")));

        if (username.isPresent() && password.isPresent()) {
            transport.connect(username.get(), password.get());
        } else {
            transport.connect();
        }
        return input;
    }
}
