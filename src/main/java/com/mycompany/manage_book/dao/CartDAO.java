package com.mycompany.manage_book.dao;

import com.mycompany.manage_book.model.Cart;
import com.mycompany.manage_book.model.CartItem;
import com.mycompany.manage_book.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    
    // Lấy hoặc tạo giỏ hàng cho user
    public Cart getOrCreateCart(int userId) {
        String sql = "SELECT cart_id, user_id, updated_at FROM Carts WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Cart cart = new Cart();
                cart.setCartId(rs.getInt("cart_id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setUpdatedAt(rs.getTimestamp("updated_at"));
                return cart;
            } else {
                // Tạo giỏ hàng mới
                return createCart(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Cart createCart(int userId) {
        String sql = "INSERT INTO Carts (user_id) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Cart cart = new Cart();
                cart.setCartId(rs.getInt(1));
                cart.setUserId(userId);
                return cart;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy tất cả items trong giỏ hàng
    public List<CartItem> getCartItems(int cartId) {
        String sql = "SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.quantity, " +
                    "p.name, p.price, p.image_url, p.stock " +
                    "FROM CartItems ci " +
                    "JOIN Products p ON ci.product_id = p.product_id " +
                    "WHERE ci.cart_id = ?";
        
        List<CartItem> items = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setCartId(rs.getInt("cart_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setProductName(rs.getString("name"));
                item.setProductPrice(rs.getBigDecimal("price"));
                item.setProductImageUrl(rs.getString("image_url"));
                item.setProductStock(rs.getInt("stock"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Thêm sản phẩm vào giỏ hàng
    public boolean addToCart(int cartId, int productId, int quantity) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        String checkSql = "SELECT cart_item_id, quantity FROM CartItems WHERE cart_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO CartItems (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE CartItems SET quantity = quantity + ? WHERE cart_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, cartId);
            checkPs.setInt(2, productId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                // Sản phẩm đã có, cập nhật số lượng
                int cartItemId = rs.getInt("cart_item_id");
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, quantity);
                updatePs.setInt(2, cartItemId);
                return updatePs.executeUpdate() > 0;
            } else {
                // Thêm mới
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                insertPs.setInt(1, cartId);
                insertPs.setInt(2, productId);
                insertPs.setInt(3, quantity);
                return insertPs.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật số lượng
    public boolean updateQuantity(int cartItemId, int quantity) {
        String sql = "UPDATE CartItems SET quantity = ? WHERE cart_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa item khỏi giỏ hàng
    public boolean removeItem(int cartItemId) {
        String sql = "DELETE FROM CartItems WHERE cart_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa tất cả items trong giỏ hàng
    public boolean clearCart(int cartId) {
        String sql = "DELETE FROM CartItems WHERE cart_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Đếm số lượng items trong giỏ hàng
    public int getCartItemCount(int cartId) {
        String sql = "SELECT COUNT(*) as count FROM CartItems WHERE cart_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartId);
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
