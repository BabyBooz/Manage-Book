package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.VoucherDAO;
import com.mycompany.manage_book.model.User;
import com.mycompany.manage_book.model.Voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/vouchers")
public class VoucherListServlet extends HttpServlet {
    private VoucherDAO voucherDAO = new VoucherDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<Voucher> vouchers = voucherDAO.getActiveVouchers();
        request.setAttribute("vouchers", vouchers);
        
        request.getRequestDispatcher("/WEB-INF/views/voucher-list.jsp").forward(request, response);
    }
}
