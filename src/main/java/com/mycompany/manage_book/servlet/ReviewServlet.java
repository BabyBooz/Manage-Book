package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.OrderDAO;
import com.mycompany.manage_book.dao.ReviewDAO;
import com.mycompany.manage_book.model.Order;
import com.mycompany.manage_book.model.OrderItem;
import com.mycompany.manage_book.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
    private ReviewDAO reviewDAO = new ReviewDAO();
    private OrderDAO orderDAO = new OrderDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String orderItemIdParam = request.getParameter("orderItemId");
        
        if (orderItemIdParam != null) {
            int orderItemId = Integer.parseInt(orderItemIdParam);
            
            // Lấy thông tin order item để hiển thị form review
            String orderId = request.getParameter("orderId");
            Order order = orderDAO.getOrderById(Integer.parseInt(orderId));
            List<OrderItem> orderItems = orderDAO.getOrderItems(Integer.parseInt(orderId));
            
            // Tìm order item cụ thể
            OrderItem reviewItem = null;
            for (OrderItem item : orderItems) {
                if (item.getOrderItemId() == orderItemId) {
                    reviewItem = item;
                    break;
                }
            }
            
            if (reviewItem != null && order.getUserId() == user.getUserId() && order.getStatus() == 2) {
                request.setAttribute("orderItem", reviewItem);
                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/views/review-form.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/orders");
        }
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
        
        try {
            int orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
            int productId = Integer.parseInt(request.getParameter("productId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");
            
            // Kiểm tra đã review chưa
            if (reviewDAO.hasUserReviewed(user.getUserId(), productId)) {
                response.sendRedirect(request.getContextPath() + "/orders?orderId=" + orderId + "&error=already_reviewed");
                return;
            }
            
            // Thêm review
            boolean success = reviewDAO.addReview(user.getUserId(), productId, rating, comment);
            
            if (success) {
                // Đánh dấu order item đã review
                reviewDAO.markOrderItemAsReviewed(orderItemId);
                response.sendRedirect(request.getContextPath() + "/orders?orderId=" + orderId + "&success=review");
            } else {
                response.sendRedirect(request.getContextPath() + "/orders?orderId=" + orderId + "&error=review");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/orders?error=review");
        }
    }
}
