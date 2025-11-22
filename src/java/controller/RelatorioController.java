package controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Relatorio;
import model.RelatorioDAO;

@WebServlet(name = "RelatorioController", urlPatterns = {"/gerenciarRelatorio"})
public class RelatorioController extends HttpServlet {

    private final Gson gson = new Gson();
  
 private void aplicarCORS(HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    response.setHeader("Access-Control-Allow-Credentials", "true");
}

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                aplicarCORS(response);

        response.setContentType("application/json; charset=UTF-8");

        String acao = request.getParameter("acao");
        String idRelatorio = request.getParameter("idRelatorio");

        try (PrintWriter out = response.getWriter()) {
            RelatorioDAO rdao = new RelatorioDAO();

            if ("listar".equalsIgnoreCase(acao)) {
                List<Relatorio> relatorios = rdao.listar();
                out.print(gson.toJson(relatorios));

            } else if ("buscar".equalsIgnoreCase(acao) && idRelatorio != null) {
                Relatorio relatorio = rdao.buscarPorId(Long.parseLong(idRelatorio));
                if (relatorio != null) {
                    out.print(gson.toJson(relatorio));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"erro\": \"Relatório não encontrado.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\": \"Ação inválida ou parâmetros ausentes.\"}");
            }
rdao.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                aplicarCORS(response);

        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            Long idPanificadora = Long.parseLong(request.getParameter("idPanificadora"));
            Long idProduto = Long.parseLong(request.getParameter("idProduto"));
            Double percentual = request.getParameter("percentual") != null && !request.getParameter("percentual").isEmpty()
                    ? Double.parseDouble(request.getParameter("percentual"))
                    : null;
            String causa = request.getParameter("causa");

            Date periodo = null;
            if (request.getParameter("periodo") != null && !request.getParameter("periodo").isEmpty()) {
                periodo = sdf.parse(request.getParameter("periodo"));
            }

            Relatorio relatorio = new Relatorio(idPanificadora, idProduto, percentual, causa, periodo);
            RelatorioDAO rdao = new RelatorioDAO();

            if (rdao.inserir(relatorio)) {
                out.print(gson.toJson(relatorio));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"erro\": \"Falha ao inserir relatório.\"}");
            }
rdao.close();

        } catch (SQLException | NumberFormatException | ParseException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                aplicarCORS(response);
        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            Long idRelatorio = Long.parseLong(request.getParameter("idRelatorio"));
            Long idPanificadora = Long.parseLong(request.getParameter("idPanificadora"));
            Long idProduto = Long.parseLong(request.getParameter("idProduto"));
            Double percentual = request.getParameter("percentual") != null && !request.getParameter("percentual").isEmpty()
                    ? Double.parseDouble(request.getParameter("percentual"))
                    : null;
            String causa = request.getParameter("causa");

            Date periodo = null;
            if (request.getParameter("periodo") != null && !request.getParameter("periodo").isEmpty()) {
                periodo = sdf.parse(request.getParameter("periodo"));
            }

            Relatorio relatorio = new Relatorio(idPanificadora, idProduto, percentual, causa, periodo);
            relatorio.setIdRelatorio(idRelatorio);

            RelatorioDAO rdao = new RelatorioDAO();

            if (rdao.atualizar(relatorio)) {
                out.print("{\"sucesso\": true}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"erro\": \"Falha ao atualizar relatório.\"}");
            }
rdao.close();

        } catch (SQLException | NumberFormatException | ParseException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
@Override
protected void doOptions(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    aplicarCORS(response);
    response.setStatus(HttpServletResponse.SC_OK);
}

@Override
protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    aplicarCORS(response);

    response.setContentType("application/json; charset=UTF-8");

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
        return;
    }

    try (PrintWriter out = response.getWriter()) {

        String idParam = request.getParameter("idRelatorio");
        if (idParam == null || idParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"Parâmetro idRelatorio ausente.\"}");
            return;
        }

        long idRelatorio = Long.parseLong(idParam);
        RelatorioDAO rdao = new RelatorioDAO();

        if (rdao.deletar(idRelatorio)) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"sucesso\": true}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"erro\": \"Relatório não encontrado.\"}");
        }

        rdao.close(); // mantém fechamento manual

    } catch (SQLException | NumberFormatException e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}

}
