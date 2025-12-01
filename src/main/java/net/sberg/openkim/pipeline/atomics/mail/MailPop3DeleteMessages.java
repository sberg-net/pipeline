package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.*;
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
 * Atomic MailPop3DeleteMessage by pop3 UIDs.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_SESSION}<br>
 *          value: MailSession [{@code jakarta.mail.Session}] <br>
 *          key: {@code MAIL_GETMESSAGES_USER}<br>
 *          value: Username [{@code String}] <br>
 *          key: {@code MAIL_GETMESSAGES_PASSWORD}<br>
 *          value: Password [{@code String}] <br>
 *          key: {@code MAIL_POP3DELETEMESSAGES}<br>
 *          value: Pop3UidList [{@code List<String>}] <br>
 * @Output  key: {@code MAIL_POP3FETCHMSGINFO}<br>
 *          value: MessageHeadInfoList [{@code List<MessageHeadInfo>}]
 */

public class MailPop3DeleteMessages extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailPop3DeleteMessages.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
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
        if (input.get(MAIL_POP3DELETEMESSAGES) == null)
            throw new AtomicInputException("MAIL_POP3DELETEMESSAGES not exist or is null!");
        if (!(input.get(MAIL_POP3DELETEMESSAGES) instanceof List<?>))
            throw new AtomicInputException("MAIL_POP3DELETEMESSAGES is not instance of List!");

        POP3Store store = null;
        POP3Folder folder = null;
        List<String> delPop3Uids = new ArrayList<>();
        int msgDeleteCount = 0;

        ((List<?>) input.get(MAIL_POP3DELETEMESSAGES)).forEach(object -> {
            if (! (object instanceof String) ) {
                throw new AtomicInputException("MAIL_POP3DELETEMESSAGES elements not instance of String!");
            }
            else
                delPop3Uids.add((String) object);
        });

        logger.debug("Got {} pop3 uids to delete!", delPop3Uids.size());

        try {
            store = (POP3Store) session.getStore("pop3");
            store.connect(user,password);

            folder = (POP3Folder) store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message[] msgs = folder.getMessages();
            FetchProfile profile = new FetchProfile();
            profile.add(UIDFolder.FetchProfileItem.UID);

            folder.fetch(msgs,profile);

            for (Message msg : msgs) {
                if (delPop3Uids.contains(folder.getUID(msg))) {
                    msg.setFlag(Flags.Flag.DELETED, true);
                    msgDeleteCount++;
                    logger.debug("Set deleted flag an pop3 message: {} account: {}", folder.getUID(msg), user);
                }
            }
        } finally {
            try {
                if (folder != null && folder.isOpen())
                    folder.close(true);
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                logger.error("Error closing folder or store", e);
            }
        }
        input.put(MAIL_POP3DELETEMESSAGES_DELCOUNT, msgDeleteCount);
        return input;
    }
}
