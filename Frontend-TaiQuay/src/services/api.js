import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

// Tự động "bóc vỏ hộp" ApiResponse
api.interceptors.response.use(
  (response) => {
    const box = response.data;
    if (box && typeof box.success !== 'undefined') {
      if (box.success) return { data: box.data, message: box.message };
      return Promise.reject(new Error(box.message || 'Lỗi hệ thống'));
    }
    return response;
  },
  (error) => Promise.reject(error)
)

export default api
