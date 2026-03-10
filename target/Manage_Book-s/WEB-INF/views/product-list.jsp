<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách sản phẩm - Manage Books</title>
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
        <div style="display: flex; gap: 30px; margin-top: 20px;">
            <!-- Sidebar Categories -->
            <div style="width: 250px; flex-shrink: 0;">
                <div style="background: white; padding: 20px; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h3 style="margin-bottom: 20px; color: #81c784;"><i class="fas fa-list"></i> Danh mục</h3>
                    <ul style="list-style: none;">
                        <li style="margin-bottom: 10px;">
                            <a href="${pageContext.request.contextPath}/products" 
                               style="display: block; padding: 10px; text-decoration: none; color: #333; border-radius: 4px; ${empty selectedCategoryId ? 'background-color: #e8f5e9;' : ''}">
                                Tất cả sản phẩm
                            </a>
                        </li>
                        <c:forEach var="category" items="${categories}">
                            <li style="margin-bottom: 10px;">
                                <a href="${pageContext.request.contextPath}/products?categoryId=${category.categoryId}" 
                                   style="display: block; padding: 10px; text-decoration: none; color: #333; border-radius: 4px; ${selectedCategoryId == category.categoryId ? 'background-color: #e8f5e9;' : ''}">
                                    ${category.name}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>

            <!-- Products Grid -->
            <div style="flex: 1;">
                <h2 style="margin-bottom: 20px; color: #81c784;">
                    <c:choose>
                        <c:when test="${not empty selectedCategoryId}">
                            <c:forEach var="category" items="${categories}">
                                <c:if test="${category.categoryId == selectedCategoryId}">
                                    ${category.name}
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            Tất cả sản phẩm
                        </c:otherwise>
                    </c:choose>
                </h2>

                <c:if test="${empty products}">
                    <p style="text-align: center; padding: 40px; color: #666;">Không có sản phẩm nào.</p>
                </c:if>

                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px;">
                    <c:forEach var="product" items="${products}">
                        <div class="feature-card" style="cursor: pointer;" onclick="location.href='${pageContext.request.contextPath}/product-detail?id=${product.productId}'">
                            <img src="${product.imageUrl}" alt="${product.name}" 
                                 style="width: 100%; height: 200px; object-fit: cover; border-radius: 4px; margin-bottom: 15px;"
                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.png'">
                            <h3 style="color: #333; margin-bottom: 10px; font-size: 16px;">${product.name}</h3>
                            <p style="color: #81c784; font-size: 18px; font-weight: bold; margin-bottom: 10px;">
                                ${product.price} VNĐ
                            </p>
                            <p style="color: #666; font-size: 14px;">
                                Còn lại: ${product.stock} sản phẩm
                            </p>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div style="display: flex; justify-content: center; gap: 10px; margin-top: 30px;">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/products?page=${currentPage - 1}${not empty selectedCategoryId ? '&categoryId='.concat(selectedCategoryId) : ''}" 
                               style="padding: 8px 16px; background: #81c784; color: white; text-decoration: none; border-radius: 4px;">
                                Trước
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="${pageContext.request.contextPath}/products?page=${i}${not empty selectedCategoryId ? '&categoryId='.concat(selectedCategoryId) : ''}" 
                               style="padding: 8px 16px; ${i == currentPage ? 'background: #81c784; color: white;' : 'background: white; color: #333;'} text-decoration: none; border-radius: 4px; border: 1px solid #ddd;">
                                ${i}
                            </a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/products?page=${currentPage + 1}${not empty selectedCategoryId ? '&categoryId='.concat(selectedCategoryId) : ''}" 
                               style="padding: 8px 16px; background: #81c784; color: white; text-decoration: none; border-radius: 4px;">
                                Sau
                            </a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>
