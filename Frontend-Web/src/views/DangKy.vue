<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow border-0 rounded-3 mt-5">
          <div class="card-body p-5">
            <h3 class="text-center mb-4 fw-bold text-primary">ĐĂNG KÝ TÀI KHOẢN</h3>
            
            <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>
            
            <form @submit.prevent="register">
              <div class="mb-3">
                <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                  <input type="text" v-model="form.hoTen" class="form-control" required placeholder="Nhập họ và tên">
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                  <input type="email" v-model="form.email" class="form-control" required placeholder="Nhập email của bạn">
                </div>
              </div>
              
              <div class="mb-3">
                <label class="form-label fw-semibold">Số điện thoại</label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-telephone"></i></span>
                  <input type="tel" v-model="form.soDienThoai" class="form-control" placeholder="Nhập số điện thoại">
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label fw-semibold">Địa chỉ</label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-geo-alt"></i></span>
                  <input type="text" v-model="form.diaChi" class="form-control" placeholder="Nhập địa chỉ của bạn">
                </div>
              </div>
              
              <div class="mb-3">
                <label class="form-label fw-semibold">Mật khẩu <span class="text-danger">*</span></label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                  <input type="password" v-model="form.matKhau" class="form-control" required placeholder="••••••••">
                </div>
              </div>

              <div class="mb-4">
                <label class="form-label fw-semibold">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-lock-fill"></i></span>
                  <input type="password" v-model="confirmPassword" class="form-control" required placeholder="••••••••">
                </div>
              </div>
              
              <button type="submit" class="btn btn-primary w-100 fw-bold py-2" :disabled="loading">
                {{ loading ? 'Đang xử lý...' : 'ĐĂNG KÝ' }}
              </button>
            </form>
            
            <div class="text-center mt-4">
              <span class="text-muted">Đã có tài khoản?</span>
              <router-link to="/login" class="text-primary fw-bold ms-1 text-decoration-none">Đăng nhập</router-link>
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

import { API_URL } from '@/config.js'
const router = useRouter()

const form = ref({
  hoTen: '',
  email: '',
  soDienThoai: '',
  diaChi: '',
  matKhau: '',
  maQuyen: 3
})
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

const register = async () => {
  if (form.value.matKhau !== confirmPassword.value) {
    error.value = 'Mật khẩu xác nhận không khớp!'
    return
  }

  if (form.value.matKhau.length < 6) {
    error.value = 'Mật khẩu phải có ít nhất 6 ký tự!'
    return
  }

  loading.value = true
  error.value = ''
  
  try {
    await axios.post(`${API_URL}/api/nguoi-dung/register`, form.value)
    alert('Đăng ký thành công! Vui lòng đăng nhập.')
    router.push('/login')
  } catch (err) {
    error.value = err.response?.data?.message || 'Đăng ký thất bại. Email có thể đã tồn tại!'
  } finally {
    loading.value = false
  }
}
</script>
