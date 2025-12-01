package net.sberg.openkim.pipeline;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.ParseException;
import net.sberg.openkim.pipeline.atomics.mail.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MailTest extends MailKeys {

    private static final Logger log = LoggerFactory.getLogger(MailTest.class);

    @Test void testMail() throws Exception {
        Map<String, Object> m2 = new HashMap<>();
        m2.put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
        new MailGetMimeMessage()
            .andThen(new MailGetSubject())
            .execute(m2);

        System.out.println("Mail From[0]: " + ((MimeMessage) m2.get(MAIL_MIMEMESSAGE)).getFrom()[0].toString());
        System.out.println("Mail Subject: " + m2.get(MAIL_SUBJECT));

    }

    @Test void testMailGetMimeMessage() throws Exception {

        Map<String, Object> mStream = new HashMap<>();

        mStream = new MailGetMimeMessage().execute(mStream);

        // check empty Session & new clean MimeMessage
        assert(mStream.get(MAIL_MIMEMESSAGE) instanceof MimeMessage);
        assert(((MimeMessage)mStream.get(MAIL_MIMEMESSAGE)).getSession().getProperties().isEmpty());
        assert(((MimeMessage)mStream.get(MAIL_MIMEMESSAGE)).getFrom() == null);
        assert(((MimeMessage)mStream.get(MAIL_MIMEMESSAGE)).getAllRecipients() == null);

        mStream.put(MAIL_STREAM, new FileInputStream("src/test/resources/testMail.eml"));

        mStream = new MailGetMimeMessage().execute(mStream);

        assert(mStream.get(MAIL_MIMEMESSAGE) instanceof MimeMessage);
        assert(((MimeMessage)mStream.get(MAIL_MIMEMESSAGE)).getMessageID()).equals("<2159976e-5502-41c3-a078-d96be2add8a6@sberg.net>");

        Map<String, Object> mFile = new HashMap<>();
        mFile.put(MAIL_FILE, new File("src/test/resources/testMail.eml"));

        mFile = new MailGetMimeMessage().execute(mFile);

        assert(mFile.get(MAIL_MIMEMESSAGE) instanceof MimeMessage);
        assert(((MimeMessage)mFile.get(MAIL_MIMEMESSAGE)).getMessageID()).equals("<2159976e-5502-41c3-a078-d96be2add8a6@sberg.net>");

    }

    @Test void testMailGetSubject() throws Exception {

        Map<String, Object> mSub = new HashMap<>();
        mSub.put(MAIL_FILE, new File("src/test/resources/testMail.eml"));

        mSub = new MailGetMimeMessage()
            .andThen(new MailGetSubject())
            .execute(mSub);

        assert(mSub.get(MAIL_SUBJECT) instanceof String);
        assert(mSub.get(MAIL_SUBJECT)).equals("Test");

    }

    @Test void testMailAddHeader() throws Exception {

        Map<String, String> headers = new HashMap<>() {{
            put("sberg", "gmbh");
            put("X-APP", "openKim");
            put("Subject", "openKim");
        }};
        Map<String, Object> mHeader = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
            put(MAIL_HEADER, headers);
        }};

        mHeader = new MailGetMimeMessage()
            .andThen(new MailAddHeader())
            .execute(mHeader);

        MimeMessage message = (MimeMessage) mHeader.get(MAIL_MIMEMESSAGE);
        assert(message.getHeader("sberg")[0].equals("gmbh"));
        assert(message.getHeader("X-APP")[0].equals("openKim"));

    }

    @Test void testMailGetMsgId() throws Exception {

        Map<String, Object> msgID = new HashMap<>();
        msgID.put(MAIL_FILE, new File("src/test/resources/testMail.eml"));

        msgID = new MailGetMimeMessage()
            .andThen(new MailGetMsgID())
            .execute(msgID);

        MimeMessage message = (MimeMessage) msgID.get(MAIL_MIMEMESSAGE);
        assert(message.getMessageID().equals("<2159976e-5502-41c3-a078-d96be2add8a6@sberg.net>"));

        msgID.put(MAIL_FILE, new File("src/test/resources/testMailNoMsgID.eml"));

        msgID = new MailGetMimeMessage()
            .andThen(new MailGetMsgID())
            .execute(msgID);

        message = (MimeMessage) msgID.get(MAIL_MIMEMESSAGE);
        assertNull(message.getMessageID());

        msgID.put(MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS, true);

        msgID = new MailGetMsgID().execute(msgID);

        message = (MimeMessage) msgID.get(MAIL_MIMEMESSAGE);
        assertNotNull(message.getMessageID());

        Map<String, Object> excetionMap = new HashMap<>();
        Throwable exception1 = assertThrows(AtomicInputException.class, ()
                -> new MailGetMsgID().execute(excetionMap));
        assertEquals("MAIL_MIMEMESSAGE not exist or is null!", exception1.getMessage());

        excetionMap.put(MAIL_MIMEMESSAGE, new File("src/test/resources/testMailNoMsgID.eml"));
        Throwable exception2 =  assertThrows(AtomicInputException.class, ()
                -> new MailGetMsgID().execute(excetionMap));
        assertEquals("MAIL_MIMEMESSAGE is not instance of MimeMessage!", exception2.getMessage());
    }

    @Test void testMailGetSender() {


    }

    @Test void testMailGetRecipients() throws Exception {

        Map<String, Object> getRecipients = new HashMap<>();
        getRecipients.put(MAIL_FILE, new File("src/test/resources/testMail.eml"));

        getRecipients = new MailGetMimeMessage()
            .andThen(new MailGetRecipients())
            .execute(getRecipients);

        assert(getRecipients.get(MAIL_RECIPIENTS) instanceof List<?>);
        assert(( (List<?>) getRecipients.get(MAIL_RECIPIENTS))).get(0)
                .equals("Chirurgie_am_Goethepark_Cottbus.800704300@i-motion.kim.telematik");

        getRecipients.put(MAIL_RECIPIENTS_TYPES, new ArrayList<>(){{ add(Message.RecipientType.CC); }});
        getRecipients = new MailGetRecipients().execute(getRecipients);

        assert(( (List<?>) getRecipients.get(MAIL_RECIPIENTS))).isEmpty();
    }

    @Test void testMailSetHeader() throws Exception {

        List<Header> headers = new ArrayList<>() {{
            add(new Header("sberg", "gmbh"));
            add(new Header("X-APP", "openKim"));
            add(new Header("Subject", "openKim"));
            add(new Header("Nix", null));
        }};
        Map<String, Object> mHeader = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
            put(MAIL_HEADER, headers);
        }};

        mHeader = new MailGetMimeMessage()
            .andThen(new MailSetHeader())
            .execute(mHeader);

        MimeMessage message = (MimeMessage) mHeader.get(MAIL_MIMEMESSAGE);
        assert(message.getHeader("sberg")[0].equals("gmbh"));
        assert(message.getHeader("X-APP")[0].equals("openKim"));
        assert(message.getHeader("Subject").length == 1);
        assert(message.getHeader("Subject")[0].equals("openKim"));
        assert(message.getHeader("Nix") == null);
    }

    @Test void testMailSetFrom() throws Exception {

        Map<String, Object> setFrom = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
            put(MAIL_FROM, "blubber@sberg.net");
        }};

        setFrom = new MailGetMimeMessage()
            .andThen(new MailSetFrom())
            .execute(setFrom);

        MimeMessage message = (MimeMessage) setFrom.get(MAIL_MIMEMESSAGE);
        assert(message.getFrom().length == 1);
        assert(message.getFrom()[0].toString().equals("blubber@sberg.net"));

        setFrom.put(MAIL_FROM, "blubbersberg.net");
        Map<String, Object> finalSetFrom = setFrom;

        Throwable exception2 =  assertThrows(MessagingException.class, () ->  new MailSetFrom().execute(finalSetFrom));
        assert(exception2.getMessage().equals("Missing final '@domain'"));
    }

    @SuppressWarnings("unchecked")
    @Test void testMailGetHeader() throws Exception {

        List<String> headers = new ArrayList<>() {{
            add("Not-Exist");
            add("MIME-.*");
            add("X-MULTI-TEST");
            add("^Subje.*");
        }};
        Map<String, Object> getHeader = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
            put(MAIL_HEADER_NAMES, headers);
        }};

        getHeader = new MailGetMimeMessage()
            .andThen(new MailGetHeader())
            .execute(getHeader);

        List<Header> headerList = (List<Header>) getHeader.get(MAIL_HEADER);
        assert(headerList.stream().filter(header -> header.getName().equals("X-MULTI-TEST")).count() == 2);
        assert(headerList.stream().filter(header -> header.getName().equals("MIME-Version")).count() == 1);
        headerList.stream().filter(header -> header.getName().equals("MIME-Version"))
                .findFirst().ifPresent(header -> {
            assert(header.getValue().equals("1.0"));
        });
        assert(headerList.stream().noneMatch(header -> header.getName().equals("Not-Exist")));
        assert(headerList.stream().filter(header -> header.getName().equals("Subject"))
                .anyMatch(header -> header.getValue().equals("Test")));
    }

    @Test void testMailGetSession() throws AtomicInputException, MessagingException {

        String protocol = "imap";

        Map<String,String> sessionProps = new HashMap<>(){{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", "mail.hosting.de");
            put("mail." + protocol + ".port", "143");
            put("mail." + protocol + ".starttls.enable", "true");
        }};
        Map<String, Object> mSession = new HashMap<>(){{
            put(MAIL_SESSION_PROPS, sessionProps);
        }};

        new MailGetSession().execute(mSession);

        Session session = (Session)mSession.get(MAIL_SESSION);
        assert(session != null);
        assert(session.getProperty("mail.store.protocol").equals("imap"));
        assert(session.getProperty("mail.imap.host").equals("mail.hosting.de"));
    }

    @SuppressWarnings("unchecked")
    @Test void testMailPop3GetMessages() throws Exception {

        String protocol = "pop3";

        Map<String, Object> sessionProps = new HashMap<>() {{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", System.getenv("mail_host"));
            put("mail." + protocol + ".port", "110");
            put("mail." + protocol + ".starttls.enable", "true");
        }};
        Map<String, Object> getPop3Messages = new HashMap<>() {{
            put(MAIL_SESSION_PROPS, sessionProps);
            put(MAIL_GETMESSAGES_PASSWORD, System.getenv("mail_pwd"));
            put(MAIL_GETMESSAGES_USER, System.getenv("mail_user"));
            put(MAIL_GETMESSAGES_FOLDER, "inbox");
            put(MAIL_GETMESSAGES_POP3IDS, new ArrayList<String>() {{
                add("0000362c59a67238");
                add("000037b159a67238");
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

    @Test void testMailSendMessage() throws Exception {

        String protocol = "smtp";

        Map<String,Object> sessionProps = new HashMap<>(){{
            put("mail.store.protocol", protocol);
            put("mail." + protocol + ".host", System.getenv("mail_host"));
            put("mail." + protocol + ".port", "25");
            put("mail." + protocol + ".starttls.enable", "true");
        }};
        Map<String, Object> sendMessages = new HashMap<>(){{
            put(MAIL_SESSION_PROPS, sessionProps);
            put(MAIL_SENDMESSAGE_USER, System.getenv("mail_user"));
            put(MAIL_SENDMESSAGE_PASSWORD, System.getenv("mail_pwd"));
            put(MAIL_SENDMESSAGE_ADDRESSES, new Address[]{
                new InternetAddress("derlinuxer@sberg.net")
            });
            put(MAIL_FILE, new File("src/test/resources/testMailforSending.eml"));
        }};

        new MailGetSession().andThen(new MailGetMimeMessage()).andThen(new MailSendMessage()).execute(sendMessages);

        sendMessages.put(MAIL_SENDMESSAGE_PASSWORD, "wrongPWD");

        Throwable exception5 = assertThrows(AuthenticationFailedException.class, () ->
                new MailGetSession().andThen(new MailGetMimeMessage()).andThen(new MailSendMessage()).execute(sendMessages));
        assert(exception5.getMessage().contains("authentication failed"));

        sendMessages.put(MAIL_SENDMESSAGE_USER, "");
        sendMessages.put(MAIL_SENDMESSAGE_PASSWORD, "");

        Throwable exception6 = assertThrows(SendFailedException.class, () ->
                new MailGetSession().andThen(new MailGetMimeMessage()).andThen(new MailSendMessage()).execute(sendMessages));
        assert(exception6.getMessage().contains("Invalid Addresses"));


        sendMessages.put(MAIL_SENDMESSAGE_ADDRESSES, new Address[]{});

        Throwable exception3 = assertThrows(AtomicInputException.class, () ->
                new MailGetSession().andThen(new MailGetMimeMessage()).andThen(new MailSendMessage()).execute(sendMessages));
        assert(exception3.getMessage().equals("MAIL_SENDMESSAGE_ADDRESSES are empty!"));

        sendMessages.put(MAIL_SENDMESSAGE_ADDRESSES, new String[]{});

        Throwable exception4 = assertThrows(AtomicInputException.class, () ->
                new MailGetSession().andThen(new MailGetMimeMessage()).andThen(new MailSendMessage()).execute(sendMessages));
        assert(exception4.getMessage().equals("MAIL_SENDMESSAGE_ADDRESSES is not instance of Address[]!"));

        sendMessages.put(MAIL_SESSION, null);
        sendMessages.put(MAIL_SENDMESSAGE_ADDRESSES, new Address[]{
                new InternetAddress("derlinuxer@sberg.net")
        });


        Throwable exception1 = assertThrows(AtomicInputException.class, () ->
                new MailGetMimeMessage().andThen(new MailSendMessage()).execute(sendMessages));
        assert(exception1.getMessage().equals("Session in MimeMessage is null but is needed in this atomic!"));

        sendMessages.put(MAIL_SESSION, Session.getDefaultInstance(new Properties()));

        Throwable exception2 = assertThrows(AtomicInputException.class, () ->
                new MailGetMimeMessage().andThen(new MailSendMessage()).execute(sendMessages));
        assert(exception2.getMessage().equals("mail.smtp.host not set in MimeMessage Session!"));
    }

    @Test void MailReplaceRecipients() throws Exception {

        Map<String, Object> recipientsReplace = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMailforReplacing.eml"));
            put(MAIL_REPLACERECIPIENTS, new HashMap<String, String >(){{
                put("derlinuxer@sberg.net","replaced@sberg.net");
                put("blubber@sberg.net","info@sberg.net");
            }});
            put(MAIL_RECIPIENTS_TYPES, new ArrayList<Message.RecipientType>(){{
                add(Message.RecipientType.CC);
                add(Message.RecipientType.BCC);
                add(Message.RecipientType.TO);
            }});
        }};

        new MailGetMimeMessage().andThen(new MailReplaceRecipients()).execute(recipientsReplace);

        MimeMessage message = (MimeMessage) recipientsReplace.get(MAIL_MIMEMESSAGE);
        assert (Integer) recipientsReplace.get(MAIL_REPLACERECIPIENTS_COUNT) == 4;
        assert (Arrays.stream(message.getRecipients(Message.RecipientType.CC)).anyMatch(recipient ->
                recipient.toString().equals("replaced@sberg.net")));
        assert (Arrays.stream(message.getRecipients(Message.RecipientType.CC)).noneMatch(recipient ->
                recipient.toString().equals("derlinuxer@sberg.net")));

    }

    @Test void MailSetAddRecipients() throws Exception {
        Map<String, Object> setAddRecipients = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
            put(MAIL_SETADDRECIPIENTS, new HashMap<Message.RecipientType, String >(){{
                put(MimeMessage.RecipientType.TO, "NewAddress@test.de");
            }});
            put(MAIL_SETADDRECIPIENTS_ADD, true);
        }};

        setAddRecipients = new MailGetMimeMessage()
                .andThen(new MailSetAddRecipients())
                .execute(setAddRecipients);

        MimeMessage message = (MimeMessage) setAddRecipients.get(MAIL_MIMEMESSAGE);
        assert(message.getRecipients(Message.RecipientType.TO).length == 2);
        assert(message.getRecipients(Message.RecipientType.TO)[1].toString().equals("NewAddress@test.de"));
        assert(message.getRecipients(Message.RecipientType.TO)[0].toString().equals("Chirurgie_am_Goethepark_Cottbus.800704300@i-motion.kim.telematik"));

        setAddRecipients.put(MAIL_SETADDRECIPIENTS_ADD, false);

        new MailSetAddRecipients().execute(setAddRecipients);

        assert(message.getRecipients(Message.RecipientType.TO).length == 1);
        assert(message.getRecipients(Message.RecipientType.TO)[0].toString().equals("NewAddress@test.de"));

        setAddRecipients.put(MAIL_SETADDRECIPIENTS, new HashMap<String, String >(){{
                put("Fehler", "NewAddress@test.de");
            }});

        Map<String, Object> finalSetAddRecipients = setAddRecipients;
        Throwable exception1 =  assertThrows(AtomicInputException.class, () ->  new MailSetAddRecipients().execute(finalSetAddRecipients));
        assert exception1.getMessage().equals("Map is not of type Map<Message.RecipientType, String>!");
    }

    @Test void MailSetSubject() throws Exception {
        Map<String, Object> setSubject = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
            put(MAIL_SUBJECT, "The new Subject");
        }};

        setSubject = new MailGetMimeMessage()
                .andThen(new MailSetSubject())
                .execute(setSubject);

        MimeMessage message = (MimeMessage) setSubject.get(MAIL_MIMEMESSAGE);
        assert(message.getSubject().equals("The new Subject"));
    }

    @SuppressWarnings("unchecked")
    @Test void MailGetMimeBodyParts() throws Exception {
        Map<String, Object> getMimeBodyParts = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMailWithAttachments.eml"));
            put(MAIL_GETMIMEBODYPARTS_FILTER_DISPO, MimeBodyPart.ATTACHMENT);
            put(MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN, 20);
        }};

        getMimeBodyParts = new MailGetMimeMessage()
                .andThen(new MailGetMimeBodyParts())
                .execute(getMimeBodyParts);

        List<MimeBodyPart> parts = (List<MimeBodyPart>) getMimeBodyParts.get(MAIL_MIMEBODYPARTS);
        assert (parts != null);
    }

    @Test void MailRemoveMimeBodyParts() throws Exception {
        Map<String, Object> removeMimeBodyParts = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMailWithAttachments.eml"));
            put(MAIL_GETMIMEBODYPARTS_FILTER_DISPO, MimeBodyPart.ATTACHMENT);
        }};

        removeMimeBodyParts = new MailGetMimeMessage()
                .andThen(new MailGetMimeBodyParts())
                .andThen(new MailRemoveMimeBodyParts())
                .execute(removeMimeBodyParts);

        MimeMessage message = (MimeMessage) removeMimeBodyParts.get(MAIL_MIMEMESSAGE);
        Multipart multipart = (Multipart) message.getContent();
        assert (multipart.getCount() == 1);

        removeMimeBodyParts.put(MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN, 20);

        removeMimeBodyParts = new MailGetMimeMessage()
                .andThen(new MailGetMimeBodyParts())
                .andThen(new MailRemoveMimeBodyParts())
                .execute(removeMimeBodyParts);

        message = (MimeMessage) removeMimeBodyParts.get(MAIL_MIMEMESSAGE);
        multipart = (Multipart) message.getContent();
        assert multipart.getCount() == 2;

        removeMimeBodyParts.remove(MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN);
        removeMimeBodyParts.put(MAIL_GETMIMEBODYPARTS_FILTER_DISPO, MimeBodyPart.INLINE);

        removeMimeBodyParts = new MailGetMimeMessage()
                .andThen(new MailGetMimeBodyParts())
                .andThen(new MailRemoveMimeBodyParts())
                .execute(removeMimeBodyParts);

        message = (MimeMessage) removeMimeBodyParts.get(MAIL_MIMEMESSAGE);
        multipart = (Multipart) message.getContent();
        assert multipart.getCount() == 4;

        removeMimeBodyParts.put(MAIL_FILE, new File("src/test/resources/testMail.eml"));
        removeMimeBodyParts.put(MAIL_MIMEBODYPARTS, new ArrayList<MimeBodyPart>());
        removeMimeBodyParts = new MailGetMimeMessage().andThen(new MailRemoveMimeBodyParts()).execute(removeMimeBodyParts);

        message = (MimeMessage) removeMimeBodyParts.get(MAIL_MIMEMESSAGE);
        assertFalse((message.getContentType().contains("multipart")));
    }

    @SuppressWarnings("unchecked")
    @Test void MailSaveAttachmentFile() throws Exception {
        Map<String, Object> saveAttachment = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMailWithAttachments.eml"));
            put(MAIL_GETMIMEBODYPARTS_FILTER_DISPO, MimeBodyPart.ATTACHMENT);
            put(MAIL_SAVEATTACHMENTFILE_BASEDIR, "/tmp");
        }};

        saveAttachment = new MailGetMimeMessage()
                .andThen(new MailGetMimeBodyParts())
                .andThen(new MailSaveAttachmentFile())
                .execute(saveAttachment);

        List<String> savedFiles = (List<String>) saveAttachment.get(MAIL_SAVEATTACHMENTFILE_SAVEDFILES);
        assert savedFiles.size() == 3;
        assert new File("/tmp/helm-chart-0.37.0.zip").exists();
    }

    @Test void MailModTextBody() throws Exception {
        Map<String, Object> modTextBody = new HashMap<>(){{
            put(MAIL_FILE, new File("src/test/resources/testMailWithAttachments.eml"));
            put(MAIL_MODTEXTBODY_PLAIN, "Appended test!");
            put(MAIL_MODTEXTBODY_HTML, "<p>Appended test!<p>");
            put(MAIL_MODTEXTBODY_TYPE, MailModTextBody.Type.APPEND);
        }};

        modTextBody = new MailGetMimeMessage()
                .andThen(new MailModTextBody())
                .execute(modTextBody);

        MimeMessage message = (MimeMessage) modTextBody.get(MAIL_MIMEMESSAGE);
        Multipart multiPart = (Multipart) message.getContent();
        String htmlBodyText = (String)multiPart.getBodyPart(0).getContent();
        assert htmlBodyText.contains("Appended test!");
    }

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
        assert !msgInfos.get(0).getMsgId().isBlank();
        //noinspection StatementWithEmptyBody
        for (MessageHeadInfo info : msgInfos) {
             log.info("\nSubject: {}\nPop3UID: {}\nTo: {}\nCc: {}\nFrom: {}\nDate: {}\nSize: {}",info.getSubject(),
                    info.getUid(), info.getTo(), info.getCc(), info.getFrom(), info.getSendDate(), info.getSize());
        }
    }

    @Test
    void testContentTypeCleaner() throws Exception {

        log.info("Classname of GenericContentTypeCleaner: {}", GenericContentTypeCleaner.class.getName());

        Map<String, Object> contentTypeCleanerOff = new HashMap<>() {{
            put(MAIL_FILE, new File("src/test/resources/ContentTypeNull_Problem.eml"));
        }};

        contentTypeCleanerOff = new MailGetMimeMessage().execute(contentTypeCleanerOff);
        MimeMessage message = (MimeMessage) contentTypeCleanerOff.get(MAIL_MIMEMESSAGE);
        Throwable exception = assertThrows(ParseException.class, message::saveChanges);
        assert exception.getMessage().equals("In Content-Type string <null>, expected '/', got null");

        System.setProperty("mail.mime.contenttypehandler", System.getenv("mail_mime_contenttypehandler"));
        contentTypeCleanerOff = new MailGetMimeMessage().execute(contentTypeCleanerOff);
        MimeMessage message1 = (MimeMessage) contentTypeCleanerOff.get(MAIL_MIMEMESSAGE);
        message1.saveChanges();
    }
}
