package de.vinado.spring.test.mail.javamail;

import lombok.Getter;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll
 */
@Getter
public class MockTransport extends Transport {

    private final List<Message> sentMessages = new ArrayList<>();

    public MockTransport(Session session, URLName urlName) {
        super(session, urlName);
    }

    public MimeMessage getSentMessage(int index) {
        return (MimeMessage) this.sentMessages.get(index);
    }

    @Override
    public void connect(String host, int port, String username, String password) {
        setConnected(true);
    }

    @Override
    public synchronized void close() {
        setConnected(false);
    }

    @Override
    public void sendMessage(Message msg, Address[] addresses) throws MessagingException {
        if ("fail".equals(msg.getSubject())) {
            throw new MessagingException("failed");
        }
        this.sentMessages.add(msg);
    }

}
