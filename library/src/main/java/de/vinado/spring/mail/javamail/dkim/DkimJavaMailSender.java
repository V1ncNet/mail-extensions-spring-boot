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
            mimeMessages.add(createSignedMimeMessage(message.getMimeMessage()));
        }

        doSend(mimeMessages.toArray(new MimeMessage[0]), simpleMessages);
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
        } catch (Exception ex) {
            throw new MailPreparationException(ex);
        }
    }

    /**
     * This implementation creates a {@link DkimMessage} which is signed off with the configured private key before
     * shipment.
     *
     * @param message must not be {@code null}
     * @return new {@link DkimMessage}
     * @throws MailException in case copying raw data from the original to the signable message fails
     */
    public MimeMessage createSignedMimeMessage(MimeMessage message) throws MailException {
        try {
            return new DkimMessage(message, signer);
        } catch (MessagingException e) {
            throw new MailParseException("Could not parse raw MIME content", e);
        }
    }
}
