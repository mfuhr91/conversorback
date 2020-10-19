package com.conversorback.api.services;

public interface IMailService {

    public void enviarMail(String destinatario, String asunto, String contenido);
    
}
