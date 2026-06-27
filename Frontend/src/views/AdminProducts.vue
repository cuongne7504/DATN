<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý sản phẩm</h2>

    <!-- Form thêm/sửa sản phẩm -->
    <div class="card mb-4">
      <div class="card-body">
        <h5>{{ isEditing ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}</h5>
        <form @submit.prevent="saveProduct">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Tên sản phẩm *</label>
              <input type="text" v-model="form.tenSanPham" class="form-control" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Mô tả</label>
              <textarea v-model="form.moTa" class="form-control"></textarea>
            </div>
          </div>
          <div class="row">
            <div class="col-md-3 mb-3">
              <label class="form-label">Danh mục *</label>
              <input type="number" v-model="form.maDanhMuc" class="form-control" required>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Thương hiệu *</label>
              <input type="number" v-model="form.maThuongHieu" class="form-control" required>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Giá gốc *</label>
              <input type="number" v-model="form.giaGoc" class="form-control" required>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Giá KM *</label>
              <input type="number" v-model="form.giaKhuyenMai" class="form-control" required>
            </div>
          </div>
          
          <!-- Quản lý hình ảnh -->
          <div class="mb-3">
            <label class="form-label">Hình ảnh sản phẩm</label>
            <div class="mb-2">
              <input type="file" @change="handleImageUpload" accept="image/*" multiple class="form-control">
            </div>
            <div v-if="images.length > 0" class="d-flex flex-wrap gap-2">
              <div v-for="(img, index) in images" :key="index" class="position-relative">
                <img :src="img" class="img-thumbnail" style="width: 80px; height: 80px; object-fit: cover;">
                <button @click="removeImage(index)" class="btn btn-sm btn-danger position-absolute top-0 end-0" style="width: 20px; height: 20px; padding: 0;">×</button>
              </div>
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
          </button>
          <button v-if="isEditing" @click="resetForm" class="btn btn-secondary ms-2">Hủy</button>
        </form>
      </div>
    </div>

    <!-- Danh sách sản phẩm -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Danh mục</th>
            <th>Thương hiệu</th>
            <th>Giá gốc</th>
            <th>Giá KM</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.maSanPham">
            <td>{{ product.maSanPham }}</td>
            <td>{{ product.tenSanPham }}</td>
            <td>{{ product.maDanhMuc }}</td>
            <td>{{ product.maThuongHieu }}</td>
            <td>{{ formatPrice(product.giaGoc) }}</td>
            <td>{{ formatPrice(product.giaKhuyenMai) }}</td>
            <td>
              <button @click="editProduct(product)" class="btn btn-sm btn-warning me-2">Sửa</button>
              <button @click="deleteProduct(product.maSanPham)" class="btn btn-sm btn-danger">Xóa</button>
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

const products = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const images = ref([])

const form = ref({
  maDanhMuc: '',
  maThuongHieu: '',
  tenSanPham: '',
  moTa: '',
  giaGoc: '',
  giaKhuyenMai: ''
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_URL}/api/san-pham`)
    if (response.data && response.data.success) {
      products.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const handleImageUpload = (event) => {
  const files = event.target.files
  for (let i = 0; i < files.length; i++) {
    const reader = new FileReader()
    reader.onload = (e) => {
      images.value.push(e.target.result)
    }
    reader.readAsDataURL(files[i])
  }
}

const removeImage = (index) => {
  images.value.splice(index, 1)
}

const saveProduct = async () => {
  loading.value = true
  try {
    const payload = {
      maDanhMuc: Number(form.value.maDanhMuc),
      maThuongHieu: Number(form.value.maThuongHieu),
      tenSanPham: form.value.tenSanPham,
      moTa: form.value.moTa,
      giaGoc: Number(form.value.giaGoc),
      giaKhuyenMai: Number(form.value.giaKhuyenMai)
    }

    let productId
    if (isEditing.value) {
      productId = editingId.value
      await axios.put(`${API_URL}/api/san-pham/${productId}`, payload)
    } else {
      const response = await axios.post(`${API_URL}/api/san-pham`, payload)
      productId = response.data.data.maSanPham
    }
    
    // Upload ảnh nếu có
    if (images.value.length > 0 && productId) {
      for (let i = 0; i < images.value.length; i++) {
        try {
          const imagePayload = {
            maSanPham: productId,
            duongDanAnh: images.value[i],
            laAnhChinh: i === 0 // Ảnh đầu tiên là ảnh chính
          }
          await axios.post(`${API_URL}/api/hinh-anh`, imagePayload)
        } catch (imgError) {
          console.error('Lỗi khi upload ảnh:', imgError)
          alert('Lỗi khi upload ảnh: ' + (imgError.response?.data?.message || imgError.message))
        }
      }
    }
    
    resetForm()
    await fetchProducts()
    alert('Lưu sản phẩm thành công!')
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error)
    alert('Lỗi khi lưu sản phẩm: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editProduct = (product) => {
  isEditing.value = true
  editingId.value = product.maSanPham
  form.value = {
    maDanhMuc: product.maDanhMuc,
    maThuongHieu: product.maThuongHieu,
    tenSanPham: product.tenSanPham,
    moTa: product.moTa || '',
    giaGoc: product.giaGoc,
    giaKhuyenMai: product.giaKhuyenMai
  }
  images.value = []
}

const deleteProduct = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa?')) return
  try {
    await axios.delete(`${API_URL}/api/san-pham/${id}`)
    await fetchProducts()
  } catch (error) {
    console.error('Lỗi khi xóa:', error)
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    maDanhMuc: '',
    maThuongHieu: '',
    tenSanPham: '',
    moTa: '',
    giaGoc: '',
    giaKhuyenMai: ''
  }
  images.value = []
}

onMounted(() => {
  fetchProducts()
})
</script>
