HƯỚNG DẪN SỬ DỤNG THƯ MỤC IMAGES
=====================================

Thư mục này chứa các hình ảnh cho website Manage Books.

CẤU TRÚC:
---------
/images/
  - no-image.png          : Ảnh mặc định khi không có ảnh sản phẩm
  - book-sample-1.jpg     : Ảnh mẫu sách 1 (màu xanh lá)
  - book-sample-2.jpg     : Ảnh mẫu sách 2 (màu xanh dương)
  - book-sample-3.jpg     : Ảnh mẫu sách 3 (màu cam)

CÁCH SỬ DỤNG:
-------------
1. Khi thêm sản phẩm mới trong Admin, nhập URL ảnh:
   - Dùng ảnh mẫu: ${pageContext.request.contextPath}/images/book-sample-1.jpg
   - Dùng ảnh online: https://example.com/image.jpg
   - Dùng ảnh local: ${pageContext.request.contextPath}/images/ten-anh.jpg

2. Để thêm ảnh mới:
   - Copy file ảnh vào thư mục: src/main/webapp/images/
   - Đặt tên file: vd: sach-van-hoc.jpg
   - Sử dụng URL: ${pageContext.request.contextPath}/images/sach-van-hoc.jpg

3. Định dạng ảnh khuyến nghị:
   - Kích thước: 300x400px (tỷ lệ 3:4)
   - Định dạng: JPG, PNG
   - Dung lượng: < 500KB

LƯU Ý:
------
- Nếu URL ảnh bị lỗi, hệ thống tự động hiển thị no-image.png
- Không xóa file no-image.png
- Đặt tên file không dấu, không khoảng trắng
