package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "relatorio_perda")
public class RelatorioPerda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relatorio")
    private Long idRelatorio;

    @Column(name = "id_panificadora")
    private Long idPanificadora;

    @Column(name = "id_produto")
    private Long idProduto;

    @Column(name = "percentual_perda")
    private Double percentualPerda;

    private String causa;

    private LocalDate periodo;

    // Getters e Setters
    public Long getIdRelatorio() { return idRelatorio; }
    public void setIdRelatorio(Long idRelatorio) { this.idRelatorio = idRelatorio; }

    public Long getIdPanificadora() { return idPanificadora; }
    public void setIdPanificadora(Long idPanificadora) { this.idPanificadora = idPanificadora; }

    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }

    public Double getPercentualPerda() { return percentualPerda; }
    public void setPercentualPerda(Double percentualPerda) { this.percentualPerda = percentualPerda; }

    public String getCausa() { return causa; }
    public void setCausa(String causa) { this.causa = causa; }

    public LocalDate getPeriodo() { return periodo; }
    public void setPeriodo(LocalDate periodo) { this.periodo = periodo; }
}
