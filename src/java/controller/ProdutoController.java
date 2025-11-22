package controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Produto;
import model.ProdutoDAO;

@WebServlet(name = "ProdutoController", urlPatterns = {"/gerenciarProduto"})
public class ProdutoController extends HttpServlet {

    private final Gson gson = new Gson();

    // ---------- MÉTODO UTILITÁRIO DE CORS ----------
    private void aplicarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    // ---------- OPTIONS (pré-flight) ----------
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aplicarCORS(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // ---------- GET ----------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        String acao = request.getParameter("acao");
        String idProduto = request.getParameter("idProduto");

        try (PrintWriter out = response.getWriter()) {
            ProdutoDAO pdao = new ProdutoDAO();

            if ("listar".equalsIgnoreCase(acao)) {
                List<Produto> produtos = pdao.listarTodos();
                out.print(gson.toJson(produtos));

            } else if ("buscar".equalsIgnoreCase(acao) && idProduto != null) {
                Produto produto = pdao.buscarPorId(Integer.parseInt(idProduto));
                if (produto != null) {
                    out.print(gson.toJson(produto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"mensagem\":\"Produto não encontrado\", \"id\":\"" + idProduto + "\"}");
                }

            } else if ("excluir".equalsIgnoreCase(acao) && idProduto != null) {
                boolean deletado = pdao.excluir(Integer.parseInt(idProduto));
                if (deletado) {
                    out.print("{\"mensagem\":\"Produto excluído com sucesso\", \"id\":\"" + idProduto + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"mensagem\":\"Falha ao excluir o produto\", \"id\":\"" + idProduto + "\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"mensagem\":\"Ação inválida\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"mensagem\":\"Erro no banco de dados: " + e.getMessage() + "\"}");
        }
    }

    // ---------- POST ----------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            ProdutoDAO pdao = new ProdutoDAO();

            String idProduto = request.getParameter("idProduto");
            String nome = request.getParameter("nome");
            String categoria = request.getParameter("categoria");
            String unidadeMedida = request.getParameter("unidadeMedida");
            String perecivel = request.getParameter("perecivel");
            String marca = request.getParameter("marca");

            Produto produto = new Produto();

            if (idProduto != null && !idProduto.isEmpty()) {
                produto.setId(Integer.parseInt(idProduto));
            }

            produto.setNome(nome);
            produto.setCategoria(categoria);
            produto.setUnidadeMedida(unidadeMedida);
            produto.setPerecivel("true".equalsIgnoreCase(perecivel));
            produto.setMarca(marca);

            boolean sucesso = pdao.gravar(produto);

            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"mensagem\":\"Produto salvo com sucesso!\",\"id\":\"" + produto.getId() + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"mensagem\":\"Falha ao salvar o produto\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"mensagem\":\"Erro no banco de dados: " + e.getMessage() + "\"}");
        }
    }
}
