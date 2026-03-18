<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách mã giảm giá</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="header-container">
            <a href="${pageContext.request.contextPath}/products" class="logo"><i class="fas fa-book"></i> Book Store</a>
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/products"><i class="fas fa-home"></i> Trang chủ</a>
                <a href="${pageContext.request.contextPath}/wishlist"><i class="fas fa-heart"></i> Yêu thích</a>
                <a href="${pageContext.request.contextPath}/cart"><i class="fas fa-shopping-cart"></i> Giỏ hàng</a>
                <a href="${pageContext.request.contextPath}/orders"><i class="fas fa-box"></i> Đơn hàng</a>
                <a href="${pageContext.request.contextPath}/vouchers"><i class="fas fa-ticket-alt"></i> Mã giảm giá</a>
                <c:if test="${sessionScope.user.role == 1}">
                    <a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-user-shield"></i> Admin</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user"></i> ${sessionScope.user.fullName}</a>
                <a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <h1 style="color: #81c784; margin: 20px 0;"><i class="fas fa-ticket-alt"></i> Mã giảm giá hiện có</h1>

        <c:choose>
            <c:when test="${empty vouchers}">
                <div class="card" style="text-align: center; padding: 40px;">
                    <i class="fas fa-ticket-alt" style="font-size: 64px; color: #ddd; margin-bottom: 20px;"></i>
                    <p style="font-size: 18px; color: #666;">Hiện tại không có mã giảm giá nào</p>
                </div>
            </c:when>
            <c:otherwise>
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 20px;">
                    <c:forEach var="voucher" items="${vouchers}">
                        <div class="card" style="border-left: 4px solid #81c784;">
                            <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 15px;">
                                <div>
                                    <h2 style="color: #81c784; margin: 0 0 10px 0; font-size: 24px;">
                                        <i class="fas fa-tag"></i> ${voucher.code}
                                    </h2>
                                    <p style="font-size: 18px; color: #333; margin: 0;">
                                        Giảm <strong>${voucher.discountPercent}%</strong>
                                    </p>
                                </div>
                                <button class="btn btn-secondary" onclick="copyVoucher('${voucher.code}')" style="padding: 8px 12px;">
                                    <i class="fas fa-copy"></i> Copy
                                </button>
                            </div>
                            
                            <div style="border-top: 1px dashed #ddd; padding-top: 15px;">
                                <p style="margin: 5px 0; color: #666;">
                                    <i class="fas fa-coins"></i> Giảm tối đa: <strong>${voucher.maxDiscount} VNĐ</strong>
                                </p>
                                <p style="margin: 5px 0; color: #666;">
                                    <i class="fas fa-clock"></i> Hạn sử dụng: 
                                    <c:choose>
                                        <c:when test="${not empty voucher.expirationDate}">
                                            <fmt:formatDate value="${voucher.expirationDate}" pattern="dd/MM/yyyy" />
                                        </c:when>
                                        <c:otherwise>Không giới hạn</c:otherwise>
                                    </c:choose>
                                </p>
                                <p style="margin: 5px 0; color: #666;">
                                    <i class="fas fa-ticket-alt"></i> Số lượt còn lại: <strong>${voucher.usageLimit}</strong>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
        function copyVoucher(code) {
            navigator.clipboard.writeText(code).then(function() {
                alert('Đã copy mã: ' + code);
            });
        }
    </script>
</body>
</html>
