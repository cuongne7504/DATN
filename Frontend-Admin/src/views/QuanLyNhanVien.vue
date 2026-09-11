<template>
  <div class="container-fluid">
    <PageHeader title="Quản lý Nhân viên" subtitle="Thêm và phân quyền nhân viên bán hàng">
      <template #actions>
        <button class="btn btn-primary" @click="openCreateModal">
          <i class="bi bi-plus-lg me-1"></i> Thêm Nhân viên
        </button>
      </template>
    </PageHeader>

    <!-- Bảng danh sách Nhân viên -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0 align-middle">
            <thead class="table-light">
              <tr>
                <th class="text-nowrap">ID</th>
                <th class="text-nowrap">Họ tên</th>
                <th class="text-nowrap">Chức vụ</th>
                <th class="text-nowrap">Email</th>
                <th class="text-nowrap">Số điện thoại</th>
                <th class="text-nowrap">Địa chỉ</th>
                <th class="text-nowrap">Ngày tham gia</th>
                <th class="text-nowrap">Trạng thái</th>
                <th class="text-end text-nowrap">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="9" class="p-3">
                  <SkeletonLoader variant="table" :rows="5" />
                </td>
              </tr>
              <tr v-else-if="employees.length === 0">
                <td colspan="9" class="p-0">
                  <EmptyState
                    icon="bi bi-person-badge"
                    title="Chưa có nhân viên"
                    description="Nhấn nút Thêm Nhân viên để tạo tài khoản mới"
                  />
                </td>
              </tr>
              <tr v-for="e in employees" :key="e.maNguoiDung">
                <td>#{{ e.maNguoiDung }}</td>
                <td class="fw-semibold">
                  <i class="bi bi-person-badge text-primary me-2"></i>{{ e.hoTen }}
                </td>
                <td>
                  <span class="badge" :class="e.maQuyen === 1 ? 'bg-danger' : 'bg-success'">
                    {{ e.maQuyen === 1 ? 'Quản lý' : 'Nhân viên' }}
                  </span>
                </td>
                <td>{{ e.email }}</td>
                <td>{{ e.soDienThoai || 'Chưa cập nhật' }}</td>
                <td>{{ e.diaChi || 'Chưa cập nhật' }}</td>
                <td>{{ formatDate(e.ngayTao) }}</td>
                <td class="text-nowrap" style="width: 130px;">
                  <span
                    class="badge text-nowrap d-inline-flex align-items-center"
                    :class="isLocked(e.trangThai) ? 'bg-secondary' : 'bg-success'"
                    style="cursor: pointer;"
                    @click="toggleStatus(e)"
                    :title="'Click để ' + (isLocked(e.trangThai) ? 'Mở khóa tài khoản' : 'Tạm khóa tài khoản')"
                  >
                    <i :class="isLocked(e.trangThai) ? 'bi bi-lock-fill me-1' : 'bi bi-check-circle me-1'"></i>
                    {{ isLocked(e.trangThai) ? 'Tạm khóa' : 'Hoạt động' }}
                  </span>
                </td>
                <td class="text-end text-nowrap" style="width: 280px;">
                  <div class="d-inline-flex align-items-center justify-content-end gap-1 flex-nowrap">
                    <button class="btn btn-sm btn-info text-white text-nowrap" @click="viewOrders(e)" title="Xem các đơn hàng đã xử lý">
                      <i class="bi bi-list-check me-1"></i>Đơn đã xử lý
                    </button>
                    <button class="btn btn-sm btn-outline-secondary text-nowrap" @click="editEmployee(e)" title="Sửa thông tin nhân viên">
                      <i class="bi bi-pencil me-1"></i>Sửa
                    </button>
                    <button
                      v-if="isLocked(e.trangThai)"
                      class="btn btn-sm btn-outline-success text-nowrap"
                      @click="toggleStatus(e)"
                      title="Mở khóa tài khoản nhân viên"
                    >
                      <i class="bi bi-unlock-fill me-1"></i>Mở khóa
                    </button>
                    <button
                      v-else
                      class="btn btn-sm btn-outline-warning text-nowrap"
                      @click="toggleStatus(e)"
                      title="Tạm khóa tài khoản nhân viên"
                    >
                      <i class="bi bi-lock-fill me-1"></i>Tạm khóa
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Thêm/Cập nhật Nhân viên -->
    <div class="modal fade" id="employeeModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? 'Cập nhật thông tin Nhân viên' : 'Thêm Nhân viên mới' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveEmployee">
              <div class="mb-3">
                <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="form.hoTen" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" v-model="form.email" required :disabled="isEditing">
              </div>
              <div class="mb-3">
                <label class="form-label">Chức vụ <span class="text-danger">*</span></label>
                <select class="form-select" v-model="form.maQuyen" required>
                  <option :value="1">Quản lý</option>
                  <option :value="2">Nhân viên</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Mật khẩu <span v-if="!isEditing" class="text-danger">*</span><span v-else> (Bỏ trống nếu không đổi)</span></label>
                <input type="password" class="form-control" v-model="form.matKhau" :required="!isEditing">
              </div>
              <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input type="text" class="form-control" v-model="form.soDienThoai">
              </div>
              <div class="mb-3">
                <label class="form-label">Địa chỉ</label>
                <input type="text" class="form-control" v-model="form.diaChi">
              </div>
              <div class="mb-3" v-if="isEditing">
                <label class="form-label">Trạng thái tài khoản</label>
                <select class="form-select" v-model="form.trangThai">
                  <option value="Hoạt động">Hoạt động</option>
                  <option value="Tạm khóa">Tạm khóa</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="saveEmployee" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
              {{ isEditing ? 'Lưu thay đổi' : 'Tạo tài khoản' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Đơn hàng đã xử lý -->
    <div class="modal fade" id="ordersModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title">Đơn hàng đã xử lý bởi: <span class="fw-bold text-primary">{{ currentEmployee?.hoTen }}</span></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-0">
            <div v-if="loadingOrders" class="text-center py-4">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
            <div v-else-if="employeeOrders.length === 0" class="text-center py-4 text-muted">
              Nhân viên này chưa xử lý đơn hàng nào.
            </div>
            <div v-else class="table-responsive">
              <table class="table table-striped mb-0">
                <thead class="table-light">
                  <tr>
                    <th>Mã đơn</th>
                    <th>Ngày đặt</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th class="text-end">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="order in employeeOrders" :key="order.maDonHang">
                    <td>#{{ order.maDonHang }}</td>
                    <td>{{ formatDate(order.ngayDat) }}</td>
                    <td class="text-danger fw-bold">{{ formatPrice(order.tongTien) }}</td>
                    <td>
                      <span class="badge" :class="getStatusBadgeClass(order.trangThai)">
                        {{ getStatusText(order.trangThai) }}
                      </span>
                    </td>
                    <td class="text-end">
                      <button class="btn btn-sm btn-outline-info" @click="viewOrderDetail(order)">Chi tiết</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chi Tiết Đơn Hàng con -->
    <div class="modal fade" id="employeeOrderDetailModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết đơn hàng #{{ selectedOrder?.maDonHang }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body" v-if="selectedOrder">
            <div class="row mb-4">
              <div class="col-md-6">
                <h6 class="fw-bold border-bottom pb-2">Thông tin giao hàng</h6>
                <p class="mb-1"><strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}</p>
                <p class="mb-1"><strong>Địa chỉ:</strong> {{ selectedOrder.diaChiGiao || 'Bán tại quầy' }}</p>
              </div>
              <div class="col-md-6">
                <h6 class="fw-bold border-bottom pb-2">Thông tin thanh toán</h6>
                <p class="mb-1"><strong>Phương thức:</strong> {{ selectedOrder.phuongThucTt }}</p>
                <p class="mb-1"><strong>Trạng thái ĐH:</strong> <span class="badge bg-secondary">{{ selectedOrder.trangThai }}</span></p>
                <p class="mb-1"><strong>Mã Khuyến Mãi:</strong> {{ selectedOrder.maKhuyenMai ? 'Có' : 'Không' }}</p>
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
                    <div class="fw-bold">{{ item.sanPham?.tenSanPham || 'Sản phẩm ' + item.maChiTietSp }}</div>
                    <div class="small text-muted">Màu: {{ item.chiTietSanPham?.mauSac }} - Size: {{ item.chiTietSanPham?.kichCo }}</div>
                  </td>
                  <td>{{ item.soLuong }}</td>
                  <td>{{ formatPrice(item.donGia) }}</td>
                  <td class="fw-bold">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="3" class="text-end fw-semibold">Tổng tiền hàng:</td>
                  <td class="fw-bold">{{ formatPrice(calculateSubTotal(selectedOrder)) }}</td>
                </tr>
                <tr v-if="selectedOrder.phiShip > 0">
                  <td colspan="3" class="text-end fw-semibold text-primary">Phí giao hàng:</td>
                  <td class="fw-bold text-primary">{{ formatPrice(selectedOrder.phiShip) }}</td>
                </tr>
                <tr v-if="calculateDiscount(selectedOrder) > 0">
                  <td colspan="3" class="text-end fw-semibold text-success">Khuyến mãi:</td>
                  <td class="fw-bold text-success">-{{ formatPrice(calculateDiscount(selectedOrder)) }}</td>
                </tr>
                <tr class="table-light border-top">
                  <td colspan="3" class="text-end fw-bold fs-5">Tổng cộng:</td>
                  <td class="fw-bold text-danger fs-5">{{ formatPrice(selectedOrder.tongTien) }}</td>
                </tr>
              </tfoot>
            </table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="backToOrders">Quay lại</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as bootstrap from 'bootstrap'
import PageHeader from '@/components/PageHeader.vue'
import EmptyState from '@/components/EmptyState.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'

import { API_URL } from '@/config.js'
const employees = ref([])
const loading = ref(false)
const saving = ref(false)

// Modals
let employeeModal = null
let ordersModal = null
let orderDetailModal = null

// Form edit
const isEditing = ref(false)
const form = ref({
  maNguoiDung: null,
  hoTen: '',
  email: '',
  matKhau: '',
  soDienThoai: '',
  diaChi: '',
  maQuyen: 2,
  trangThai: 'Hoạt động'
})

const isLocked = (status) => {
  if (!status) return false
  const s = status.toLowerCase()
  return s.includes('khóa') || s.includes('khoa') || s.includes('nghỉ') || s.includes('nghi') || s.includes('ngừng') || s.includes('ngung')
}

// Orders state
const currentEmployee = ref(null)
const employeeOrders = ref([])
const loadingOrders = ref(false)
const selectedOrder = ref(null)

const fetchEmployees = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/nguoi-dung/nhan-vien`)
    employees.value = res.data.data || []
  } catch (error) {
    console.error('Lỗi khi tải nhân viên:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchEmployees()
  employeeModal = new bootstrap.Modal(document.getElementById('employeeModal'))
  ordersModal = new bootstrap.Modal(document.getElementById('ordersModal'))
  orderDetailModal = new bootstrap.Modal(document.getElementById('employeeOrderDetailModal'))
})

const openCreateModal = () => {
  isEditing.value = false
  form.value = {
    maNguoiDung: null,
    hoTen: '',
    email: '',
    matKhau: '',
    soDienThoai: '',
    diaChi: '',
    maQuyen: 2,
    trangThai: 'Hoạt động'
  }
  employeeModal.show()
}

const editEmployee = (e) => {
  isEditing.value = true
  form.value = {
    maNguoiDung: e.maNguoiDung,
    hoTen: e.hoTen,
    email: e.email,
    matKhau: '',
    soDienThoai: e.soDienThoai || '',
    diaChi: e.diaChi || '',
    maQuyen: e.maQuyen || 2,
    trangThai: e.trangThai || 'Hoạt động'
  }
  employeeModal.show()
}

const toggleStatus = async (e) => {
  const currentlyLocked = isLocked(e.trangThai)
  const actionText = currentlyLocked ? 'mở khóa' : 'tạm khóa'
  const newStatus = currentlyLocked ? 'Hoạt động' : 'Tạm khóa'
  
  if (!confirm(`Bạn có chắc chắn muốn ${actionText} tài khoản nhân viên "${e.hoTen}"?`)) {
    return
  }

  try {
    await axios.put(`${API_URL}/api/nguoi-dung/${e.maNguoiDung}/trang-thai`, {
      trangThai: newStatus
    })
    e.trangThai = newStatus
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
    alert(error.response?.data?.message || 'Có lỗi xảy ra khi đổi trạng thái!')
  }
}

const saveEmployee = async () => {
  if (!form.value.hoTen || !form.value.email) {
    alert('Vui lòng nhập đầy đủ Họ tên và Email')
    return
  }

  saving.value = true
  try {
    const payload = { ...form.value }
    if (isEditing.value) {
      await axios.put(`${API_URL}/api/nguoi-dung/${form.value.maNguoiDung}`, payload)
      alert('Cập nhật thành công!')
    } else {
      await axios.post(`${API_URL}/api/nguoi-dung/tao-nhan-vien`, payload)
      alert('Thêm nhân viên mới thành công!')
    }
    employeeModal.hide()
    fetchEmployees()
  } catch (error) {
    console.error('Lỗi khi lưu:', error)
    alert(error.response?.data?.message || 'Có lỗi xảy ra!')
  } finally {
    saving.value = false
  }
}

const viewOrders = async (e) => {
  currentEmployee.value = e
  employeeOrders.value = []
  ordersModal.show()
  
  loadingOrders.value = true
  try {
    // Lấy toàn bộ đơn hàng và lọc theo nhân viên
    const res = await axios.get(`${API_URL}/api/don-hang`)
    const allOrders = res.data.data || []
    employeeOrders.value = allOrders.filter(o => o.maNhanVien === e.maNguoiDung)
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
  } finally {
    loadingOrders.value = false
  }
}

const viewOrderDetail = async (order) => {
  selectedOrder.value = null
  ordersModal.hide()
  orderDetailModal.show()
  
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/${order.maDonHang}`)
    selectedOrder.value = res.data.data
  } catch (error) {
    console.error('Lỗi lấy chi tiết đơn:', error)
  }
}

const backToOrders = () => {
  orderDetailModal.hide()
  ordersModal.show()
}

// Helpers
const formatDate = (dateString) => {
  if (!dateString) return ''
  const d = new Date(dateString)
  return d.toLocaleDateString('vi-VN') + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}

const formatPrice = (price) => {
  if (!price) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const getStatusText = (status) => {
  const map = {
    '1': 'Chờ xử lý',
    '2': 'Đang xử lý',
    '3': 'Đang giao hàng',
    '4': 'Đã giao hàng',
    '5': 'Đã hủy'
  }
  return map[status] || status
}

const getStatusBadgeClass = (status) => {
  const map = {
    '1': 'bg-warning text-dark',
    '2': 'bg-info text-dark',
    '3': 'bg-primary',
    '4': 'bg-success',
    '5': 'bg-danger'
  }
  return map[status] || 'bg-secondary'
}

const calculateSubTotal = (order) => {
  if (!order || !order.chiTietList) return 0
  return order.chiTietList.reduce((sum, item) => sum + (item.soLuong * (item.donGia || 0)), 0)
}

const calculateDiscount = (order) => {
  if (!order) return 0
  const subTotal = calculateSubTotal(order)
  const ship = order.phiShip || 0
  return Math.max(0, subTotal + ship - order.tongTien)
}
</script>
