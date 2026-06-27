<template>
  <div class="container mt-4">
    <h2 class="mb-4">Dashboard Tài chính</h2>

    <!-- Thống kê tổng quan -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card text-white bg-primary">
          <div class="card-body">
            <h5 class="card-title">Tổng doanh thu</h5>
            <h3 class="card-text">{{ formatPrice(stats.totalRevenue) }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-success">
          <div class="card-body">
            <h5 class="card-title">Tổng lợi nhuận</h5>
            <h3 class="card-text">{{ formatPrice(stats.totalProfit) }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-info">
          <div class="card-body">
            <h5 class="card-title">Tổng đơn hàng</h5>
            <h3 class="card-text">{{ stats.totalOrders }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-warning">
          <div class="card-body">
            <h5 class="card-title">Đơn hoàn thành</h5>
            <h3 class="card-text">{{ stats.completedOrders }}</h3>
          </div>
        </div>
      </div>
    </div>

    <!-- Thông tin bổ sung -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h5>Tỷ lệ hoàn thành đơn</h5>
            <h3>{{ ((stats.completedOrders / stats.totalOrders) * 100).toFixed(1) }}%</h3>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h5>Tỷ lệ lợi nhuận</h5>
            <h3>{{ ((stats.totalProfit / stats.totalRevenue) * 100).toFixed(1) }}%</h3>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng top sản phẩm bán chạy -->
    <div class="card">
      <div class="card-body">
        <h5>Top sản phẩm bán chạy</h5>
        <div v-if="loading" class="text-center py-3">
          <div class="spinner-border text-primary"></div>
        </div>
        <div v-else class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Tên sản phẩm</th>
                <th>Số lượng bán</th>
                <th>Doanh thu</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in topProducts" :key="product.maSanPham">
                <td>{{ product.tenSanPham }}</td>
                <td>{{ product.soLuongBan }}</td>
                <td>{{ formatPrice(product.doanhThu) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const loading = ref(false)
const stats = ref({
  totalRevenue: 0,
  totalProfit: 0,
  totalOrders: 0,
  completedOrders: 0
})
const topProducts = ref([])

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const fetchStats = async () => {
  loading.value = true
  try {
    // Giả lập dữ liệu thống kê (cần backend API thực tế)
    stats.value = {
      totalRevenue: 150000000,
      totalProfit: 45000000,
      totalOrders: 150,
      completedOrders: 120
    }
    
    topProducts.value = [
      { maSanPham: 1, tenSanPham: 'Giày chạy bộ Nike', soLuongBan: 50, doanhThu: 25000000 },
      { maSanPham: 2, tenSanPham: 'Áo thể thao Adidas', soLuongBan: 35, doanhThu: 17500000 },
      { maSanPham: 3, tenSanPham: 'Quần short Puma', soLuongBan: 30, doanhThu: 12000000 },
      { maSanPham: 4, tenSanPham: 'Bóng đá FIFA', soLuongBan: 25, doanhThu: 7500000 },
      { maSanPham: 5, tenSanPham: 'Túi thể thao', soLuongBan: 20, doanhThu: 6000000 }
    ]
  } catch (error) {
    console.error('Lỗi khi tải thống kê:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>
