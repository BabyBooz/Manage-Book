<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đánh giá sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .star-rating { font-size: 32px; color: #ddd; cursor: pointer; }
        .star-rating .star { display: inline-block; margin: 0 5px; }
        .star-rating .star.active { color: #ffc107; }
        .star-rating .star:hover, .star-rating .star:hover ~ .star { color: #ffc107; }
    </style>
</head>
<body>
    <header class="header">
        <div class="header-container">
            <a href="${pageContext.request.contextPath}/products" class="logo"><i class="fas fa-book"></i> Book Store</a>
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/products"><i class="fas fa-home"></i> Trang chủ</a>
                <a href="${pageContext.request.contextPath}/orders"><i class="fas fa-box"></i> Đơn hàng</a>
                <a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user"></i> ${sessionScope.user.fullName}</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <h1 style="color: #81c784; margin: 20px 0;"><i class="fas fa-star"></i> Đánh giá sản phẩm</h1>

        <div class="card">
            <div style="display: flex; gap: 20px; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #ddd;">
                <img src="${pageContext.request.contextPath}/${orderItem.productImageUrl}" 
                     alt="${orderItem.productName}"
                     style="width: 100px; height: 120px; object-fit: cover; border-radius: 4px;"
                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.png';">
                <div>
                    <h2 style="margin: 0 0 10px 0;">${orderItem.productName}</h2>
                    <p style="color: #666;">Đơn hàng #${order.orderId}</p>
                </div>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/review">
                <input type="hidden" name="orderItemId" value="${orderItem.orderItemId}">
                <input type="hidden" name="productId" value="${orderItem.productId}">
                <input type="hidden" name="orderId" value="${order.orderId}">
                <input type="hidden" name="rating" id="ratingValue" value="5">

                <div class="form-group">
                    <label>Đánh giá của bạn</label>
                    <div class="star-rating" id="starRating">
                        <span class="star active" data-rating="5"><i class="fas fa-star"></i></span>
                        <span class="star active" data-rating="4"><i class="fas fa-star"></i></span>
                        <span class="star active" data-rating="3"><i class="fas fa-star"></i></span>
                        <span class="star active" data-rating="2"><i class="fas fa-star"></i></span>
                        <span class="star active" data-rating="1"><i class="fas fa-star"></i></span>
                    </div>
                </div>

                <div class="form-group">
                    <label>Nhận xét của bạn</label>
                    <textarea name="comment" rows="5" placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm này..." required></textarea>
                </div>

                <div style="display: flex; gap: 10px;">
                    <button type="submit" class="btn">
                        <i class="fas fa-paper-plane"></i> Gửi đánh giá
                    </button>
                    <a href="${pageContext.request.contextPath}/orders?orderId=${order.orderId}" class="btn btn-secondary" style="text-decoration: none;">
                        <i class="fas fa-times"></i> Hủy
                    </a>
                </div>
            </form>
        </div>
    </div>

    <script>
        const stars = document.querySelectorAll('.star');
        const ratingValue = document.getElementById('ratingValue');

        stars.forEach(star => {
            star.addEventListener('click', function() {
                const rating = this.getAttribute('data-rating');
                ratingValue.value = rating;
                
                stars.forEach(s => {
                    if (s.getAttribute('data-rating') <= rating) {
                        s.classList.add('active');
                    } else {
                        s.classList.remove('active');
                    }
                });
            });
        });
    </script>
</body>
</html>
