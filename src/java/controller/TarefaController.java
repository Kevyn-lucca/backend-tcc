package controller;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Tarefa;
import model.TarefaDAO;
import model.Usuario;
import model.UsuarioDAO;

@WebServlet(name = "TarefaController", urlPatterns = {"/GerenciarTarefa"})
public class TarefaController extends HttpServlet {

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
    String idUsuarioParam = request.getParameter("idUsuario");
    String idTarefaParam = request.getParameter("idTarefa");

    Gson gson = new Gson();

    try (PrintWriter out = response.getWriter()) {
        TarefaDAO tdao = new TarefaDAO();

        // 🔹 Buscar tarefa específica pelo ID
        if ("buscar".equalsIgnoreCase(acao) && idTarefaParam != null) {
            int idTarefa = Integer.parseInt(idTarefaParam);
            Tarefa tarefa = tdao.buscarPorId(idTarefa);

            if (tarefa != null) {
                out.print(gson.toJson(tarefa));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"erro\":\"Tarefa não encontrada.\"}");
            }
            return;
        }

        // 🔹 Listar tarefas baseadas no ID do usuário
        if ("listar".equalsIgnoreCase(acao)) {
            List<Tarefa> tarefas;

            if (idUsuarioParam != null && !idUsuarioParam.isEmpty()) {
                int idUsuario = Integer.parseInt(idUsuarioParam);

                UsuarioDAO udao = new UsuarioDAO();
                Usuario usuario = udao.buscarPorId(idUsuario);

                if (usuario == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"erro\":\"Usuário não encontrado.\"}");
                    return;
                }

                // Funcionário → só vê suas tarefas
                if ("funcionario".equalsIgnoreCase(usuario.getPerfil())) {
                    tarefas = tdao.listarPorUsuario(idUsuario);

                // Admin/Gerente → vê todas
                } else {
                    tarefas = tdao.listar();
                }
            } else {
                // Se não enviar idUsuario, não retorna nada (mais seguro)
                tarefas = new ArrayList<>();
            }

            out.print(gson.toJson(tarefas));
            return;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.print("{\"erro\":\"Ação inválida.\"}");

    } catch (SQLException e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter()
                .print("{\"erro\": \"Erro no banco de dados: " + e.getMessage() + "\"}");
    }
}

    // ---------- POST ----------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            Tarefa tarefa = gson.fromJson(reader, Tarefa.class);
            TarefaDAO tdao = new TarefaDAO();

            if (tarefa.getDataCriacao() == null) {
                tarefa.setDataCriacao(new Date(System.currentTimeMillis()));
            }

            if (tdao.inserir(tarefa)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(tarefa));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"erro\":\"Falha ao inserir tarefa.\"}");
            }
tdao.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\": \"Erro ao inserir tarefa: " + e.getMessage() + "\"}");
        }
    }

    // ---------- PUT ----------
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            Tarefa tarefa = gson.fromJson(reader, Tarefa.class);
            TarefaDAO tdao = new TarefaDAO();

            if (tdao.atualizar(tarefa)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"sucesso\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"erro\":\"Tarefa não encontrada.\"}");
            }
tdao.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\": \"Erro ao atualizar tarefa: " + e.getMessage() + "\"}");
        }
    }

    // ---------- DELETE ----------
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        String idParam = request.getParameter("idTarefa");

        try (PrintWriter out = response.getWriter()) {
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetro idTarefa ausente.\"}");
                return;
            }

            int idTarefa = Integer.parseInt(idParam);
            TarefaDAO tdao = new TarefaDAO();

            if (tdao.deletar(idTarefa)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"sucesso\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"erro\":\"Tarefa não encontrada.\"}");
            }
tdao.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\": \"Erro ao excluir tarefa: " + e.getMessage() + "\"}");
        }
    }
}
