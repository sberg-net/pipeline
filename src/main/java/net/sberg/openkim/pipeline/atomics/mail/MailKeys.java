package net.sberg.openkim.pipeline.atomics.mail;

public class MailKeys {
    public static final String MAIL_MIMEMESSAGE = "mail.mimemessage";
    public static final String MAIL_MESSAGEID = "mail.messageid";
    public static final String MAIL_MESSAGEID_CREATE_IF_NOT_EXISTS = "mail.messageid.createifnotexists";
    public static final String MAIL_SUBJECT = "mail.subject";
    public static final String MAIL_FILE = "mail.file";
    public static final String MAIL_STREAM = "mail.stream";
    public static final String MAIL_HEADER = "mail.header";
    public static final String MAIL_HEADER_NAMES = "mail.header.names";
    public static final String MAIL_FROM = "mail.from";
    public static final String MAIL_RECIPIENTS = "mail.recipients";
    public static final String MAIL_RECIPIENTS_TYPES = "mail.recipients.types";
    public static final String MAIL_GETMESSAGES = "mail.getmessages";
    public static final String MAIL_GETMESSAGES_USER = "mail.getmessages.user";
    public static final String MAIL_GETMESSAGES_PASSWORD = "mail.getmessages.password";
    public static final String MAIL_GETMESSAGES_FOLDER = "mail.getmessages.folder";
    public static final String MAIL_GETMESSAGES_FOLDERMODE = "mail.getmessages.foldermode";
    public static final String MAIL_GETMESSAGES_EXPUNGE = "mail.getmessages.expunge";
    public static final String MAIL_GETMESSAGES_FLAGS = "mail.getmessages.flags";
    public static final String MAIL_GETMESSAGES_POP3IDS = "mail.getmessages.pop3ids";
    public static final String MAIL_SESSION = "mail.session";
    public static final String MAIL_SESSION_PROPS = "mail.session.props";

    // MailSendMessage parameter
    public static final String MAIL_SENDMESSAGE_USER = "mail.sendmessage.user";
    public static final String MAIL_SENDMESSAGE_PASSWORD = "mail.sendmessage.password";
    public static final String MAIL_SENDMESSAGE_ADDRESSES = "mail.sendmessage.addresses";

    // ReplaceRecipients parameter
    public static final String MAIL_REPLACERECIPIENTS = "mail.replacerecipients";
    public static final String MAIL_REPLACERECIPIENTS_COUNT = "mail.replacerecipients.count";

    // MimeBodyParts
    public static final String MAIL_MIMEBODYPARTS = "mail.mimebodyparts";

    // AddMimeBodyParts
    public static final String MAIL_ADDMIMEBODYPARTS = "mail.addmimebodyparts";

    // GetMimeBodyParts
    public static final String MAIL_GETMIMEBODYPARTS_FILTER_SIZEGREATERTHEN = "mail.getmimebodyparts.filter.sizegreaterthen";
    public static final String MAIL_GETMIMEBODYPARTS_FILTER_DISPO = "mail.getmimebodyparts.filter.dispo";

    // SetAddRecipients
    public static final String MAIL_SETADDRECIPIENTS = "mail.setaddrecipients";
    public static final String MAIL_SETADDRECIPIENTS_ADD = "mail.setaddrecipients.add";

    // SaveAttachmentFile
    public static final String MAIL_SAVEATTACHMENTFILE_BASEDIR = "mail.saveattachmentfile.basedir";
    public static final String MAIL_SAVEATTACHMENTFILE_SAVEDFILES = "mail.saveattachmentfile.savedfiles";

    // ModTextBody
    public static final String MAIL_MODTEXTBODY_PLAIN = "mail.modtextbody.plain";
    public static final String MAIL_MODTEXTBODY_HTML = "mail.modtextbody.html";
    public static final String MAIL_MODTEXTBODY_TYPE = "mail.modtextbody.type";

    // Fetch message header from POP3 folder
    public static final String MAIL_POP3FETCHMSGINFO = "mail.pop3fetchmsginfo";

    // Delete pop3 message by pop3 uid
    public static final String MAIL_POP3DELETEMESSAGES = "mail.pop3deletemessages";
    public static final String MAIL_POP3DELETEMESSAGES_DELCOUNT = "mail.pop3deletemessages.delcount";
}
