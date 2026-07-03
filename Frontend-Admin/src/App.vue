<template>
  <div v-if="isLoginRoute">
    <router-view />
  </div>
  
  <div v-else class="d-flex" style="min-height: 100vh;">
    <!-- Sidebar -->
    <div class="sidebar bg-black text-white d-flex flex-column" style="width: 260px; position: fixed; top: 0; bottom: 0; left: 0; overflow-y: auto;">
      <div class="p-4 text-center border-bottom border-secondary">
        <h3 class="fw-bold m-0 tracking-wider text-uppercase" style="letter-spacing: 2px;">SPORTPRO</h3>
      </div>
      
      <div class="flex-grow-1 p-3">
        <ul class="nav nav-pills flex-column mb-auto">
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/dashboard">
              <i class="bi bi-graph-up me-2"></i> Thống kê
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/categories">
              <i class="bi bi-tags me-2"></i> Danh mục
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/brands">
              <i class="bi bi-award me-2"></i> Thương hiệu
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/products">
              <i class="bi bi-box-seam me-2"></i> Sản phẩm
            </router-link>
          </li>

          <li class="nav-item mb-2">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/orders">
              <i class="bi bi-cart-check me-2"></i> Đơn hàng
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1 || user?.maQuyen === 2">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/inventory">
              <i class="bi bi-box-seam-fill me-2"></i> Kho hàng
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/vouchers">
              <i class="bi bi-ticket-perforated me-2"></i> Khuyến mãi
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/employees">
              <i class="bi bi-person-badge me-2"></i> Nhân viên
            </router-link>
          </li>
          <li class="nav-item mb-2" v-if="user?.maQuyen === 1">
            <router-link class="nav-link text-white sidebar-link" active-class="active bg-white text-black fw-bold" to="/admin/customers">
              <i class="bi bi-people me-2"></i> Khách hàng
            </router-link>
          </li>
          <li class="nav-item mb-2 mt-4 pt-3 border-top border-secondary">
            <router-link class="nav-link text-white sidebar-link bg-secondary bg-opacity-25" active-class="active bg-white text-black fw-bold" to="/admin/pos">
              <i class="bi bi-shop me-2"></i> Bán tại quầy (POS)
            </router-link>
          </li>
        </ul>
      </div>
      
      <div class="p-3 border-top border-secondary">
        <div v-if="user" class="d-flex align-items-center justify-content-between">
          <div class="d-flex align-items-center text-white text-decoration-none" style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
            <div class="bg-white text-black rounded-circle d-flex align-items-center justify-content-center me-2 fw-bold flex-shrink-0" style="width: 32px; height: 32px;">
              {{ user.hoTen ? user.hoTen.charAt(0).toUpperCase() : 'A' }}
            </div>
            <strong class="text-truncate">{{ user.hoTen || user.email }}</strong>
          </div>
          <button class="btn btn-sm btn-outline-danger flex-shrink-0 ms-2" @click="logout" title="Đăng xuất">
            <i class="bi bi-box-arrow-right"></i> Thoát
          </button>
        </div>
      </div>
    </div>
    
    <!-- Main Content -->
    <div class="flex-grow-1 bg-light" style="margin-left: 260px;">
      <div class="p-4">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const user = ref(null)

const isLoginRoute = computed(() => {
  return route.path === '/login'
})

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
.sidebar-link {
  transition: all 0.2s;
  border-radius: 0;
}
.sidebar-link:hover {
  background-color: rgba(255,255,255,0.1);
  transform: translateX(5px);
}
.sidebar-link.active {
  color: #000 !important;
  background-color: #fff !important;
}
.tracking-wider {
  letter-spacing: 0.1em;
}
</style>