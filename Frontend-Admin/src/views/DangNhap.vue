<template>
  <div class="login-page">
    <div class="container">
      <div class="row justify-content-center align-items-center" style="min-height: 80vh;">
        <div class="col-md-5">
          <div class="card shadow-lg border-0 rounded-4 overflow-hidden">
          <div class="card-body p-5">
            <h3 class="text-center mb-4 fw-bold text-primary">ĐĂNG NHẬP ADMIN</h3>
            
            <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>
            
            <form @submit.prevent="login">
              <div class="mb-3">
                <label class="form-label fw-semibold">Email</label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                  <input type="email" v-model="form.email" class="form-control" required placeholder="admin@example.com">
                </div>
              </div>
              
              <div class="mb-4">
                <label class="form-label fw-semibold">Mật khẩu</label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                  <input type="password" v-model="form.matKhau" class="form-control" required placeholder="••••••••">
                </div>
              </div>
              
              <button type="submit" class="btn btn-primary w-100 fw-bold py-2" :disabled="loading">
                {{ loading ? 'Đang xử lý...' : 'ĐĂNG NHẬP' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const API_URL = 'http://localhost:8080'
const router = useRouter()

const form = ref({
  email: '',
  matKhau: ''
})

const loading = ref(false)
const error = ref('')

const login = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const res = await axios.post(`${API_URL}/api/nguoi-dung/login`, form.value)
    const user = res.data.data || res.data
    
    // Kiểm tra quyền Admin (Giả sử maQuyen = 1 là Admin, 2 là Nhân viên)
    if (user.maQuyen !== 1 && user.maQuyen !== 2) {
      error.value = 'Bạn không có quyền truy cập hệ thống Quản trị!'
      return
    }
    
    localStorage.setItem('user', JSON.stringify(user))
    window.dispatchEvent(new Event('storage')) // Trigger update Navbar
    router.push('/admin/dashboard')
    
  } catch (err) {
    error.value = err.response?.data?.message || 'Email hoặc mật khẩu không chính xác!'
  } finally {
    loading.value = false
  }
}
</script>
