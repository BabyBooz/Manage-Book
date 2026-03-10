package com.mycompany.manage_book.dao;

import com.mycompany.manage_book.model.Product;
import com.mycompany.manage_book.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    // Lấy tất cả sản phẩm với phân trang
    public List<Product> getProducts(int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT p.*, c.name as category_name FROM Products p " +
                     "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                     "ORDER BY p.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Lấy sản phẩm theo category với phân trang
    public List<Product> getProductsByCategory(int categoryId, int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT p.*, c.name as category_name FROM Products p " +
                     "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                     "WHERE p.category_id = ? " +
                     "ORDER BY p.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Đếm tổng số sản phẩm
    public int getTotalProducts() {
        String sql = "SELECT COUNT(*) FROM Products";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Đếm số sản phẩm theo category
    public int getTotalProductsByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE category_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Lấy chi tiết sản phẩm
    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.name as category_name FROM Products p " +
                     "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                     "WHERE p.product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Thêm sản phẩm mới
    public boolean addProduct(int categoryId, String name, String description, 
                             BigDecimal price, String imageUrl, int stock) {
        String sql = "INSERT INTO Products (category_id, name, description, price, image_url, stock) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setBigDecimal(4, price);
            ps.setString(5, imageUrl);
            ps.setInt(6, stock);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cập nhật sản phẩm
    public boolean updateProduct(int productId, int categoryId, String name, String description,
                                BigDecimal price, String imageUrl, int stock) {
        String sql = "UPDATE Products SET category_id = ?, name = ?, description = ?, " +
                     "price = ?, image_url = ?, stock = ? WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setBigDecimal(4, price);
            ps.setString(5, imageUrl);
            ps.setInt(6, stock);
            ps.setInt(7, productId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Xóa sản phẩm
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy tất cả sản phẩm (cho admin, không phân trang)
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM Products p " +
                     "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                     "ORDER BY p.product_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // Helper method để extract Product từ ResultSet
    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setImageUrl(rs.getString("image_url"));
        product.setStock(rs.getInt("stock"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setCategoryName(rs.getString("category_name"));
        return product;
    }
}
