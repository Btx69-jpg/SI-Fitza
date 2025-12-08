package com.camunda.utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * 🛠️ Classe utilitária para o envio de emails usando um servidor SMTP
 * cujas configurações são carregadas a partir de um ficheiro {@code .env}.
 * Esta classe utiliza a API Jakarta Mail.
 * <p>
 * Requer as seguintes variáveis de ambiente no ficheiro {@code .env}:
 * <ul>
 * <li>{@code SMTP_HOST}: O endereço do servidor SMTP (ex: smtp.gmail.com).</li>
 * <li>{@code SMTP_PORT}: A porta do servidor SMTP (ex: 587 para TLS/STARTTLS).</li>
 * <li>{@code SMTP_USER}: O email ou nome de utilizador para autenticação.</li>
 * <li>{@code SMTP_PASS}: A palavra-passe ou token de aplicação para autenticação.</li>
 * </ul>
 * </p>
 */
public class SendEmailUtils {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    /**
     * Envia um email simples de texto.
     * <p>
     * O remetente do email é definido pela variável de ambiente {@code SMTP_USER}.
     * A conexão é feita usando as propriedades {@code SMTP_HOST}, {@code SMTP_PORT},
     * {@code SMTP_USER} e {@code SMTP_PASS} do ficheiro {@code .env},
     * exigindo autenticação e usando STARTTLS.
     * </p>
     *
     * @param recipient Email do destinatário (ex: "cliente@gmail.com")
     * @param subject Assunto do email
     * @param content Corpo da mensagem (texto)
     * @throws MessagingException Se ocorrer erro no envio (autenticação, conexão,
     * endereço de email inválido, etc.) durante a comunicação
     * com o servidor SMTP.
     * @throws RuntimeException Se as configurações essenciais ({@code SMTP_HOST} ou
     * {@code SMTP_USER}) não forem encontradas no ficheiro {@code .env}.
     */
    public static void sendEmail(String recipient, String subject, String content) throws MessagingException {
        //Carregar Configurações
        String host = dotenv.get("SMTP_HOST");
        String port = dotenv.get("SMTP_PORT");
        String username = dotenv.get("SMTP_USER");
        String password = dotenv.get("SMTP_PASS");

        if (host == null || username == null) {
            throw new RuntimeException("Configurações de SMTP não encontradas no ficheiro .env");
        }

        //Configurar Propriedades
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        //Criar Sessão Autenticada
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        //Criar e Enviar a Mensagem
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);

        System.out.println(">>> [EMAIL UTILS] Email enviado para: " + recipient);
    }
}
