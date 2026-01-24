package net.sberg.openkim.pipeline;

import jakarta.mail.AuthenticationFailedException;
import net.sberg.openkim.pipeline.atomics.mail.MailCheckConnection;
import net.sberg.openkim.pipeline.atomics.mail.MailGetSession;
import net.sberg.openkim.pipeline.atomics.mail.MailKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MailCheckConnectionTest extends MailKeys {
    @Test
    public void testCheckConnection() throws Exception {

        Map<String,String> sessionPropsSmtp = new HashMap<>(){{
            put("mail.transport.protocol", "smtp");
            put("mail.smtp.host", System.getenv("mail_host"));
            put("mail.smtp.port", "25");
            put("mail.smtp.starttls.enable", "true");
        }};
        Map<String, Object> mSession = new HashMap<>(){{
            put(MAIL_SESSION_PROPS, sessionPropsSmtp);
            put(MAIL_AUTH_USER, System.getenv("mail_user"));
            put(MAIL_AUTH_PASSWORD, System.getenv("mail_pwd"));
        }};

        // successful connection test
        new MailGetSession().andThen(new MailCheckConnection()).execute(mSession);

        // test missing protocol type
        sessionPropsSmtp.remove("mail.transport.protocol");

        Throwable exception1 = assertThrows(AtomicInputException.class, () ->
            new MailGetSession().andThen(new MailCheckConnection()).execute(mSession));
        assert(exception1.getMessage().equals("No mail.store.protocol or mail.transport.protocol found in Session!"));

        // test wrong authentification
        sessionPropsSmtp.put("mail.transport.protocol", "smtp");
        mSession.put(MAIL_AUTH_USER, "wrongUser");
        Throwable exception2 = assertThrows(AuthenticationFailedException.class, () ->
        new MailGetSession().andThen(new MailCheckConnection()).execute(mSession));
        assert(exception2.getMessage().contains("authentication failed"));

        // test protocol pop3
        Map<String,String> sessionPropsPop3 = new HashMap<>(){{
            put("mail.store.protocol", "pop3");
            put("mail.pop3.host", System.getenv("mail_host"));
            put("mail.pop3.port", "110");
            put("mail.pop3.starttls.enable", "true");
        }};
        mSession.put(MAIL_SESSION_PROPS, sessionPropsPop3);
        mSession.put(MAIL_AUTH_USER, System.getenv("mail_user"));

        new MailGetSession().andThen(new MailCheckConnection()).execute(mSession);

    }
}
