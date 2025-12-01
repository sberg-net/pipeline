package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailSaveAttachmentFile.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_MIMEBODYPARTS}<br>
 *          value: MimeBodyPartList [{@code List<jakarta.mail.internet.MimeBodyPart>}]<br>
 *          key: {@code MAIL_SAVEATTACHMENTFILE_BASEDIR}<br>
 *          value: SaveBaseDir [{@code String}]
 * @Output  all input values and <br>
 *          key: {@code MAIL_SAVEATTACHMENTFILE_SAVEDFILES}<br>
 *          value: SavedFileList [{@code List<String>}]
 */

public class MailSaveAttachmentFile extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailSaveAttachmentFile.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input)
            throws IOException, MessagingException, AtomicInputException {

        if (input.get(MAIL_MIMEBODYPARTS) == null)
            throw new AtomicInputException("MAIL_MIMEBODYPARTS not exist or is null!");
        if (!(input.get(MAIL_MIMEBODYPARTS) instanceof List<?>))
            throw new AtomicInputException("MAIL_MIMEBODYPARTS is not instance of List!");
        if (input.get(MAIL_SAVEATTACHMENTFILE_BASEDIR) == null)
            throw new AtomicInputException("MAIL_SAVEATTACHMENTFILE_BASEDIR not exist or is null!");
        if (!(input.get(MAIL_SAVEATTACHMENTFILE_BASEDIR) instanceof String baseDir))
            throw new AtomicInputException("MAIL_SAVEATTACHMENTFILE_BASEDIR is not instance of String!");

        ((List<?>) input.get(MAIL_MIMEBODYPARTS)).forEach(object -> {
            if (! (object instanceof MimeBodyPart) ) {
                throw new AtomicInputException("MAIL_MIMEBODYPARTS elements not instance of MimeBodyPart!");
            }
        });

        if (! new File(baseDir).exists())
            throw new AtomicInputException("MAIL_SAVEATTACHMENTFILE_BASEDIR "+baseDir+" not exist!");

        List<MimeBodyPart> parts = (List<MimeBodyPart>) input.get(MAIL_MIMEBODYPARTS);
        List<String> savedFiles = new ArrayList<>();

        for (MimeBodyPart part : parts) {
            if (part.getDisposition() != null
                    && part.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT)
                    && part.getFileName() != null) {
                String fullFile = baseDir + File.separator + part.getFileName();
                logger.debug("Save attachment file: {}", fullFile);
                part.saveFile(fullFile);
                savedFiles.add(fullFile);
            }
        }

        input.put(MAIL_SAVEATTACHMENTFILE_SAVEDFILES, savedFiles);
        return input;
    }
}
