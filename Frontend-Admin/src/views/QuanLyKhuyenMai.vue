<template>
  <div class="container mt-4">
    <PageHeader title="Quản lý Khuyến mãi" subtitle="Tạo và quản lý mã giảm giá / voucher" />

    <div class="card mb-4 shadow-sm">
      <div class="card-body p-4">
        <h5 class="mb-3 fw-semibold">{{ isEditing ? 'Cập nhật Khuyến mãi' : 'Thêm Khuyến mãi mới' }}</h5>
        <form @submit.prevent="saveVoucher">
          <div class="row">
            <div class="col-md-3 mb-3">
              <label class="form-label">Mã code <span class="text-danger">*</span></label>
              <input type="text" v-model="form.maCode" class="form-control" required placeholder="VD: SUMMER2026">
            </div>
            <div class="col-md-5 mb-3">
              <label class="form-label">Mô tả</label>
              <input type="text" v-model="form.moTa" class="form-control">
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">% Giảm <span class="text-danger">*</span></label>
              <input type="number" v-model="form.phanTramGiam" class="form-control" min="1" max="100" required>
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Số lượng <span class="text-danger">*</span></label>
              <input type="number" v-model="form.soLuongDung" class="form-control" min="1" required>
            </div>
          </div>
          <div class="row">
            <div class="col-md-5 mb-3">
              <label class="form-label">Thời gian bắt đầu <span class="text-danger">*</span></label>
              <input type="datetime-local" v-model="form.ngayBatDau" class="form-control" required>
            </div>
            <div class="col-md-5 mb-3">
              <label class="form-label">Thời gian kết thúc <span class="text-danger">*</span></label>
              <input type="datetime-local" v-model="form.ngayKetThuc" class="form-control" required>
            </div>
            <div class="col-md-2 mb-3 d-flex align-items-end">
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
              </button>
            </div>
          </div>
          <button type="button" v-if="isEditing" @click="resetForm" class="btn btn-secondary">Hủy</button>
        </form>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="table-responsive bg-white rounded shadow-sm p-3">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th class="text-nowrap">Mã Code</th>
            <th class="text-nowrap">Mô tả</th>
            <th class="text-nowrap">Giảm (%)</th>
            <th class="text-nowrap">Số lượng</th>
            <th class="text-nowrap">Thời gian</th>
            <th class="text-nowrap">Trạng thái</th>
            <th class="text-nowrap">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in vouchers" :key="v.maKhuyenMai">
            <td class="fw-bold text-danger text-nowrap">{{ v.maCode }}</td>
            <td>{{ v.moTa }}</td>
            <td class="text-nowrap"><span class="badge bg-success">{{ v.phanTramGiam }}%</span></td>
            <td class="text-nowrap">{{ v.soLuongDung }}</td>
            <td class="text-nowrap" style="font-size: 0.85rem;">
              {{ formatDate(v.ngayBatDau) }}<br>
              đến {{ formatDate(v.ngayKetThuc) }}
            </td>
            <td class="text-nowrap">
              <span class="badge text-nowrap" :class="getVoucherStatusBadgeClass(v)">
                {{ getVoucherStatus(v) }}
              </span>
            </td>
            <td class="text-nowrap">
              <div class="d-flex flex-nowrap align-items-center gap-1">
                <button @click="editVoucher(v)" class="btn btn-sm btn-outline-primary text-nowrap">Sửa</button>
                <button v-if="getVoucherStatus(v) === 'Hoạt động'" @click="stopVoucher(v)" class="btn btn-sm btn-outline-warning text-nowrap" title="Dừng áp dụng mã">Dừng</button>
                <button @click="deleteVoucher(v.maKhuyenMai)" class="btn btn-sm btn-outline-danger text-nowrap">Xóa</button>
              </div>
            </td>
          </tr>
          <tr v-if="vouchers.length === 0">
            <td colspan="7" class="text-center text-muted py-3">Chưa có mã khuyến mãi nào</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import PageHeader from '@/components/PageHeader.vue'

import { API_URL } from '@/config.js'
const vouchers = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = ref({
  maCode: '',
  phanTramGiam: 10,
  soTienGiam: 0,
  donToiThieu: 0,
  ngayBatDau: '',
  ngayKetThuc: '',
  soLuongDung: 100
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleString('vi-VN')
}

const getVoucherStatus = (v) => {
  if (!v.ngayBatDau || !v.ngayKetThuc) return 'Không xác định'
  const now = new Date()
  const start = new Date(v.ngayBatDau)
  const end = new Date(v.ngayKetThuc)

  if (v.soLuongDung != null && v.soLuongDung <= 0) return 'Hết lượt dùng'
  if (now < start) return 'Sắp diễn ra'
  if (now > end) return 'Đã hết hạn'
  return 'Hoạt động'
}

const getVoucherStatusBadgeClass = (v) => {
  const status = getVoucherStatus(v)
  switch (status) {
    case 'Hoạt động': return 'bg-success'
    case 'Sắp diễn ra': return 'bg-info text-dark'
    case 'Hết lượt dùng': return 'bg-danger'
    case 'Đã hết hạn': return 'bg-secondary'
    default: return 'bg-secondary'
  }
}

// Convert "2026-06-27T10:00:00" to "2026-06-27T10:00" for datetime-local input
const toDateTimeLocal = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 16)
}

const fetchVouchers = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/khuyen-mai`)
    vouchers.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi khi tải khuyến mãi:', error)
  } finally {
    loading.value = false
  }
}

const saveVoucher = async () => {
  loading.value = true
  try {
    // Validate dates
    const startDate = new Date(form.value.ngayBatDau)
    const endDate = new Date(form.value.ngayKetThuc)
    if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
      alert('Vui lòng nhập thời gian bắt đầu và kết thúc hợp lệ!')
      loading.value = false
      return
    }
    if (endDate <= startDate) {
      alert('Thời gian kết thúc phải sau thời gian bắt đầu!')
      loading.value = false
      return
    }

    if (isEditing.value) {
      await axios.put(`${API_URL}/api/khuyen-mai/${editingId.value}`, form.value)
      alert('Cập nhật thành công!')
    } else {
      await axios.post(`${API_URL}/api/khuyen-mai`, form.value)
      alert('Thêm mới thành công!')
    }
    resetForm()
    await fetchVouchers()
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editVoucher = (v) => {
  isEditing.value = true
  editingId.value = v.maKhuyenMai
  form.value = {
    maCode: v.maCode,
    phanTramGiam: v.phanTramGiam,
    soTienGiam: v.soTienGiam || 0,
    donToiThieu: v.donToiThieu || 0,
    ngayBatDau: toDateTimeLocal(v.ngayBatDau),
    ngayKetThuc: toDateTimeLocal(v.ngayKetThuc),
    soLuongDung: v.soLuongDung || 100
  }
}

const stopVoucher = async (v) => {
  if (!confirm(`Bạn có chắc muốn dừng áp dụng mã "${v.maCode}" ngay bây giờ?`)) return
  try {
    const payload = {
      maCode: v.maCode,
      moTa: v.moTa,
      phanTramGiam: v.phanTramGiam,
      soTienGiam: v.soTienGiam || 0,
      donToiThieu: v.donToiThieu || 0,
      ngayBatDau: v.ngayBatDau,
      ngayKetThuc: new Date().toISOString().slice(0, 19),
      soLuongDung: 0
    }
    await axios.put(`${API_URL}/api/khuyen-mai/${v.maKhuyenMai}`, payload)
    await fetchVouchers()
    alert(`Đã dừng áp dụng mã "${v.maCode}" thành công`)
  } catch (err) {
    alert(err.response?.data?.message || 'Không thể dừng mã khuyến mãi!')
  }
}

const deleteVoucher = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa mã này?')) return
  try {
    await axios.delete(`${API_URL}/api/khuyen-mai/${id}`)
    await fetchVouchers()
  } catch (error) {
    alert(error.response?.data?.message || 'Không thể xóa mã khuyến mãi này!')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    maCode: '',
    phanTramGiam: 10,
    soTienGiam: 0,
    donToiThieu: 0,
    ngayBatDau: '',
    ngayKetThuc: '',
    soLuongDung: 100
  }
}

onMounted(() => {
  fetchVouchers()
})
</script>
