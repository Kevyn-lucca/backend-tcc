package model;

import factory.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanificadoraDAO {
    private final Connection connection;

    public PanificadoraDAO() throws SQLException {
        this.connection = ConexaoFactory.conectar();
    }

    // LISTAR TODAS
    public List<Panificadora> listar() throws SQLException {
        List<Panificadora> panificadoras = new ArrayList<>();
        String sql = "SELECT * FROM panificadora";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                panificadoras.add(mapResultSetToPanificadora(rs));
            }
        }

        return panificadoras;
    }

    // BUSCAR POR ID
    public Panificadora buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM panificadora WHERE id_panificadora = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPanificadora(rs);
                }
            }
        }

        return null;
    }

    // INSERIR NOVA PANIFICADORA
    public boolean inserir(Panificadora p) throws SQLException {
        String sql = "INSERT INTO panificadora (nome, cnpj, endereco, telefone, desativado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCnpj());
            stmt.setString(3, p.getEndereco());
            stmt.setString(4, p.getTelefone());
            stmt.setBoolean(5, p.isDesativado());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        p.setIdPanificadora(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // ATUALIZAR PANIFICADORA
    public boolean atualizar(Panificadora p) throws SQLException {
        String sql = "UPDATE panificadora SET nome = ?, cnpj = ?, endereco = ?, telefone = ?, desativado = ? " +
                     "WHERE id_panificadora = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCnpj());
            stmt.setString(3, p.getEndereco());
            stmt.setString(4, p.getTelefone());
            stmt.setBoolean(5, p.isDesativado());
            stmt.setLong(6, p.getIdPanificadora());

            return stmt.executeUpdate() > 0;
        }
    }

    // DELETAR PANIFICADORA (opcional - geralmente se usa desativar)
    public boolean deletar(Long id) throws SQLException {
        String sql = "DELETE FROM panificadora WHERE id_panificadora = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // DESATIVAR / ATIVAR PANIFICADORA (Soft Delete)
    public boolean alterarStatus(Long id, boolean desativado) throws SQLException {
        String sql = "UPDATE panificadora SET desativado = ? WHERE id_panificadora = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, desativado);
            stmt.setLong(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // MAPEAMENTO DO RESULTSET
    private Panificadora mapResultSetToPanificadora(ResultSet rs) throws SQLException {
        Panificadora p = new Panificadora();
        p.setIdPanificadora(rs.getLong("id_panificadora"));
        p.setNome(rs.getString("nome"));
        p.setCnpj(rs.getString("cnpj"));
        p.setEndereco(rs.getString("endereco"));
        p.setTelefone(rs.getString("telefone"));
        p.setDesativado(rs.getBoolean("desativado"));
        return p;
    }

    // FECHAR CONEXÃO
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
