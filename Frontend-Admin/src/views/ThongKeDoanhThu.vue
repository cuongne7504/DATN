<template>
  <div class="container mt-4">
    <PageHeader
      title="Thống kê Doanh thu"
      subtitle="Theo dõi hiệu suất bán hàng theo khoảng thời gian"
    >
      <template #actions>
        <div class="filter-bar">
          <input type="date" v-model="startDate" class="form-control form-control-sm date-input" />
          <span class="text-muted small fw-semibold">→</span>
          <input type="date" v-model="endDate" class="form-control form-control-sm date-input" />
          <button v-if="startDate || endDate" @click="clearDates" class="btn btn-sm btn-outline-secondary">
            Xóa lọc
          </button>
          <button @click="exportExcel" class="btn btn-success btn-sm" :disabled="loadingExport">
            <i class="bi bi-file-earmark-excel me-1"></i>
            {{ loadingExport ? 'Đang xuất...' : 'Xuất Excel' }}
          </button>
        </div>
      </template>
    </PageHeader>

    <SkeletonLoader v-if="loading" variant="kpi" :rows="4" class="mb-4" />
    <div v-else class="row mb-4 g-3">
      <div class="col-md-3 col-sm-6">
        <div class="sp-kpi-card sp-kpi-blue">
          <div class="kpi-label"><i class="bi bi-cash-stack me-1"></i>Tổng Doanh thu</div>
          <p class="kpi-value">{{ formatPrice(totalRevenue) }}</p>
        </div>
      </div>
      <div class="col-md-3 col-sm-6">
        <div class="sp-kpi-card sp-kpi-green">
          <div class="kpi-label"><i class="bi bi-graph-up-arrow me-1"></i>Tổng Lợi nhuận</div>
          <p class="kpi-value">{{ formatPrice(totalProfit) }}</p>
        </div>
      </div>
      <div class="col-md-3 col-sm-6">
        <div class="sp-kpi-card sp-kpi-orange">
          <div class="kpi-label"><i class="bi bi-bag-check me-1"></i>Tổng đơn hàng</div>
          <p class="kpi-value">{{ totalOrders }}</p>
        </div>
      </div>
      <div class="col-md-3 col-sm-6">
        <div class="sp-kpi-card sp-kpi-cyan">
          <div class="kpi-label"><i class="bi bi-boxes me-1"></i>Sản phẩm đã bán</div>
          <p class="kpi-value">{{ totalSold }}</p>
        </div>
      </div>
    </div>

    <div class="card chart-card mb-4">
      <div class="card-body p-4">
        <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
          <h5 class="mb-0 fw-semibold">Biểu đồ Doanh thu & Lợi nhuận</h5>
          <span class="text-muted small">Top 10 sản phẩm</span>
        </div>
        <SkeletonLoader v-if="loading" variant="chart" />
        <EmptyState
          v-else-if="chartData.labels.length === 0"
          icon="bi bi-bar-chart"
          title="Chưa có dữ liệu thống kê"
          description="Không có giao dịch trong khoảng thời gian đã chọn"
        />
        <div v-else style="height: 400px">
          <Bar :data="chartData" :options="chartOptions" />
        </div>
      </div>
    </div>

    <!-- Bảng chi tiết giao dịch bán hàng -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h5 class="mb-0 fw-bold"><i class="bi bi-receipt me-2 text-primary"></i>Chi tiết sản phẩm đã bán</h5>
        <span class="badge bg-primary-subtle text-primary border">{{ filteredReportData.length }} bản ghi</span>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>Mã ĐH</th>
                <th>Ngày đặt</th>
                <th>Tên sản phẩm</th>
                <th>Phân loại</th>
                <th class="text-center">Số lượng</th>
                <th class="text-end">Đơn giá bán</th>
                <th class="text-end">Doanh thu</th>
                <th class="text-end">Lợi nhuận</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in paginatedReportData" :key="idx">
                <td class="fw-bold text-primary">#{{ item.maDonHang }}</td>
                <td>{{ formatDate(item.ngayDat) }}</td>
                <td class="fw-semibold">{{ item.tenSanPham }}</td>
                <td>
                  <span class="badge bg-light text-dark border">
                    {{ item.mauSac || '-' }} / {{ item.kichCo || '-' }}
                  </span>
                </td>
                <td class="text-center fw-bold">{{ item.soLuong }}</td>
                <td class="text-end">{{ formatPrice(item.donGiaBan) }}</td>
                <td class="text-end fw-semibold text-primary">{{ formatPrice((item.donGiaBan || 0) * (item.soLuong || 0)) }}</td>
                <td class="text-end fw-bold text-success">{{ formatPrice(item.tongLoiNhuanItem) }}</td>
              </tr>
              <tr v-if="filteredReportData.length === 0">
                <td colspan="8" class="text-center py-4 text-muted">Không có dữ liệu chi tiết trong khoảng thời gian này</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="card-footer bg-white py-2 d-flex justify-content-between align-items-center" v-if="totalPages > 1">
        <small class="text-muted">Hiển thị trang {{ currentPage }} / {{ totalPages }}</small>
        <div class="btn-group btn-group-sm">
          <button class="btn btn-outline-secondary" :disabled="currentPage <= 1" @click="currentPage--">
            <i class="bi bi-chevron-left"></i>
          </button>
          <button class="btn btn-outline-secondary" :disabled="currentPage >= totalPages" @click="currentPage++">
            <i class="bi bi-chevron-right"></i>
          </button>
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
import { API_URL } from '@/config.js'
import PageHeader from '@/components/PageHeader.vue'
import EmptyState from '@/components/EmptyState.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'
import { useToast } from '@/composables/useToast.js'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const { success, error } = useToast()
const reportData = ref([])
const loading = ref(false)
const loadingExport = ref(false)
const startDate = ref('')
const endDate = ref('')

watch([startDate, endDate], ([newStart, newEnd]) => {
  if (newStart && newEnd && new Date(newStart) > new Date(newEnd)) {
    error('Ngày bắt đầu không thể lớn hơn ngày kết thúc')
    startDate.value = ''
  }
})

const clearDates = () => {
  startDate.value = ''
  endDate.value = ''
}

const formatPrice = (price) =>
  price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'

const filteredReportData = computed(() => {
  if (!reportData.value) return []
  return reportData.value.filter((item) => {
    if (!item.ngayDat) return true
    const itemDate = new Date(item.ngayDat)
    if (startDate.value && itemDate < new Date(startDate.value + 'T00:00:00')) return false
    if (endDate.value && itemDate > new Date(endDate.value + 'T23:59:59')) return false
    return true
  })
})

const totalRevenue = computed(() =>
  filteredReportData.value.reduce((sum, item) => sum + (item.donGiaBan || 0) * (item.soLuong || 0), 0)
)
const totalProfit = computed(() =>
  filteredReportData.value.reduce((sum, item) => sum + (item.tongLoiNhuanItem || 0), 0)
)
const totalSold = computed(() =>
  filteredReportData.value.reduce((sum, item) => sum + (item.soLuong || 0), 0)
)
const totalOrders = computed(() => new Set(filteredReportData.value.map((item) => item.maDonHang)).size)

const chartData = computed(() => {
  const productSummary = {}
  filteredReportData.value.forEach((item) => {
    const name = item.tenSanPham
    const revenue = (item.donGiaBan || 0) * (item.soLuong || 0)
    const profit = item.tongLoiNhuanItem || 0
    if (!productSummary[name]) productSummary[name] = { revenue: 0, profit: 0 }
    productSummary[name].revenue += revenue
    productSummary[name].profit += profit
  })

  const labels = Object.keys(productSummary).slice(0, 10)
  return {
    labels,
    datasets: [
      {
        label: 'Doanh thu',
        backgroundColor: '#1d4ed8',
        borderRadius: 8,
        data: labels.map((l) => productSummary[l].revenue)
      },
      {
        label: 'Lợi nhuận',
        backgroundColor: '#f97316',
        borderRadius: 8,
        data: labels.map((l) => productSummary[l].profit)
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'top' }
  },
  scales: {
    x: { grid: { display: false } },
    y: { grid: { color: '#eef2f7' } }
  }
}

const fetchReport = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/bao-cao/loi-nhuan`)
    reportData.value = res.data.data || res.data || []
  } catch (e) {
    error('Không tải được báo cáo')
  } finally {
    loading.value = false
  }
}

const exportExcel = async () => {
  loadingExport.value = true
  try {
    const params = {}
    if (startDate.value) params.startDate = startDate.value
    if (endDate.value) params.endDate = endDate.value
    const res = await axios.get(`${API_URL}/api/bao-cao/loi-nhuan/export`, { params, responseType: 'blob' })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'bao_cao_loi_nhuan.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    success('Xuất Excel thành công')
  } catch (e) {
    error('Không thể xuất Excel')
  } finally {
    loadingExport.value = false
  }
}

const currentPage = ref(1)
const pageSize = 10
const totalPages = computed(() => Math.ceil(filteredReportData.value.length / pageSize) || 1)
const paginatedReportData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredReportData.value.slice(start, start + pageSize)
})

watch([startDate, endDate], () => {
  currentPage.value = 1
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return isNaN(d.getTime()) ? dateStr : d.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(fetchReport)
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.45rem;
}

.date-input {
  width: 140px;
}

.chart-card {
  border: 1px solid var(--sp-border);
  box-shadow: var(--sp-shadow-sm);
}
</style>
