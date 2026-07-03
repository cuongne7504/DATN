<template>
  <div class="container mt-4">
    <h2 class="mb-4 fw-bold">Lịch sử Đơn hàng của bạn</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="orders.length === 0" class="text-center py-5 bg-light rounded shadow-sm">
      <i class="bi bi-bag-x text-muted" style="font-size: 4rem;"></i>
      <h4 class="mt-3 text-muted">Chưa có đơn hàng nào</h4>
      <p>Bạn chưa thực hiện bất kỳ giao dịch nào.</p>
      <router-link to="/" class="btn btn-primary mt-2 px-4 fw-bold">Bắt đầu mua sắm</router-link>
    </div>

    <div v-else class="row">
      <div class="col-12">
        <div class="card shadow-sm border-0 mb-4" v-for="orderInfo in orders" :key="orderInfo.maDonHang">
          <div class="card-header bg-white border-bottom pt-3 pb-3">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <span class="fw-bold fs-5">Mã đơn: #{{ orderInfo.maDonHang }}</span>
                <span class="ms-3 text-muted"><i class="bi bi-clock"></i> Đặt ngày: {{ formatDate(orderInfo.ngayDat) }}</span>
              </div>
              <span class="badge px-3 py-2 fs-6" :class="getStatusBadgeClass(orderInfo.trangThai)">
                {{ getStatusText(orderInfo.trangThai) }}
              </span>
            </div>
          </div>
          <div class="card-body">
            <!-- Sản phẩm -->
            <div class="d-flex border-bottom pb-3 mb-3" v-for="item in orderInfo.chiTietList" :key="item.maCtDonHang">
              <div class="flex-grow-1">
                <h6 class="fw-bold mb-1">{{ item.sanPham?.tenSanPham || 'Sản phẩm (Mã SKU: ' + (item.chiTietSanPham?.maVachSku || item.maChiTietSp) + ')' }}</h6>
                <div class="text-muted small mb-2">
                  Phân loại: Màu {{ item.chiTietSanPham?.mauSac || 'N/A' }} - Size {{ item.chiTietSanPham?.kichCo || 'N/A' }}<br>
                  x{{ item.soLuong }}
                </div>
              </div>
              <div class="text-end fw-bold text-primary">
                {{ formatPrice(item.soLuong * item.donGia) }}
              </div>
            </div>

            <!-- Tổng kết -->
            <div class="d-flex justify-content-between align-items-end mt-3">
              <div>
                <div class="text-muted small"><i class="bi bi-geo-alt-fill text-danger"></i> Giao đến: {{ orderInfo.diaChiGiao }}</div>
                <div class="text-muted small"><i class="bi bi-credit-card-2-front text-primary"></i> Thanh toán: {{ orderInfo.phuongThucTt }}</div>
              </div>
              <div class="text-end">
                <div class="text-muted small mb-1">Tổng tiền:</div>
                <div class="fs-4 fw-bold text-danger">{{ formatPrice(orderInfo.tongTien) }}</div>
              </div>
            </div>
            
            <div class="mt-3 text-end">
              <button @click="showDetails(orderInfo)" class="btn btn-outline-info btn-sm px-4 fw-bold me-2" data-bs-toggle="modal" data-bs-target="#orderDetailModal">Chi tiết</button>
              <button v-if="orderInfo.trangThai === 'Chờ xử lý'" @click="cancelOrder(orderInfo.maDonHang)" class="btn btn-outline-danger btn-sm px-4 fw-bold">Hủy đơn hàng</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chi Tiết Đơn Hàng -->
    <div class="modal fade" id="orderDetailModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content" v-if="selectedOrder">
          <div class="modal-header">
            <h5 class="modal-title fw-bold">Chi tiết đơn hàng #{{ selectedOrder.maDonHang }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}<br>
                <strong>Trạng thái:</strong> 
                <span class="badge" :class="getStatusBadgeClass(selectedOrder.trangThai)">{{ getStatusText(selectedOrder.trangThai) }}</span>
              </div>
              <div class="col-md-6 text-md-end">
                <strong>Phương thức thanh toán:</strong> {{ selectedOrder.phuongThucTt }}<br>
                <strong>Địa chỉ giao:</strong> {{ selectedOrder.diaChiGiao }}
              </div>
            </div>
            <h6 class="fw-bold mt-4 border-bottom pb-2">Danh sách sản phẩm</h6>
            <div class="table-responsive">
              <table class="table align-middle">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Phân loại</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Đơn giá</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in selectedOrder.chiTietList" :key="item.maCtDonHang">
                    <td>{{ item.sanPham?.tenSanPham || 'Sản phẩm' }}</td>
                    <td>Màu {{ item.chiTietSanPham?.mauSac }} - Size {{ item.chiTietSanPham?.kichCo }}</td>
                    <td class="text-center">{{ item.soLuong }}</td>
                    <td class="text-end fw-bold text-primary">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="3" class="text-end fw-bold">Phí ship:</td>
                    <td class="text-end text-muted">{{ formatPrice(selectedOrder.phiShip || 0) }}</td>
                  </tr>
                  <tr v-if="selectedOrder.khuyenMai">
                    <td colspan="3" class="text-end fw-bold text-success">
                      Khuyến mãi ({{ selectedOrder.khuyenMai.maCode }}):
                    </td>
                    <td class="text-end text-success fw-bold">
                      -{{ formatPrice(selectedOrder.chiTietList.reduce((sum, item) => sum + (item.soLuong * item.donGia), 0) + (selectedOrder.phiShip || 0) - selectedOrder.tongTien) }}
                    </td>
                  </tr>
                  <tr>
                    <td colspan="3" class="text-end fw-bold fs-5">Tổng cộng:</td>
                    <td class="text-end text-danger fs-5 fw-bold">{{ formatPrice(selectedOrder.tongTien) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
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
import { useRouter } from 'vue-router'
import axios from 'axios'

const API_URL = 'http://localhost:8080'
const router = useRouter()
const orders = ref([])
const loading = ref(false)
const selectedOrder = ref(null)

const showDetails = (order) => {
  selectedOrder.value = order
}

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('vi-VN')
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'Chờ xử lý': return 'bg-warning text-dark'
    case 'Đang xử lý': return 'bg-info text-dark'
    case 'Đang giao hàng': return 'bg-primary'
    case 'Đã giao hàng': return 'bg-success'
    case 'Đã hủy': return 'bg-danger'
    default: return 'bg-secondary'
  }
}

const getStatusText = (status) => {
  return status || 'Không rõ'
}

const fetchOrders = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }

  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/nguoi-dung/${user.maNguoiDung}`)
    orders.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi tải lịch sử đơn hàng:', error)
  } finally {
    loading.value = false
  }
}

const cancelOrder = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) return
  
  try {
    await axios.put(`${API_URL}/api/don-hang/${id}/trang-thai?trangThai=Đã hủy`)
    alert('Hủy đơn hàng thành công!')
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi hủy đơn:', error)
    alert('Không thể hủy đơn hàng này!')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
