package model;

import factory.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private final Connection conn;

    public ProdutoDAO() throws SQLException {
        this.conn = ConexaoFactory.conectar();
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getInt("id_produto"));
                p.setNome(rs.getString("nome"));
                p.setCategoria(rs.getString("categoria"));
                p.setUnidadeMedida(rs.getString("unidade_medida"));
                p.setPerecivel(rs.getBoolean("perecivel"));
                p.setMarca(rs.getString("marca"));
                p.setUrl(rs.getString("url"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }

        return lista;
    }

    public Produto buscarPorId(int id) {
        Produto produto = null;
        String sql = "SELECT * FROM produto WHERE id_produto = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto = new Produto();
                    produto.setId(rs.getInt("id_produto"));
                    produto.setNome(rs.getString("nome"));
                    produto.setCategoria(rs.getString("categoria"));
                    produto.setUnidadeMedida(rs.getString("unidade_medida"));
                    produto.setPerecivel(rs.getBoolean("perecivel"));
                    produto.setMarca(rs.getString("marca"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
        }

        return produto;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir produto: " + e.getMessage());
            return false;
        }
    }

    public boolean gravar(Produto produto) {
        if (produto.getId() > 0) {
            // Atualizar
            String sql = "UPDATE produto SET nome=?, categoria=?, unidade_medida=?, perecivel=?, marca=? WHERE id_produto=?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, produto.getNome());
                stmt.setString(2, produto.getCategoria());
                stmt.setString(3, produto.getUnidadeMedida());
                stmt.setBoolean(4, produto.isPerecivel());
                stmt.setString(5, produto.getMarca());
                stmt.setInt(6, produto.getId());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar produto: " + e.getMessage());
                return false;
            }

        } else {
            // Inserir
            String sql = "INSERT INTO produto (nome, categoria, unidade_medida, perecivel, marca) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, produto.getNome());
                stmt.setString(2, produto.getCategoria());
                stmt.setString(3, produto.getUnidadeMedida());
                stmt.setBoolean(4, produto.isPerecivel());
                stmt.setString(5, produto.getMarca());

                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            produto.setId(generatedKeys.getInt(1));
                        }
                    }
                    return true;
                }

                return false;

            } catch (SQLException e) {
                System.err.println("Erro ao inserir produto: " + e.getMessage());
                return false;
            }
        }
        
    }
    
}
