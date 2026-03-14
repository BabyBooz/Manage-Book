<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Voucher - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        table { width: 100%; border-collapse: collapse; background: white; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #81c784; color: white; }
        .btn-small { padding: 6px 12px; font-size: 14px; margin-right: 5px; }
        .modal { 
            display: none; 
            position: fixed; 
            z-index: 1000; 
            left: 0; 
            top: 0; 
            width: 100%; 
            height: 100%; 
            background-color: rgba(0,0,0,0.5); 
            overflow-y: auto;
        }
        .modal-content { 
            background-color: white; 
            margin: 30px auto; 
            padding: 30px; 
            width: 80%; 
            max-width: 600px; 
            border-radius: 4px;
            max-height: calc(100vh - 60px);
            overflow-y: auto;
        }
        .close { color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer; }
        .close:hover { color: #000; }
        .status-active { color: #81c784; }
        .status-inactive { color: #e57373; }
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
        <h1 style="color: #81c784; margin-bottom: 20px;"><i class="fas fa-ticket-alt"></i> Quản lý Voucher</h1>

        <!-- Messages -->
        <c:if test="${param.success == 'add'}">
            <div class="alert alert-success">Thêm voucher thành công!</div>
        </c:if>
        <c:if test="${param.success == 'edit'}">
            <div class="alert alert-success">Cập nhật voucher thành công!</div>
        </c:if>
        <c:if test="${param.success == 'delete'}">
            <div class="alert alert-success">Xóa voucher thành công!</div>
        </c:if>

        <button class="btn" onclick="openAddModal()" style="margin-bottom: 20px;"><i class="fas fa-plus"></i> Thêm voucher mới</button>

        <!-- Vouchers Table -->
        <div style="background: white; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow-x: auto;">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Mã</th>
                        <th>Giảm giá</th>
                        <th>Giảm tối đa</th>
                        <th>Hạn sử dụng</th>
                        <th>Số lượt</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="voucher" items="${vouchers}">
                        <tr>
                            <td>${voucher.voucherId}</td>
                            <td><strong>${voucher.code}</strong></td>
                            <td>${voucher.discountPercent}%</td>
                            <td>${voucher.maxDiscount} VNĐ</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty voucher.expirationDate}">
                                        <fmt:formatDate value="${voucher.expirationDate}" pattern="dd/MM/yyyy" />
                                    </c:when>
                                    <c:otherwise>Không giới hạn</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${voucher.usageLimit}</td>
                            <td>
                                <span class="${voucher.active ? 'status-active' : 'status-inactive'}">
                                    ${voucher.active ? 'Hoạt động' : 'Tạm dừng'}
                                </span>
                            </td>
                            <td>
                                <button class="btn btn-small" onclick="openEditModal(${voucher.voucherId}, '${voucher.code}', ${voucher.discountPercent}, ${voucher.maxDiscount}, '<fmt:formatDate value="${voucher.expirationDate}" pattern="yyyy-MM-dd" />', ${voucher.usageLimit}, ${voucher.active})">
                                    <i class="fas fa-edit"></i> Sửa
                                </button>
                                <button class="btn btn-small btn-secondary" onclick="deleteVoucher(${voucher.voucherId}, '${voucher.code}')">
                                    <i class="fas fa-trash"></i> Xóa
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Add Modal -->
    <div id="addModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeAddModal()">&times;</span>
            <h2 style="color: #81c784; margin-bottom: 20px;">Thêm voucher mới</h2>
            <form action="${pageContext.request.contextPath}/admin/vouchers" method="post">
                <input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label>Mã voucher</label>
                    <input type="text" name="code" required>
                </div>
                <div class="form-group">
                    <label>Phần trăm giảm giá (%)</label>
                    <input type="number" name="discountPercent" min="1" max="100" required>
                </div>
                <div class="form-group">
                    <label>Giảm giá tối đa (VNĐ)</label>
                    <input type="number" name="maxDiscount" step="0.01" required>
                </div>
                <div class="form-group">
                    <label>Ngày hết hạn</label>
                    <input type="date" name="expirationDate">
                </div>
                <div class="form-group">
                    <label>Số lượt sử dụng</label>
                    <input type="number" name="usageLimit" min="1" required>
                </div>
                <button type="submit" class="btn">Thêm voucher</button>
            </form>
        </div>
    </div>

    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">&times;</span>
            <h2 style="color: #81c784; margin-bottom: 20px;">Sửa voucher</h2>
            <form action="${pageContext.request.contextPath}/admin/vouchers" method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="voucherId" id="editVoucherId">
                <div class="form-group">
                    <label>Mã voucher</label>
                    <input type="text" name="code" id="editCode" required>
                </div>
                <div class="form-group">
                    <label>Phần trăm giảm giá (%)</label>
                    <input type="number" name="discountPercent" id="editDiscountPercent" min="1" max="100" required>
                </div>
                <div class="form-group">
                    <label>Giảm giá tối đa (VNĐ)</label>
                    <input type="number" name="maxDiscount" id="editMaxDiscount" step="0.01" required>
                </div>
                <div class="form-group">
                    <label>Ngày hết hạn</label>
                    <input type="date" name="expirationDate" id="editExpirationDate">
                </div>
                <div class="form-group">
                    <label>Số lượt sử dụng</label>
                    <input type="number" name="usageLimit" id="editUsageLimit" min="1" required>
                </div>
                <div class="form-group">
                    <label>Trạng thái</label>
                    <select name="isActive" id="editIsActive" style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px;">
                        <option value="1">Hoạt động</option>
                        <option value="0">Tạm dừng</option>
                    </select>
                </div>
                <button type="submit" class="btn">Cập nhật</button>
            </form>
        </div>
    </div>

    <script>
        function openAddModal() {
            document.getElementById('addModal').style.display = 'block';
        }
        function closeAddModal() {
            document.getElementById('addModal').style.display = 'none';
        }
        function openEditModal(id, code, discountPercent, maxDiscount, expirationDate, usageLimit, isActive) {
            document.getElementById('editVoucherId').value = id;
            document.getElementById('editCode').value = code;
            document.getElementById('editDiscountPercent').value = discountPercent;
            document.getElementById('editMaxDiscount').value = maxDiscount;
            document.getElementById('editExpirationDate').value = expirationDate;
            document.getElementById('editUsageLimit').value = usageLimit;
            document.getElementById('editIsActive').value = isActive ? '1' : '0';
            document.getElementById('editModal').style.display = 'block';
        }
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
        }
        function deleteVoucher(id, code) {
            if (confirm('Bạn có chắc muốn xóa voucher "' + code + '"?')) {
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/vouchers';
                form.innerHTML = '<input type="hidden" name="action" value="delete"><input type="hidden" name="voucherId" value="' + id + '">';
                document.body.appendChild(form);
                form.submit();
            }
        }
        window.onclick = function(event) {
            if (event.target.className === 'modal') {
                event.target.style.display = 'none';
            }
        }
    </script>
</body>
</html>
