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
 * Atomic MailPop3FetchMessageHeader from Session.
 * A {@code Map<String,Object>} will be used for input / output information transport.
 * @Input   key: {@code MAIL_SESSION}<br>
 *          value: MailSession [{@code jakarta.mail.Session}] <br>
 *          key: {@code MAIL_GETMESSAGES_USER}<br>
 *          value: Username [{@code String}] <br>
 *          key: {@code MAIL_GETMESSAGES_PASSWORD}<br>
 *          value: Password [{@code String}] <br>
 * @Output  key: {@code MAIL_POP3FETCHMSGINFO}<br>
 *          value: MessageHeadInfoList [{@code List<MessageHeadInfo>}]
 */

public class MailPop3FetchMessageInfo extends MailKeys implements PipelineOp {

    Logger logger = LoggerFactory.getLogger(MailPop3FetchMessageInfo.class);

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

        POP3Store store = null;
        POP3Folder folder = null;

        List<MessageHeadInfo> msgInfoList = new ArrayList<>();

        try {
            store = (POP3Store) session.getStore("pop3");
            store.connect(user,password);

            folder = (POP3Folder) store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] msgs = folder.getMessages();
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            profile.add(UIDFolder.FetchProfileItem.UID);
            profile.add(FetchProfile.Item.CONTENT_INFO);
            profile.add(FetchProfile.Item.SIZE);

            folder.fetch(msgs,profile);

            logger.debug("Fetched {} pop3 messages", msgs.length);

            for (Message msg : msgs) {
                MessageHeadInfo headInfo = getMessageHeadInfo(msg, folder);
                msgInfoList.add(headInfo);
            }
        } finally {
            try {
                if (folder != null && folder.isOpen())
                    folder.close();
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                logger.error("Error closing folder or store", e);
            }
        }

        input.put(MAIL_POP3FETCHMSGINFO, msgInfoList);
        return input;
    }

    private static MessageHeadInfo getMessageHeadInfo(Message msg, POP3Folder folder) throws MessagingException {

        return new MessageHeadInfo() {{
            setSubject(msg.getSubject());
            setSize(msg.getSize());
            setSendDate(msg.getSentDate());
            setFrom(List.of(msg.getFrom()));
            setTo(msg.getRecipients(Message.RecipientType.TO) != null
                    ? List.of(msg.getRecipients(Message.RecipientType.TO))
                    : new ArrayList<>());
            setCc(msg.getRecipients(Message.RecipientType.CC) != null
                    ? List.of(msg.getRecipients(Message.RecipientType.CC))
                    : new ArrayList<>());
            setMsgId(msg.getHeader("Message-ID")[0]);
            setUid(folder.getUID(msg));
        }};
    }
}
