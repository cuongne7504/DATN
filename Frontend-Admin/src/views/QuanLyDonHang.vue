<template>
  <div class="container mt-4">
    <PageHeader
      title="Quản lý Đơn hàng"
      subtitle="Theo dõi và cập nhật trạng thái đơn online / tại quầy"
    />

    <div class="card orders-card mb-4">
      <div class="card-body p-4">
        <div class="status-tabs mb-3">
          <button
            v-for="status in statuses"
            :key="status.value"
            class="status-tab"
            :class="{ active: currentStatus === status.value }"
            @click="currentStatus = status.value"
          >
            {{ status.label }}
            <span class="count" v-if="countByStatus(status.value) > 0">{{ countByStatus(status.value) }}</span>
          </button>
        </div>

        <div v-if="loading">
          <SkeletonLoader variant="table" :rows="6" />
        </div>

        <EmptyState
          v-else-if="filteredOrders.length === 0"
          icon="bi bi-bag-x"
          title="Không có đơn hàng"
          description="Chưa có đơn nào trong trạng thái này"
        />

        <div v-else class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>Mã ĐH</th>
                <th>Khách hàng</th>
                <th>Ngày đặt</th>
                <th>Tổng tiền</th>
                <th>Thanh toán</th>
                <th>Loại đơn</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in filteredOrders" :key="order.maDonHang">
                <td class="fw-bold text-primary">#{{ order.maDonHang }}</td>
                <td>
                  <div class="fw-semibold">{{ customerName(order) }}</div>
                  <small class="text-muted">{{ customerPhone(order) }}</small>
                </td>
                <td>{{ formatDate(order.ngayDat) }}</td>
                <td class="fw-bold text-danger">{{ formatPrice(order.tongTien) }}</td>
                <td>
                  <span class="badge" :class="order.phuongThucTt === 'VNPay' ? 'bg-info text-dark' : 'bg-secondary'">
                    {{ order.phuongThucTt || 'Tiền mặt' }}
                  </span>
                </td>
                <td>
                  <span class="badge" :class="isPosOrder(order) ? 'bg-warning text-dark' : 'bg-primary'">
                    {{ isPosOrder(order) ? 'Tại quầy' : 'Online' }}
                  </span>
                </td>
                <td>
                  <div class="action-group">
                    <button @click="viewDetail(order)" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#orderDetailModal">
                      Chi tiết
                    </button>
                    <button v-if="order.trangThai === 'Chờ xử lý'" @click="updateStatus(order.maDonHang, 'Đang xử lý')" class="btn btn-sm btn-success">
                      Xác nhận
                    </button>
                    <button v-if="order.trangThai === 'Chờ xử lý'" @click="updateStatus(order.maDonHang, 'Đã hủy')" class="btn btn-sm btn-outline-danger">
                      Hủy
                    </button>
                    <button v-if="order.trangThai === 'Đang xử lý'" @click="openShipperModal(order)" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#shipperModal">
                      Shipper
                    </button>
                    <button v-if="order.trangThai === 'Đang xử lý'" @click="sendToGHN(order.maDonHang)" class="btn btn-sm btn-warning">
                      GHN
                    </button>
                    <button v-if="order.trangThai === 'Đang giao hàng'" @click="updateStatus(order.maDonHang, 'Đã giao hàng')" class="btn btn-sm btn-success">
                      Hoàn thành
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

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
                <div class="info-box">
                  <h6>Thông tin giao hàng</h6>
                  <p class="mb-1"><strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}</p>
                  <p class="mb-0"><strong>Địa chỉ:</strong> {{ selectedOrder.diaChiGiao || 'Bán tại quầy' }}</p>
                </div>
              </div>
              <div class="col-md-6">
                <div class="info-box">
                  <h6>Thanh toán</h6>
                  <p class="mb-1"><strong>Phương thức:</strong> {{ selectedOrder.phuongThucTt }}</p>
                  <p class="mb-1"><strong>Trạng thái:</strong> <span class="badge bg-secondary">{{ selectedOrder.trangThai }}</span></p>
                  <p class="mb-0"><strong>Khuyến mãi:</strong> {{ selectedOrder.maKhuyenMai ? 'Có' : 'Không' }}</p>
                </div>
              </div>
            </div>

            <div class="info-box mb-4" v-if="selectedOrder.shipperName || selectedOrder.shippingCode">
              <h6>Shipper / vận chuyển</h6>
              <p class="mb-1" v-if="selectedOrder.shippingCode"><strong>Mã GHN:</strong> <span class="badge bg-success">{{ selectedOrder.shippingCode }}</span></p>
              <p class="mb-1" v-if="selectedOrder.shipperName"><strong>Shipper:</strong> {{ selectedOrder.shipperName }}</p>
              <p class="mb-1" v-if="selectedOrder.shipperPhone"><strong>SĐT:</strong> {{ selectedOrder.shipperPhone }}</p>
              <p class="mb-0" v-if="selectedOrder.shippingNote"><strong>Ghi chú:</strong> {{ selectedOrder.shippingNote }}</p>
            </div>

            <h6 class="fw-bold mb-2">Sản phẩm</h6>
            <div class="table-responsive">
              <table class="table table-sm align-middle mb-0">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>SL</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in selectedOrder.chiTietList" :key="item.maCtDonHang">
                    <td>
                      <div class="fw-bold">{{ item.sanPham?.tenSanPham || 'Sản phẩm ' + item.maChiTietSp }}</div>
                      <div class="small text-muted">{{ item.chiTietSanPham?.mauSac }} · {{ item.chiTietSanPham?.kichCo }}</div>
                    </td>
                    <td>{{ item.soLuong }}</td>
                    <td>{{ formatPrice(item.donGia) }}</td>
                    <td class="fw-bold">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="3" class="text-end fw-bold">Tổng tiền:</td>
                    <td class="fw-bold text-danger">{{ formatPrice(selectedOrder.tongTien) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline-primary" @click="openInvoiceFromDetail">
              <i class="bi bi-receipt-cutoff me-1"></i> Hóa đơn
            </button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="shipperModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Giao shipper · Đơn #{{ shipperOrder?.maDonHang }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <form @submit.prevent="submitShipper">
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Tên shipper <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="shipperForm.shipperName" required placeholder="Nhập tên shipper" />
              </div>
              <div class="mb-3">
                <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="shipperForm.shipperPhone" required placeholder="Nhập SĐT" />
              </div>
              <div class="mb-0">
                <label class="form-label">Ghi chú</label>
                <textarea class="form-control" v-model="shipperForm.shippingNote" rows="3" placeholder="Ghi chú giao hàng"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" ref="closeShipperModalBtn">Đóng</button>
              <button type="submit" class="btn btn-primary">Xác nhận</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <InvoiceModal
      modal-id="orderInvoiceModal"
      :order="invoiceOrder"
      :nhan-vien-ten="invoiceNhanVienTen"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { Modal } from 'bootstrap'
import { API_URL } from '@/config.js'
import PageHeader from '@/components/PageHeader.vue'
import EmptyState from '@/components/EmptyState.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'
import InvoiceModal from '@/components/InvoiceModal.vue'
import { useToast } from '@/composables/useToast.js'
import { isInvoiceable } from '@/composables/useInvoice.js'

const { success, error } = useToast()
const orders = ref([])
const loading = ref(false)
const currentStatus = ref('TatCa')
const selectedOrder = ref(null)
const shipperOrder = ref(null)
const shipperForm = ref({ shipperName: '', shipperPhone: '', shippingNote: '' })
const closeShipperModalBtn = ref(null)
const invoiceOrder = ref(null)
const invoiceNhanVienTen = ref('')
const employeeMap = ref({})

const statuses = [
  { label: 'Tất cả', value: 'TatCa' },
  { label: 'Chờ xác nhận', value: 'Chờ xử lý' },
  { label: 'Đã xác nhận', value: 'Đang xử lý' },
  { label: 'Đang giao', value: 'Đang giao hàng' },
  { label: 'Hoàn thành', value: 'Đã giao hàng' },
  { label: 'Đã hủy', value: 'Đã hủy' }
]

const filteredOrders = computed(() => {
  if (currentStatus.value === 'TatCa') return orders.value
  return orders.value.filter((o) => o.trangThai === currentStatus.value)
})

const countByStatus = (value) => {
  if (value === 'TatCa') return orders.value.length
  return orders.value.filter((o) => o.trangThai === value).length
}

const isPosOrder = (order) => !order.diaChiGiao || order.diaChiGiao.includes('tại quầy')
const customerName = (order) => (order.diaChiGiao ? order.diaChiGiao.split(' - ')[0] : 'Khách vãng lai')
const customerPhone = (order) => (order.diaChiGiao ? order.diaChiGiao.split(' - ')[1] || '' : '')

const formatPrice = (price) =>
  price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'

const formatDate = (dateStr) => (dateStr ? new Date(dateStr).toLocaleString('vi-VN') : '')

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/don-hang`)
    orders.value = res.data.data || res.data || []
    orders.value.sort((a, b) => b.maDonHang - a.maDonHang)
  } catch (e) {
    error('Không tải được danh sách đơn hàng')
  } finally {
    loading.value = false
  }
}

const openShipperModal = (order) => {
  shipperOrder.value = order
  shipperForm.value = { shipperName: '', shipperPhone: '', shippingNote: '' }
}

const submitShipper = async () => {
  if (!shipperOrder.value) return
  try {
    await axios.put(`${API_URL}/api/don-hang/${shipperOrder.value.maDonHang}/giao-hang`, shipperForm.value)
    closeShipperModalBtn.value?.click()
    success('Đã giao đơn cho shipper')
    await fetchOrders()
  } catch (e) {
    error('Giao shipper thất bại')
  }
}

const sendToGHN = async (id) => {
  if (!confirm('Gửi đơn sang GHN để tạo vận đơn?')) return
  try {
    const res = await axios.put(`${API_URL}/api/don-hang/${id}/gui-ghn`)
    success('Gửi GHN thành công · Mã: ' + (res.data?.data?.shippingCode || ''))
    await fetchOrders()
  } catch (e) {
    error(e.response?.data?.message || 'Gửi GHN thất bại')
  }
}

const updateStatus = async (id, status) => {
  if (!confirm(`Chuyển sang trạng thái: ${status}?`)) return
  try {
    await axios.put(`${API_URL}/api/don-hang/${id}/trang-thai`, null, { params: { trangThai: status } })
    success('Cập nhật trạng thái thành công')
    await fetchOrders()
  } catch (e) {
    error('Cập nhật trạng thái thất bại')
  }
}

const viewDetail = async (order) => {
  selectedOrder.value = null
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/${order.maDonHang}`)
    selectedOrder.value = res.data.data || res.data || order
  } catch {
    selectedOrder.value = order
  }
}

const fetchEmployees = async () => {
  try {
    const res = await axios.get(`${API_URL}/api/nguoi-dung`)
    const users = res.data.data || res.data || []
    const map = {}
    users.forEach((u) => {
      map[u.maNguoiDung] = u.hoTen || u.email
    })
    employeeMap.value = map
  } catch {
    employeeMap.value = {}
  }
}

const openInvoiceFromDetail = () => {
  if (!selectedOrder.value || !isInvoiceable(selectedOrder.value)) {
    error('Chỉ xuất hóa đơn cho đơn đã hoàn thành hoặc bán tại quầy')
    return
  }
  invoiceOrder.value = selectedOrder.value
  invoiceNhanVienTen.value = employeeMap.value[selectedOrder.value.maNhanVien] || ''
  const detailModal = document.getElementById('orderDetailModal')
  if (detailModal) Modal.getInstance(detailModal)?.hide()
  setTimeout(() => {
    const invoiceModal = document.getElementById('orderInvoiceModal')
    if (invoiceModal) Modal.getOrCreateInstance(invoiceModal).show()
  }, 250)
}

onMounted(async () => {
  await fetchEmployees()
  await fetchOrders()
})
</script>

<style scoped>
.orders-card {
  border: 1px solid var(--sp-border);
  box-shadow: var(--sp-shadow-sm);
}

.status-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
}

.status-tab {
  border: 1px solid var(--sp-border);
  background: #fff;
  color: #64748b;
  border-radius: 999px;
  padding: 0.45rem 0.9rem;
  font-weight: 650;
  font-size: 0.88rem;
  transition: all 0.2s ease;
}

.status-tab:hover {
  border-color: rgba(29, 78, 216, 0.3);
  color: var(--sp-blue);
}

.status-tab.active {
  background: rgba(29, 78, 216, 0.1);
  border-color: rgba(29, 78, 216, 0.2);
  color: var(--sp-blue);
}

.status-tab .count {
  margin-left: 0.35rem;
  background: rgba(15, 23, 42, 0.06);
  border-radius: 999px;
  padding: 0.05rem 0.4rem;
  font-size: 0.75rem;
}

.status-tab.active .count {
  background: rgba(29, 78, 216, 0.15);
}

.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}

.info-box {
  background: #f8fafc;
  border: 1px solid var(--sp-border);
  border-radius: 14px;
  padding: 0.95rem 1rem;
}

.info-box h6 {
  font-weight: 750;
  margin-bottom: 0.65rem;
  color: #0f172a;
}
</style>
