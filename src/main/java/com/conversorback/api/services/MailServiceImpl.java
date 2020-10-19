package com.conversorback.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements IMailService {

    //Importante hacer la inyección de dependencia de JavaMailSender:
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void enviarMail(String destinatario, String asunto, String contenido) {
        
        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(contenido);

        try{
            mailSender.send(email);

        }catch(MailException m){
            System.out.println(m.getMessage());
            
        }
        System.out.println("EMAIL ENVIADO");
        

    }
    
}
