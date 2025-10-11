package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "produto")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    @Column(name = "perecivel")
    private Boolean perecivel;

    @Column(name = "marca", length = 50) // NOVO CAMPO
    private String marca;

    // Construtores
    public Produto() {}

    public Produto(String nome, String categoria, String unidadeMedida, Boolean perecivel, String marca) {
        this.nome = nome;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida;
        this.perecivel = perecivel;
        this.marca = marca;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public Boolean getPerecivel() { return perecivel; }
    public void setPerecivel(Boolean perecivel) { this.perecivel = perecivel; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", unidadeMedida='" + unidadeMedida + '\'' +
                ", perecivel=" + perecivel +
                ", marca='" + marca + '\'' +
                '}';
    }
}
