/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waxzce.twitterbot.regisrobert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author waxzce
 */
public class TwitterPost extends HttpServlet {

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
            if (request.getQueryString() != null) {
                if (request.getQueryString().startsWith("twitterusername")) {
                    String username = request.getQueryString().substring("twitterusername=".length(), request.getQueryString().indexOf("&"));
                    Long tid = Long.parseLong(request.getParameter("tid"));
                    out.write(String.valueOf(tid));
                    PersistenceManager pm = PMF.get().getPersistenceManager();
                    Query query = pm.newQuery(DejaTwitte.class,
                            "username == usernameParam");
                    query.declareParameters("String usernameParam");

                    List<DejaTwitte> results = (List<DejaTwitte>) query.execute(username);

                    List<String> listexclude = new ArrayList<String>();
                    listexclude.add("wamiz");

                    if (results.isEmpty() && !listexclude.contains(username)) {

                        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                        configurationBuilder.setOAuthConsumerKey("---put here ---");
                        configurationBuilder.setOAuthConsumerSecret("---put here ---");
                        configurationBuilder.setOAuthAccessToken("----put here ---");
                        configurationBuilder.setOAuthAccessTokenSecret("---put here ---");
                        Configuration configuration = configurationBuilder.build();
                        Twitter twitter = new TwitterFactory(configuration).getInstance();

                        
                        twitter.getOAuthAccessToken();
                        twitter.updateStatus("@" + username + " Les chats c'est des connards !!", tid);

                        DejaTwitte dt = new DejaTwitte();
                        dt.setDate(new Date());
                        dt.setUsername(username);
                        // PersistenceManager pm = PMF.get().getPersistenceManager();
                        try {
                            pm.makePersistent(dt);
                        } finally {
                            pm.close();
                        }
                        out.write("tw " + username + " | tid : " + tid);

                    } else {
                        out.write("déjà tw " + username);
                    }
                }
            }
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterPost.class.getName()).log(Level.SEVERE, null, ex);
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
