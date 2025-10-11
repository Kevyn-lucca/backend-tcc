package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoque")
    private Long idEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", insertable = false, updatable = false)
    private Produto produto;

    @Column(name = "id_produto")
    private Long idProduto;

    @Column(name = "id_panificadora")
    private Long idPanificadora;

    private Integer quantidade;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    private String status; // novo campo

    // Getters e Setters
    public Long getIdEstoque() { return idEstoque; }
    public void setIdEstoque(Long idEstoque) { this.idEstoque = idEstoque; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }

    public Long getIdPanificadora() { return idPanificadora; }
    public void setIdPanificadora(Long idPanificadora) { this.idPanificadora = idPanificadora; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
