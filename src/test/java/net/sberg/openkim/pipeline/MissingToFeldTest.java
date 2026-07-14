package net.sberg.openkim.pipeline;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.atomics.mail.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingToFeldTest extends MailKeys {

    private static final Logger log = LoggerFactory.getLogger(MissingToFeldTest.class);

    @Test
    @SuppressWarnings("unchecked")
    void testMailPop3GetMessages() throws Exception {

        String protocol = "pop3";

        Map<String, Object> sessionProps = new HashMap<>() {{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", System.getenv("mail_host"));
            put("mail." + protocol + ".port", "110");
            put("mail." + protocol + ".starttls.enable", "true");
        }};
        Map<String, Object> getPop3Messages = new HashMap<>() {{
            put(MAIL_SESSION_PROPS, sessionProps);
            put(MAIL_GETMESSAGES_PASSWORD, System.getenv("mail_pwd"));
            put(MAIL_GETMESSAGES_USER, System.getenv("mail_user"));
            put(MAIL_GETMESSAGES_FOLDER, "inbox");
        }};

        getPop3Messages = new MailGetSession()
                .andThen(new MailPop3FetchMessageInfo())
                .execute(getPop3Messages);

        List<MessageHeadInfo> msgInfos = (List<MessageHeadInfo>) getPop3Messages.get(MAIL_POP3FETCHMSGINFO);
        assert !msgInfos.isEmpty();
        assert !msgInfos.getFirst().getMsgId().isBlank();
        for (MessageHeadInfo info : msgInfos) {
            log.info("\nSubject: {}\nPop3UID: {}\nTo: {}\nCc: {}\nFrom: {}\nDate: {}\nSize: {}",info.getSubject(),
                    info.getUid(), info.getTo(), info.getCc(), info.getFrom(), info.getSendDate(), info.getSize());
        }

        getPop3Messages = new MailGetSession()
                .andThen(new MailPop3GetMessages())
                .execute(getPop3Messages);

        List<MimeMessage> messages = (List<MimeMessage>)getPop3Messages.get(MAIL_GETMESSAGES);
        if (messages != null) {
            for(Message m : messages) {
                log.info(m.getSubject());
                assert m.getAllRecipients() == null;
            }
        }
    }
}
