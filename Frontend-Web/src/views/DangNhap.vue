<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-4">
        <div class="card">
          <div class="card-body">
            <h3 class="text-center mb-4">Đăng nhập</h3>
            <form @submit.prevent="login">
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" v-model="form.email" class="form-control" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mật khẩu</label>
                <input type="password" v-model="form.matKhau" class="form-control" required>
              </div>
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
              </button>
            </form>
            <p v-if="error" class="text-danger mt-3 text-center">{{ error }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

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
    const response = await axios.post(`${API_URL}/api/nguoi-dung/login`, {
      email: form.value.email,
      matKhau: form.value.matKhau
    })

    if (response.data && response.data.success) {
      const user = response.data.data
      localStorage.setItem('user', JSON.stringify(user))
      
      // Trigger event để cập nhật Navbar
      window.dispatchEvent(new Event('storage'))
      
      // Redirect theo role: 1=Admin, 2=Nhân viên, 3=Khách hàng
      if (user.maQuyen === 1 || user.maQuyen === 2) {
        router.push('/admin/products')
      } else {
        router.push('/')
      }
    }
  } catch (err) {
    error.value = 'Đăng nhập thất bại. Vui lòng kiểm tra email và mật khẩu.'
  } finally {
    loading.value = false
  }
}
</script>
