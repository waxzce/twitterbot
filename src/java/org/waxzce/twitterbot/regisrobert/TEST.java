/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waxzce.twitterbot.regisrobert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author waxzce
 */
public class TEST extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            javax.jdo.Query dsquery = pm.newQuery(SimpleData.class,
                    "realkey == realkeyParam");
            dsquery.declareParameters("String realkeyParam");

            List<SimpleData> results = (List<SimpleData>) dsquery.execute("test");
            Long lastid = null;
            if (!results.isEmpty()) {
                out.write(results.get(0).getData());
                if (request.getQueryString() != null) {
                    results.get(0).setData(request.getQueryString());
                    out.write(" | updated = " + request.getQueryString());
                    pm.makePersistent(results.get(0));
                    pm.close();
                }
            } else {
                SimpleData sd = new SimpleData();
                sd.setRealkey("test");
                sd.setData("sqdqsdfsdf");
                try {
                    pm.makePersistent(sd);
                } finally {
                    pm.close();
                }
            }


        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
