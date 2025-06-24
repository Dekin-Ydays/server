package com.projetfilrougeapi.apifilrouge.email;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;

import java.util.Properties;

@Getter
@Setter
public class EmailSender {
    private HtmlEmail email = new HtmlEmail();
    private String host = "smtp.gmail.com";
    private String port = "587";
    private String username = "";
    private String password = "mjqgtkjjwwcapdps";


    public EmailSender(String username) {
        this.username = username;
        email.setHostName(host);
        email.setSmtpPort(Integer.parseInt(port));
        email.setAuthentication(username, password);
        email.setSSLOnConnect(true);
    }


    public void sendInvitationEmail(User sender, User receiver, Event event) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("nomEvenement", event.getName());
        context.put("nom", sender.getLastName());
        context.put("prenom", sender.getFirstName());
        context.put("emailExpediteur", sender.getEmail());
        context.put("dateEnvoi", sender.getEmail());


        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/emailTemplate.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Configuration de l'email
        email.setFrom(sender.getEmail());
        email.setSubject("Invitation concernant l'événement : " + event.getName());
        email.setHtmlMsg(writer.toString());

        email.addTo(receiver.getEmail());
        System.out.println("Email envoyé à : " + receiver.getEmail());

        // Envoi de l'email
        email.send();
    }

}
