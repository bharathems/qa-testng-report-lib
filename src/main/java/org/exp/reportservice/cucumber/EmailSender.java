package org.exp.reportservice.cucumber;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.util.Base64;
import java.util.Properties;

public class EmailSender {
    private static final String MAIL_HOST_NAME = "mckesent.mck.experian.com";
    private static final String MAIL_FROM = "AutoTest@experian.com";
    private static final String password = new String(Base64.getDecoder().decode("U2FwaWVudDk5MDgxOTUyMzg="));
    public static void send(String mailTo, String mailSubject, StringBuilder results, File pie, File bar) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host", MAIL_HOST_NAME);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_FROM, password);
            }
        });
        session.setDebug(false);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MAIL_FROM));

        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));

        message.setSubject(mailSubject);

        MimeMultipart multipart = new MimeMultipart("related");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(results.toString(), "text/html");
        multipart.addBodyPart(htmlPart);

        // Pie image
        MimeBodyPart piePart = new MimeBodyPart();
        piePart.attachFile(pie);
//        piePart.setContentID("<cid:bar_summary>");
        piePart.setContentID("<pieChart>");
        piePart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(piePart);

        // Bar image
        MimeBodyPart barPart = new MimeBodyPart();
        barPart.attachFile(bar);
        barPart.setContentID("<barChart>");
        barPart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(barPart);

        message.setContent(multipart);
        Transport.send(message);
    }
}
