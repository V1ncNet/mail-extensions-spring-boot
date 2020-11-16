package de.vinado.spring.mail.javamail.dkim;

import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended {@link org.springframework.mail.javamail.JavaMailSenderImpl} which signs off {@link MimeMessage MIME
 * messages} before shipment.
 *
 * @author Vincent Nadoll
 */
public class DkimJavaMailSender extends JavaMailSenderImpl {

    private final DkimSigner signer;

    public DkimJavaMailSender(DkimSigner signer) {
        this.signer = signer;
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        List<MimeMessage> mimeMessages = new ArrayList<>(simpleMessages.length);
        for (SimpleMailMessage simpleMessage : simpleMessages) {
            MimeMailMessage message = new MimeMailMessage(createMimeMessage());
            simpleMessage.copyTo(message);
            mimeMessages.add(createSignedMimeMessage(message));
        }

        doSend(mimeMessages.toArray(new MimeMessage[0]), simpleMessages);
    }

    public MimeMessage createSignedMimeMessage(MimeMailMessage message) throws MailException {
        try {
            return new DkimMessage(message.getMimeMessage(), signer);
        } catch (MessagingException e) {
            throw new MailParseException("Could not parse raw MIME content", e);
        }
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        try {
            List<MimeMessage> mimeMessages = new ArrayList<>(mimeMessagePreparators.length);
            for (MimeMessagePreparator preparator : mimeMessagePreparators) {
                MimeMessage mimeMessage = createMimeMessage();
                preparator.prepare(mimeMessage);
                mimeMessages.add(createSignedMimeMessage(mimeMessage));
            }

            send(mimeMessages.toArray(new MimeMessage[0]));
        } catch (MailException ex) {
            throw ex;
        } catch (MessagingException ex) {
            throw new MailParseException(ex);
        } catch (Exception ex) {
            throw new MailPreparationException(ex);
        }
    }

    public MimeMessage createSignedMimeMessage(MimeMessage message) throws MailException {
        try {
            return new DkimMessage(message, signer);
        } catch (MessagingException e) {
            throw new MailParseException("Could not parse raw MIME content", e);
        }
    }
}
