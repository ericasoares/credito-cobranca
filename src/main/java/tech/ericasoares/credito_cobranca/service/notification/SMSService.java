package tech.ericasoares.credito_cobranca.service.notification;

public interface SMSService {
    void enviarSms(Long clienteId, String mensagem);
}