/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waxzce.twitterbot.regisrobert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author waxzce
 */
public class FlushDataStore extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs·
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            PersistenceManager pm = PMF.get().getPersistenceManager();
            Query query = pm.newQuery(DejaTwitte.class);
                    //,
                    //"date < dateParam");
//            query.declareParameters("Date dateParam");

  //          Date d = new Date();
            /*
            if (d.getDate() > 28) {
            d.setDate(26);
            } else {
            d.setDate((new Date()).getDate() - 2);
            }
             */
            List<DejaTwitte> results = (List<DejaTwitte>) query.execute();
        /*    for (Iterator<DejaTwitte> it = results.iterator(); it.hasNext();) {
                DejaTwitte dejaTwitte = it.next();
                out.write(dejaTwitte.getUsername());
            }
*/
            
            pm.deletePersistentAll(results);
             
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
