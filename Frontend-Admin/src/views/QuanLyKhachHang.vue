<template>
  <div class="container mt-4">
    <h2 class="mb-4 fw-bold text-uppercase tracking-wider">Quản lý Khách hàng</h2>

    <!-- Form thêm/sửa khách hàng -->
    <div class="card mb-4 shadow-sm border-0">
      <div class="card-body p-4">
        <h5 class="fw-bold mb-3 text-secondary">{{ isEditing ? 'Cập nhật thông tin Khách hàng' : 'Thêm Khách hàng mới' }}</h5>
        <form @submit.prevent="saveCustomer">
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label fw-semibold">Họ tên <span class="text-danger">*</span></label>
              <input type="text" v-model="form.hoTen" class="form-control shadow-none" required placeholder="VD: Nguyễn Văn A">
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
              <input type="email" v-model="form.email" class="form-control shadow-none" :disabled="isEditing" required placeholder="VD: email@example.com">
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label fw-semibold">Mật khẩu <span class="text-danger" v-if="!isEditing">*</span></label>
              <input type="password" v-model="form.matKhau" class="form-control shadow-none" :required="!isEditing" :placeholder="isEditing ? 'Để trống nếu không muốn đổi' : 'Nhập mật khẩu (tối thiểu 6 ký tự)'">
            </div>
          </div>

          <div class="row">
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Số điện thoại</label>
              <input type="tel" v-model="form.soDienThoai" class="form-control shadow-none" placeholder="VD: 0987654321">
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Giới tính</label>
              <select v-model="form.gioiTinh" class="form-select shadow-none">
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
                <option value="Khác">Khác</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Ngày sinh</label>
              <input type="date" v-model="form.ngaySinh" class="form-control shadow-none">
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Trạng thái</label>
              <select v-model="form.trangThai" class="form-select shadow-none">
                <option :value="true">Hoạt động</option>
                <option :value="false">Khóa tài khoản</option>
              </select>
            </div>
          </div>

          <div class="row mb-3">
            <div class="col-md-12">
              <label class="form-label fw-semibold">Địa chỉ</label>
              <textarea v-model="form.diaChi" class="form-control shadow-none" rows="2" placeholder="Nhập địa chỉ của khách hàng..."></textarea>
            </div>
          </div>

          <div class="d-flex justify-content-end gap-2">
            <button type="button" v-if="isEditing" @click="resetForm" class="btn btn-secondary px-4">Hủy</button>
            <button type="submit" class="btn btn-primary px-4" :disabled="loading">
              {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Thanh tìm kiếm & lọc -->
    <div class="card mb-4 shadow-sm border-0">
      <div class="card-body">
        <div class="row g-3 align-items-center">
          <div class="col-md-6 col-lg-8">
            <div class="input-group">
              <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
              <input type="text" v-model="searchKeyword" @input="debouncedSearch" class="form-control border-start-0 shadow-none ps-0" placeholder="Tìm kiếm khách hàng theo tên, email hoặc số điện thoại...">
            </div>
          </div>
          <div class="col-md-6 col-lg-4 d-flex justify-content-md-end">
            <button @click="fetchCustomers" class="btn btn-outline-secondary w-100 w-md-auto"><i class="bi bi-arrow-clockwise me-1"></i> Tải lại</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Spinner Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Danh sách khách hàng -->
    <div v-else class="table-responsive bg-white rounded shadow-sm p-3 border-0">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th class="ps-3">ID KH</th>
            <th>Họ và tên</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Giới tính</th>
            <th>Ngày sinh</th>
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th class="pe-3 text-end">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cust in customers" :key="cust.maKhachHang">
            <td class="ps-3"><code class="text-secondary fw-semibold">KH#{{ cust.maKhachHang }}</code></td>
            <td class="fw-bold">{{ cust.hoTen }}</td>
            <td>{{ cust.email }}</td>
            <td>{{ cust.soDienThoai || '---' }}</td>
            <td>
              <span class="badge" :class="cust.gioiTinh === 'Nam' ? 'bg-info-subtle text-info border border-info-subtle' : cust.gioiTinh === 'Nữ' ? 'bg-danger-subtle text-danger border border-danger-subtle' : 'bg-secondary-subtle text-secondary border border-secondary-subtle'">
                {{ cust.gioiTinh || '---' }}
              </span>
            </td>
            <td>{{ formatDate(cust.ngaySinh) }}</td>
            <td>
              <span class="d-inline-block text-truncate" style="max-width: 150px;" :title="cust.diaChi">
                {{ cust.diaChi || '---' }}
              </span>
            </td>
            <td>
              <span class="badge rounded-pill px-2.5 py-1.5" :class="cust.trangThai ? 'bg-success' : 'bg-danger'">
                {{ cust.trangThai ? 'Hoạt động' : 'Đang khóa' }}
              </span>
            </td>
            <td class="pe-3 text-end">
              <button @click="editCustomer(cust)" class="btn btn-sm btn-outline-primary me-2 shadow-sm"><i class="bi bi-pencil-square"></i> Sửa</button>
              <button @click="deleteCustomer(cust.maKhachHang)" class="btn btn-sm btn-outline-danger shadow-sm"><i class="bi bi-trash"></i> Xóa</button>
            </td>
          </tr>
          <tr v-if="customers.length === 0">
            <td colspan="9" class="text-center text-muted py-4">
              <i class="bi bi-people mb-2" style="font-size: 2rem;"></i>
              <p class="mb-0">Không tìm thấy khách hàng nào</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'
const customers = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const searchKeyword = ref('')
let searchTimeout = null

const form = ref({
  hoTen: '',
  email: '',
  matKhau: '',
  soDienThoai: '',
  diaChi: '',
  gioiTinh: 'Nam',
  ngaySinh: '',
  trangThai: true
})

const fetchCustomers = async () => {
  loading.value = true
  try {
    const url = searchKeyword.value.trim()
      ? `${API_URL}/api/khach-hang?search=${encodeURIComponent(searchKeyword.value.trim())}`
      : `${API_URL}/api/khach-hang`
    const res = await axios.get(url)
    customers.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi khi tải danh sách khách hàng:', error)
  } finally {
    loading.value = false
  }
}

const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    fetchCustomers()
  }, 400)
}

const saveCustomer = async () => {
  loading.value = true
  try {
    const payload = { ...form.value }
    if (isEditing.value && !payload.matKhau) {
      delete payload.matKhau
    }

    if (isEditing.value) {
      await axios.put(`${API_URL}/api/khach-hang/${editingId.value}`, payload)
      alert('Cập nhật thông tin khách hàng thành công!')
    } else {
      await axios.post(`${API_URL}/api/khach-hang`, payload)
      alert('Thêm mới khách hàng thành công!')
    }
    resetForm()
    await fetchCustomers()
  } catch (error) {
    console.error('Lỗi lưu khách hàng:', error)
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editCustomer = (cust) => {
  isEditing.value = true
  editingId.value = cust.maKhachHang
  form.value = {
    hoTen: cust.hoTen || '',
    email: cust.email || '',
    matKhau: '',
    soDienThoai: cust.soDienThoai || '',
    diaChi: cust.diaChi || '',
    gioiTinh: cust.gioiTinh || 'Nam',
    ngaySinh: cust.ngaySinh || '',
    trangThai: cust.trangThai !== undefined ? cust.trangThai : true
  }
}

const deleteCustomer = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa khách hàng này? Mọi thông tin tài khoản liên quan cũng sẽ bị xóa.')) return
  try {
    await axios.delete(`${API_URL}/api/khach-hang/${id}`)
    await fetchCustomers()
    alert('Xóa khách hàng thành công!')
  } catch (error) {
    console.error('Lỗi khi xóa khách hàng:', error)
    alert('Không thể xóa khách hàng này!')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    hoTen: '',
    email: '',
    matKhau: '',
    soDienThoai: '',
    diaChi: '',
    gioiTinh: 'Nam',
    ngaySinh: '',
    trangThai: true
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '---'
  try {
    const parts = dateStr.split('-')
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}/${parts[0]}`
    }
    return dateStr
  } catch (e) {
    return dateStr
  }
}

onMounted(() => {
  fetchCustomers()
})
</script>

<style scoped>
.tracking-wider {
  letter-spacing: 0.1em;
}
.badge {
  font-weight: 500;
}
</style>
