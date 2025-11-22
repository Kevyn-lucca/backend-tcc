package model;

import factory.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private final Connection connection;

    public TarefaDAO() throws SQLException {
        this.connection = ConexaoFactory.conectar();
    }

    public boolean inserir(Tarefa tarefa) throws SQLException {
        String sql = "INSERT INTO tarefas (titulo, mensagem, status, color, data_criacao, avatar, id_usuario) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getMensagem());
            stmt.setString(3, tarefa.getStatus());
            stmt.setString(4, tarefa.getColor());
            stmt.setDate(5, tarefa.getDataCriacao());
            stmt.setString(6, tarefa.getAvatar());
            stmt.setInt(7, tarefa.getIdUsuario());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tarefa.setIdTarefa(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    // ✅ Atualizar tarefa
    public boolean atualizar(Tarefa tarefa) throws SQLException {
        String sql = "UPDATE tarefas SET titulo = ?, mensagem = ?, status = ?, color = ?, data_criacao = ?, avatar = ?, id_usuario = ? "
                   + "WHERE id_tarefa = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getMensagem());
            stmt.setString(3, tarefa.getStatus());
            stmt.setString(4, tarefa.getColor());
            stmt.setDate(5, tarefa.getDataCriacao());
            stmt.setString(6, tarefa.getAvatar());
            stmt.setInt(7, tarefa.getIdUsuario());
            stmt.setInt(8, tarefa.getIdTarefa());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int idTarefa) throws SQLException {
        String sql = "DELETE FROM tarefas WHERE id_tarefa = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTarefa);
            return stmt.executeUpdate() > 0;
        }
    }

    public Tarefa buscarPorId(int idTarefa) throws SQLException {
        String sql = "SELECT * FROM tarefas WHERE id_tarefa = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTarefa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTarefa(rs);
                }
            }
        }
        return null;
    }

    // ✅ Listar todas as tarefas
    public List<Tarefa> listar() throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas ORDER BY id_tarefa DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tarefas.add(mapResultSetToTarefa(rs));
            }
        }
        return tarefas;
    }

    // ✅ Listar tarefas por usuário
    public List<Tarefa> listarPorUsuario(int idUsuario) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE id_usuario = ? ORDER BY id_tarefa DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(mapResultSetToTarefa(rs));
                }
            }
        }
        return tarefas;
    }

    // ✅ Mapeamento ResultSet → Objeto
    private Tarefa mapResultSetToTarefa(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setIdTarefa(rs.getInt("id_tarefa"));
        t.setTitulo(rs.getString("titulo"));
        t.setMensagem(rs.getString("mensagem"));
        t.setStatus(rs.getString("status"));
        t.setColor(rs.getString("color"));
        t.setDataCriacao(rs.getDate("data_criacao"));
        t.setAvatar(rs.getString("avatar"));
        t.setIdUsuario(rs.getInt("id_usuario"));
        return t;
    }

    // ✅ Fechar conexão
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
