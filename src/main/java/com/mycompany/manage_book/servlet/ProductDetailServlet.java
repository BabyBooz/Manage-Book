package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.ProductDAO;
import com.mycompany.manage_book.dao.ReviewDAO;
import com.mycompany.manage_book.model.Product;
import com.mycompany.manage_book.model.Review;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String productIdStr = request.getParameter("id");
        
        if (productIdStr == null || productIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }
        
        try {
            int productId = Integer.parseInt(productIdStr);
            Product product = productDAO.getProductById(productId);
            
            if (product == null) {
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }
            
            // Lấy reviews và rating
            List<Review> reviews = reviewDAO.getReviewsByProduct(productId);
            double avgRating = reviewDAO.getAverageRating(productId);
            int reviewCount = reviewDAO.getReviewCount(productId);
            
            request.setAttribute("product", product);
            request.setAttribute("reviews", reviews);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("reviewCount", reviewCount);
            
            request.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}
