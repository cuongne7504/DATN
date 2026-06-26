<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h2 class="card-title text-center mb-4">Đăng ký tài khoản</h2>
            
            <form @submit.prevent="register">
              <div class="mb-3">
                <label class="form-label">Họ tên *</label>
                <input type="text" v-model="form.hoTen" class="form-control" required>
              </div>
              
              <div class="mb-3">
                <label class="form-label">Email *</label>
                <input type="email" v-model="form.email" class="form-control" required>
              </div>
              
              <div class="mb-3">
                <label class="form-label">Số điện thoại *</label>
                <input type="tel" v-model="form.soDienThoai" class="form-control" required>
              </div>
              
              <div class="mb-3">
                <label class="form-label">Địa chỉ</label>
                <input type="text" v-model="form.diaChi" class="form-control">
              </div>
              
              <div class="mb-3">
                <label class="form-label">Mật khẩu *</label>
                <input type="password" v-model="form.matKhau" class="form-control" required minlength="6">
              </div>
              
              <div class="mb-3">
                <label class="form-label">Xác nhận mật khẩu *</label>
                <input type="password" v-model="form.xacNhanMatKhau" class="form-control" required minlength="6">
              </div>
              
              <div v-if="error" class="alert alert-danger">
                {{ error }}
              </div>
              
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                {{ loading ? 'Đang đăng ký...' : 'Đăng ký' }}
              </button>
            </form>
            
            <div class="text-center mt-3">
              <p>Đã có tài khoản? <router-link to="/login">Đăng nhập</router-link></p>
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

const loading = ref(false)
const error = ref('')

const form = ref({
  hoTen: '',
  email: '',
  soDienThoai: '',
  diaChi: '',
  matKhau: '',
  xacNhanMatKhau: ''
})

const register = async () => {
  // Validate
  if (form.value.matKhau !== form.value.xacNhanMatKhau) {
    error.value = 'Mật khẩu xác nhận không khớp'
    return
  }
  
  if (form.value.matKhau.length < 6) {
    error.value = 'Mật khẩu phải có ít nhất 6 ký tự'
    return
  }
  
  loading.value = true
  error.value = ''
  
  try {
    const payload = {
      hoTen: form.value.hoTen,
      email: form.value.email,
      soDienThoai: form.value.soDienThoai,
      diaChi: form.value.diaChi,
      matKhau: form.value.matKhau,
      maQuyen: 3 // Mặc định là Khách hàng
    }
    
    const response = await axios.post(`${API_URL}/api/nguoi-dung/register`, payload)
    
    if (response.data && response.data.success) {
      alert('Đăng ký thành công! Vui lòng đăng nhập.')
      router.push('/login')
    }
  } catch (err) {
    console.error('Lỗi khi đăng ký:', err)
    error.value = err.response?.data?.message || 'Đăng ký thất bại. Email có thể đã tồn tại.'
  } finally {
    loading.value = false
  }
}
</script>
