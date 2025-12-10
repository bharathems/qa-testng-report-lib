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
//        final String from = "AutoTest@sephora.com";
//        final String password = new String(Base64.getDecoder().decode("U2FwaWVudDk5MDgxOTUyMzg="));
//        Properties props = new Properties();
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", MAIL_HOST_NAME);
//        props.put("mail.smtp.ssl.trust", "*");
//
//        props.put("mail.smtp.port", "25");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.timeout", "5000");
//        props.put("mail.smtp.connectiontimeout", "5000");
//        props.put("mail.smtp.writetimeout", "5000");
//        props.put("mail.smtp.socketFactory.port", "465");
//        Session session = Session.getInstance(props,
//                new Authenticator() {
//                    @Override
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(from, password);
//                    }
//                });
//
//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(from));
//
//        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
//        message.setSubject(subject);
//
//        MimeMultipart multipart = new MimeMultipart("related");
//
//        // HTML Part
//        MimeBodyPart htmlPart = new MimeBodyPart();
//        htmlPart.setContent(results.toString(), "text/html");
//        multipart.addBodyPart(htmlPart);
//
//        // Pie image
//        MimeBodyPart piePart = new MimeBodyPart();
//        piePart.attachFile(barChartSummary);
//        piePart.setContentID("<bar_summary>");
//        piePart.setDisposition(MimeBodyPart.INLINE);
//        multipart.addBodyPart(piePart);
//
//        // Bar image
//        MimeBodyPart barPart = new MimeBodyPart();
//        barPart.attachFile(bar);
//        barPart.setContentID("<bar>");
//        barPart.setDisposition(MimeBodyPart.INLINE);
//        multipart.addBodyPart(barPart);
//
//        message.setContent(multipart);
//        Transport.send(message);

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
        // HTML Part
//        MimeBodyPart htmlPart = new MimeBodyPart();
//        htmlPart.setContent(results.toString(), "text/html");
//        multipart.addBodyPart(htmlPart);
//
//        // Pie image
//        MimeBodyPart piePart = new MimeBodyPart();
//        piePart.attachFile(pie);
//        piePart.setContentID("<pie>");
//        piePart.setDisposition(MimeBodyPart.INLINE);
//        multipart.addBodyPart(piePart);
//
//        // Bar image
//        MimeBodyPart barPart = new MimeBodyPart();
//        barPart.attachFile(bar);
//        barPart.setContentID("<bar>");
//        barPart.setDisposition(MimeBodyPart.INLINE);
//        multipart.addBodyPart(barPart);



        //
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
