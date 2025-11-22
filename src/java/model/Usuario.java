package model;

public class Usuario {

    private int idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String perfil; // Ex: "GERENTE", "FUNCIONARIO"
    private boolean ativo;
    private int idPanificadora; // Nova coluna no banco

    public Usuario() {}

    public Usuario(int idUsuario, String nome, String email, String senha, String perfil, boolean ativo, int idPanificadora) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = ativo;
        this.idPanificadora = idPanificadora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getIdPanificadora() {
        return idPanificadora;
    }

    public void setIdPanificadora(int idPanificadora) {
        this.idPanificadora = idPanificadora;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", perfil='" + perfil + '\'' +
                ", ativo=" + ativo +
                ", idPanificadora=" + idPanificadora +
                '}';
    }
}
