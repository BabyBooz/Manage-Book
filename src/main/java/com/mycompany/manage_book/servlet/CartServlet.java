package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.CartDAO;
import com.mycompany.manage_book.model.Cart;
import com.mycompany.manage_book.model.CartItem;
import com.mycompany.manage_book.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDAO = new CartDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Cart cart = cartDAO.getOrCreateCart(user.getUserId());
        List<CartItem> cartItems = cartDAO.getCartItems(cart.getCartId());
        
        // Tính tổng tiền
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("cart", cart);
        
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
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
        } else if ("update".equals(action)) {
            handleUpdate(request, response);
        } else if ("remove".equals(action)) {
            handleRemove(request, response);
        }
    }
    
    private void handleAdd(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            Cart cart = cartDAO.getOrCreateCart(user.getUserId());
            boolean success = cartDAO.addToCart(cart.getCartId(), productId, quantity);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/cart?success=add");
            } else {
                response.sendRedirect(request.getContextPath() + "/cart?error=add");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart?error=add");
        }
    }
    
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            if (quantity > 0) {
                cartDAO.updateQuantity(cartItemId, quantity);
            }
            
            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart?error=update");
        }
    }
    
    private void handleRemove(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
            cartDAO.removeItem(cartItemId);
            
            response.sendRedirect(request.getContextPath() + "/cart?success=remove");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart?error=remove");
        }
    }
}
