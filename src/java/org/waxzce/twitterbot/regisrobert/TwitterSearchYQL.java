/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waxzce.twitterbot.regisrobert;

//import com.simpleyql.*;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

/**
 *
 * @author waxzce
 */
public class TwitterSearchYQL extends HttpServlet {

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
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();


        PersistenceManager pm = PMF.get().getPersistenceManager();
        javax.jdo.Query dsquery = pm.newQuery(SimpleData.class,
                "realkey == realkeyParam");
        dsquery.declareParameters("String realkeyParam");

        List<SimpleData> results = (List<SimpleData>) dsquery.execute("maxtwitterid");
        Long lastid = null;
        URL url = null;
        if (!results.isEmpty()) {
            lastid = Long.getLong(results.get(0).getData());
            url = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20twitter.search%20where%20q%3D'chats'%20and%20lang%3D'fr'%20and%20rpp%3D'60'%20and%20since_id%3D%22" + results.get(0).getData() + "%22%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");

        } else {
            url = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20twitter.search%20where%20q%3D'chats'%20and%20lang%3D'fr'%20and%20rpp%3D'60'%3B&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");

        }


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            String full = "";
            while ((line = in.readLine()) != null) {
                full = full + line;
            }
            //out.print(full);

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(full);

            JsonArray yqlresults = je.getAsJsonObject().get("query").getAsJsonObject().get("results").getAsJsonObject().get("results").getAsJsonArray();




            if (yqlresults.size() > 0) {
                Logger.getAnonymousLogger().info("chats tweets : " + yqlresults.size());
                if (!results.isEmpty()) {
                    lastid = yqlresults.get(0).getAsJsonObject().get("id").getAsLong();
                    out.print(lastid);
                    results.get(0).setData(lastid.toString());
                    pm.makePersistent(results.get(0));
                    pm.close();

                    for (Iterator<JsonElement> it = yqlresults.iterator(); it.hasNext();) {
                        JsonObject tweet = it.next().getAsJsonObject();
                        if (!tweet.get("from_user").getAsString().equals("RegisRobertMan")) {
                            out.print(tweet.get("from_user") + " | ");
                            Queue queue = QueueFactory.getQueue("twitterpost");
                            queue.add(url("/TwitterPost").param("twitterusername", tweet.get("from_user").getAsString()).param("tid", tweet.get("id").getAsString()).method(TaskOptions.Method.GET));
                            //  twitter.updateStatus("@" + ""  + " Les chats c'est des connards !!");
                        }
                    }
                } else {
                    lastid = yqlresults.get(0).getAsJsonObject().get("id").getAsLong();
                    SimpleData sd = new SimpleData();
                    sd.setRealkey("maxtwitterid");
                    sd.setData(lastid.toString());
                    out.print(lastid);
                    try {
                        pm.makePersistent(sd);
                    } finally {
                        pm.close();
                    }
                    for (Iterator<JsonElement> it = yqlresults.iterator(); it.hasNext();) {
                        JsonObject tweet = it.next().getAsJsonObject();
                        if (!tweet.get("from_user").getAsString().equals("RegisRobertMan")) {
                            out.print(tweet.get("from_user") + " | ");
                            //       Queue queue = QueueFactory.getQueue("twitterpost");
                            //     queue.add(url("/TwitterPost").param("twitterusername", tweet.getFromUser()).method(TaskOptions.Method.GET));
                            //  twitter.updateStatus("@" + ""  + " Les chats c'est des connards !!");
                        }
                    }
                }

            } else {
                Logger.getAnonymousLogger().info("no chats tweets");
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
