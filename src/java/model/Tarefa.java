package model;

import java.sql.Date;

public class Tarefa {

    private int idTarefa;
    private String titulo;
    private String mensagem;
    private String status;
    private String color;
    private Date dataCriacao;
    private String avatar;
    private int idUsuario;

    // Construtores
    public Tarefa() {
    }

    public Tarefa(String titulo, String mensagem, String status, String color, Date dataCriacao, String avatar, int idUsuario) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.status = status;
        this.color = color;
        this.dataCriacao = dataCriacao;
        this.avatar = avatar;
        this.idUsuario = idUsuario;
    }

    // Getters e Setters
    public int getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(int idTarefa) {
        this.idTarefa = idTarefa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "idTarefa=" + idTarefa +
                ", titulo='" + titulo + '\'' +
                ", mensagem='" + mensagem + '\'' +
                ", status='" + status + '\'' +
                ", color='" + color + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", avatar='" + avatar + '\'' +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
