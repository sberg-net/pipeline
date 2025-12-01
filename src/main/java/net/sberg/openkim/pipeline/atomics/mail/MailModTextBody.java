package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Atomic MailModTextBody.
 * If no MimeBodyPart is found, the MAIL_MIMEBODYPARTS will be empty.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 * @Output  key: {@code MAIL_MIMEBODYPARTS}<br>
 *          value: MimeBodyPartList [{@code List<jakarta.mail.internet.MimeBodyPart>}]
 */

public class MailModTextBody extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailModTextBody.class);

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input)
            throws IOException, MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_MODTEXTBODY_HTML) == null && input.get(MAIL_MODTEXTBODY_PLAIN) == null)
            throw new AtomicInputException("MAIL_MODTEXTBODY (plain or html) not exist or is null!");
        if (input.get(MAIL_MODTEXTBODY_TYPE) == null)
            throw new AtomicInputException("MAIL_MODTEXTBODY not exist or is null!");
        if (!(input.get(MAIL_MODTEXTBODY_TYPE) instanceof Type modTextBodytype))
            throw new AtomicInputException("MAIL_MODTEXTBODY_TYPE is not instance of MailModTextBody.Type!");

        String modTextBodyPlain = input.containsKey(MAIL_MODTEXTBODY_PLAIN)
                ? (String) input.get(MAIL_MODTEXTBODY_PLAIN)
                : null;
        String modTextBodyHtml = input.containsKey(MAIL_MODTEXTBODY_HTML)
                ? (String) input.get(MAIL_MODTEXTBODY_HTML)
                : null;

        if (message.getContentType().contains("multipart")){
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++){
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.getContentType().contains("text")){
                    String modifiedBodyText = modifyBodyText(bodyPart.getContent().toString(),
                            bodyPart.getContentType(), modTextBodyPlain, modTextBodyHtml, modTextBodytype);
                    bodyPart.setContent(modifiedBodyText, bodyPart.getContentType());
                }
            }
        }
        else {
            String modifiedBodyText = modifyBodyText(message.getContent().toString(),
                    message.getContentType(), modTextBodyPlain, modTextBodyHtml, modTextBodytype);
            message.setContent(modifiedBodyText, message.getContentType());
        }
        return input;
    }

    public enum Type {
        REPLACE,
        APPEND
    }

    private String modifyBodyText(String bodyText, String contentType, String modTextBodyPlain, String modTextBodyHtml, Type modTextBodytype)
            throws IOException {
        StringBuilder decodedBodyText = new StringBuilder(MimeUtility.decodeText(bodyText));
        if (contentType.contains("text/plain") && modTextBodyPlain != null){
            if (modTextBodytype.equals(Type.APPEND)){
                bodyText = decodedBodyText.append(modTextBodyPlain).toString();
            }
            if (modTextBodytype.equals(Type.REPLACE)){
                bodyText = MimeUtility.decodeText(modTextBodyPlain);
            }
        }
        if (contentType.contains("text/html") && modTextBodyHtml != null){
            if (modTextBodytype.equals(Type.APPEND)){
                Document document = Jsoup.parse(decodedBodyText.toString());
                document.body().append(modTextBodyHtml);
                bodyText = document.html();
            }
            if (modTextBodytype.equals(Type.REPLACE)){
                logger.warn("Not implemented jet");
            }
        }
        return bodyText;
    }
}
