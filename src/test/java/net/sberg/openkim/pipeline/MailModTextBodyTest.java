package net.sberg.openkim.pipeline;

import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.atomics.mail.MailGetMimeMessage;
import net.sberg.openkim.pipeline.atomics.mail.MailKeys;
import net.sberg.openkim.pipeline.atomics.mail.MailModTextBody;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MailModTextBodyTest extends MailKeys {

    @Test
    public void testMailModTextBodyNestedMultipart() throws Exception {
        Map<String, Object> modTextBody = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMailforModText-MultiPartInMultiPart.eml"));
            put(MAIL_MODTEXTBODY_PLAIN, "Appended test!");
            put(MAIL_MODTEXTBODY_HTML, "<p>Appended test!<p>");
            put(MAIL_MODTEXTBODY_TYPE, MailModTextBody.Type.APPEND);
        }};

        modTextBody = new MailGetMimeMessage()
                .andThen(new MailModTextBody())
                .execute(modTextBody);

        MimeMessage message = (MimeMessage) modTextBody.get(MAIL_MIMEMESSAGE);
        Multipart multiPart = (Multipart) message.getContent();
        
        String htmlBodyText;
        if (multiPart.getBodyPart(0).getContent() instanceof Multipart nested) {
             htmlBodyText = (String) nested.getBodyPart(1).getContent();
        } else {
             htmlBodyText = (String) multiPart.getBodyPart(0).getContent();
        }
        assert htmlBodyText.contains("Appended test!");
    }
}
