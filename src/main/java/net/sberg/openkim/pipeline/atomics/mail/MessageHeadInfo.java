package net.sberg.openkim.pipeline.atomics.mail;

import jakarta.mail.Address;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MessageHeadInfo {
    private List<Address> from;
    private List<Address> to;
    private List<Address> cc;
    private String subject;
    private double size;
    private String msgId;
    private String uid;
    private String contentTypes;
    private Date sendDate;
}
