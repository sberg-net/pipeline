package net.sberg.openkim.pipeline;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.atomics.mail.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HugeMailTest extends MailKeys {

    private static final Logger log = LoggerFactory.getLogger(HugeMailTest.class);

    @SuppressWarnings("unchecked")
    @Test void testPop3FetchMessageHeader() throws Exception {
        String protocol = "pop3";

        Map<String, Object> sessionProps = new HashMap<>() {{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", System.getenv("mail_host"));
            put("mail." + protocol + ".port", "110");
            put("mail." + protocol + ".starttls.enable", "true");
        }};
        Map<String, Object> fetchPop3Messages = new HashMap<>() {{
            put(MAIL_SESSION_PROPS, sessionProps);
            put(MAIL_GETMESSAGES_PASSWORD, System.getenv("mail_pwd"));
            put(MAIL_GETMESSAGES_USER, System.getenv("mail_user"));
            put(MAIL_GETMESSAGES_FOLDER, "inbox");
        }};

        fetchPop3Messages = new MailGetSession()
                .andThen(new MailPop3FetchMessageInfo())
                .execute(fetchPop3Messages);

        List<MessageHeadInfo> msgInfos = (List<MessageHeadInfo>) fetchPop3Messages.get(MAIL_POP3FETCHMSGINFO);
        assert !msgInfos.isEmpty();
        assert !msgInfos.getFirst().getMsgId().isBlank();
        for (MessageHeadInfo info : msgInfos) {
            log.info("\nSubject: {}\nPop3UID: {}\nTo: {}\nCc: {}\nFrom: {}\nDate: {}\nSize: {}",info.getSubject(),
                    info.getUid(), info.getTo(), info.getCc(), info.getFrom(), info.getSendDate(), info.getSize());
        }
    }

    @SuppressWarnings("unchecked")
    @Test void testMailPop3GetMessages() throws Exception {

        String protocol = "pop3";

        Map<String, Object> sessionProps = new HashMap<>() {{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", System.getenv("mail_host"));
            put("mail." + protocol + ".port", "110");
            put("mail." + protocol + ".starttls.enable", "true");
            put("mail." + protocol + ".timeout", "100");
            put("mail." + protocol + ".connectiontimeout", "100");
            put("mail." + protocol + ".writetimeout", "10");
            put("mail." + protocol + ".readtimeout", "10");
        }};
        Map<String, Object> getPop3Messages = new HashMap<>() {{
            put(MAIL_SESSION_PROPS, sessionProps);
            put(MAIL_GETMESSAGES_PASSWORD, System.getenv("mail_pwd"));
            put(MAIL_GETMESSAGES_USER, System.getenv("mail_user"));
            put(MAIL_GETMESSAGES_FOLDER, "inbox");
            put(MAIL_GETMESSAGES_EXPUNGE, false);
            put(MAIL_GETMESSAGES_POP3IDS, new ArrayList<String>() {{
                add("000042c759a67238");
                add("0000429059a67238");
            }});
            put(MAIL_GETMESSAGES_FLAGS, new ArrayList<Flags.Flag>(){{ add(Flags.Flag.SEEN); }});
        }};

        getPop3Messages = new MailGetSession()
                .andThen(new MailPop3GetMessages())
                .execute(getPop3Messages);

        List<MimeMessage> messages = (List<MimeMessage>)getPop3Messages.get(MAIL_GETMESSAGES);
        if (messages != null) {
            for(Message m : messages) {
                System.out.println(m.getSubject());
            }
        }
    }


}
