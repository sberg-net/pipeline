package net.sberg.openkim.pipeline;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import net.sberg.openkim.pipeline.atomics.mail.MailKeys;
import org.junit.jupiter.api.Test;

public class PlayTest extends MailKeys {

    @Test public void test() throws AddressException {
        String address = "derlinuxer <derlinuxer@example.com>";
        InternetAddress fia = new InternetAddress(address);
        InternetAddress ia = new InternetAddress(fia.getAddress());
        String desc = ia.getPersonal();
        System.out.println(address);
    }
}
