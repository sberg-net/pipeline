package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import net.sberg.openkim.pipeline.AtomicInputException;
import net.sberg.openkim.pipeline.PipelineOp;
import org.eclipse.angus.mail.pop3.POP3Folder;
import org.eclipse.angus.mail.pop3.POP3Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Atomic MailGetMessages from Session.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_SESSION}<br>
 *          value: MailSession [{@code jakarta.mail.Session}] <br>
 *          key: {@code MAIL_GETMESSAGES_USER}<br>
 *          value: Username [{@code String}] <br>
 *          key: {@code MAIL_GETMESSAGES_PASSWORD}<br>
 *          value: Password [{@code String}] <br>
 *          key: {@code MAIL_GETMESSAGES_FOLDER}<br>
 *          value: MailFolder [{@code String}]<br>
 *          optional: default -> INBOX<br>
 *          key: {@code MAIL_GETMESSAGES_FOLDERMODE}<br>
 *          value: ModeToOpenMailFolder [{@code jakarta.mail.Folder.Mode}]<br>
 *          optional: default -> Folder.READ_WRITE<br>
 *          key: {@code MAIL_GETMESSAGES_EXPUNGE}<br>
 *          value: expungeDeletedMessagesOnClose [{@code boolean}]<br>
 *          optional: default -> true<br>
 *          key: {@code MAIL_GETMESSAGES_FLAGS}<br>
 *          value: MessageFlagsToBeSet [{@code List<Flags.Flag>}]<br>
 *          optional: default -> emptyList<br>
 *          value: GetMessagesByPop3UIDList [{@code List<String>}]<br>
 *          optional: default -> emptyList (means get all messages)
 * @Output  key: {@code MAIL_GETMESSAGES}<br>
 *          value: MimeMessages [{@code List<jakarta.mail.internet.MimeMessage>}]
 */

public class MailPop3GetMessages extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailPop3GetMessages.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String,Object> execute(Map input) throws MessagingException, AtomicInputException {

        if (input.get(MAIL_SESSION) == null)
            throw new AtomicInputException("MAIL_SESSION not exist or is null!");
        if (!(input.get(MAIL_SESSION) instanceof Session session))
            throw new AtomicInputException("MAIL_SESSION is not instance of Session!");
        if (input.get(MAIL_GETMESSAGES_USER) == null)
            throw new AtomicInputException("MAIL_GETMESSAGES_USER not exist or is null!");
        if (!(input.get(MAIL_GETMESSAGES_USER) instanceof String user))
            throw new AtomicInputException("MAIL_GETMESSAGES_USER is not instance of Session!");
        if (input.get(MAIL_GETMESSAGES_PASSWORD) == null)
            throw new AtomicInputException("MAIL_GETMESSAGES_PASSWORD not exist or is null!");
        if (!(input.get(MAIL_GETMESSAGES_PASSWORD) instanceof String password))
            throw new AtomicInputException("MAIL_GETMESSAGES_PASSWORD is not instance of Session!");

        if (! input.containsValue(MAIL_GETMESSAGES_FOLDERMODE))
            input.put(MAIL_GETMESSAGES_FOLDERMODE, POP3Folder.READ_WRITE);

        if (! input.containsKey(MAIL_GETMESSAGES_FOLDER))
            input.put(MAIL_GETMESSAGES_FOLDER, "INBOX");

        if (! input.containsKey(MAIL_GETMESSAGES_EXPUNGE))
            input.put(MAIL_GETMESSAGES_EXPUNGE, true);

        if (! input.containsKey(MAIL_GETMESSAGES_FLAGS))
            input.put(MAIL_GETMESSAGES_FLAGS, new ArrayList<Flags.Flag>());

        if (! input.containsKey(MAIL_GETMESSAGES_POP3IDS))
            input.put(MAIL_GETMESSAGES_POP3IDS, new ArrayList<String>());


        String protocol = session.getProperty("mail.store.protocol");
        List<Flags.Flag> flags = (List<Flags.Flag>)input.get(MAIL_GETMESSAGES_FLAGS);
        List<String> pop3Uids = (List<String>)input.get(MAIL_GETMESSAGES_POP3IDS);

        POP3Store store = null;
        POP3Folder folder = null;

        List<MimeMessage> mimeMessages = new ArrayList<>();
        try {
            store = (POP3Store)session.getStore(protocol);
            store.connect(user,password);

            folder = (POP3Folder)store.getFolder((String)input.get(MAIL_GETMESSAGES_FOLDER));
            folder.open((int)input.get(MAIL_GETMESSAGES_FOLDERMODE));

            Message[] messages = folder.getMessages();

            logger.info("Mail {} read folder: {} by {} - {} messages found",protocol,
                    input.get(MAIL_GETMESSAGES_FOLDER), user, messages.length);
            if (!pop3Uids.isEmpty()){
                logger.debug("Messages getting by Pop3UID filter: {}", pop3Uids);
            }

            for(Message message : messages) {
                if (pop3Uids.isEmpty() || pop3Uids.contains(folder.getUID(message))){
                    if (!flags.isEmpty()){
                        for(Flags.Flag flag : flags) {
                            message.setFlag(flag, true);
                        }
                    }
                    mimeMessages.add(new MimeMessage((MimeMessage)message));
                }
            }
        } finally {
            try {
                if (folder != null && folder.isOpen())
                    folder.close((boolean)input.get(MAIL_GETMESSAGES_EXPUNGE));
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                logger.error("Error closing folder or store", e);
            }
        }

        input.put(MAIL_GETMESSAGES, mimeMessages);

        return input;
    }
}
