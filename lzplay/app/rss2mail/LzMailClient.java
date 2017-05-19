package rss2mail;

import models.RssConfigModel;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Logger;

import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

/**
 * Created by cussyou on 20170105.
 */
public class LzMailClient {
    private HtmlEmail email = null;

    public boolean sendMail(String title, String content, RssConfigModel config) {
        try {
            if(email == null) {
                email = new HtmlEmail();
                email.setHostName(config.src_smtp);
                email.setAuthentication(config.src_mail, Rss2Mail.decodeFromB64(config.src_pass) );
                if(config.ssl == 1) {
                    Logger.info("use ssl");
                    email.setSSLOnConnect(true);
                }
            } else {
                HtmlEmail oldMail = email;
                Transport transport = oldMail.getMailSession().getTransport();
                email = new HtmlEmail();
                email.setHostName(config.src_smtp);
                email.setAuthentication(config.src_mail, Rss2Mail.decodeFromB64(config.src_pass) );
                if(config.ssl == 1) {
                    Logger.info("use ssl");
                    email.setSSLOnConnect(true);
                }
                email.getMailSession().getProperties().put("mail.transport.protocol", transport);
            }

            try {
                email.addTo(config.to_mail);
                email.setFrom(config.src_mail, "Rss2Mail");
            } catch (EmailException e) {
                e.printStackTrace();
            }

            // how I wrote in Ruby
            // title = '=?UTF-8?B?' + Base64.strict_encode64(title) + '?='
            title = "=?UTF-8?B?" + Rss2Mail.encodeToB64(title) + "?=";
            email.setSubject(title);

            // set the html message
            email.setCharset("UTF-8");
            email.setHtmlMsg("<html><head><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\"></head>"+content+"</html>");

            // send the email
            email.send();
            Logger.info("mail sent");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
            // next use a new client
            email = null;
            return false;
        }
    }

}
