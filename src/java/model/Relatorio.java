package model;

import java.util.Date;

public class Relatorio {

    private Long idRelatorio;
    private Long idPanificadora;
    private Long idProduto;
    private Double percentual;
    private String causa;
    private Date periodo;
    private String nomeProduto;

    // Construtores
    public Relatorio() {
    }

    public Relatorio(Long idPanificadora, Long idProduto, Double percentual, String causa, Date periodo) {
        this.idPanificadora = idPanificadora;
        this.idProduto = idProduto;
        this.percentual = percentual;
        this.causa = causa;
        this.periodo = periodo;
    }

        // getters e setters existentes...
    public String getNomeProduto() {
        return nomeProduto;
    }
    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    
    // Getters e Setters
    public Long getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Long idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Long getIdPanificadora() {
        return idPanificadora;
    }

    public void setIdPanificadora(Long idPanificadora) {
        this.idPanificadora = idPanificadora;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public Date getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Date periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "idRelatorio=" + idRelatorio +
                ", idPanificadora=" + idPanificadora +
                ", idProduto=" + idProduto +
                ", percentual=" + percentual +
                ", causa='" + causa + '\'' +
                ", periodo=" + periodo +
                '}';
    }
}
