package com.mycompany.manage_book.dao;

import com.mycompany.manage_book.model.Order;
import com.mycompany.manage_book.model.OrderItem;
import com.mycompany.manage_book.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    // Tạo đơn hàng mới (transaction)
    public int createOrder(int userId, Integer voucherId, String receiverName, String receiverPhone, 
                          String shippingAddress, BigDecimal totalAmount, List<OrderItem> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Tạo order
            String orderSql = "INSERT INTO Orders (user_id, voucher_id, receiver_name, receiver_phone, shipping_address, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement orderPs = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, userId);
            if (voucherId != null) {
                orderPs.setInt(2, voucherId);
            } else {
                orderPs.setNull(2, Types.INTEGER);
            }
            orderPs.setString(3, receiverName);
            orderPs.setString(4, receiverPhone);
            orderPs.setString(5, shippingAddress);
            orderPs.setBigDecimal(6, totalAmount);
            orderPs.executeUpdate();
            
            ResultSet rs = orderPs.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            
            // 2. Thêm order items và trừ tồn kho
            String itemSql = "INSERT INTO OrderItems (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE Products SET stock = stock - ? WHERE product_id = ?";
            
            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            PreparedStatement stockPs = conn.prepareStatement(updateStockSql);
            
            for (OrderItem item : items) {
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, item.getProductId());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setBigDecimal(4, item.getPrice());
                itemPs.executeUpdate();
                
                stockPs.setInt(1, item.getQuantity());
                stockPs.setInt(2, item.getProductId());
                stockPs.executeUpdate();
            }
            
            // 3. Giảm usage_limit của voucher (nếu có)
            if (voucherId != null) {
                String voucherSql = "UPDATE Vouchers SET usage_limit = usage_limit - 1 WHERE voucher_id = ?";
                PreparedStatement voucherPs = conn.prepareStatement(voucherSql);
                voucherPs.setInt(1, voucherId);
                voucherPs.executeUpdate();
            }
            
            conn.commit();
            return orderId;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return 0;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Lấy đơn hàng theo user
    public List<Order> getOrdersByUser(int userId) {
        String sql = "SELECT o.*, v.code as voucher_code FROM Orders o " +
                    "LEFT JOIN Vouchers v ON o.voucher_id = v.voucher_id " +
                    "WHERE o.user_id = ? ORDER BY o.created_at DESC";
        
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Lấy tất cả đơn hàng (admin)
    public List<Order> getAllOrders() {
        String sql = "SELECT o.*, v.code as voucher_code, u.username FROM Orders o " +
                    "LEFT JOIN Vouchers v ON o.voucher_id = v.voucher_id " +
                    "LEFT JOIN Users u ON o.user_id = u.user_id " +
                    "ORDER BY o.created_at DESC";
        
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setUsername(rs.getString("username"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Lấy chi tiết đơn hàng
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, v.code as voucher_code FROM Orders o " +
                    "LEFT JOIN Vouchers v ON o.voucher_id = v.voucher_id " +
                    "WHERE o.order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Lấy order items
    public List<OrderItem> getOrderItems(int orderId) {
        String sql = "SELECT oi.*, p.name as product_name, p.image_url FROM OrderItems oi " +
                    "JOIN Products p ON oi.product_id = p.product_id " +
                    "WHERE oi.order_id = ?";
        
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setReviewed(rs.getBoolean("is_reviewed"));
                item.setProductName(rs.getString("product_name"));
                item.setProductImageUrl(rs.getString("image_url"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Cập nhật trạng thái đơn hàng
    public boolean updateOrderStatus(int orderId, int status) {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Hủy đơn hàng (chỉ khi status = 0)
    public boolean cancelOrder(int orderId, int userId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Kiểm tra đơn hàng có thuộc user và đang chờ duyệt không
            String checkSql = "SELECT status FROM Orders WHERE order_id = ? AND user_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, orderId);
            checkPs.setInt(2, userId);
            ResultSet rs = checkPs.executeQuery();
            
            if (!rs.next() || rs.getInt("status") != 0) {
                conn.rollback();
                return false;
            }
            
            // Cập nhật trạng thái đơn hàng
            String updateSql = "UPDATE Orders SET status = 3 WHERE order_id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, orderId);
            updatePs.executeUpdate();
            
            // Hoàn lại tồn kho
            String itemsSql = "SELECT product_id, quantity FROM OrderItems WHERE order_id = ?";
            PreparedStatement itemsPs = conn.prepareStatement(itemsSql);
            itemsPs.setInt(1, orderId);
            ResultSet itemsRs = itemsPs.executeQuery();
            
            String restockSql = "UPDATE Products SET stock = stock + ? WHERE product_id = ?";
            PreparedStatement restockPs = conn.prepareStatement(restockSql);
            
            while (itemsRs.next()) {
                restockPs.setInt(1, itemsRs.getInt("quantity"));
                restockPs.setInt(2, itemsRs.getInt("product_id"));
                restockPs.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        
        int voucherId = rs.getInt("voucher_id");
        if (!rs.wasNull()) {
            order.setVoucherId(voucherId);
            order.setVoucherCode(rs.getString("voucher_code"));
        }
        
        order.setReceiverName(rs.getString("receiver_name"));
        order.setReceiverPhone(rs.getString("receiver_phone"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getInt("status"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }
}
