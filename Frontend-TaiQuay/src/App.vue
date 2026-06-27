<script setup>
import { RouterView, RouterLink } from 'vue-router'
</script>

<template>
  <div class="d-flex" style="min-height: 100vh;">
    <!-- Sidebar -->
    <div class="bg-dark text-white p-3" style="width: 250px;">
      <h4>Giao Diện Tại Quầy</h4>
      <hr />
      <ul class="nav flex-column">
        <li class="nav-item">
          <RouterLink to="/" class="nav-link text-white"><i class="bi bi-house me-2"></i>Trang Chủ</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/pos" class="nav-link text-white"><i class="bi bi-cart me-2"></i>Bán Hàng (POS)</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/san-pham" class="nav-link text-white"><i class="bi bi-box me-2"></i>Sản Phẩm</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/don-hang" class="nav-link text-white"><i class="bi bi-receipt me-2"></i>Đơn Hàng</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/thong-ke" class="nav-link text-white"><i class="bi bi-graph-up me-2"></i>Thống Kê</RouterLink>
        </li>
      </ul>
    </div>
    
    <!-- Main Content -->
    <div class="flex-grow-1 bg-light">
      <nav class="navbar navbar-light bg-white shadow-sm px-4 mb-4">
        <span class="navbar-brand mb-0 h1">Hệ Thống Quản Trị</span>
      </nav>
      <div class="container-fluid px-4">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<style scoped>
.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
}
.router-link-active {
  background-color: #0d6efd;
  border-radius: 5px;
  font-weight: bold;
}
</style>
