<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - Manage Books</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="header-container">
            <a href="${pageContext.request.contextPath}/home" class="logo"><i class="fas fa-book"></i> Manage Books</a>
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/products"><i class="fas fa-home"></i> Trang chủ</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/wishlist"><i class="fas fa-heart"></i> Yêu thích</a>
                        <a href="${pageContext.request.contextPath}/cart"><i class="fas fa-shopping-cart"></i> Giỏ hàng</a>
                        <a href="${pageContext.request.contextPath}/orders"><i class="fas fa-box"></i> Đơn hàng</a>
                        <c:if test="${sessionScope.user.role == 1}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-user-shield"></i> Admin</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user"></i> ${sessionScope.user.fullName}</a>
                        <a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login"><i class="fas fa-sign-in-alt"></i> Đăng nhập</a>
                        <a href="${pageContext.request.contextPath}/register"><i class="fas fa-user-plus"></i> Đăng ký</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>

    <!-- Main Content -->
    <div class="container">
        <div style="background: white; padding: 30px; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin-top: 20px;">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px;">
                <!-- Product Image -->
                <div>
                    <img src="${product.imageUrl}" alt="${product.name}" 
                         style="width: 100%; border-radius: 4px;"
                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.png'">
                </div>

                <!-- Product Info -->
                <div>
                    <h1 style="color: #81c784; margin-bottom: 15px;">${product.name}</h1>
                    
                    <p style="color: #666; margin-bottom: 10px;">
                        <strong>Danh mục:</strong> ${product.categoryName}
                    </p>
                    
                    <p style="color: #81c784; font-size: 32px; font-weight: bold; margin: 20px 0;">
                        ${product.price} VNĐ
                    </p>
                    
                    <p style="color: ${product.stock > 0 ? '#4caf50' : '#f44336'}; font-size: 16px; margin-bottom: 20px;">
                        <c:choose>
                            <c:when test="${product.stock > 0}">
                                Còn hàng: ${product.stock} sản phẩm
                            </c:when>
                            <c:otherwise>
                                Hết hàng
                            </c:otherwise>
                        </c:choose>
                    </p>
                    
                    <div style="margin-bottom: 30px;">
                        <h3 style="margin-bottom: 10px;">Mô tả sản phẩm:</h3>
                        <p style="color: #666; line-height: 1.6;">
                            ${product.description}
                        </p>
                    </div>
                    
                    <c:if test="${not empty sessionScope.user && product.stock > 0}">
                        <div style="margin-bottom: 20px;">
                            <label style="display: block; margin-bottom: 10px;">Số lượng:</label>
                            <input type="number" id="quantity" value="1" min="1" max="${product.stock}" 
                                   style="width: 100px; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
                        </div>
                        <div style="display: flex; gap: 10px;">
                            <form method="post" action="${pageContext.request.contextPath}/cart" style="flex: 1;">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <input type="hidden" name="quantity" id="cartQuantity" value="1">
                                <button type="submit" class="btn" style="width: 100%;" onclick="document.getElementById('cartQuantity').value = document.getElementById('quantity').value;">
                                    <i class="fas fa-shopping-cart"></i> Thêm vào giỏ hàng
                                </button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/wishlist">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <button type="submit" class="btn btn-secondary">
                                    <i class="fas fa-heart"></i> Yêu thích
                                </button>
                            </form>
                        </div>
                    </c:if>
                    
                    <c:if test="${empty sessionScope.user}">
                        <p style="color: #666; margin-top: 20px;">
                            <a href="${pageContext.request.contextPath}/login" style="color: #81c784;">Đăng nhập</a> để mua hàng
                        </p>
                    </c:if>
                </div>
            </div>

            <!-- Reviews Section -->
            <div style="margin-top: 40px; padding-top: 40px; border-top: 2px solid #f0f0f0;">
                <h2 style="color: #81c784; margin-bottom: 20px;">
                    <i class="fas fa-star"></i> Đánh giá sản phẩm
                    <c:if test="${reviewCount > 0}">
                        <span style="font-size: 18px; color: #666;">
                            (${avgRating} <i class="fas fa-star" style="color: #ffc107; font-size: 16px;"></i> - ${reviewCount} đánh giá)
                        </span>
                    </c:if>
                </h2>
                
                <c:choose>
                    <c:when test="${empty reviews}">
                        <p style="color: #666; text-align: center; padding: 20px;">
                            Chưa có đánh giá nào cho sản phẩm này
                        </p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="review" items="${reviews}">
                            <div style="padding: 20px; border-bottom: 1px solid #f0f0f0;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                    <div>
                                        <strong style="color: #333;">${review.userFullName}</strong>
                                        <div style="color: #ffc107; margin-top: 5px;">
                                            <c:forEach begin="1" end="${review.rating}">
                                                <i class="fas fa-star"></i>
                                            </c:forEach>
                                            <c:forEach begin="${review.rating + 1}" end="5">
                                                <i class="far fa-star"></i>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <span style="color: #999; font-size: 14px;">${review.createdAt}</span>
                                </div>
                                <p style="color: #666; line-height: 1.6;">${review.comment}</p>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/products" style="color: #81c784; text-decoration: none;">
                ← Quay lại danh sách sản phẩm
            </a>
        </div>
    </div>
</body>
</html>
