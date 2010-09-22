/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waxzce.twitterbot.regisrobert;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
//import com.simpleyql.*;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author waxzce
 */
public class TwitterSearch extends HttpServlet {

    //   private static Long lastid = null;
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
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey("---put here ---");
            configurationBuilder.setOAuthConsumerSecret("---put here ---");
            Configuration configuration = configurationBuilder.build();
            Twitter twitter = new TwitterFactory().getInstance("---put here ---", "---put here ---");

        

            //since_id

            Query q = new Query("chats");
            q.setLang("fr");
            q.setRpp(60);

            PersistenceManager pm = PMF.get().getPersistenceManager();
            javax.jdo.Query dsquery = pm.newQuery(SimpleData.class,
                    "realkey == realkeyParam");
            dsquery.declareParameters("String realkeyParam");

            List<SimpleData> results = (List<SimpleData>) dsquery.execute("maxtwitterid");
            Long lastid = null;
            if (!results.isEmpty()) {
                lastid = Long.getLong(results.get(0).getData());
            }


            if (lastid != null) {
                q.setSinceId(lastid);
                //     RateLimitStatus rls = twitter.rateLimitStatus();

                QueryResult qr = twitter.search(q);
                lastid = qr.getMaxId();
                results.get(0).setData(lastid.toString());
                pm.makePersistent(results.get(0));
                pm.close();

                List<Tweet> lt = qr.getTweets();
                for (Iterator<Tweet> it = lt.iterator(); it.hasNext();) {
                    Tweet tweet = it.next();
                    if (!tweet.getFromUser().equals("RegisRobertMan")) {
                        Queue queue = QueueFactory.getQueue("twitterpost");

                        queue.add(url("/TwitterPost").param("twitterusername", tweet.getFromUser()).method(TaskOptions.Method.GET));
                        //  twitter.updateStatus("@" + ""  + " Les chats c'est des connards !!");
                    }
                }
            } else {
                QueryResult qr = twitter.search(q);
                lastid = qr.getMaxId();
                SimpleData sd = new SimpleData();
                sd.setRealkey("maxtwitterid");
                sd.setData(lastid.toString());
                try {
                    pm.makePersistent(sd);
                } finally {
                    pm.close();
                }
            }


        } catch (TwitterException ex) {
            Logger.getLogger(TwitterSearch.class.getName()).log(Level.SEVERE, null, ex);
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
