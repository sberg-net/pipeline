package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Atomic MailReplaceRecipients replace recipients. Will replace all addresses of given MailRecipientList (key -> find, value -> replace).
 * Recipient types to be searched can be set via MAIL_RECIPIENTS_TYPES. Count of all replacements will be fined in MAIL_REPLACERECIPIENTS_COUNT.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input  key: {@code MAIL_MIMEMESSAGE} <br>
 *         value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *         key: {@code MAIL_RECIPIENTS_TYPES}<br>
 *         value: MailRecipientList [{@code List<Message.RecipientType>}] <br>
 *         key: {@code MAIL_REPLACERECIPIENTS}<br>
 *         value: MailReplacements [{@code Map<String,String>}] <br>
 * @Output all Inputs and<br>
 *          key: {@code MAIL_REPLACERECIPIENTS_COUNT}<br>
 *          value: replacementCount [{@code Integer}]
 */

public class MailReplaceRecipients extends MailKeys implements PipelineOp {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");
        if (input.get(MAIL_RECIPIENTS_TYPES) == null)
            throw new AtomicInputException("MAIL_RECIPIENTS_TYPES not exist or is null!");
        if (! (input.get(MAIL_RECIPIENTS_TYPES) instanceof List<?>))
            throw new AtomicInputException("MAIL_RECIPIENTS_TYPES is not instance of List!");
        if (input.get(MAIL_REPLACERECIPIENTS) == null)
            throw new AtomicInputException("MAIL_REPLACERECIPIENTS not exist or is null!");
        if (! (input.get(MAIL_REPLACERECIPIENTS) instanceof Map<?,?>))
            throw new AtomicInputException("MAIL_RECIPIENTS_TYPES is not instance of Map!");

        List<Message.RecipientType> types = (List<Message.RecipientType>) input.get(MAIL_RECIPIENTS_TYPES);
        Map<String, String> replacements = (Map<String, String>) input.get(MAIL_REPLACERECIPIENTS);
        AtomicReference<Integer> replaceCount = new AtomicReference<>(0);

        for (Message.RecipientType type : types) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                String find = entry.getKey();
                String replace = entry.getValue();
                if (message.getRecipients(type) != null) {
                    List<Address> addresses = new ArrayList<>(List.of(message.getRecipients(type)));
                    addresses.replaceAll(address -> {
                        if (address.toString().toLowerCase().contains(find.toLowerCase())) {
                            try {
                                address = new InternetAddress(replace);
                                replaceCount.getAndSet(replaceCount.get() + 1);
                            }
                            catch (AddressException e) { throw new RuntimeException(e); }
                        }
                        return address;
                    });
                    message.setRecipients(type, addresses.toArray(new Address[0]));
                }
            }
        }
        input.put(MAIL_REPLACERECIPIENTS_COUNT, replaceCount.get());
        return input;
    }
}
