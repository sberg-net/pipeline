package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailGetMimeBodyParts extracts all mimeBodyParts recursive in a MimeBodyPartList.
 * Filters can be set to extract only specified MimeBodyPart.
 * The Current filters are filterSizeGreaterThen (in kB) and filterDisposition (attached or inline).
 * If no MimeBodyPart is found, the MAIL_MIMEBODYPARTS will be empty.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_MIMEMESSAGE}<br>
 *          value: MimeMessage [{@code jakarta.mail.internet.MimeMessage}]<br>
 *          key: {@code MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN}<br>
 *          value: FilterSizeGreaterThen (in kB) [{@code integer}]<br>
 *          optional: default -> 0 (ignored)
 *          key: {@code MAIL_GETMIMEBODYPARTS_FILTER_DISPO}<br>
 *          value: FilterByDisposition (in kB) [{@code String}]<br>
 *          optional: default -> null (ignored)
 * @Output  key: {@code MAIL_MIMEBODYPARTS}<br>
 *          value: MimeBodyPartList [{@code List<jakarta.mail.internet.MimeBodyPart>}]
 */

public class MailGetMimeBodyParts extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailGetMimeBodyParts.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input)
            throws IOException, MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEMESSAGE) == null)
            throw new AtomicInputException("MAIL_MIMEMESSAGE not exist or is null!");
        if (!(input.get(MAIL_MIMEMESSAGE) instanceof MimeMessage message))
            throw new AtomicInputException("MAIL_MIMEMESSAGE is not instance of MimeMessage!");

        int filterSizeGreaterThen = input.containsKey(MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN)
                ? (int) input.get(MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN)
                : 0;
        String filterDispo = input.containsKey(MAIL_GETMIMEBODYPARTS_FILTER_DISPO)
                ? (String) input.get(MAIL_GETMIMEBODYPARTS_FILTER_DISPO)
                : null;

        List<MimeBodyPart> parts = new ArrayList<>();

        if (message.getContentType().contains("multipart")) {
            Multipart multiPartContent = (Multipart) message.getContent();
            List<MimeBodyPart> singleMimeBodyParts = new ArrayList<>();
            extractAllSingleMimeBodyParts(multiPartContent,singleMimeBodyParts);
            parts.addAll(singleMimeBodyParts);
            if (filterDispo != null) {
                parts.removeIf(part -> {
                    try {
                        String dispo = part.getDisposition();
                        if (dispo == null || ! dispo.equalsIgnoreCase(filterDispo)){
                            logger.debug("Part-{} disposition {} does not match MAIL_GETMIMEBODYPARTS_FILTER_DISPO={} " +
                                            "and will filtered out",parts.indexOf(part), dispo, filterDispo);
                            return true;
                        }
                        else
                            logger.debug("Part-{} disposition {} matches MAIL_GETMIMEBODYPARTS_FILTER_DISPO={} " +
                                    "and will be added",parts.indexOf(part), dispo, filterDispo);
                    } catch (MessagingException e) { throw new RuntimeException(e); }
                    return false;
                });
            }
            if (filterSizeGreaterThen > 0) {
                parts.removeIf(part -> {
                    try {
                        if (part.getSize() <= filterSizeGreaterThen * 1024){
                            logger.debug("Part-{} size {}kB is smaller then MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN=" +
                                    "{}kB and will filtered out",parts.indexOf(part), part.getSize()/1024, filterSizeGreaterThen);
                            return true;
                        }
                        else
                            logger.debug("Part-{} size {}kB is greater then MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN=" +
                                    "{}kB and will be added",parts.indexOf(part), part.getSize()/1024, filterSizeGreaterThen);
                    } catch (MessagingException e) { throw new RuntimeException(e); }
                    return false;
                });
            }
        }
        else
            logger.warn("MimeMessage does not contain multipart!");

        input.put(MAIL_MIMEBODYPARTS, parts);
        return input;
    }

    private void extractAllSingleMimeBodyParts(Multipart multiPartContent, List<MimeBodyPart> singleMimeBodyParts)
            throws MessagingException, IOException {
        for (int i = 0; i < multiPartContent.getCount(); i++) {
            MimeBodyPart part = (MimeBodyPart) multiPartContent.getBodyPart(i);
            if (part.getContentType().contains("multipart")){
                Multipart internalMultiPart = (Multipart) part.getContent();
                this.extractAllSingleMimeBodyParts(internalMultiPart,singleMimeBodyParts);
            }
            singleMimeBodyParts.add(part);
        }
        logger.debug("Found {} single MimeBodyParts in MimeMessage", singleMimeBodyParts.size());
    }

}
