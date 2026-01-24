package net.sberg.openkim.pipeline;

import jakarta.mail.AuthenticationFailedException;
import net.sberg.openkim.pipeline.atomics.mail.MailCheckConnection;
import net.sberg.openkim.pipeline.atomics.mail.MailGetSession;
import net.sberg.openkim.pipeline.atomics.mail.MailKeys;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MailCheckConnectionTest extends MailKeys {
    @Test
    public void testCheckConnection() throws Exception {

        String protocol = "smtp";

        Map<String,String> sessionProps = new HashMap<>(){{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", System.getenv("mail_host"));
            put("mail." + protocol + ".port", "25");
            put("mail." + protocol + ".starttls.enable", "true");
        }};
        Map<String, Object> mSession = new HashMap<>(){{
            put(MAIL_SESSION_PROPS, sessionProps);
            put(MAIL_AUTH_USER, System.getenv("mail_user"));
            put(MAIL_AUTH_PASSWORD, System.getenv("mail_pwd"));
        }};

        // successful connection test
        new MailGetSession().andThen(new MailCheckConnection()).execute(mSession);

        // test missing protocol type
        sessionProps.remove("mail.store.protocol");

        Throwable exception1 = assertThrows(AtomicInputException.class, () ->
            new MailGetSession().andThen(new MailCheckConnection()).execute(mSession));
        assert(exception1.getMessage().equals("No mail.transport.protocol or mail.store.protocol found in Session!"));

        // test wrong authentification
        sessionProps.put("mail.store.protocol", protocol);
        mSession.put(MAIL_AUTH_USER, "wrongUser");
        Throwable exception2 = assertThrows(AuthenticationFailedException.class, () ->
        new MailGetSession().andThen(new MailCheckConnection()).execute(mSession));
        assert(exception2.getMessage().contains("authentication failed"));
    }
}
