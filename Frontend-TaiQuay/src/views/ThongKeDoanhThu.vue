<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const data = ref([])

const loadData = async () => {
  try {
    // API thống kê do Kỳ viết trên nhánh ky (cần merge nhánh ky vào để chạy được)
    const res = await api.get('/bao-cao/loi-nhuan')
    data.value = res.data || []
  } catch (error) {
    console.warn('Cần gộp code nhánh ky để chạy API này')
  }
}

onMounted(() => loadData())

const exportExcel = () => {
  window.open('http://localhost:8080/api/bao-cao/loi-nhuan/export', '_blank')
}
</script>

<template>
  <div class="card shadow-sm mt-3">
    <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
      <h5 class="mb-0 fw-bold"><i class="bi bi-graph-up-arrow me-2"></i>Thống Kê Doanh Thu</h5>
      <button class="btn btn-success" @click="exportExcel"><i class="bi bi-file-earmark-excel me-1"></i>Xuất Excel</button>
    </div>
    <div class="card-body">
      <div class="alert alert-info">
        <i class="bi bi-info-circle me-2"></i><strong>Lưu ý:</strong> Chức năng này sẽ tự động lấy dữ liệu từ nhánh của Kỳ sau khi Sếp gộp code!
      </div>
      <table class="table table-bordered mt-3 text-center align-middle">
        <thead class="table-light">
          <tr>
            <th>Tháng</th>
            <th>Tổng Doanh Thu</th>
            <th>Vốn Nhập Hàng</th>
            <th>Lợi Nhuận Thuần</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="data.length === 0">
            <td colspan="4" class="text-muted py-4">Chưa có dữ liệu thống kê</td>
          </tr>
          <tr v-for="(item, idx) in data" :key="idx">
            <td class="fw-bold">Tháng {{ item.thang }}</td>
            <td class="text-primary">{{ (item.doanh_thu || 0).toLocaleString('vi-VN') }} đ</td>
            <td class="text-warning">{{ (item.von || 0).toLocaleString('vi-VN') }} đ</td>
            <td class="text-danger fw-bold">{{ (item.loi_nhuan || 0).toLocaleString('vi-VN') }} đ</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
