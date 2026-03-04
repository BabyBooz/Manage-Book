package com.mycompany.manage_book.model;

import java.sql.Timestamp;

public class Wishlist {
    private int wishlistId;
    private int userId;
    private int productId;
    private Timestamp addedAt;
    
    // For joined queries
    private Product product;
    
    public Wishlist() {}
    
    public Wishlist(int wishlistId, int userId, int productId) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productId = productId;
    }
    
    // Getters and Setters
    public int getWishlistId() { return wishlistId; }
    public void setWishlistId(int wishlistId) { this.wishlistId = wishlistId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
