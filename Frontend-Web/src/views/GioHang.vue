<template>
  <div class="container mt-4">
    <h2 class="mb-4">Giỏ hàng</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tải...</p>
    </div>

    <div v-else-if="!cartData || cartData.items.length === 0" class="text-center py-5">
      <p class="text-muted">Giỏ hàng trống</p>
      <router-link to="/" class="btn btn-primary">Tiếp tục mua sắm</router-link>
    </div>

    <div v-else>
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Biến thể</th>
              <th>Số lượng</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in cartData.items" :key="item.maCtGioHang">
              <td>{{ item.tenSanPham }}</td>
              <td>{{ item.mauSac }} / {{ item.kichCo }}</td>
              <td>{{ item.soLuong }}</td>
              <td>{{ formatPrice(item.giaBan) }}</td>
              <td>{{ formatPrice(item.thanhTien) }}</td>
              <td>
                <button @click="removeFromCart(item.maCtGioHang)" class="btn btn-sm btn-danger">Xóa</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="row mt-4">
        <div class="col-md-6">
          <router-link to="/" class="btn btn-secondary">Tiếp tục mua sắm</router-link>
        </div>
        <div class="col-md-6 text-end">
          <h4>Tổng tiền: {{ formatPrice(cartData.tongTien) }}</h4>
          <router-link to="/checkout" class="btn btn-primary btn-lg">Thanh toán</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const cartData = ref(null)
const loading = ref(false)

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const fetchCart = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_URL}/api/gio-hang/1`)
    if (response.data && response.data.success) {
      cartData.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải giỏ hàng:', error)
  } finally {
    loading.value = false
  }
}

const removeFromCart = async (maCtGioHang) => {
  if (!confirm('Bạn có chắc muốn xóa?')) return
  try {
    await axios.delete(`${API_URL}/api/gio-hang/1/items/${maCtGioHang}`)
    await fetchCart()
  } catch (error) {
    console.error('Lỗi khi xóa:', error)
  }
}

onMounted(() => {
  fetchCart()
})
</script>
