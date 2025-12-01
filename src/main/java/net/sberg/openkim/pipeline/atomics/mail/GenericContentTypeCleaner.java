package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MimePart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericContentTypeCleaner {

    static Logger logger = LoggerFactory.getLogger(GenericContentTypeCleaner.class);

    public static final String cleanContentType(MimePart mp, String contentType) {
        try {
            new ContentType(contentType);
            return contentType;
        }
        catch (Exception e) {
            logger.warn("Found wrong and problematic contentType in MimeMessage/MimePart, will be set to default type: application/octet-stream", e);
            return "application/octet-stream";
        }
    }
}
