<template>
  <div class="login-page">
    <div class="login-orb login-orb-a"></div>
    <div class="login-orb login-orb-b"></div>
    <div class="login-grid"></div>

    <div class="login-card">
      <div class="login-brand">
        <img src="/logo.png" alt="SportPro" class="login-logo" />
        <div>
          <h1>Đăng nhập</h1>
          <p>Tiếp tục quản lý cửa hàng của bạn</p>
        </div>
      </div>

      <div v-if="error" class="alert alert-danger py-2 mb-3">{{ error }}</div>

      <form @submit.prevent="login" class="login-form">
        <div class="mb-3">
          <label class="form-label">Email</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
            <input
              type="email"
              v-model="form.email"
              class="form-control"
              required
              placeholder="admin@sportpro.com"
            />
          </div>
        </div>

        <div class="mb-4">
          <label class="form-label">Mật khẩu</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-lock"></i></span>
            <input
              :type="showPassword ? 'text' : 'password'"
              v-model="form.matKhau"
              class="form-control"
              required
              placeholder="••••••••"
            />
            <button class="btn btn-outline-secondary" type="button" @click="showPassword = !showPassword">
              <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
            </button>
          </div>
        </div>

        <button type="submit" class="btn btn-primary w-100 login-btn" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? 'Đang xử lý...' : 'Đăng nhập' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { API_URL } from '@/config.js'

const router = useRouter()
const showPassword = ref(false)

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

    if (user.maQuyen !== 1 && user.maQuyen !== 2) {
      error.value = 'Bạn không có quyền truy cập hệ thống Quản trị!'
      return
    }

    localStorage.setItem('user', JSON.stringify(user))
    localStorage.removeItem('admin_remember_email')
    window.dispatchEvent(new Event('storage'))
    router.push(user.maQuyen === 2 ? '/admin/pos' : '/admin/dashboard')
  } catch (err) {
    error.value = err.response?.data?.message || 'Email hoặc mật khẩu không chính xác!'
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
  width: min(440px, 100%);
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 22px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.1);
  padding: 2rem 2rem 2.15rem;
  position: relative;
  z-index: 1;
  animation: rise 0.45s ease;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 0.95rem;
  margin-bottom: 1.4rem;
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
  font-size: 1.35rem;
  font-weight: 780;
  letter-spacing: -0.02em;
  color: #0f172a;
}

.login-brand p {
  margin: 0.15rem 0 0;
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 500;
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

@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(14px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
