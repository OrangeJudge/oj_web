package utils;

import models.Mail;
import models.User;
import play.Logger;
import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mailer {
    /**
     * This method will not directly send
     * @param user Which user the email is sent to.
     * @param subject Email's subject.
     * @param content Email's content, encoded in UTF-8 HTML.
     */
    public static void sendMail(User user, String subject, String content) {
        assert user != null && user.name != null && user.email != null;
        assert subject != null && content != null;
        Logger.info("Mailer receives an email with subject " + subject + " to " + user.name);
        Mail mail = new Mail();
        mail.receiver = user.email;
        mail.user = user;
        mail.subject = subject;
        mail.content = content;
        mail.save();
        executor.execute(new SendMail(mail));
    }

    /**
     *
     * @param mail A email object from database.
     */
    private static void sendSingleMail(Mail mail) {
        try {
            Email email = new Email();
            email.setSubject(mail.subject);
            email.setBodyHtml(mail.content);
            email.setFrom("Orange Judge <support@orangejudge.com>");
            email.addTo(mail.user.displayName + " <" + mail.receiver + ">");
            MailerPlugin.send(email);
            mail.status = 1;
            mail.save();
        } catch (Exception ex) {
            Logger.error("Exception in sending email: " + ex.toString());
            ex.printStackTrace();
            mail.status = 11;
            mail.save();
        }
    }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static class SendMail implements Runnable {

        private Mail mail;

        public SendMail(Mail mail) {
            this.mail = mail;
        }

        @Override
        public void run() {
            Logger.info("Sending job...");
            sendSingleMail(mail);
        }
    }
}
