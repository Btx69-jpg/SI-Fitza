package com.camunda.utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class SendEmailUtils {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    /**
     * Envia um email simples de texto.
     *
     * @param recipient Email do destinatário (ex: "cliente@gmail.com")
     * @param subject Assunto do email
     * @param content Corpo da mensagem (texto)
     * @throws MessagingException Se ocorrer erro no envio (autenticação, conexão, etc.)
     */
    public static void sendEmail(String recipient, String subject, String content) throws MessagingException {
        // 1. Carregar Configurações
        String host = dotenv.get("SMTP_HOST");
        String port = dotenv.get("SMTP_PORT");
        String username = dotenv.get("SMTP_USER");
        String password = dotenv.get("SMTP_PASS");

        if (host == null || username == null) {
            throw new RuntimeException("Configurações de SMTP não encontradas no ficheiro .env");
        }

        // 2. Configurar Propriedades
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // 3. Criar Sessão Autenticada
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // 4. Criar e Enviar a Mensagem
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);

        System.out.println(">>> [EMAIL UTILS] Email enviado para: " + recipient);
    }
}
