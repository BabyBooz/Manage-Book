package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.WishlistDAO;
import com.mycompany.manage_book.model.User;
import com.mycompany.manage_book.model.Wishlist;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    private WishlistDAO wishlistDAO = new WishlistDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<Wishlist> wishlists = wishlistDAO.getWishlistByUser(user.getUserId());
        request.setAttribute("wishlists", wishlists);
        
        request.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            handleAdd(request, response, user);
        } else if ("remove".equals(action)) {
            handleRemove(request, response);
        }
    }
    
    private void handleAdd(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            boolean success = wishlistDAO.addToWishlist(user.getUserId(), productId);
            
            String referer = request.getHeader("Referer");
            if (referer != null) {
                response.sendRedirect(referer + (success ? "?wishlist=added" : "?wishlist=exists"));
            } else {
                response.sendRedirect(request.getContextPath() + "/wishlist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/wishlist?error=add");
        }
    }
    
    private void handleRemove(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
            wishlistDAO.removeFromWishlist(wishlistId);
            
            response.sendRedirect(request.getContextPath() + "/wishlist?success=remove");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/wishlist?error=remove");
        }
    }
}
