<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold m-0"><i class="bi bi-arrow-return-left text-primary"></i> Quản lý Yêu cầu Hoàn Hàng</h2>
    </div>

    <div class="card shadow-sm border-0">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>Mã YC</th>
                <th>Mã ĐH</th>
                <th>Lý do</th>
                <th>Hình ảnh</th>
                <th>Số tiền hoàn</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th class="text-center">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="8" class="text-center py-4">Đang tải dữ liệu...</td>
              </tr>
              <tr v-else-if="requests.length === 0">
                <td colspan="8" class="text-center py-4 text-muted">Chưa có yêu cầu hoàn hàng nào.</td>
              </tr>
              <tr v-for="req in requests" :key="req.maYeuCau">
                <td class="fw-bold">#{{ req.maYeuCau }}</td>
                <td><router-link :to="'/orders/' + req.maDonHang">#{{ req.maDonHang }}</router-link></td>
                <td style="max-width: 200px;" class="text-truncate" :title="req.lyDo">{{ req.lyDo }}</td>
                <td>
                  <a v-if="req.hinhAnhMinhHoa" :href="req.hinhAnhMinhHoa" target="_blank" class="btn btn-sm btn-outline-secondary">Xem ảnh</a>
                  <span v-else class="text-muted">Không có</span>
                </td>
                <td class="text-danger fw-bold">{{ formatPrice(req.soTienHoan) }}</td>
                <td>
                  <span class="badge" :class="getStatusBadgeClass(req.trangThai)">
                    {{ req.trangThai }}
                  </span>
                </td>
                <td>{{ formatDate(req.ngayTao) }}</td>
                <td class="text-center">
                  <select 
                    class="form-select form-select-sm border-primary shadow-sm" 
                    style="width: 150px; display: inline-block; cursor: pointer;" 
                    @change="updateStatus(req, $event.target.value); $event.target.value = ''"
                  >
                    <option value="" disabled selected>Chọn thao tác...</option>
                    <option value="Đã duyệt">✅ Duyệt yêu cầu</option>
                    <option value="Đã nhận hàng (Nhập kho)">📦 Nhập lại kho</option>
                    <option value="Đã hoàn tiền">💸 Đã hoàn tiền</option>
                    <option value="Từ chối">❌ Từ chối</option>
                  </select>
                </td>
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
import { API_URL } from '@/config.js'

const requests = ref([])
const loading = ref(false)

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('vi-VN')
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'Chờ duyệt': return 'bg-warning text-dark'
    case 'Đã duyệt': return 'bg-info text-dark'
    case 'Đã nhận hàng (Nhập kho)': return 'bg-primary'
    case 'Đã hoàn tiền': return 'bg-success'
    case 'Từ chối': return 'bg-danger'
    default: return 'bg-secondary'
  }
}

const fetchRequests = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/hoan-hang`)
    requests.value = res.data.data || res.data || []
    // Sắp xếp yêu cầu mới nhất lên đầu
    requests.value.sort((a, b) => b.maYeuCau - a.maYeuCau)
  } catch (error) {
    console.error('Lỗi tải danh sách yêu cầu hoàn hàng:', error)
  } finally {
    loading.value = false
  }
}

const updateStatus = async (req, newStatus) => {
  if (!confirm(`Bạn chắc chắn muốn cập nhật yêu cầu #${req.maYeuCau} thành "${newStatus}"?`)) return
  
  try {
    await axios.put(`${API_URL}/api/hoan-hang/${req.maYeuCau}/trang-thai?trangThaiMoi=${newStatus}`)
    alert('Cập nhật trạng thái thành công!')
    await fetchRequests()
  } catch (error) {
    console.error('Lỗi cập nhật:', error)
    alert('Không thể cập nhật trạng thái!')
  }
}

onMounted(() => {
  fetchRequests()
})
</script>
