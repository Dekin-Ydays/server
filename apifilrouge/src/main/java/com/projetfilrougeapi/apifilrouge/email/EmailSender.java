package com.projetfilrougeapi.apifilrouge.email;

import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;

import java.util.Properties;

public class EmailSender {
    HtmlEmail email = new HtmlEmail();
    String host = "smtp.gmail.com";
    String port = "587";
    String username = "";
    String password = "mjqgtkjjwwcapdps";

    public EmailSender(String username, String password) {
        this.username = username;
        this.password = password;
        email.setHostName(host);
        email.setSmtpPort(Integer.parseInt(port));
        email.setAuthentication(username, password);
        email.setSSLOnConnect(true);
    }


    public void sendWelcomeEmail(String toValue) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("subject", "Bienvenue !");
        context.put("name", "Quentin Marchal");
        context.put("currentyear", 2025);
        context.put("message", "Nous sommes ravis de vous accueillir parmi nous !");
        context.put("bannerImageUrl", "https://as1.ftcdn.net/jpg/05/62/13/54/1000_F_562135455_9cRKUGrx050EVpT8iyn9Q5DgThUG8l28.jpg");

        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/emailTemplate.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Configuration de l'email
        email.setFrom(username);
        email.setSubject("Bienvenue !");
        email.setHtmlMsg(writer.toString());

        email.addTo(toValue);
        System.out.println("Email envoyé à : " + toValue);

        // Envoi de l'email
        email.send();
    }
    public void sendConfirmationEmail(String toValue, String orderNumber, double totalAmount) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("subject", "Confirmation de subscription");
        context.put("name", "Quentin Marchal");
        context.put("currentyear", 2025);
        context.put("message", "vous êtes l'adhérent n° " + orderNumber + "et devrez payez un montant de " + totalAmount + "€ a été confirmée !");
        context.put("bannerImageUrl", "https://as2.ftcdn.net/jpg/02/92/84/11/1000_F_292841149_VrPSRTAGq6h4vzYsqGg4nBEJzumoCpQV.jpg");

        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/emailTemplate.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Configuration de l'email
        email.setFrom(username);
        email.setSubject("Confirmation de votre commande #" + orderNumber);
        email.setHtmlMsg(writer.toString());
        email.addTo(toValue);
        System.out.println("Email de confirmation envoyé à : " + toValue);

        // Envoi de l'email
        email.send();
    }

    public void sendPromotionEmail(String toValue, String promoCode, int discountPercentage, String endDate) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("subject", "Promotion Spéciale !");
        context.put("name", "Quentin Marchal");
        context.put("currentyear", 2025);
        context.put("message", "Nous avons le plaisir de vous offrir une remise exceptionnelle de " +
                discountPercentage + "% avec le code promotionnel : " + promoCode +
                ". Cette offre est valable jusqu'au " + endDate + " !");
        context.put("bannerImageUrl", "https://as2.ftcdn.net/jpg/02/80/89/73/1000_F_280897317_P9Ic2jYdgCLLRv6KwILIzwNnvNYm2b4.jpg");
        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/emailTemplate.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        // Configuration de l'email
        email.setFrom(username);
        email.setSubject("Promotion Spéciale !");
        email.setHtmlMsg(writer.toString());
        email.addTo(toValue);
        System.out.println("Email promotionnel envoyé à : " + toValue);
        // Envoi de l'email
        email.send();
    }

}
