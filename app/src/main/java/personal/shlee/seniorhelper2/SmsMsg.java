package personal.shlee.seniorhelper2;

import java.util.Date;

public class SmsMsg {
    public String sender;
    public String contents;
    public Date ReceivedDate;

    public SmsMsg(String sender, String contents, Date receivedDate) {
        this.sender = sender;
        this.contents = contents;
        this.ReceivedDate = receivedDate;
    }
}

