package controller;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Panificadora;
import model.PanificadoraDAO;

@WebServlet(name = "PanificadoraController", urlPatterns = {"/GerenciarPanificadora/*"})
public class PanificadoraController extends HttpServlet {

    private final Gson gson = new Gson();

    // === MÉTODO CORS PADRÃO ===
    private void aplicarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    // === OPTIONS (Preflight) ===
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aplicarCORS(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // === LISTAR ===
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            PanificadoraDAO dao = new PanificadoraDAO();
            List<Panificadora> lista = dao.listar();
            out.print(gson.toJson(lista));
                        dao.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
    }

    // === INSERIR NOVA FILIAL ===
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            Panificadora nova = gson.fromJson(reader, Panificadora.class);
            PanificadoraDAO dao = new PanificadoraDAO();

            if (dao.inserir(nova)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(nova));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"erro\":\"Falha ao adicionar panificadora.\"}");
            }
                        dao.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // === ATUALIZAR OU DESATIVAR ===
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            Panificadora p = gson.fromJson(reader, Panificadora.class);
            PanificadoraDAO dao = new PanificadoraDAO();

            if (p.getNome() == null && p.getCnpj() == null && p.getEndereco() == null && p.getTelefone() == null) {
                boolean novoStatus = p.isDesativado();
                if (dao.alterarStatus(p.getIdPanificadora(), novoStatus)) {
                    out.print("{\"sucesso\":true,\"statusAlterado\":" + novoStatus + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"erro\":\"Panificadora não encontrada.\"}");
                }
                return;
            }

            if (dao.atualizar(p)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"sucesso\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"erro\":\"Panificadora não encontrada.\"}");
            }
            dao.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // === EXCLUIR ===
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("idPanificadora");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetro idPanificadora ausente.\"}");
                return;
            }

            long id = Long.parseLong(idParam);
            PanificadoraDAO dao = new PanificadoraDAO();

            if (dao.deletar(id)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"sucesso\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"erro\":\"Panificadora não encontrada.\"}");
            }
            dao.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
