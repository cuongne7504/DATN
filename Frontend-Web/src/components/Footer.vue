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
  background:
    radial-gradient(700px 280px at 0% 0%, rgba(29, 78, 216, 0.22), transparent 60%),
    radial-gradient(600px 240px at 100% 100%, rgba(249, 115, 22, 0.14), transparent 55%),
    #0b1220;
  color: #e2e8f0;
  border-top: 3px solid #1d4ed8;
  margin-top: 3.5rem;
}

.footer-logo {
  color: #ffffff;
  letter-spacing: 1px;
}
.footer-logo span {
  color: #f97316;
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
  background-color: #f97316;
}

/* Kiểu dáng liên kết & hiệu ứng hover mượt mà */
.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  color: #94a3b8;
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.25s ease, transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  display: inline-block;
}

.footer-links a:hover {
  color: #60a5fa;
  transform: translateX(6px);
}

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
  color: #64748b;
  font-size: 0.85rem;
}

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
  transition: transform 0.3s cubic-bezier(0.34, 1.4, 0.64, 1), background-color 0.25s ease, color 0.25s ease;
  text-decoration: none;
}

.social-icon:hover {
  background-color: #1d4ed8;
  color: #ffffff;
  transform: translateY(-4px) scale(1.06);
}
</style>
