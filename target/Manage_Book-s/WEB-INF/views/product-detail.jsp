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
                <a href="${pageContext.request.contextPath}/products"><i class="fas fa-shopping-bag"></i> Sản phẩm</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <span class="user-info"><i class="fas fa-user"></i> Xin chào, ${sessionScope.user.fullName}</span>
                        <c:if test="${sessionScope.user.role == 1}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-user-shield"></i> Admin</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-circle"></i> Tài khoản</a>
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
                        <div style="display: flex; gap: 10px;">
                            <button class="btn" style="flex: 1;" onclick="alert('Chức năng thêm vào giỏ hàng sẽ được phát triển sau')">
                                <i class="fas fa-shopping-cart"></i> Thêm vào giỏ hàng
                            </button>
                            <button class="btn btn-secondary" style="width: auto;" onclick="alert('Chức năng wishlist sẽ được phát triển sau')">
                                <i class="fas fa-heart"></i> Yêu thích
                            </button>
                        </div>
                    </c:if>
                    
                    <c:if test="${empty sessionScope.user}">
                        <p style="color: #666; margin-top: 20px;">
                            <a href="${pageContext.request.contextPath}/login" style="color: #81c784;">Đăng nhập</a> để mua hàng
                        </p>
                    </c:if>
                </div>
            </div>

            <!-- Reviews Section (placeholder) -->
            <div style="margin-top: 40px; padding-top: 40px; border-top: 2px solid #f0f0f0;">
                <h2 style="color: #81c784; margin-bottom: 20px;"><i class="fas fa-star"></i> Đánh giá sản phẩm</h2>
                <p style="color: #666; text-align: center; padding: 20px;">
                    Chức năng đánh giá sẽ được phát triển sau
                </p>
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
