package com.mycompany.manage_book.dao;

import com.mycompany.manage_book.model.Review;
import com.mycompany.manage_book.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    
    // Lấy tất cả reviews của một sản phẩm
    public List<Review> getReviewsByProduct(int productId) {
        String sql = "SELECT r.*, u.username, u.full_name FROM Reviews r " +
                    "JOIN Users u ON r.user_id = u.user_id " +
                    "WHERE r.product_id = ? " +
                    "ORDER BY r.created_at DESC";
        
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setProductId(rs.getInt("product_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
                review.setUsername(rs.getString("username"));
                review.setUserFullName(rs.getString("full_name"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reviews;
    }
    
    // Thêm review mới
    public boolean addReview(int userId, int productId, int rating, String comment) {
        String sql = "INSERT INTO Reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Kiểm tra user đã review sản phẩm chưa
    public boolean hasUserReviewed(int userId, int productId) {
        String sql = "SELECT review_id FROM Reviews WHERE user_id = ? AND product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Đánh dấu order item đã được review
    public boolean markOrderItemAsReviewed(int orderItemId) {
        String sql = "UPDATE OrderItems SET is_reviewed = 1 WHERE order_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy rating trung bình của sản phẩm
    public double getAverageRating(int productId) {
        String sql = "SELECT AVG(CAST(rating AS FLOAT)) as avg_rating FROM Reviews WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    // Đếm số lượng reviews của sản phẩm
    public int getReviewCount(int productId) {
        String sql = "SELECT COUNT(*) as count FROM Reviews WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}
