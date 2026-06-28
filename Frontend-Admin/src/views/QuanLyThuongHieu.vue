<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý Thương hiệu</h2>

    <div class="card mb-4 shadow-sm">
      <div class="card-body">
        <h5 class="mb-3">{{ isEditing ? 'Cập nhật Thương hiệu' : 'Thêm Thương hiệu mới' }}</h5>
        <form @submit.prevent="saveBrand">
          <div class="row">
            <div class="col-md-8 mb-3">
              <label class="form-label">Tên thương hiệu <span class="text-danger">*</span></label>
              <input type="text" v-model="form.tenThuongHieu" class="form-control" required>
            </div>
            <div class="col-md-4 mb-3 d-flex align-items-end">
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
            <th>ID</th>
            <th>Tên thương hiệu</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="brand in brands" :key="brand.maThuongHieu">
            <td>{{ brand.maThuongHieu }}</td>
            <td class="fw-bold">{{ brand.tenThuongHieu }}</td>
            <td>
              <button @click="editBrand(brand)" class="btn btn-sm btn-outline-primary me-2">Sửa</button>
              <button @click="deleteBrand(brand.maThuongHieu)" class="btn btn-sm btn-outline-danger">Xóa</button>
            </td>
          </tr>
          <tr v-if="brands.length === 0">
            <td colspan="3" class="text-center text-muted py-3">Chưa có thương hiệu nào</td>
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
const brands = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = ref({
  tenThuongHieu: ''
})

const fetchBrands = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/thuong-hieu`)
    brands.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi khi tải thương hiệu:', error)
  } finally {
    loading.value = false
  }
}

const saveBrand = async () => {
  loading.value = true
  try {
    if (isEditing.value) {
      await axios.put(`${API_URL}/api/thuong-hieu/${editingId.value}`, form.value)
      alert('Cập nhật thành công!')
    } else {
      await axios.post(`${API_URL}/api/thuong-hieu`, form.value)
      alert('Thêm mới thành công!')
    }
    resetForm()
    await fetchBrands()
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editBrand = (brand) => {
  isEditing.value = true
  editingId.value = brand.maThuongHieu
  form.value.tenThuongHieu = brand.tenThuongHieu
}

const deleteBrand = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa?')) return
  try {
    await axios.delete(`${API_URL}/api/thuong-hieu/${id}`)
    await fetchBrands()
  } catch (error) {
    alert('Không thể xóa thương hiệu này!')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value.tenThuongHieu = ''
}

onMounted(() => {
  fetchBrands()
})
</script>
