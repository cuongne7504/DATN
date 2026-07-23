<template>
  <div class="container mt-4">
    <PageHeader title="Quản lý Biến thể" subtitle="Quản lý màu sắc, kích cỡ và tồn kho theo SKU" />

    <div class="card mb-4 shadow-sm">
      <div class="card-body p-4">
        <h5 class="mb-3 fw-semibold">{{ isEditing ? 'Cập nhật Biến thể' : 'Thêm Biến thể mới' }}</h5>
        <form @submit.prevent="saveVariant">
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">Sản phẩm <span class="text-danger">*</span></label>
              <select v-model="form.maSanPham" class="form-select" @change="fetchVariants" required>
                <option value="">Chọn sản phẩm</option>
                <option v-for="p in products" :key="p.maSanPham" :value="p.maSanPham">{{ p.tenSanPham }}</option>
              </select>
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Màu sắc <span class="text-danger">*</span></label>
              <input type="text" v-model="form.mauSac" class="form-control" required placeholder="VD: Đỏ">
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Kích cỡ <span class="text-danger">*</span></label>
              <input type="text" v-model="form.kichCo" class="form-control" required placeholder="VD: XL">
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Số lượng tồn <span class="text-danger">*</span></label>
              <input type="number" v-model="form.soLuongTon" class="form-control" min="0" required>
            </div>
            <div class="col-md-2 mb-3 d-flex align-items-end">
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm') }}
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
            <th>ID (Mã vạch)</th>
            <th>Sản phẩm</th>
            <th>Màu sắc</th>
            <th>Kích cỡ</th>
            <th>Tồn kho</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="variant in variants" :key="variant.maChiTietSp">
            <td class="fw-bold text-secondary">{{ variant.maChiTietSp }}</td>
            <td class="fw-bold">{{ getProductName(variant.maSanPham) }}</td>
            <td>{{ variant.mauSac }}</td>
            <td>{{ variant.kichCo }}</td>
            <td>
              <span class="badge" :class="variant.soLuongTon > 0 ? 'bg-success' : 'bg-danger'">
                {{ variant.soLuongTon > 0 ? variant.soLuongTon : 'Hết hàng' }}
              </span>
            </td>
            <td>
              <button @click="editVariant(variant)" class="btn btn-sm btn-outline-primary me-2">Sửa</button>
              <button @click="deleteVariant(variant.maChiTietSp)" class="btn btn-sm btn-outline-danger">Xóa</button>
            </td>
          </tr>
          <tr v-if="variants.length === 0">
            <td colspan="6" class="text-center text-muted py-3">Chưa có dữ liệu biến thể</td>
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
const variants = ref([])
const products = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = ref({
  maSanPham: '',
  mauSac: '',
  kichCo: '',
  soLuongTon: 0
})

const getProductName = (id) => {
  const p = products.value.find(x => x.maSanPham === id)
  return p ? p.tenSanPham : id
}

const fetchData = async () => {
  loading.value = true
  try {
    const prodRes = await axios.get(`${API_URL}/api/san-pham`)
    products.value = prodRes.data.data || prodRes.data || []
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const fetchVariants = async () => {
  if (!form.value.maSanPham) {
    variants.value = []
    return
  }
  try {
    const res = await axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${form.value.maSanPham}`)
    variants.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi khi tải biến thể:', error)
    variants.value = []
  }
}

const saveVariant = async () => {
  loading.value = true
  try {
    const payload = {
      maSanPham: Number(form.value.maSanPham),
      mauSac: form.value.mauSac,
      kichCo: form.value.kichCo,
      soLuongTon: Number(form.value.soLuongTon)
    }

    if (isEditing.value) {
      await axios.put(`${API_URL}/api/chi-tiet-san-pham/${editingId.value}`, payload)
      alert('Cập nhật thành công!')
    } else {
      await axios.post(`${API_URL}/api/chi-tiet-san-pham`, payload)
      alert('Thêm mới thành công!')
    }
    
    resetForm()
    await fetchVariants()
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editVariant = (variant) => {
  isEditing.value = true
  editingId.value = variant.maChiTietSp
  form.value = {
    maSanPham: variant.maSanPham,
    mauSac: variant.mauSac,
    kichCo: variant.kichCo,
    soLuongTon: variant.soLuongTon
  }
}

const deleteVariant = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa biến thể này?')) return
  try {
    await axios.delete(`${API_URL}/api/chi-tiet-san-pham/${id}`)
    await fetchVariants()
  } catch (error) {
    alert('Không thể xóa biến thể này!')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  const currentMaSP = form.value.maSanPham
  form.value = {
    maSanPham: currentMaSP,
    mauSac: '',
    kichCo: '',
    soLuongTon: 0
  }
}

onMounted(() => {
  fetchData()
})
</script>
