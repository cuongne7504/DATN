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
              <button v-if="orderInfo.trangThai === 'Đã giao hàng'" @click="openReturnModal(orderInfo)" class="btn btn-outline-warning btn-sm px-4 fw-bold" data-bs-toggle="modal" data-bs-target="#returnOrderModal">Yêu cầu hoàn hàng</button>
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

            <!-- Hành trình đơn hàng -->
            <div class="order-timeline my-4">
              <h6 class="fw-bold mb-3 border-bottom pb-2"><i class="bi bi-geo-fill text-primary"></i> Hành trình đơn hàng</h6>
              
              <div class="d-flex justify-content-between align-items-center position-relative mb-4 timeline-steps">
                <div class="timeline-line"></div>
                
                <!-- Bước 1: Đặt hàng -->
                <div class="text-center timeline-step" :class="{ active: isStepActive(1) }">
                  <div class="step-icon"><i class="bi bi-file-earmark-check"></i></div>
                  <div class="step-text mt-2 small fw-semibold">Đặt hàng</div>
                </div>
                
                <!-- Bước 2: Xử lý -->
                <div class="text-center timeline-step" :class="{ active: isStepActive(2) }">
                  <div class="step-icon"><i class="bi bi-gear-fill"></i></div>
                  <div class="step-text mt-2 small fw-semibold">Xử lý</div>
                </div>
                
                <!-- Bước 3: Đang giao -->
                <div class="text-center timeline-step" :class="{ active: isStepActive(3) }">
                  <div class="step-icon"><i class="bi bi-truck"></i></div>
                  <div class="step-text mt-2 small fw-semibold">Đang giao</div>
                </div>
                
                <!-- Bước 4: Kết quả -->
                <div class="text-center timeline-step" :class="{ active: isStepActive(4), 'step-danger': selectedOrder.trangThai === 'Đã hủy', 'step-warning': isReturnStatus }">
                  <div class="step-icon">
                    <i v-if="selectedOrder.trangThai === 'Đã hủy'" class="bi bi-x-circle-fill"></i>
                    <i v-else-if="isReturnStatus" class="bi bi-arrow-counterclockwise"></i>
                    <i v-else class="bi bi-check-circle-fill"></i>
                  </div>
                  <div class="step-text mt-2 small fw-semibold">
                    {{ selectedOrder.trangThai === 'Đã hủy' ? 'Đã hủy' : (isReturnStatus ? 'Hoàn hàng' : 'Hoàn thành') }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Thông tin giao vận -->
            <div v-if="selectedOrder.shippingCode || selectedOrder.shipperName" class="alert alert-light border mt-3 mb-4 p-3 shadow-sm rounded">
              <h6 class="fw-bold mb-3 text-dark"><i class="bi bi-truck text-primary"></i> Thông tin giao vận</h6>
              <div class="row g-2">
                <div class="col-md-6" v-if="selectedOrder.shipperName">
                  <span class="text-muted d-block small">Người giao hàng</span>
                  <strong>{{ selectedOrder.shipperName }}</strong>
                  <span v-if="selectedOrder.shipperPhone" class="ms-1 text-primary">({{ selectedOrder.shipperPhone }})</span>
                </div>
                <div class="col-md-6" v-if="selectedOrder.shippingCode">
                  <span class="text-muted d-block small">Mã vận đơn (GHN)</span>
                  <div class="d-flex align-items-center gap-2 mt-1">
                    <span class="badge bg-secondary px-2 py-1.5 fs-7">{{ selectedOrder.shippingCode }}</span>
                    <a :href="'https://donhang.ghn.vn/?order_code=' + selectedOrder.shippingCode" target="_blank" class="btn btn-xs btn-outline-primary py-0 px-2 fw-bold text-xs" style="font-size: 0.75rem;">Theo dõi đơn hàng 🌐</a>
                  </div>
                </div>
                <div class="col-12 border-top pt-2 mt-2" v-if="selectedOrder.shippingNote">
                  <span class="text-muted d-block small">Ghi chú vận chuyển</span>
                  <span class="text-dark small">{{ selectedOrder.shippingNote }}</span>
                </div>
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

    <!-- Modal Yêu Cầu Hoàn Hàng -->
    <div class="modal fade" id="returnOrderModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-warning">
            <h5 class="modal-title fw-bold text-dark">Yêu cầu hoàn hàng (Đơn #{{ orderToReturn?.maDonHang }})</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" id="closeReturnModal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label fw-bold">Lý do hoàn hàng:</label>
              <textarea v-model="returnForm.lyDo" class="form-control" rows="3" placeholder="Nhập lý do chi tiết..." required></textarea>
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">Link hình ảnh minh họa (tuỳ chọn):</label>
              <input type="text" v-model="returnForm.hinhAnhMinhHoa" class="form-control" placeholder="URL hình ảnh sản phẩm lỗi">
            </div>
            <div class="alert alert-info small mt-2">
              <strong>Lưu ý:</strong> Chúng tôi sẽ kiểm tra và xét duyệt yêu cầu của bạn. Sau khi duyệt, bạn cần gửi hàng về cho shop để hoàn tất quá trình hoàn tiền.
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-warning fw-bold text-dark" @click="submitReturnRequest">Gửi yêu cầu</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import { API_URL } from '@/config.js'
const router = useRouter()
const orders = ref([])
const loading = ref(false)
const selectedOrder = ref(null)

const orderToReturn = ref(null)
const returnForm = ref({ lyDo: '', hinhAnhMinhHoa: '' })

const isReturnStatus = computed(() => {
  if (!selectedOrder.value) return false
  const status = selectedOrder.value.trangThai
  return ['Yêu cầu hoàn hàng', 'Đang xử lý hoàn hàng', 'Đã hoàn hàng', 'Đã hoàn tiền'].includes(status)
})

const isStepActive = (step) => {
  if (!selectedOrder.value) return false
  const status = selectedOrder.value.trangThai
  
  if (step === 1) return true // Đặt hàng luôn active
  
  if (step === 2) {
    return !['Chờ xử lý', 'Đã hủy'].includes(status)
  }
  
  if (step === 3) {
    return ['Đang giao hàng', 'Đã giao hàng', 'Yêu cầu hoàn hàng', 'Đang xử lý hoàn hàng', 'Đã hoàn hàng', 'Đã hoàn tiền'].includes(status)
  }
  
  if (step === 4) {
    return ['Đã giao hàng', 'Đã hủy', 'Yêu cầu hoàn hàng', 'Đang xử lý hoàn hàng', 'Đã hoàn hàng', 'Đã hoàn tiền'].includes(status)
  }
  
  return false
}

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
    case 'Yêu cầu hoàn hàng': return 'bg-warning text-dark'
    case 'Đang xử lý hoàn hàng': return 'bg-info text-dark'
    case 'Đã hoàn hàng': return 'bg-secondary'
    case 'Đã hoàn tiền': return 'bg-success'
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

const openReturnModal = (order) => {
  orderToReturn.value = order
  returnForm.value = { lyDo: '', hinhAnhMinhHoa: '' }
}

const submitReturnRequest = async () => {
  if (!returnForm.value.lyDo) {
    alert('Vui lòng nhập lý do hoàn hàng.')
    return
  }
  try {
    const payload = {
      maDonHang: orderToReturn.value.maDonHang,
      lyDo: returnForm.value.lyDo,
      hinhAnhMinhHoa: returnForm.value.hinhAnhMinhHoa,
      soTienHoan: orderToReturn.value.tongTien
    }
    await axios.post(`${API_URL}/api/hoan-hang`, payload)
    alert('Đã gửi yêu cầu hoàn hàng thành công! Vui lòng chờ nhân viên xét duyệt.')
    document.getElementById('closeReturnModal').click()
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi gửi yêu cầu hoàn hàng:', error)
    alert(error.response?.data?.message || 'Có lỗi xảy ra, không thể gửi yêu cầu.')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.timeline-steps {
  padding: 0 30px;
}
.timeline-line {
  position: absolute;
  top: 22px;
  left: 30px;
  right: 30px;
  height: 4px;
  background-color: #e9ecef;
  z-index: 1;
}
.timeline-step {
  position: relative;
  z-index: 2;
  flex: 1;
}
.step-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: #ffffff;
  border: 4px solid #e9ecef;
  color: #6c757d;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  transition: all 0.3s ease;
  margin: 0 auto;
}
.timeline-step.active .step-icon {
  border-color: #0d6efd;
  background-color: #0d6efd;
  color: #ffffff;
  box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.2);
}
.timeline-step.step-danger.active .step-icon {
  border-color: #dc3545;
  background-color: #dc3545;
  color: #ffffff;
  box-shadow: 0 0 0 4px rgba(220, 53, 69, 0.2);
}
.timeline-step.step-warning.active .step-icon {
  border-color: #ffc107;
  background-color: #ffc107;
  color: #212529;
  box-shadow: 0 0 0 4px rgba(255, 193, 7, 0.2);
}
.step-text {
  color: #6c757d;
  font-size: 0.85rem;
}
.timeline-step.active .step-text {
  color: #212529;
  font-weight: 700;
}
.btn-xs {
  padding: 0.15rem 0.4rem;
  font-size: 0.75rem;
  line-height: 1.2;
  border-radius: 0.2rem;
}
</style>
