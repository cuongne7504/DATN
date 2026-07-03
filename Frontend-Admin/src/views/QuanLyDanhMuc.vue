<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý Danh mục</h2>

    <div class="card mb-4 shadow-sm">
      <div class="card-body">
        <h5 class="mb-3">{{ isEditing ? 'Cập nhật Danh mục' : 'Thêm Danh mục mới' }}</h5>
        <form @submit.prevent="saveCategory">
          <div class="row">
            <div class="col-md-8 mb-3">
              <label class="form-label">Tên danh mục <span class="text-danger">*</span></label>
              <input type="text" v-model="form.tenDanhMuc" class="form-control" required>
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
            <th>Tên danh mục</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cat in categories" :key="cat.maDanhMuc">
            <td>{{ cat.maDanhMuc }}</td>
            <td class="fw-bold">{{ cat.tenDanhMuc }}</td>
            <td>
              <button @click="editCategory(cat)" class="btn btn-sm btn-outline-primary me-2">Sửa</button>
              <button @click="deleteCategory(cat.maDanhMuc)" class="btn btn-sm btn-outline-danger">Xóa</button>
            </td>
          </tr>
          <tr v-if="categories.length === 0">
            <td colspan="3" class="text-center text-muted py-3">Chưa có danh mục nào</td>
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
const categories = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = ref({
  tenDanhMuc: ''
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/danh-muc`)
    categories.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi khi tải danh mục:', error)
  } finally {
    loading.value = false
  }
}

const saveCategory = async () => {
  loading.value = true
  try {
    if (isEditing.value) {
      await axios.put(`${API_URL}/api/danh-muc/${editingId.value}`, form.value)
      alert('Cập nhật thành công!')
    } else {
      await axios.post(`${API_URL}/api/danh-muc`, form.value)
      alert('Thêm mới thành công!')
    }
    resetForm()
    await fetchCategories()
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editCategory = (cat) => {
  isEditing.value = true
  editingId.value = cat.maDanhMuc
  form.value.tenDanhMuc = cat.tenDanhMuc
}

const deleteCategory = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa?')) return
  try {
    await axios.delete(`${API_URL}/api/danh-muc/${id}`)
    await fetchCategories()
  } catch (error) {
    alert('Không thể xóa danh mục này!')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value.tenDanhMuc = ''
}

onMounted(() => {
  fetchCategories()
})
</script>
