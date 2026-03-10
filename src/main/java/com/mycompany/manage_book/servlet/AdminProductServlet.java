package com.mycompany.manage_book.servlet;

import com.mycompany.manage_book.dao.CategoryDAO;
import com.mycompany.manage_book.dao.ProductDAO;
import com.mycompany.manage_book.model.Category;
import com.mycompany.manage_book.model.Product;
import com.mycompany.manage_book.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/products")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class AdminProductServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền admin
        if (!checkAdmin(request, response)) return;
        
        List<Product> products = productDAO.getAllProducts();
        List<Category> categories = categoryDAO.getAllCategories();
        
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        
        request.getRequestDispatcher("/WEB-INF/views/admin-products.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền admin
        if (!checkAdmin(request, response)) return;
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            handleAdd(request, response);
        } else if ("edit".equals(action)) {
            handleEdit(request, response);
        } else if ("delete".equals(action)) {
            handleDelete(request, response);
        }
    }
    
    private void handleAdd(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        try {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            
            // Xử lý upload ảnh
            String imageUrl = uploadImage(request);
            
            boolean success = productDAO.addProduct(categoryId, name, description, price, imageUrl, stock);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=add");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=add");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/products?error=add");
        }
    }
    
    private void handleEdit(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            
            // Xử lý upload ảnh mới (nếu có)
            String imageUrl = uploadImage(request);
            
            // Nếu không upload ảnh mới, giữ ảnh cũ
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = request.getParameter("oldImageUrl");
            }
            
            boolean success = productDAO.updateProduct(productId, categoryId, name, description, price, imageUrl, stock);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=edit");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=edit");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/products?error=edit");
        }
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            boolean success = productDAO.deleteProduct(productId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=delete");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=delete");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/products?error=delete");
        }
    }
    
    private boolean checkAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        if (user.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/home");
            return false;
        }
        
        return true;
    }
    
    private String uploadImage(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("imageFile");
        
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        
        // Kiểm tra định dạng file
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex).toLowerCase();
        }
        
        if (!fileExtension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            return null;
        }
        
        // Tạo tên file unique
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Lấy đường dẫn thực tế của thư mục images
        String uploadPath = getServletContext().getRealPath("") + File.separator + "images";
        
        // Tạo thư mục nếu chưa tồn tại
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Lưu file
        String filePath = uploadPath + File.separator + uniqueFileName;
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Trả về đường dẫn tương đối để lưu vào database
        return "images/" + uniqueFileName;
    }
}
