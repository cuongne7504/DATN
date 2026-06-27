<template>
  <div class="container mt-4">
    <h2 class="mb-4">Thanh toán</h2>

    <div class="row">
      <div class="col-md-8">
        <div class="card mb-4">
          <div class="card-body">
            <h5>Thông tin giao hàng</h5>
            <form @submit.prevent="createOrder">
              <div class="mb-3">
                <label class="form-label">Địa chỉ giao *</label>
                <input type="text" v-model="orderForm.diaChiGiao" class="form-control" required>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Phí ship</label>
                  <input type="number" v-model="orderForm.phiShip" class="form-control" value="0">
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Phương thức thanh toán</label>
                  <select v-model="orderForm.phuongThucTt" class="form-select">
                    <option>Tiền mặt (COD)</option>
                    <option>Chuyển khoản</option>
                  </select>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">Mã khuyến mãi</label>
                <input type="text" v-model="promoCode" class="form-control" placeholder="Nhập mã nếu có">
              </div>
              <button type="submit" class="btn btn-primary btn-lg" :disabled="loading">
                {{ loading ? 'Đang xử lý...' : 'Đặt hàng' }}
              </button>
            </form>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card">
          <div class="card-body">
            <h5>Tóm tắt đơn hàng</h5>
            <div v-if="cartData">
              <div v-for="item in cartData.items" :key="item.maCtGioHang" class="d-flex justify-content-between mb-2">
                <span>{{ item.tenSanPham }} x{{ item.soLuong }}</span>
                <span>{{ formatPrice(item.thanhTien) }}</span>
              </div>
              <hr>
              <div class="d-flex justify-content-between">
                <strong>Tổng tiền:</strong>
                <strong class="text-danger">{{ formatPrice(cartData.tongTien) }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const API_URL = 'http://localhost:8080'
const router = useRouter()

const cartData = ref(null)
const loading = ref(false)
const promoCode = ref('')

const orderForm = ref({
  maNguoiDung: 1,
  items: [],
  phiShip: 0,
  diaChiGiao: '',
  phuongThucTt: 'Tiền mặt (COD)',
  maKhuyenMai: null
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const fetchCart = async () => {
  try {
    const response = await axios.get(`${API_URL}/api/gio-hang/1`)
    if (response.data && response.data.success) {
      cartData.value = response.data.data
      orderForm.value.items = cartData.value.items.map(item => ({
        maChiTietSp: item.maChiTietSp,
        soLuong: item.soLuong,
        donGia: item.giaBan
      }))
    }
  } catch (error) {
    console.error('Lỗi khi tải giỏ hàng:', error)
  }
}

const createOrder = async () => {
  loading.value = true
  try {
    const response = await axios.post(`${API_URL}/api/don-hang`, orderForm.value)
    if (response.data && response.data.success) {
      alert('Đặt hàng thành công!')
      router.push('/orders')
    }
  } catch (error) {
    console.error('Lỗi khi đặt hàng:', error)
    alert('Lỗi khi đặt hàng')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCart()
})
</script>
