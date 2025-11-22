package model;

import java.util.Date;
import model.Produto;

public class Estoque {
    private Long idEstoque;
    private Produto produto;
    private Long idPanificadora;
    private Integer quantidade;
    private Date dataValidade;
    private String status;

    public Estoque() {}

    public Estoque(Produto produto, Long idPanificadora, Integer quantidade, Date dataValidade, String status) {
        this.produto = produto;
        this.idPanificadora = idPanificadora;
        this.quantidade = quantidade;
        this.dataValidade = dataValidade;
        this.status = status;
    }

    // Getters e Setters
    public Long getIdEstoque() {
        return idEstoque;
    }

    public void setIdEstoque(Long idEstoque) {
        this.idEstoque = idEstoque;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Long getIdPanificadora() {
        return idPanificadora;
    }

    public void setIdPanificadora(Long idPanificadora) {
        this.idPanificadora = idPanificadora;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Métodos de compatibilidade
    public Long getId() {
        return idEstoque;
    }

    public void setId(Long id) {
        this.idEstoque = id;
    }

    @Override
    public String toString() {
        return "Estoque{" +
                "idEstoque=" + idEstoque +
                ", produto=" + produto +
                ", idPanificadora=" + idPanificadora +
                ", quantidade=" + quantidade +
                ", dataValidade=" + dataValidade +
                ", status='" + status + '\'' +
                '}';
    }
}