package tech.ericasoares.credito_cobranca.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens_nao_enviadas")
public class MensagemNaoEnviada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCliente;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private boolean reenviado;

    public MensagemNaoEnviada() {
    }

    public MensagemNaoEnviada(Long idCliente, String mensagem, LocalDateTime dataCriacao) {
        this.idCliente = idCliente;
        this.mensagem = mensagem;
        this.dataCriacao = dataCriacao;
        this.reenviado = false;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isReenviado() {
        return reenviado;
    }

    public void setReenviado(boolean reenviado) {
        this.reenviado = reenviado;
    }

    @Override
    public String toString() {
        return "MensagemNaoEnviada{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", mensagem='" + mensagem + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", reenviado=" + reenviado +
                '}';
    }
}
