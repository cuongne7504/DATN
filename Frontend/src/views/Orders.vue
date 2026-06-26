<template>
  <div class="container mt-4">
    <h2 class="mb-4">Lịch sử đơn hàng</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tải...</p>
    </div>

    <div v-else-if="orders.length === 0" class="text-center py-5">
      <p class="text-muted">Chưa có đơn hàng nào</p>
      <router-link to="/" class="btn btn-primary">Mua sắm ngay</router-link>
    </div>

    <div v-else>
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Ngày đặt</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th>Địa chỉ</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.maDonHang">
              <td>#{{ order.maDonHang }}</td>
              <td>{{ formatDate(order.ngayDat) }}</td>
              <td>{{ formatPrice(order.tongTien) }}</td>
              <td>
                <span class="badge" :class="getStatusClass(order.trangThai)">{{ order.trangThai }}</span>
              </td>
              <td>{{ order.diaChiGiao }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const orders = ref([])
const loading = ref(false)

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('vi-VN')
}

const getStatusClass = (status) => {
  switch (status) {
    case 'Đã giao hàng': return 'bg-success'
    case 'Đang giao hàng': return 'bg-info'
    case 'Đang xử lý': return 'bg-warning'
    case 'Đã hủy': return 'bg-danger'
    default: return 'bg-secondary'
  }
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_URL}/api/don-hang`)
    if (response.data && response.data.success) {
      orders.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
