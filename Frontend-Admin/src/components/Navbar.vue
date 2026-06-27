<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
      <router-link class="navbar-brand fw-bold" to="/admin/dashboard">SportPro Admin</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavAdmin">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNavAdmin">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/admin/dashboard">Thống kê doanh thu</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/admin/products">Quản lý Sản phẩm</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/admin/variants">Quản lý Biến thể</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/admin/orders">Quản lý Đơn hàng</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/admin/pos">Bán hàng tại quầy</router-link>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li v-if="user" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              Xin chào, {{ user.hoTen || user.email }}
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="#" @click="logout">Đăng xuất</a></li>
            </ul>
          </li>
          <li v-if="!user" class="nav-item">
            <router-link class="nav-link" to="/login">Đăng nhập</router-link>
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
