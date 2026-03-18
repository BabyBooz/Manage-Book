package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.UserDAO;
import com.mycompany.manage_book.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy thông tin user mới nhất từ database
        User updatedUser = userDAO.getUserById(user.getUserId());
        session.setAttribute("user", updatedUser);
        
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
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
        
        if ("update_profile".equals(action)) {
            handleUpdateProfile(request, response, user, session);
        } else if ("change_password".equals(action)) {
            handleChangePassword(request, response, user);
        }
    }
    
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User user, HttpSession session) 
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        
        // Kiểm tra email đã tồn tại (trừ email của chính user)
        if (!email.equals(user.getEmail()) && userDAO.isEmailExists(email)) {
            request.setAttribute("error", "Email đã được sử dụng bởi tài khoản khác!");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }
        
        boolean success = userDAO.updateProfile(user.getUserId(), fullName, email);
        
        if (success) {
            User updatedUser = userDAO.getUserById(user.getUserId());
            session.setAttribute("user", updatedUser);
            request.setAttribute("success", "Cập nhật thông tin thành công!");
        } else {
            request.setAttribute("error", "Cập nhật thông tin thất bại!");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
    
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Kiểm tra mật khẩu hiện tại
        if (!currentPassword.equals(user.getPassword())) {
            request.setAttribute("passwordError", "Mật khẩu hiện tại không đúng!");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu mới khớp
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("passwordError", "Mật khẩu mới không khớp!");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }
        
        boolean success = userDAO.changePassword(user.getUserId(), newPassword);
        
        if (success) {
            request.setAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        } else {
            request.setAttribute("passwordError", "Đổi mật khẩu thất bại!");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}
