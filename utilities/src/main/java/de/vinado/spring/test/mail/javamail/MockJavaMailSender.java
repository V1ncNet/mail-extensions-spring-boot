package de.vinado.spring.test.mail.javamail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.activation.FileTypeMap;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * @author Vincent Nadoll
 */
@Getter
@Setter
public class MockJavaMailSender extends JavaMailSenderImpl {

    private MockTransport transport;

    @Override
    protected Transport getTransport(Session session) {
        this.transport = new MockTransport(session, null);
        return transport;
    }

    public List<Message> getSentMessages() {
        return getTransport().getSentMessages();
    }

    public MimeMessage getSentMessage(int index) {
        return getTransport().getSentMessage(index);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder defaultSender() {
        return builder()
            .host("host")
            .username("username")
            .password("password")
            .defaultEncoding("UTF-8")
            .defaultFileTypeMap(FileTypeMap.getDefaultFileTypeMap())
            ;
    }

    public static final class Builder {

        private MockTransport transport;
        private Properties javaMailProperties = new Properties();
        private Session session = Session.getInstance(javaMailProperties);
        private String protocol = JavaMailSenderImpl.DEFAULT_PROTOCOL;
        private String host;
        private int port = JavaMailSenderImpl.DEFAULT_PORT;
        private String username;
        private String password;
        private String defaultEncoding;
        private FileTypeMap defaultFileTypeMap;

        public Builder transport(MockTransport transport) {
            this.transport = transport;
            return this;
        }

        public Builder properties(Properties javaMailProperties) {
            this.javaMailProperties = javaMailProperties;
            return this;
        }

        public Builder session(Session session) {
            this.session = session;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder defaultEncoding(String defaultEncoding) {
            this.defaultEncoding = defaultEncoding;
            return this;
        }

        public Builder defaultFileTypeMap(FileTypeMap defaultFileTypeMap) {
            this.defaultFileTypeMap = defaultFileTypeMap;
            return this;
        }

        public MockJavaMailSender build() {
            MockJavaMailSender sender = new MockJavaMailSender();
            sender.setTransport(transport);
            sender.setJavaMailProperties(javaMailProperties);
            sender.setSession(session);
            sender.setProtocol(protocol);
            sender.setHost(host);
            sender.setPort(port);
            sender.setUsername(username);
            sender.setPassword(password);
            sender.setDefaultEncoding(defaultEncoding);
            sender.setDefaultFileTypeMap(defaultFileTypeMap);
            return sender;
        }
    }
}
