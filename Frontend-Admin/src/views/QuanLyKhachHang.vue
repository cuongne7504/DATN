<template>
  <div class="container-fluid py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold mb-0">Quản lý Khách hàng</h2>
    </div>

    <!-- Bảng danh sách Khách hàng -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover table-striped mb-0 align-middle">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Địa chỉ</th>
                <th>Ngày tạo</th>
                <th class="text-end">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="7" class="text-center py-4">
                  <div class="spinner-border text-primary" role="status"></div>
                </td>
              </tr>
              <tr v-else-if="customers.length === 0">
                <td colspan="7" class="text-center py-4 text-muted">Không có dữ liệu khách hàng</td>
              </tr>
              <tr v-for="c in customers" :key="c.maNguoiDung">
                <td>#{{ c.maNguoiDung }}</td>
                <td class="fw-semibold">{{ c.hoTen }}</td>
                <td>{{ c.email }}</td>
                <td>{{ c.soDienThoai || 'Chưa cập nhật' }}</td>
                <td>{{ c.diaChi || 'Chưa cập nhật' }}</td>
                <td>{{ formatDate(c.ngayTao) }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-info me-2 text-white" @click="viewOrders(c)">
                    <i class="bi bi-box-seam"></i> Đơn hàng
                  </button>
                  <button class="btn btn-sm btn-warning me-2" @click="editCustomer(c)">
                    <i class="bi bi-pencil"></i> Sửa
                  </button>
                  <button class="btn btn-sm btn-danger" @click="deleteCustomer(c.maNguoiDung)">
                    <i class="bi bi-trash"></i> Xóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Cập nhật Khách hàng -->
    <div class="modal fade" id="customerModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Cập nhật thông tin Khách hàng</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveCustomer">
              <div class="mb-3">
                <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="form.hoTen" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" v-model="form.email" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mật khẩu mới (Bỏ trống nếu không đổi)</label>
                <input type="password" class="form-control" v-model="form.matKhau">
              </div>
              <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input type="text" class="form-control" v-model="form.soDienThoai">
              </div>
              <div class="mb-3">
                <label class="form-label">Địa chỉ</label>
                <input type="text" class="form-control" v-model="form.diaChi">
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="saveCustomer" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
              Lưu thay đổi
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Đơn hàng của Khách hàng -->
    <div class="modal fade" id="ordersModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title">Lịch sử đơn hàng: <span class="fw-bold text-primary">{{ currentCustomer?.hoTen }}</span></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-0">
            <div v-if="loadingOrders" class="text-center py-4">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
            <div v-else-if="customerOrders.length === 0" class="text-center py-4 text-muted">
              Khách hàng này chưa có đơn hàng nào.
            </div>
            <div v-else class="table-responsive">
              <table class="table table-striped mb-0">
                <thead class="table-light">
                  <tr>
                    <th>Mã đơn</th>
                    <th>Ngày đặt</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="order in customerOrders" :key="order.maDonHang">
                    <td>#{{ order.maDonHang }}</td>
                    <td>{{ formatDate(order.ngayDat) }}</td>
                    <td class="text-danger fw-bold">{{ formatPrice(order.tongTien) }}</td>
                    <td>
                      <span class="badge" :class="getStatusBadgeClass(order.trangThai)">
                        {{ getStatusText(order.trangThai) }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
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

const API_URL = 'http://localhost:8080'
const customers = ref([])
const loading = ref(false)
const saving = ref(false)

// Modals
let customerModal = null
let ordersModal = null

// Form edit
const form = ref({
  maNguoiDung: null,
  hoTen: '',
  email: '',
  matKhau: '',
  soDienThoai: '',
  diaChi: ''
})

// Orders state
const currentCustomer = ref(null)
const customerOrders = ref([])
const loadingOrders = ref(false)

const fetchCustomers = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/nguoi-dung/khach-hang`)
    customers.value = res.data.data || []
  } catch (error) {
    console.error('Lỗi khi tải khách hàng:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCustomers()
  customerModal = new bootstrap.Modal(document.getElementById('customerModal'))
  ordersModal = new bootstrap.Modal(document.getElementById('ordersModal'))
})

const editCustomer = (c) => {
  form.value = {
    maNguoiDung: c.maNguoiDung,
    hoTen: c.hoTen,
    email: c.email,
    matKhau: '', // Không hiển thị mật khẩu cũ
    soDienThoai: c.soDienThoai || '',
    diaChi: c.diaChi || ''
  }
  customerModal.show()
}

const saveCustomer = async () => {
  if (!form.value.hoTen || !form.value.email) {
    alert('Vui lòng nhập đầy đủ Họ tên và Email')
    return
  }

  saving.value = true
  try {
    const payload = { ...form.value }
    // API update nguoi-dung requires PUT /api/nguoi-dung/{id}
    await axios.put(`${API_URL}/api/nguoi-dung/${form.value.maNguoiDung}`, payload)
    alert('Cập nhật thành công!')
    customerModal.hide()
    fetchCustomers()
  } catch (error) {
    console.error('Lỗi khi lưu:', error)
    alert(error.response?.data?.message || 'Có lỗi xảy ra!')
  } finally {
    saving.value = false
  }
}

const deleteCustomer = async (id) => {
  if (confirm('Bạn có chắc chắn muốn xóa khách hàng này không?')) {
    try {
      await axios.delete(`${API_URL}/api/nguoi-dung/${id}`)
      alert('Xóa thành công!')
      fetchCustomers()
    } catch (error) {
      console.error('Lỗi khi xóa:', error)
      alert('Có lỗi xảy ra, có thể khách hàng này đã có đơn hàng nên không thể xóa!')
    }
  }
}

const viewOrders = async (c) => {
  currentCustomer.value = c
  customerOrders.value = []
  ordersModal.show()
  
  loadingOrders.value = true
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/nguoi-dung/${c.maNguoiDung}`)
    customerOrders.value = res.data.data || []
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
  } finally {
    loadingOrders.value = false
  }
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
</script>
