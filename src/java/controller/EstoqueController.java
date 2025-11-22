package controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Estoque;
import model.EstoqueDAO;
import model.Produto;

@WebServlet(name = "EstoqueController", urlPatterns = {"/gerenciarEstoque"})
public class EstoqueController extends HttpServlet {
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
        String idEstoque = request.getParameter("idEstoque");

        try (PrintWriter out = response.getWriter()) {
            EstoqueDAO edao = new EstoqueDAO();

            if ("listar".equalsIgnoreCase(acao)) {
                List<Estoque> estoques = edao.listar();
                out.print(gson.toJson(estoques));

            } else if ("buscar".equalsIgnoreCase(acao) && idEstoque != null) {
                try {
                    Estoque estoque = edao.buscarPorId(Long.parseLong(idEstoque));
                    if (estoque != null) {
                        out.print(gson.toJson(estoque));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"mensagem\":\"Estoque não encontrado\", \"id\":\"" + idEstoque + "\"}");
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"mensagem\":\"ID inválido\"}");
                }

            } else if ("excluir".equalsIgnoreCase(acao) && idEstoque != null) {
                try {
                    boolean deletado = edao.deletar(Long.parseLong(idEstoque));
                    if (deletado) {
                        out.print("{\"mensagem\":\"Estoque excluído com sucesso\", \"id\":\"" + idEstoque + "\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"mensagem\":\"Falha ao excluir o estoque\", \"id\":\"" + idEstoque + "\"}");
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"mensagem\":\"ID inválido\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"mensagem\":\"Ação inválida ou parâmetros insuficientes\"}");
            }
edao.close();

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
            EstoqueDAO edao = new EstoqueDAO();

            String idEstoque = request.getParameter("idEstoque");
            String idProduto = request.getParameter("idProduto");
            String idPanificadora = request.getParameter("idPanificadora");
            String quantidade = request.getParameter("quantidade");
            String dataValidade = request.getParameter("dataValidade");
            String status = request.getParameter("status");

            // Validações básicas
            if (idProduto == null || idPanificadora == null || quantidade == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"mensagem\":\"Parâmetros obrigatórios faltando\"}");
                return;
            }

            try {
                Estoque estoque = new Estoque();

                if (idEstoque != null && !idEstoque.isEmpty()) {
                    estoque.setIdEstoque(Long.parseLong(idEstoque));
                }

                Produto produto = new Produto();
                produto.setIdProduto((int) Long.parseLong(idProduto));
                estoque.setProduto(produto);

                estoque.setIdPanificadora(Long.parseLong(idPanificadora));
                estoque.setQuantidade(Integer.parseInt(quantidade));
                estoque.setStatus(status != null ? status : "ATIVO");

                if (dataValidade != null && !dataValidade.isEmpty()) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        estoque.setDataValidade(df.parse(dataValidade));
                    } catch (ParseException ex) {
                        System.err.println("Erro ao parsear data: " + ex.getMessage());
                    }
                }

                boolean sucesso = edao.salvar(estoque);
                if (sucesso) {
                    out.print("{\"mensagem\":\"Estoque salvo com sucesso!\",\"id\":\"" + estoque.getId() + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"mensagem\":\"Falha ao salvar o estoque\"}");
                }

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"mensagem\":\"Parâmetros numéricos inválidos\"}");
            }
edao.close();
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"mensagem\":\"Erro no banco de dados: " + e.getMessage() + "\"}");
        }
    }
}
