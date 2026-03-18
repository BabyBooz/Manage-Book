package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.CategoryDAO;
import com.mycompany.manage_book.dao.ProductDAO;
import com.mycompany.manage_book.dao.VoucherDAO;
import com.mycompany.manage_book.model.Category;
import com.mycompany.manage_book.model.Product;
import com.mycompany.manage_book.model.Voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductListServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private VoucherDAO voucherDAO = new VoucherDAO();
    private static final int PAGE_SIZE = 12;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy parameters
        String categoryIdStr = request.getParameter("categoryId");
        String pageStr = request.getParameter("page");
        
        int page = 1;
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // Lấy danh sách categories
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        
        // Lấy sản phẩm theo category hoặc tất cả
        List<Product> products;
        int totalProducts;
        Integer selectedCategoryId = null;
        
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                selectedCategoryId = Integer.parseInt(categoryIdStr);
                products = productDAO.getProductsByCategory(selectedCategoryId, page, PAGE_SIZE);
                totalProducts = productDAO.getTotalProductsByCategory(selectedCategoryId);
            } catch (NumberFormatException e) {
                products = productDAO.getProducts(page, PAGE_SIZE);
                totalProducts = productDAO.getTotalProducts();
            }
        } else {
            products = productDAO.getProducts(page, PAGE_SIZE);
            totalProducts = productDAO.getTotalProducts();
        }
        
        // Tính số trang
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        
        // Lấy danh sách vouchers đang active
        List<Voucher> vouchers = voucherDAO.getActiveVouchers();
        
        // Set attributes
        request.setAttribute("products", products);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("selectedCategoryId", selectedCategoryId);
        request.setAttribute("vouchers", vouchers);
        
        request.getRequestDispatcher("/WEB-INF/views/product-list.jsp").forward(request, response);
    }
}
