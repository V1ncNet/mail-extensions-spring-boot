Spring Boot DKIM JavaMail
===

This project provides an easy way to digitally sign your emails. Therefore, this project uses the
[java-utils-mail-dkim](https://github.com/markenwerk/java-utils-mail-dkim) library from Markenwerk and extends Spring's
JavaMailSender to sign MIME messages before they were transported to the SMTP server. To verify the signed parts of the
email you must configure your DNS. This authentication method is called DKIM (DomainKeys Identified Mail).


## Usage

Include the `spring-boot-starter-dkim-javamail` dependency in your `pom.xml` and adjust your `application.properties` or
`application.yml` as described in [Configuration](#Configuration).

The starter, as well as the other artifacts are hosted in the
[Maven Central Repository](https://search.maven.org/artifact/de.vinado.boot/spring-boot-starter-dkim-javamail/1.2.2/jar).
You can use it with the following coordinates:

```xml
<dependency>
    <groupId>de.vinado.boot</groupId>
    <artifactId>spring-boot-starter-dkim-javamail</artifactId>
    <version>1.2.2</version>
</dependency>
```

Version `v1.0.0` is still available in the
[JitPack repository](https://jitpack.io/#V1ncNet/spring-boot-dkim-javamail/v1.0.0).

## Setup

Due to the Spring Boot Start, there is no coding required to include the project. Still, there are a few things that are
required for emails to be signed and verified. Read the
[developer documentation](https://github.com/markenwerk/java-utils-mail-dkim#setup) in the signer-library for complete
setup instructions on how to create an RSA keypair and properly configure DNS.


### Configuration

The starter uses auto configuration to avoid Java-based adjustments. The mandatory configuration looks like this:

```properties
dkim.signing-domain=domain.tld
dkim.selector=default
dkim.private-key=file:/path/to/your/private.key.der
#dkim.private-key=classpath:/path/to/your/private.key.der
#dkim.private-key=/path/to/your/private.key.der
```

The example assumes your TXT record uses the domain `default._domainkey.domain.tld`. The following example contains
optional properties which configures the DKIM signer itself:

```properties
dkim.signer.identity=
dkim.signer.header-canonicalization=SIMPLE
dkim.signer.body-canonicalization=RELAXED
dkim.signer.signing-algorithm=SHA256_WITH_RSA
dkim.signer.length-param=true
dkim.signer.copy-header-fields=false
dkim.signer.check-domain-key=true
```

To learn more about the identity and canonicalization read the [specification](https://tools.ietf.org/html/rfc6376)
about DKIM.

## Licence

Apache License 2.0 - [Vinado](https://vinado.de) - Built with :heart: in Dresden
