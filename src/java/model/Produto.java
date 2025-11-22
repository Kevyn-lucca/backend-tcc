package model;

public class Produto {

    private int idProduto;
    private String nome;
    private String categoria;
    private String unidadeMedida;
    private boolean perecivel;
    private String marca;
        private String url;

    
    public Produto() {
    }

    public Produto(String nome, String categoria, String unidadeMedida, boolean perecivel, String marca) {
        this.nome = nome;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida;
        this.perecivel = perecivel;
        this.marca = marca;
        this.url = url;
    }

     
    public String getUrl() {
        return url;
    }   
    
    public void setUrl(String url){
        this.url = url;
    }
    
    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public boolean isPerecivel() {
        return perecivel;
    }

    public void setPerecivel(boolean perecivel) {
        this.perecivel = perecivel;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "idProduto=" + idProduto +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", unidadeMedida='" + unidadeMedida + '\'' +
                ", perecivel=" + perecivel +
                ", marca='" + marca + '\'' +
                ", img='" + url + '\'' +

                '}';
    }

    public Integer getId() { return idProduto; }
    public void setId(Integer id) { this.idProduto = id; }
}
