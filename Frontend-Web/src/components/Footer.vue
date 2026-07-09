<template>
  <footer class="custom-footer py-5 mt-5">
    <div class="container">
      <div class="row g-4">
        <!-- Cột 1: Thông tin thương hiệu -->
        <div class="col-md-4">
          <h4 class="footer-logo fw-bold mb-3">SPORT<span>PRO</span></h4>
          <p class="footer-desc">
            Cửa hàng cung cấp quần áo và giày thể thao chuyên nghiệp, dẫn đầu xu hướng thời trang thể thao năng động.
          </p>
          <div class="social-icons d-flex gap-3 mt-3">
            <a href="#" class="social-icon"><i class="bi bi-facebook"></i></a>
            <a href="#" class="social-icon"><i class="bi bi-instagram"></i></a>
            <a href="#" class="social-icon"><i class="bi bi-youtube"></i></a>
          </div>
        </div>

        <!-- Cột 2: Các liên kết nhanh -->
        <div class="col-md-4">
          <h5 class="footer-title fw-bold mb-3">Liên kết</h5>
          <ul class="list-unstyled footer-links">
            <li>
              <router-link to="/">
                <i class="bi bi-chevron-right me-1"></i> Trang chủ
              </router-link>
            </li>
            <li>
              <router-link to="/cart">
                <i class="bi bi-chevron-right me-1"></i> Giỏ hàng
              </router-link>
            </li>
            <li>
              <router-link to="/history">
                <i class="bi bi-chevron-right me-1"></i> Đơn hàng
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Cột 3: Thông tin liên hệ -->
        <div class="col-md-4">
          <h5 class="footer-title fw-bold mb-3">Liên hệ</h5>
          <ul class="list-unstyled footer-contact">
            <li>
              <i class="bi bi-envelope-fill text-primary"></i> 
              <span>Email: info@sportpro.com</span>
            </li>
            <li>
              <i class="bi bi-telephone-fill text-primary"></i> 
              <span>Hotline: 1900 6868</span>
            </li>
            <li>
              <i class="bi bi-geo-alt-fill text-primary"></i> 
              <span>Địa chỉ: Hà Nội, Việt Nam</span>
            </li>
          </ul>
        </div>
      </div>

      <hr class="my-4 footer-divider">
      
      <div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
        <p class="footer-copy mb-0">&copy; 2026 SPORTPRO. All rights reserved.</p>
        <p class="footer-copy mb-0">Thiết kế bởi Đội ngũ SportPro</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.custom-footer {
  background-color: #0f172a; /* Slate 900 - Nền tối hiện đại và sang trọng hơn */
  color: #e2e8f0; /* Slate 200 - Chữ xám sáng giúp đọc cực kỳ rõ */
  border-top: 3px solid #3b82f6; /* Đường viền màu xanh thương hiệu trên đỉnh */
}

.footer-logo {
  color: #ffffff;
  letter-spacing: 1px;
}
.footer-logo span {
  color: #3b82f6;
}

.footer-desc {
  color: #94a3b8; /* Slate 400 - Màu chữ phụ xám dịu */
  font-size: 0.95rem;
  line-height: 1.6;
}

.footer-title {
  color: #ffffff;
  position: relative;
  padding-bottom: 8px;
}
.footer-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 40px;
  height: 2px;
  background-color: #3b82f6; /* Vạch gạch chân nhỏ màu xanh */
}

/* Kiểu dáng liên kết & hiệu ứng hover mượt mà */
.footer-links li {
  margin-bottom: 10px;
}
.footer-links a {
  color: #94a3b8;
  text-decoration: none;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  display: inline-block;
}
.footer-links a:hover {
  color: #3b82f6; /* Đổi màu xanh khi di chuột */
  transform: translateX(5px); /* Dịch nhẹ sang phải */
}

/* Kiểu dáng liên hệ */
.footer-contact li {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  color: #94a3b8;
  font-size: 0.95rem;
}

.footer-divider {
  border-color: rgba(255, 255, 255, 0.08);
}

.footer-copy {
  color: #64748b; /* Slate 500 */
  font-size: 0.85rem;
}

/* Icon mạng xã hội */
.social-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.05);
  color: #94a3b8;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  text-decoration: none;
}
.social-icon:hover {
  background-color: #3b82f6;
  color: #ffffff;
  transform: translateY(-3px);
}
</style>
