package model;

import factory.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Produto;

public class EstoqueDAO {
    private final Connection connection;

    public EstoqueDAO() throws SQLException {
        this.connection = ConexaoFactory.conectar();
    }

    // LISTAR
    public List<Estoque> listar() throws SQLException {
        List<Estoque> estoques = new ArrayList<>();
        String sql = "SELECT e.*, p.nome, p.categoria, p.unidade_medida, p.perecivel, p.marca,p.url " +
                    "FROM estoque e " +
                    "LEFT JOIN produto p ON e.id_produto = p.id_produto";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Estoque estoque = mapResultSetToEstoque(rs);
                estoques.add(estoque);
            }
        }
        return estoques;
    }

    // BUSCAR POR ID
    public Estoque buscarPorId(Long id) throws SQLException {
        String sql = "SELECT e.*, p.nome, p.categoria, p.unidade_medida, p.perecivel, p.marca,p.url " +
                    "FROM estoque e " +
                    "LEFT JOIN produto p ON e.id_produto = p.id_produto " +
                    "WHERE e.id_estoque = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEstoque(rs);
                }
            }
        }
        return null;
    }

    // SALVAR (CREATE/UPDATE)
    public boolean salvar(Estoque estoque) throws SQLException {
        if (estoque.getIdEstoque() == null) {
            return inserir(estoque);
        } else {
            return atualizar(estoque);
        }
    }

    // INSERIR
    private boolean inserir(Estoque estoque) throws SQLException {
        String sql = "INSERT INTO estoque (id_produto, id_panificadora, quantidade, data_validade, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, estoque.getProduto().getIdProduto());
            stmt.setLong(2, estoque.getIdPanificadora());
            stmt.setInt(3, estoque.getQuantidade());
            
            if (estoque.getDataValidade() != null) {
                stmt.setDate(4, new java.sql.Date(estoque.getDataValidade().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, estoque.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Recupera o ID gerado
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        estoque.setIdEstoque(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // ATUALIZAR
    private boolean atualizar(Estoque estoque) throws SQLException {
        String sql = "UPDATE estoque SET id_produto = ?, id_panificadora = ?, quantidade = ?, " +
                    "data_validade = ?, status = ? WHERE id_estoque = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, estoque.getProduto().getIdProduto());
            stmt.setLong(2, estoque.getIdPanificadora());
            stmt.setInt(3, estoque.getQuantidade());
            
            if (estoque.getDataValidade() != null) {
                stmt.setDate(4, new java.sql.Date(estoque.getDataValidade().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, estoque.getStatus());
            stmt.setLong(6, estoque.getIdEstoque());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETAR
    public boolean deletar(Long id) throws SQLException {
        String sql = "DELETE FROM estoque WHERE id_estoque = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // MÉTODO AUXILIAR para mapear ResultSet para Estoque
    private Estoque mapResultSetToEstoque(ResultSet rs) throws SQLException {
        Estoque estoque = new Estoque();
        
        // Dados do Estoque
        estoque.setIdEstoque(rs.getLong("id_estoque"));
        estoque.setIdPanificadora(rs.getLong("id_panificadora"));
        estoque.setQuantidade(rs.getInt("quantidade"));
        estoque.setDataValidade(rs.getDate("data_validade"));
        estoque.setStatus(rs.getString("status"));

        // Dados do Produto (se existir)
        if (rs.getLong("id_produto") != 0) {
            Produto produto = new Produto();
            produto.setIdProduto((int) rs.getLong("id_produto"));
            produto.setNome(rs.getString("nome"));
            produto.setCategoria(rs.getString("categoria"));
            produto.setUnidadeMedida(rs.getString("unidade_medida"));
            produto.setPerecivel(rs.getBoolean("perecivel"));
            produto.setMarca(rs.getString("marca"));
            produto.setUrl(rs.getString("url"));
            estoque.setProduto(produto);
        }
        
        return estoque;
    }

    // Fechar conexão
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}