<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>Thống kê Doanh thu</h2>
      <button @click="exportExcel" class="btn btn-success" :disabled="loadingExport">
        <i class="bi bi-file-earmark-excel"></i> 
        {{ loadingExport ? 'Đang xuất...' : 'Xuất Excel' }}
      </button>
    </div>

    <div class="row mb-4">
      <div class="col-md-4">
        <div class="card text-white bg-primary shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title">Tổng Doanh thu</h5>
            <h3 class="fw-bold">{{ formatPrice(totalRevenue) }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card text-white bg-success shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title">Tổng Lợi nhuận</h5>
            <h3 class="fw-bold">{{ formatPrice(totalProfit) }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card text-white bg-info shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title">Sản phẩm đã bán</h5>
            <h3 class="fw-bold">{{ totalSold }}</h3>
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <h5 class="mb-4">Biểu đồ Lợi nhuận</h5>
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-else style="height: 400px">
          <Bar v-if="chartData.labels.length > 0" :data="chartData" :options="chartOptions" />
          <div v-else class="text-center text-muted py-5">Chưa có dữ liệu thống kê</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import { Bar } from 'vue-chartjs'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const API_URL = 'http://localhost:8080'
const reportData = ref([])
const loading = ref(false)
const loadingExport = ref(false)

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const totalRevenue = computed(() => {
  return reportData.value.reduce((sum, item) => sum + ((item.donGiaBan || 0) * (item.soLuong || 0)), 0)
})

const totalProfit = computed(() => {
  return reportData.value.reduce((sum, item) => sum + (item.tongLoiNhuanItem || 0), 0)
})

const totalSold = computed(() => {
  return reportData.value.reduce((sum, item) => sum + (item.soLuong || 0), 0)
})

const chartData = computed(() => {
  return {
    labels: reportData.value.map(item => item.tenSanPham),
    datasets: [
      {
        label: 'Doanh thu',
        backgroundColor: '#0d6efd',
        data: reportData.value.map(item => (item.donGiaBan || 0) * (item.soLuong || 0))
      },
      {
        label: 'Lợi nhuận',
        backgroundColor: '#198754',
        data: reportData.value.map(item => item.tongLoiNhuanItem || 0)
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    }
  }
}

const fetchReport = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/bao-cao/loi-nhuan`)
    reportData.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi tải báo cáo:', error)
  } finally {
    loading.value = false
  }
}

const exportExcel = async () => {
  loadingExport.value = true
  try {
    const res = await axios.get(`${API_URL}/api/bao-cao/loi-nhuan/export`, {
      responseType: 'blob'
    })
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'bao_cao_loi_nhuan.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('Lỗi xuất Excel:', error)
    alert('Không thể xuất Excel')
  } finally {
    loadingExport.value = false
  }
}

onMounted(() => {
  fetchReport()
})
</script>
