package org.ozsoft.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSenderImpl implements MailSender {
    
    private static final String FROM = "donotreply@ozsoft.net";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = "oscar.stigter@gmail.com";
    private static final String PASSWORD = "secret";
    
    private final Session session;
    
    public MailSenderImpl() {
        Properties props = new Properties();
        // Required for regular SMTP server:
//        props.put("mail.host", HOST);
//        props.put("mail.port", PORT);
        
        // Required for GMail:
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Authenticator auth = new SimpleAuthenticator(USERNAME, PASSWORD);
        
        session = Session.getInstance(props, auth);
    }

    @Override
    public void send(String recipient, String subject, String body) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(FROM));
            message.addRecipient(RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            System.err.println("ERROR: Could not create or send mail: " + e.getMessage());
        }
    }

    static class SimpleAuthenticator extends Authenticator {
        
        private final String username;

        private final String password;
        
        public SimpleAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
        
    }

}
