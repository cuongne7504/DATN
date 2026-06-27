<template>
  <footer class="bg-dark text-light py-4 mt-5">
    <div class="container">
      <div class="row">
        <div class="col-md-4">
          <h5>SportPro</h5>
          <p class="text-muted">Cửa hàng thể thao chuyên nghiệp</p>
        </div>
        <div class="col-md-4">
          <h5>Liên kết</h5>
          <ul class="list-unstyled">
            <li><router-link class="text-muted text-decoration-none" to="/">Trang chủ</router-link></li>
            <li><router-link class="text-muted text-decoration-none" to="/cart">Giỏ hàng</router-link></li>
            <li><router-link class="text-muted text-decoration-none" to="/orders">Đơn hàng</router-link></li>
          </ul>
        </div>
        <div class="col-md-4">
          <h5>Liên hệ</h5>
          <p class="text-muted mb-1">Email: info@sportpro.com</p>
          <p class="text-muted mb-1">Hotline: 1900 xxxx</p>
          <p class="text-muted">Địa chỉ: Hà Nội, Việt Nam</p>
        </div>
      </div>
      <hr class="my-4 border-secondary">
      <p class="text-center text-muted mb-0">&copy; 2024 SportPro. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
</script>
