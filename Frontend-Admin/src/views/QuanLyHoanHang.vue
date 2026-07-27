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
                  <div class="d-flex justify-content-center gap-2">
                    <button @click="viewDetail(req)" class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#orderDetailModal">Chi tiết</button>
                    <select 
                      class="form-select form-select-sm border-primary shadow-sm" 
                      style="width: 140px; display: inline-block; cursor: pointer;" 
                      @change="updateStatus(req, $event.target.value); $event.target.value = ''"
                    >
                      <option value="" disabled selected>Chọn thao tác...</option>
                      <option value="Đã duyệt">✅ Duyệt yêu cầu</option>
                      <option value="Đã hoàn tiền">💸 Đã hoàn tiền</option>
                      <option value="Từ chối">❌ Từ chối</option>
                    </select>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Chi Tiết Đơn Hàng -->
    <div class="modal fade" id="orderDetailModal" tabindex="-1">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết đơn hàng #{{ selectedOrder?.maDonHang }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body" v-if="selectedOrder">
            <div class="row mb-4 g-3">
              <div class="col-md-6">
                <div class="border p-3 rounded h-100 bg-light">
                  <h6 class="fw-bold">Thông tin giao hàng</h6>
                  <p class="mb-1"><strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}</p>
                  <p class="mb-0"><strong>Địa chỉ:</strong> {{ selectedOrder.diaChiGiao || 'Bán tại quầy' }}</p>
                </div>
              </div>
              <div class="col-md-6">
                <div class="border p-3 rounded h-100 bg-light">
                  <h6 class="fw-bold">Thanh toán</h6>
                  <p class="mb-1"><strong>Phương thức:</strong> {{ selectedOrder.phuongThucTt }}</p>
                  <p class="mb-1"><strong>Trạng thái:</strong> <span class="badge bg-secondary">{{ selectedOrder.trangThai }}</span></p>
                </div>
              </div>
            </div>
            <h6 class="fw-bold border-bottom pb-2">Sản phẩm</h6>
            <table class="table table-sm align-middle">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.chiTietList" :key="item.maCtDonHang">
                  <td>
                    <div>{{ item.sanPham?.tenSanPham }}</div>
                    <small class="text-muted">Màu {{ item.chiTietSanPham?.mauSac }} - Size {{ item.chiTietSanPham?.kichCo }}</small>
                  </td>
                  <td>{{ item.soLuong }}</td>
                  <td>{{ formatPrice(item.donGia) }}</td>
                  <td>{{ formatPrice(item.soLuong * item.donGia) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          </div>
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
const selectedOrder = ref(null)

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
  if (newStatus === 'Đã hoàn tiền' && req.trangThai !== 'Đã duyệt') {
    alert('Vui lòng "Duyệt yêu cầu" trước khi tiến hành hoàn tiền!')
    return
  }
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

const viewDetail = async (req) => {
  selectedOrder.value = null
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/${req.maDonHang}`)
    selectedOrder.value = res.data.data || res.data
  } catch (error) {
    console.error('Lỗi tải chi tiết đơn hàng:', error)
    alert('Không thể lấy chi tiết đơn hàng!')
  }
}

onMounted(() => {
  fetchRequests()
})
</script>
