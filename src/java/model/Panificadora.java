package model;

public class Panificadora {
    
    private Long idPanificadora;
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private boolean desativado;

    // Construtor vazio
    public Panificadora() {}

    // Construtor completo
    public Panificadora(Long idPanificadora, String nome, String cnpj, String endereco, String telefone, boolean desativado) {
        this.idPanificadora = idPanificadora;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.desativado = desativado;
    }

    // Getters e Setters
    public Long getIdPanificadora() {
        return idPanificadora;
    }

    public void setIdPanificadora(Long idPanificadora) {
        this.idPanificadora = idPanificadora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isDesativado() {
        return desativado;
    }

    public void setDesativado(boolean desativado) {
        this.desativado = desativado;
    }

    @Override
    public String toString() {
        return "Panificadora{" +
                "idPanificadora=" + idPanificadora +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", endereco='" + endereco + '\'' +
                ", telefone='" + telefone + '\'' +
                ", desativado=" + desativado +
                '}';
    }
}
