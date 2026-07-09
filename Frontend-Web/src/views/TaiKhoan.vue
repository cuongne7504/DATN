<template>
  <div class="container mt-5">
    <div class="row">
      <div class="col-md-3 mb-4">
        <div class="card shadow-sm border-0">
          <div class="card-body p-4 text-center">
            <div class="bg-dark text-white rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3" style="width: 80px; height: 80px; font-size: 2rem;">
              {{ user?.hoTen ? user.hoTen.charAt(0).toUpperCase() : 'U' }}
            </div>
            <h5 class="fw-bold mb-1">{{ user?.hoTen }}</h5>
            <p class="text-muted small mb-3">{{ user?.email }}</p>
            <hr>
            <div class="nav flex-column nav-pills text-start">
              <router-link to="/account" class="nav-link text-dark fw-semibold active-link"><i class="bi bi-person me-2"></i> Hồ sơ của tôi</router-link>
              <router-link to="/history" class="nav-link text-dark fw-semibold"><i class="bi bi-bag-check me-2"></i> Đơn hàng</router-link>
              <router-link to="/wishlist" class="nav-link text-dark fw-semibold"><i class="bi bi-heart me-2"></i> Yêu thích</router-link>
              <a href="#" @click="logout" class="nav-link text-danger fw-semibold mt-2"><i class="bi bi-box-arrow-right me-2"></i> Đăng xuất</a>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-9">
        <div class="card shadow-sm border-0">
          <div class="card-body p-4">
            <h4 class="fw-bold mb-4 border-bottom pb-3">Hồ Sơ Của Tôi</h4>
            
            <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>
            <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>
            
            <form @submit.prevent="updateProfile">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-semibold">Họ và tên</label>
                  <input type="text" v-model="form.hoTen" class="form-control" required>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-semibold">Email</label>
                  <input type="email" v-model="form.email" class="form-control" disabled>
                  <div class="form-text">Không thể thay đổi email.</div>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-semibold">Số điện thoại</label>
                  <input type="tel" v-model="form.soDienThoai" class="form-control">
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-semibold">Mật khẩu mới (Tùy chọn)</label>
                  <input type="password" v-model="form.matKhau" class="form-control" placeholder="Để trống nếu không muốn đổi">
                </div>
                <div class="col-md-12 mb-4">
                  <label class="form-label fw-semibold">Địa chỉ</label>
                  <textarea v-model="form.diaChi" class="form-control" rows="3"></textarea>
                </div>
              </div>
              
              <button type="submit" class="btn btn-primary px-4 fw-bold" :disabled="loading">
                {{ loading ? 'Đang lưu...' : 'LƯU THAY ĐỔI' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import { API_URL } from '@/config.js'
const router = useRouter()
const user = ref(null)

const form = ref({
  hoTen: '',
  email: '',
  soDienThoai: '',
  diaChi: '',
  matKhau: ''
})

const loading = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (!userData) {
    router.push('/login')
    return
  }
  user.value = JSON.parse(userData)
  if (!user.value) {
    router.push('/login')
    return
  }
  form.value.hoTen = user.value?.hoTen || ''
  form.value.email = user.value?.email || ''
  form.value.soDienThoai = user.value?.soDienThoai || ''
  form.value.diaChi = user.value?.diaChi || ''
})

const updateProfile = async () => {
  loading.value = true
  successMsg.value = ''
  errorMsg.value = ''
  
  try {
    const payload = { ...form.value }
    if (!payload.matKhau) delete payload.matKhau // Don't send empty password
    
    const res = await axios.put(`${API_URL}/api/nguoi-dung/${user.value.maNguoiDung}`, payload)
    const updatedUser = res.data.data || res.data
    
    localStorage.setItem('user', JSON.stringify(updatedUser))
    user.value = updatedUser
    successMsg.value = 'Cập nhật hồ sơ thành công!'
    window.dispatchEvent(new Event('storage'))
  } catch (err) {
    errorMsg.value = err.response?.data?.message || 'Lỗi khi cập nhật hồ sơ'
  } finally {
    loading.value = false
  }
}

const logout = () => {
  localStorage.removeItem('user')
  window.dispatchEvent(new Event('storage'))
  router.push('/login')
}
</script>

<style scoped>
.active-link {
  background-color: #f8f9fa;
  border-left: 3px solid #000;
}
.nav-link:hover {
  background-color: #f8f9fa;
}
</style>
