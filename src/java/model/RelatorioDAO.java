package model;
import factory.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {
    private final Connection connection;

    public RelatorioDAO() throws SQLException {
        this.connection = ConexaoFactory.conectar();
    }

    // LISTAR TODOS
    public List<Relatorio> listar() throws SQLException {
        List<Relatorio> relatorios = new ArrayList<>();
        String sql = "SELECT r.*, p.nome AS nome_produto, p.categoria, p.unidade_medida, p.marca, p.perecivel " +
                     "FROM relatorio r " +
                     "LEFT JOIN produto p ON r.id_produto = p.id_produto";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Relatorio relatorio = mapResultSetToRelatorio(rs);
                relatorios.add(relatorio);
            }
        }
        return relatorios;
    }

    // BUSCAR POR ID
    public Relatorio buscarPorId(Long id) throws SQLException {
        String sql = "SELECT r.*, p.nome AS nome_produto, p.categoria, p.unidade_medida, p.marca, p.perecivel " +
                     "FROM relatorio r " +
                     "LEFT JOIN produto p ON r.id_produto = p.id_produto " +
                     "WHERE r.id_relatorio = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRelatorio(rs);
                }
            }
        }
        return null;
    }

    // INSERIR NOVO RELATÓRIO
    public boolean inserir(Relatorio relatorio) throws SQLException {
        String sql = "INSERT INTO relatorio (id_panificadora, id_produto, percentual, causa, periodo) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, relatorio.getIdPanificadora());
            stmt.setLong(2, relatorio.getIdProduto());
            if (relatorio.getPercentual() != null) {
                stmt.setDouble(3, relatorio.getPercentual());
            } else {
                stmt.setNull(3, Types.DOUBLE);
            }
            stmt.setString(4, relatorio.getCausa());
            if (relatorio.getPeriodo() != null) {
                stmt.setDate(5, new java.sql.Date(relatorio.getPeriodo().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        relatorio.setIdRelatorio(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    // ATUALIZAR RELATÓRIO EXISTENTE
    public boolean atualizar(Relatorio relatorio) throws SQLException {
        String sql = "UPDATE relatorio SET id_panificadora = ?, id_produto = ?, percentual = ?, causa = ?, periodo = ? " +
                     "WHERE id_relatorio = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, relatorio.getIdPanificadora());
            stmt.setLong(2, relatorio.getIdProduto());
            if (relatorio.getPercentual() != null) {
                stmt.setDouble(3, relatorio.getPercentual());
            } else {
                stmt.setNull(3, Types.DOUBLE);
            }
            stmt.setString(4, relatorio.getCausa());
            if (relatorio.getPeriodo() != null) {
                stmt.setDate(5, new java.sql.Date(relatorio.getPeriodo().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setLong(6, relatorio.getIdRelatorio());
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETAR RELATÓRIO
    public boolean deletar(Long id) throws SQLException {
        String sql = "DELETE FROM relatorio WHERE id_relatorio = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // MAPEAR RESULTSET → OBJETO RELATORIO
    private Relatorio mapResultSetToRelatorio(ResultSet rs) throws SQLException {
        Relatorio relatorio = new Relatorio();
        relatorio.setIdRelatorio(rs.getLong("id_relatorio"));
        relatorio.setIdPanificadora(rs.getLong("id_panificadora"));
        relatorio.setIdProduto(rs.getLong("id_produto"));
        relatorio.setPercentual(rs.getObject("percentual") != null ? rs.getDouble("percentual") : null);
        relatorio.setCausa(rs.getString("causa"));
        relatorio.setPeriodo(rs.getDate("periodo"));

        // Mapeia também o nome do produto (campo selecionado como nome_produto)
        relatorio.setNomeProduto(rs.getString("nome_produto"));

        return relatorio;
    }

    // FECHAR CONEXÃO
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}