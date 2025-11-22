package model;

import factory.ConexaoFactory;
import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import factory.HashFactory;
import java.util.Base64;
import java.util.UUID;

public class UsuarioDAO {

    private final Connection connection;

    public UsuarioDAO() throws SQLException {
        this.connection = ConexaoFactory.conectar();
    }

    // ------------------ LISTAR TODOS ------------------
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT \n" +
"    id_usuario,\n" +
"    nome,\n" +
"    email,\n" +
"    perfil,\n" +
"    ativo,\n" +
"    id_panificadora\n" +
"FROM usuario;";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    // ------------------ BUSCAR POR ID ------------------
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    // ------------------ AUTENTICAR ------------------
public Usuario autenticar(String email, String senhaDigitada) throws SQLException {
    String sql = "SELECT * FROM usuario WHERE LOWER(email) = LOWER(?) AND ativo = 1";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, email.trim());
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String hashArmazenado = rs.getString("senha");

                if (factory.HashFactory.verificarSenha(senhaDigitada, hashArmazenado)) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
    }
    return null;
}


    // ------------------ INSERIR ------------------
    public boolean inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, senha, perfil, ativo, id_panificadora) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getPerfil());
            stmt.setBoolean(5, u.isAtivo());
            stmt.setInt(6, u.getIdPanificadora());

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) u.setIdUsuario(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    // ------------------ ATUALIZAR ------------------
public boolean atualizar(Usuario u) throws SQLException {
    String sql = "UPDATE usuario SET nome=?, email=?, perfil=?, id_panificadora=? WHERE id_usuario=?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, u.getNome());
        stmt.setString(2, u.getEmail());
        stmt.setString(3, u.getPerfil());
        stmt.setInt(4, u.getIdPanificadora());
        stmt.setInt(5, u.getIdUsuario());
        return stmt.executeUpdate() > 0;
    }
}

    // ------------------ DELETAR ------------------
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public String gerarTokenBase64(String perfil, int idPanificadora) {
        String uuid = UUID.randomUUID().toString();  
      
        String dados = perfil + ";" + idPanificadora + ";" + uuid;
        
        return Base64.getEncoder().encodeToString(dados.getBytes(StandardCharsets.UTF_8));
    }

    public void aplicarTokenBase64(Usuario u, String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] partes = decoded.split(";");
            if (partes.length == 3) {
                u.setPerfil(partes[0]);
                u.setIdPanificadora(Integer.parseInt(partes[1]));
                
                String uuidRecebido = partes[2];
                
            } else {
                System.err.println("Token com formato inesperado: " + decoded);
            }
        } catch (Exception e) {
            System.err.println("Falha ao decodificar token: " + e.getMessage());
        }
    }
  

    // ------------------ MAPEAMENTO ------------------
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setPerfil(rs.getString("perfil"));
        u.setAtivo(rs.getBoolean("ativo"));
        u.setIdPanificadora(rs.getInt("id_panificadora"));
        return u;
    }
    
    
    public boolean ativarUsuario(int id) throws SQLException {
    String sql = "UPDATE usuario SET ativo = 1 WHERE id_usuario = ?";

    PreparedStatement stmt = connection.prepareStatement(sql);
    stmt.setInt(1, id);

    int linhas = stmt.executeUpdate();
    stmt.close();
    return linhas > 0;
}


    // ------------------ FECHAR CONEXÃO ------------------
    public void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}









