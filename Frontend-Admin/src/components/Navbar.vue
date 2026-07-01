<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container-fluid">
      <router-link class="navbar-brand fw-bold text-uppercase tracking-wider" to="/admin/dashboard">
        <span class="text-primary">SportPro</span> Admin
      </router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavAdmin">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavAdmin">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item" v-if="user?.maQuyen === 1">
            <router-link class="nav-link" active-class="active fw-semibold" to="/admin/dashboard">Thống kê</router-link>
          </li>
          <li class="nav-item dropdown" v-if="user?.maQuyen === 1">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">Danh mục & Khuyến mãi</a>
            <ul class="dropdown-menu">
              <li><router-link class="dropdown-item" to="/admin/categories">Danh mục</router-link></li>
              <li><router-link class="dropdown-item" to="/admin/brands">Thương hiệu</router-link></li>
              <li><hr class="dropdown-divider"></li>
              <li><router-link class="dropdown-item" to="/admin/vouchers">Khuyến mãi (Voucher)</router-link></li>
            </ul>
          </li>
          <li class="nav-item dropdown" v-if="user?.maQuyen === 1">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">Sản phẩm & Biến thể</a>
            <ul class="dropdown-menu">
              <li><router-link class="dropdown-item" to="/admin/products">Danh sách Sản phẩm</router-link></li>
              <li><router-link class="dropdown-item" to="/admin/variants">Quản lý Biến thể (Size/Màu)</router-link></li>
            </ul>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" active-class="active fw-semibold" to="/admin/orders">Đơn hàng</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link text-warning fw-bold" active-class="active" to="/admin/pos">Bán tại quầy</router-link>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li v-if="user" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" role="button" data-bs-toggle="dropdown">
              <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 30px; height: 30px; font-weight: bold;">
                {{ user.hoTen ? user.hoTen.charAt(0).toUpperCase() : 'A' }}
              </div>
              {{ user.hoTen || user.email }}
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow">
              <li><a class="dropdown-item text-danger" href="#" @click="logout">Đăng xuất</a></li>
            </ul>
          </li>
          <li v-if="!user" class="nav-item">
            <router-link class="nav-link btn btn-primary text-white px-4" to="/login">Đăng nhập</router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref(null)

const checkUser = () => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  } else {
    user.value = null
  }
}

onMounted(() => {
  checkUser()
  window.addEventListener('storage', checkUser)
})

onUnmounted(() => {
  window.removeEventListener('storage', checkUser)
})

const logout = () => {
  localStorage.removeItem('user')
  user.value = null
  router.push('/login')
}
</script>

<style scoped>
.tracking-wider {
  letter-spacing: 0.05em;
}
.nav-link.active {
  border-bottom: 2px solid #0d6efd;
}
</style>
