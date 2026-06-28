<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm sticky-top">
    <div class="container">
      <router-link class="navbar-brand fw-bold fs-4 text-uppercase" to="/">
        <i class="bi bi-fire text-warning"></i> SportPro
      </router-link>
      
      <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link fw-semibold px-3" active-class="active" to="/">Trang chủ</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link fw-semibold px-3" active-class="active" to="/history">Lịch sử Đơn hàng</router-link>
          </li>
        </ul>
        
        <div class="d-flex align-items-center gap-3">

          <router-link to="/cart" class="btn btn-outline-light position-relative border-0" title="Giỏ hàng">
            <i class="bi bi-cart3 fs-5"></i>
            <span v-if="user" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-light text-dark border">
              +
            </span>
          </router-link>
          
          <div v-if="user" class="dropdown">
            <button class="btn btn-light dropdown-toggle fw-bold text-dark d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
              <div class="bg-dark text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 25px; height: 25px;">
                {{ user.hoTen ? user.hoTen.charAt(0).toUpperCase() : 'U' }}
              </div>
              {{ user.hoTen || user.email }}
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
              <li><router-link class="dropdown-item" to="/account"><i class="bi bi-person me-2"></i> Tài khoản của tôi</router-link></li>
              <li><router-link class="dropdown-item" to="/history"><i class="bi bi-bag-check me-2"></i> Đơn mua</router-link></li>

              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item text-danger fw-bold" href="#" @click="logout"><i class="bi bi-box-arrow-right me-2"></i> Đăng xuất</a></li>
            </ul>
          </div>
          
          <router-link v-else to="/login" class="btn btn-light text-dark fw-bold px-4 rounded-pill shadow-sm">
            Đăng nhập
          </router-link>
        </div>
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
.nav-link.active {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
}
</style>
