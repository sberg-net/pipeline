package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.*;

/**
 * Atomic MailAddHeader get Header from MimeMessage. Input is a List of Strings that will be checked against MimeMessage.
 * The Strings of header name will be used as regex to find a header.
 * Output will have the value if the header exist in MimeMessage.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_HEADER_NAMES}
 *          value: MailHeaderNames [{@code List<String>}] and
 *          key: {@code MAIL_MIMEMESSAGE}
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]
 * @Output  key: {@code MAIL_HEADER}<br>
 *          value: MailHeader [{@code List<jakarta.mail.Header>}]
 */

public class MailGetHeader extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (input.get(MAIL_HEADER_NAMES) == null)
            throw new AtomicInputException("MAIL_SUBJECT not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (!(input.get(MAIL_HEADER_NAMES) instanceof List<?>))
            throw new AtomicInputException("MAIL_HEADER_NAMES is not instance of List!");

        List<String> headerNames = (List<String>) input.get(MAIL_HEADER_NAMES);
        List<Header> resultHeader = new ArrayList<>();
        Enumeration<Header> allHeader = message.getAllHeaders();
        while (allHeader.hasMoreElements()) {
            Header header = allHeader.nextElement();
            headerNames.forEach(headerName -> {
                if (header.getName().toLowerCase().matches(headerName.toLowerCase())){
                    if (header.getValue() != null) {
                        resultHeader.add(header);
                    }
                }
            });
        }
        input.put(MAIL_HEADER, resultHeader);
        return input;
    }
}
