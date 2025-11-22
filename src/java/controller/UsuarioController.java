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
import model.Usuario;
import model.UsuarioDAO;
import factory.HashFactory;



@WebServlet(name = "UsuarioController", urlPatterns = {"/GerenciarUsuario"})
public class UsuarioController extends HttpServlet {

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
        String idParam = request.getParameter("idUsuario");

        try (PrintWriter out = response.getWriter()) {
            UsuarioDAO dao = new UsuarioDAO();

            if ("listar".equalsIgnoreCase(acao)) {
                List<Usuario> lista = dao.listarTodos();
                out.print(gson.toJson(lista));

            } else if ("buscar".equalsIgnoreCase(acao) && idParam != null) {
                int id = Integer.parseInt(idParam);
                Usuario u = dao.buscarPorId(id);

                if (u != null) {
                    out.print(gson.toJson(u));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"erro\":\"Usuário não encontrado.\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Ação inválida ou parâmetros ausentes.\"}");
            }
            dao.fecharConexao();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

        UsuarioDAO dao = new UsuarioDAO();

        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }

        String acao = request.getParameter("acao");
        if (acao == null || acao.isEmpty()) {
            try {
                acao = gson.fromJson(jsonBody.toString(), Usuario.class).getPerfil(); // fallback
            } catch (Exception e) {
                // ignora, deixa acao nula
            }
        }

        // 🔹 A partir daqui, podemos reusar o JSON
        Usuario u = gson.fromJson(jsonBody.toString(), Usuario.class);

        // ---------- LOGIN ----------
        if ("login".equalsIgnoreCase(acao)) {
            Usuario usuario = dao.autenticar(u.getEmail(), u.getSenha());

            if (usuario != null) {
                String token = dao.gerarTokenBase64(usuario.getPerfil(), usuario.getIdPanificadora());
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(usuario));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"erro\":\"Email ou senha invalida.\"}");
            }

        // ---------- GERAR TOKEN ----------
        } else if ("gerarToken".equalsIgnoreCase(acao)) {
            if (u == null || u.getPerfil() == null || u.getIdPanificadora() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Perfil e ID da panificadora são obrigatórios.\"}");
                return;
            }

            String token = dao.gerarTokenBase64(u.getPerfil(), u.getIdPanificadora());
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"token\":\"" + token + "\"}");
            return;

        // ---------- CRIAR ----------
        } else if ("criar".equalsIgnoreCase(acao)) {
            Usuario novo = u;

                if (novo.getSenha() != null && !novo.getSenha().isEmpty()) {
                novo.setSenha(HashFactory.hashSenha(novo.getSenha()));
                }

            
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                token = request.getParameter("token");
            }

            if (token != null && !token.isEmpty()) {
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                dao.aplicarTokenBase64(novo, token);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"erro\":\"Token não fornecido.\"}");
                return;
            }

            if (dao.inserir(novo)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                        novo.setSenha(null);
                out.print(gson.toJson(novo));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"erro\":\"Falha ao adicionar usuário.\"}");
            }

        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"Ação inválida ou ausente.\"}");
        }
            dao.fecharConexao();

    } catch (SQLException e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}

    // ---------- PUT ----------
@Override
protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    aplicarCORS(response);
    response.setContentType("application/json; charset=UTF-8");

    String acao = request.getParameter("acao");

    try (BufferedReader reader = request.getReader();
         PrintWriter out = response.getWriter()) {

        UsuarioDAO dao = new UsuarioDAO();
        Usuario atualizado = gson.fromJson(reader, Usuario.class);

        // ---------- ATIVAR USUÁRIO ----------
        if ("ativar".equalsIgnoreCase(acao)) {

            String idParam = request.getParameter("idUsuario");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetro idUsuario ausente.\"}");
                dao.fecharConexao();
                return;
            }

            try {
                int id = Integer.parseInt(idParam);

                boolean ok = dao.ativarUsuario(id);

                if (ok) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"sucesso\":true, \"mensagem\":\"Usuário ativado!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"erro\":\"Usuário não encontrado.\"}");
                }

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"ID inválido.\"}");
            }

            dao.fecharConexao();
            return;
        }

        // ---------- ATUALIZAÇÃO NORMAL ----------
        // Se o ID não veio no JSON, tenta pegar da URL
        String idParam = request.getParameter("idUsuario");
        if ((atualizado == null || atualizado.getIdUsuario() <= 0) && idParam != null) {
            try {
                atualizado.setIdUsuario(Integer.parseInt(idParam));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"ID inválido.\"}");
                dao.fecharConexao();
                return;
            }
        }

        // Verifica novamente se o ID é válido
        if (atualizado == null || atualizado.getIdUsuario() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"ID do usuário ausente ou inválido.\"}");
            dao.fecharConexao();
            return;
        }

        if (dao.atualizar(atualizado)) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"sucesso\":true}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"erro\":\"Usuário não encontrado.\"}");
        }

        dao.fecharConexao();

    } catch (SQLException e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}


    // ---------- DELETE ----------
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        String idParam = request.getParameter("idUsuario");

        try (PrintWriter out = response.getWriter()) {
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetro idUsuario ausente.\"}");
                return;
            }

            int id = Integer.parseInt(idParam);
            UsuarioDAO dao = new UsuarioDAO();

            if (dao.deletar(id)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"sucesso\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"erro\":\"Usuário não encontrado.\"}");
            }
            dao.fecharConexao();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        
    }
    
    
}




