<template>
  <div class="login-page">
    <div class="login-orb login-orb-a"></div>
    <div class="login-orb login-orb-b"></div>
    <div class="login-grid"></div>

    <div class="login-card register-card">
      <div class="login-brand">
        <img src="/logo-sm.png" alt="SportPro" class="login-logo" width="72" height="72" />
        <div>
          <h1>Đăng ký tài khoản</h1>
          <p>Tạo tài khoản để mua sắm nhanh hơn</p>
        </div>
      </div>

      <div v-if="error" class="alert alert-danger py-2 mb-3">{{ error }}</div>

      <form @submit.prevent="register">
        <div class="mb-3">
          <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-person"></i></span>
            <input type="text" v-model="form.hoTen" class="form-control" required placeholder="Nhập họ và tên" />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Email <span class="text-danger">*</span></label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
            <input type="email" v-model="form.email" class="form-control" required placeholder="Nhập email của bạn" />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Số điện thoại</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-telephone"></i></span>
            <input type="tel" v-model="form.soDienThoai" class="form-control" placeholder="Nhập số điện thoại" />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Địa chỉ</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
            <input type="text" v-model="form.diaChi" class="form-control" placeholder="Nhập địa chỉ của bạn" />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-lock"></i></span>
            <input type="password" v-model="form.matKhau" class="form-control" required placeholder="••••••••" />
          </div>
        </div>

        <div class="mb-4">
          <label class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
            <input type="password" v-model="confirmPassword" class="form-control" required placeholder="••••••••" />
          </div>
        </div>

        <button type="submit" class="btn btn-primary w-100 login-btn" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? 'Đang xử lý...' : 'Đăng ký' }}
        </button>
      </form>

      <div class="text-center mt-4">
        <span class="text-muted">Đã có tài khoản?</span>
        <router-link to="/login" class="fw-bold ms-1">Đăng nhập</router-link>
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

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 1.5rem;
  position: relative;
  overflow: hidden;
  background: linear-gradient(160deg, #eff6ff 0%, #fff7ed 45%, #f8fafc 100%);
}

.login-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(29, 78, 216, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(29, 78, 216, 0.04) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: radial-gradient(circle at center, black, transparent 75%);
  pointer-events: none;
}

.login-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(10px);
  animation: float 8s ease-in-out infinite;
}

.login-orb-a {
  width: 280px;
  height: 280px;
  background: rgba(29, 78, 216, 0.18);
  top: -60px;
  left: -40px;
}

.login-orb-b {
  width: 320px;
  height: 320px;
  background: rgba(249, 115, 22, 0.16);
  right: -80px;
  bottom: -90px;
  animation-delay: -2.5s;
}

.login-card {
  width: min(480px, 100%);
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 22px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.1);
  padding: 2rem;
  position: relative;
  z-index: 1;
}

.register-card {
  margin: 1.5rem 0;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 0.95rem;
  margin-bottom: 1.25rem;
}

.login-logo {
  width: 52px;
  height: 52px;
  object-fit: contain;
  border-radius: 16px;
  background: #fff;
  padding: 6px;
  box-shadow: 0 8px 20px rgba(249, 115, 22, 0.15);
}

.login-brand h1 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 780;
  color: #0f172a;
}

.login-brand p {
  margin: 0.15rem 0 0;
  color: #64748b;
  font-size: 0.88rem;
}

.login-btn {
  min-height: 46px;
  border-radius: 12px !important;
}

.input-group-text {
  background: #f8fafc !important;
  color: #64748b;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(16px); }
}
</style>
