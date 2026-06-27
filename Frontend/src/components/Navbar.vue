<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
      <router-link class="navbar-brand fw-bold" to="/">SportPro</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">Trang chủ</router-link>
          </li>
          <li v-if="user" class="nav-item">
            <router-link class="nav-link" to="/cart">Giỏ hàng</router-link>
          </li>
          <li v-if="user" class="nav-item">
            <router-link class="nav-link" to="/orders">Đơn hàng</router-link>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li v-if="user && (user.maQuyen === 1 || user.maQuyen === 2)" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              {{ user.maQuyen === 1 ? 'Admin' : 'Nhân viên' }}
            </a>
            <ul class="dropdown-menu">
              <li><router-link class="dropdown-item" to="/admin/dashboard">Dashboard</router-link></li>
              <li><router-link class="dropdown-item" to="/admin/products">Sản phẩm</router-link></li>
              <li><router-link class="dropdown-item" to="/admin/variants">Biến thể</router-link></li>
              <li><router-link class="dropdown-item" to="/admin/orders">Đơn hàng</router-link></li>
              <li><router-link class="dropdown-item" to="/admin/pos">POS</router-link></li>
            </ul>
          </li>
          <li v-if="user" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              {{ user.hoTen || user.email }}
            </a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#" @click="logout">Đăng xuất</a></li>
            </ul>
          </li>
          <li v-if="!user" class="nav-item">
            <router-link class="nav-link" to="/login">Đăng nhập</router-link>
          </li>
          <li v-if="!user" class="nav-item">
            <router-link class="nav-link" to="/register">Đăng ký</router-link>
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
