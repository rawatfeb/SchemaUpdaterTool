package com.Utility;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class MailUtility {

	
	
	public static void email(String subject, String message, String[] recipients) throws Exception
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        Session session = Session.getDefaultInstance(props, null);
        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(
                        "TLR.OSSWATBangalore@thomsonreuters.com");
        msg.setFrom(addressFrom);
        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < addressTo.length; i++) {
                addressTo[i] = new InternetAddress(recipients[i]);
        }
        System.out.println("mailing....");
        msg.setRecipients(Message.RecipientType.TO, addressTo);
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }
}
