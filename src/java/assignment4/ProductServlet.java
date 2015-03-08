/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package assignment4;

import com.mysql.jdbc.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;

/**
 *
 * @author c0643680
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/product";
            conn = (Connection) DriverManager.getConnection(jdbc, "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    private String getResults(String query, String... params) {
        String result = new String();
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            JSONArray productArr = new JSONArray();
            while (rs.next()) {
                Map productMap = new LinkedHashMap();
                productMap.put("productID", rs.getInt("productID"));
                productMap.put("name", rs.getString("name"));
                productMap.put("description", rs.getString("description"));
                productMap.put("quantity", rs.getInt("quantity"));
                productArr.add(productMap);
            }
            result = productArr.toString();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.replace("},", "},\n");
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                out.println(getResults("SELECT * FROM productdetails"));
            } else {
                int productID = Integer.parseInt(request.getParameter("productID"));
                out.println(getResults("SELECT * FROM productdetails WHERE productID = ?", String.valueOf(productID)));
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `productdetails`(`productID`, `name`, `description`, `quantity`) "
                        + "VALUES (null, '" + request.getParameter("name") + "', '"
                        + request.getParameter("description") + "', "
                        + request.getParameter("quantity") + ");"
                );
                try {
                    pstmt.executeUpdate();
                    request.getParameter("productID");
                    doGet(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Data inserted Error");
                }
            } else {
                out.println("Not enough data to input");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("productID") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `product` SET `name`='"
                        + request.getParameter("name") + "',`description`='"
                        + request.getParameter("description")
                        + "',`quantity`=" + request.getParameter("quantity")
                        + " WHERE `productID`=" + request.getParameter("productID"));
                try {
                    pstmt.executeUpdate();
                    doGet(request, response); //shows updated row
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Error putting values.");
                }
            } else {
                out.println("Not enough data to update");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("productID")) {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `productdetails` WHERE `productID`=" + request.getParameter("productID"));
                try {
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                    out.println("Error in deleting the product.");
                }
            } else {
                out.println("Error: in data to delete");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}


