# Spring Boot Mail Extensions

This project aims to extend Spring's `JavaMailSender` with useful features. Therefore, the library provides two advanced
sender extensions.


## Library

The library currently provides two combinable sender implementations.

The `DkimJavaMailSender` uses Markenwerk's [java-utils-mail-dkim](https://github.com/markenwerk/java-utils-mail-dkim)
library and extends Spring's `JavaMailSender` to sign MIME messages before they were transported to the SMTP server. To
verify the signed parts of the email you must configure your DNS. This authentication method is called **DKIM**
(DomainKeys Identified Mail). In case you are not using the starter, you must configure the `DkimSigner` yourself.

The `ConcurrentJavaMailSender` enqueues any supported MIME message to be sent immediately or after a configurable
cooldown. By splitting potential large amounts of messages into smaller batches your application is capable of **dealing
with** common problems or restrictions of your SMTP server or provider, like **rate limits**. The dispatch happens on
another thread, so you don't block your main thread in case of a **slow SMTP server**.


## Usage

This project provides 4 artifacts. Depending on your scope and infrastructure you may want just the library. Include
the `mail-extensions` and `utils-mail-dkim` libraries in your Spring Project **in case you don't want or can use the
Starters**. Adjust your `pom.xml` as followed:

```xml
<dependency>
    <groupId>de.vinado.spring</groupId>
    <artifactId>mail-extensions</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>net.markenwerk</groupId>
    <artifactId>utils-mail-dkim</artifactId>
    <version>1.4.0</version>
</dependency>
```

---

Those who use Spring Boot can choose between 3 Spring Boot starters.

1. `mail-extensions-concurrent-javamail-spring-boot-starter`
2. `mail-extensions-dkim-javamail-spring-boot-starter`
3. `mail-extensions-all-spring-boot-starter`

Include one of the above in your `pom.xml` and adjust your `application.properties` or `application.yml` as described in
[Configuration](#Configuration).

The starter, as well as the other artifacts are hosted in the
[Maven Central Repository](https://search.maven.org/artifact/de.vinado.boot/mail-extensions-spring-boot/2.0.0/pom).
You can use it with one of the following coordinates:

```xml
<dependency>
    <groupId>de.vinado.boot</groupId>
    <artifactId>mail-extensions-concurrent-javamail-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>

<dependency>
    <groupId>de.vinado.boot</groupId>
    <artifactId>mail-extensions-dkim-javamail-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>

<dependency>
    <groupId>de.vinado.boot</groupId>
    <artifactId>mail-extensions-all-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

Version `v1.x` is still available in an archived [Git repository](https://github.com/V1ncNet/spring-boot-dkim-javamail).


## Setup

Due to the Spring Boot Start, there is no coding required to include the project. Still, there are a few things that are
required for emails to be signed and verified. Read the
[developer documentation](https://github.com/markenwerk/java-utils-mail-dkim#setup) in the signer-library for complete
setup instructions on how to create an RSA keypair and properly configure DNS.


### Configuration

The starter uses autoconfiguration to avoid Java-based adjustments. The mandatory configuration looks like this:

```properties
javamail.dkim.signing-domain=domain.tld
javamail.dkim.selector=default
javamail.dkim.private-key=file:/path/to/your/private.key.der
#javamail.dkim.private-key=classpath:/path/to/your/private.key.der
```

The example assumes your TXT record uses the domain `default._domainkey.domain.tld`. The following example contains
optional properties which configures the DKIM signer itself:

```properties
javamail.dkim.signer.identity=
javamail.dkim.signer.header-canonicalization=SIMPLE
javamail.dkim.signer.body-canonicalization=RELAXED
javamail.dkim.signer.signing-algorithm=SHA256_WITH_RSA
javamail.dkim.signer.length-param=true
javamail.dkim.signer.copy-header-fields=false
javamail.dkim.signer.check-domain-key=true
```

To learn more about the identity and canonicalization read the [specification](https://tools.ietf.org/html/rfc6376)
about DKIM.

---

The concurrent sender can be configured as follows. This sender is deactivated by default.

```properties
javamail.concurrent.enabled=true
javamail.concurrent.batch-size=20
# 20 seconds cooldown
javamail.concurrent.cooldown-millis=20000
```


## Licence

Apache License 2.0 - [Vinado](https://vinado.de) - Built with :heart: in Dresden
