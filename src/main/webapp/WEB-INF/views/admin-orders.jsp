<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đơn hàng - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        table { width: 100%; border-collapse: collapse; background: white; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #81c784; color: white; }
        .status-badge { padding: 5px 10px; border-radius: 4px; font-size: 12px; font-weight: bold; }
        .status-0 { background: #fff3cd; color: #856404; }
        .status-1 { background: #cfe2ff; color: #084298; }
        .status-2 { background: #d1e7dd; color: #0f5132; }
        .status-3 { background: #f8d7da; color: #842029; }

        .btn {
    display: inline-flex;
    align-items: center;  /* Căn chỉnh theo chiều dọc */
    justify-content: center;  /* Căn chỉnh theo chiều ngang */
    height: 40px;  /* Chiều cao cố định */
    padding: 0 12px;  /* Padding bên trái và phải */
    background-color: #81c784;  /* Màu nền nút */
    color: white;  /* Màu chữ */
    border: none;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    margin: 0; /* Đảm bảo không có khoảng cách thừa */
}

/* Đảm bảo khoảng cách giữa biểu tượng và chữ */
.btn i {
    margin-right: 5px;  /* Khoảng cách giữa icon và chữ */
}

    </style>
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="header-container">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="logo"><i class="fas fa-user-shield"></i> Admin Panel</a>
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/products"><i class="fas fa-box"></i> Sản phẩm</a>
                <a href="${pageContext.request.contextPath}/admin/categories"><i class="fas fa-tags"></i> Danh mục</a>
                <a href="${pageContext.request.contextPath}/admin/vouchers"><i class="fas fa-ticket-alt"></i> Voucher</a>
                <a href="${pageContext.request.contextPath}/admin/orders"><i class="fas fa-shopping-bag"></i> Đơn hàng</a>
                <a href="${pageContext.request.contextPath}/products"><i class="fas fa-home"></i> Trang chủ</a>
                <span class="user-info"><i class="fas fa-user"></i> ${sessionScope.user.fullName}</span>
                <a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <h1 style="color: #81c784; margin-bottom: 20px;"><i class="fas fa-shopping-bag"></i> Quản lý đơn hàng</h1>

        <!-- Messages -->
        <c:if test="${param.success == 'update'}">
            <div class="alert alert-success">Cập nhật trạng thái thành công!</div>
        </c:if>

        <!-- Orders Table -->
        <div style="background: white; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow-x: auto;">
            <table>
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Khách hàng</th>
                        <th>Người nhận</th>
                        <th>SĐT</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Ngày đặt</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td><strong>#${order.orderId}</strong></td>
                            <td>${order.username}</td>
                            <td>${order.receiverName}</td>
                            <td>${order.receiverPhone}</td>
                            <td><strong>${order.totalAmount} VNĐ</strong></td>
                            <td>
                                <span class="status-badge status-${order.status}">${order.statusText}</span>
                            </td>
                            <td>${order.createdAt}</td>
                            <td>
                                <div style="display: flex; gap: 5px; align-items: center;">
                                    <a href="${pageContext.request.contextPath}/admin/orders?orderId=${order.orderId}" class="btn btn-small" style="text-decoration: none;">
                                        <i class="fas fa-eye"></i> Chi tiết
                                    </a>
                                    <c:if test="${order.status < 2}">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" style="margin: 0;">
                                            <input type="hidden" name="action" value="update_status">
                                            <input type="hidden" name="orderId" value="${order.orderId}">
                                            <input type="hidden" name="status" value="${order.status + 1}">
                                            <button type="submit" class="btn btn-small">
                                                <i class="fas fa-arrow-right"></i> 
                                                <c:choose>
                                                    <c:when test="${order.status == 0}">Duyệt</c:when>
                                                    <c:when test="${order.status == 1}">Hoàn thành</c:when>
                                                </c:choose>
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
