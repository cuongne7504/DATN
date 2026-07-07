<template>
  <footer class="bg-dark text-light py-4 mt-5">
    <div class="container">
      <div class="row">
        <div class="col-md-4 mb-3">
          <h5 class="text-primary fw-bold">SportPro</h5>
          <p class="text-muted">Cửa hàng thể thao chuyên nghiệp</p>
        </div>
        <div class="col-md-4 mb-3">
          <h5>Liên kết</h5>
          <ul class="list-unstyled">
            <li><router-link class="text-muted text-decoration-none" to="/">Trang chủ</router-link></li>
            <li><router-link class="text-muted text-decoration-none" to="/cart">Giỏ hàng</router-link></li>
            <li><router-link class="text-muted text-decoration-none" to="/history">Đơn hàng</router-link></li>
          </ul>
        </div>
        <div class="col-md-4 mb-3">
          <h5>Liên hệ</h5>
          <p class="text-muted mb-1"><i class="bi bi-envelope"></i> Email: info@sportpro.com</p>
          <p class="text-muted mb-1"><i class="bi bi-telephone"></i> Hotline: 1900 xxxx</p>
          <p class="text-muted"><i class="bi bi-geo-alt"></i> Địa chỉ: Hà Nội, Việt Nam</p>
        </div>
      </div>
      <hr class="my-4 border-secondary">
      <p class="text-center text-muted mb-0">&copy; 2026 SportPro. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
</script>
