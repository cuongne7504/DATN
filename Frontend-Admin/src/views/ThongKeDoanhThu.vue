<template>
  <div class="container mt-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <h2>Thống kê Doanh thu</h2>
      
      <!-- Bộ lọc ngày & Nút Xuất Excel -->
      <div class="d-flex align-items-center gap-2 flex-wrap">
        <div class="d-flex align-items-center gap-1">
          <label class="text-muted fw-semibold me-1">Từ:</label>
          <input type="date" v-model="startDate" class="form-control form-control-sm" style="width: 140px;" />
        </div>
        <div class="d-flex align-items-center gap-1">
          <label class="text-muted fw-semibold me-1">Đến:</label>
          <input type="date" v-model="endDate" class="form-control form-control-sm" style="width: 140px;" />
        </div>
        <button @click="clearDates" class="btn btn-sm btn-outline-secondary me-2" v-if="startDate || endDate">
          Xóa lọc
        </button>
        <button @click="exportExcel" class="btn btn-success btn-sm d-flex align-items-center gap-1" :disabled="loadingExport">
          <i class="bi bi-file-earmark-excel"></i> 
          {{ loadingExport ? 'Đang xuất...' : 'Xuất Excel' }}
        </button>
      </div>
    </div>

    <!-- 4 Thẻ chỉ số (Đã đổi sang col-md-3 để vừa khít 1 hàng) -->
    <div class="row mb-4 g-3">
      <div class="col-md-3">
        <div class="card text-white bg-dark shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title opacity-75 fs-6">Tổng Doanh thu</h5>
            <h3 class="fw-bold mt-2">{{ formatPrice(totalRevenue) }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-success shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title opacity-75 fs-6">Tổng Lợi nhuận</h5>
            <h3 class="fw-bold mt-2">{{ formatPrice(totalProfit) }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-primary shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title opacity-75 fs-6">Tổng đơn hàng</h5>
            <h3 class="fw-bold mt-2">{{ totalOrders }}</h3>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-white bg-info shadow-sm h-100">
          <div class="card-body">
            <h5 class="card-title opacity-75 fs-6">Sản phẩm đã bán</h5>
            <h3 class="fw-bold mt-2">{{ totalSold }}</h3>
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <h5 class="mb-4 fw-semibold">Biểu đồ Doanh thu & Lợi nhuận theo sản phẩm</h5>
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
import { ref, computed, onMounted, watch } from 'vue'
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

import { API_URL } from '@/config.js'

const reportData = ref([])
const loading = ref(false)
const loadingExport = ref(false)

// Khai báo bộ lọc ngày
const startDate = ref('')
const endDate = ref('')

// Thêm Watcher để validate ngày
watch([startDate, endDate], ([newStart, newEnd]) => {
  if (newStart && newEnd) {
    const start = new Date(newStart)
    const end = new Date(newEnd)
    if (start > end) {
      alert('Ngày bắt đầu không thể lớn hơn ngày kết thúc!')
      // Reset ngày không hợp lệ vừa chọn về rỗng
      startDate.value = ''
    }
  }
})

const clearDates = () => {
  startDate.value = ''
  endDate.value = ''
}

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

// Lọc dữ liệu theo ngày đã chọn
const filteredReportData = computed(() => {
  if (!reportData.value) return []
  return reportData.value.filter(item => {
    if (!item.ngayDat) return true
    const itemDate = new Date(item.ngayDat)
    
    if (startDate.value) {
      const start = new Date(startDate.value + 'T00:00:00')
      if (itemDate < start) return false
    }
    
    if (endDate.value) {
      const end = new Date(endDate.value + 'T23:59:59')
      if (itemDate > end) return false
    }
    
    return true
  })
})

const totalRevenue = computed(() => {
  return filteredReportData.value.reduce((sum, item) => sum + ((item.donGiaBan || 0) * (item.soLuong || 0)), 0)
})

const totalProfit = computed(() => {
  return filteredReportData.value.reduce((sum, item) => sum + (item.tongLoiNhuanItem || 0), 0)
})

const totalSold = computed(() => {
  return filteredReportData.value.reduce((sum, item) => sum + (item.soLuong || 0), 0)
})

// Tính tổng số đơn hàng duy nhất từ dữ liệu đã lọc
const totalOrders = computed(() => {
  const orderIds = new Set(filteredReportData.value.map(item => item.maDonHang))
  return orderIds.size
})

const chartData = computed(() => {
  // Nhóm theo sản phẩm để tránh trùng lặp nhãn nếu một sản phẩm được bán nhiều lần
  const productSummary = {}
  filteredReportData.value.forEach(item => {
    const name = item.tenSanPham
    const revenue = (item.donGiaBan || 0) * (item.soLuong || 0)
    const profit = item.tongLoiNhuanItem || 0
    
    if (!productSummary[name]) {
      productSummary[name] = { revenue: 0, profit: 0 }
    }
    productSummary[name].revenue += revenue
    productSummary[name].profit += profit
  })

  // Chỉ lấy top 10 sản phẩm để biểu đồ không bị quá chật chội
  const labels = Object.keys(productSummary).slice(0, 10)
  const revenueData = labels.map(l => productSummary[l].revenue)
  const profitData = labels.map(l => productSummary[l].profit)

  return {
    labels: labels,
    datasets: [
      {
        label: 'Doanh thu',
        backgroundColor: '#0d6efd',
        data: revenueData
      },
      {
        label: 'Lợi nhuận',
        backgroundColor: '#198754',
        data: profitData
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
    // Để xuất dữ liệu khớp với bộ lọc, ta gửi kèm tham số ngày
    const params = {}
    if (startDate.value) params.startDate = startDate.value
    if (endDate.value) params.endDate = endDate.value

    const res = await axios.get(`${API_URL}/api/bao-cao/loi-nhuan/export`, {
      params,
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
    
    // Hiển thị thông báo thành công
    alert('Xuất file Excel báo cáo doanh thu & lợi nhuận thành công!')
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

