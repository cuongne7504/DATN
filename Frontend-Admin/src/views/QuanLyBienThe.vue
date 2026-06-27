<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý Biến thể Sản phẩm</h2>

    <!-- Chọn sản phẩm -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-6">
            <label class="form-label">Chọn sản phẩm</label>
            <select v-model="selectedProductId" @change="loadProductVariants" class="form-select">
              <option value="">-- Chọn sản phẩm --</option>
              <option v-for="product in products" :key="product.maSanPham" :value="product.maSanPham">
                {{ product.tenSanPham }}
              </option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Ma trận biến thể -->
    <div v-if="selectedProductId" class="card mb-4">
      <div class="card-body">
        <h5>Ma trận Biến thể</h5>
        
        <!-- Nhập màu sắc và kích thước -->
        <div class="row mb-3">
          <div class="col-md-5">
            <label class="form-label">Màu sắc (ngăn cách bằng dấu phẩy)</label>
            <input type="text" v-model="colorsInput" placeholder="Đỏ, Xanh, Đen" class="form-control">
          </div>
          <div class="col-md-5">
            <label class="form-label">Kích thước (ngăn cách bằng dấu phẩy)</label>
            <input type="text" v-model="sizesInput" placeholder="S, M, L, XL" class="form-control">
          </div>
          <div class="col-md-2">
            <label class="form-label">&nbsp;</label>
            <button @click="generateMatrix" class="btn btn-primary w-100">Tạo ma trận</button>
          </div>
        </div>

        <!-- Bảng ma trận -->
        <div v-if="variantMatrix.length > 0" class="table-responsive">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Màu sắc</th>
                <th v-for="size in sizes" :key="size">{{ size }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="color in colors" :key="color">
                <td>{{ color }}</td>
                <td v-for="size in sizes" :key="size">
                  <input 
                    type="number" 
                    v-model="variantMatrix[`${color}-${size}`].soLuong" 
                    class="form-control form-control-sm"
                    placeholder="SL"
                    min="0"
                  >
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="variantMatrix.length > 0" class="mt-3">
          <button @click="saveVariants" class="btn btn-success" :disabled="loading">
            {{ loading ? 'Đang lưu...' : 'Lưu biến thể' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Danh sách biến thể hiện tại -->
    <div v-if="selectedProductId" class="card">
      <div class="card-body">
        <h5>Danh sách biến thể hiện tại</h5>
        <div v-if="loading" class="text-center py-3">
          <div class="spinner-border text-primary"></div>
        </div>
        <div v-else-if="variants.length === 0" class="text-muted">
          Chưa có biến thể nào
        </div>
        <div v-else class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Mã biến thể</th>
                <th>Màu sắc</th>
                <th>Kích thước</th>
                <th>Số lượng tồn</th>
                <th>Giá bán</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="variant in variants" :key="variant.maChiTietSp">
                <td>{{ variant.maChiTietSp }}</td>
                <td>{{ variant.mauSac }}</td>
                <td>{{ variant.kichCo }}</td>
                <td>{{ variant.soLuongTon }}</td>
                <td>{{ formatPrice(variant.giaBan) }}</td>
                <td>
                  <button @click="deleteVariant(variant.maChiTietSp)" class="btn btn-sm btn-danger">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const products = ref([])
const variants = ref([])
const selectedProductId = ref(null)
const loading = ref(false)

const colorsInput = ref('')
const sizesInput = ref('')
const variantMatrix = ref({})

const colors = computed(() => colorsInput.value.split(',').map(c => c.trim()).filter(c => c))
const sizes = computed(() => sizesInput.value.split(',').map(s => s.trim()).filter(s => s))

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const fetchProducts = async () => {
  try {
    const response = await axios.get(`${API_URL}/api/san-pham`)
    if (response.data && response.data.success) {
      products.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
  }
}

const loadProductVariants = async () => {
  if (!selectedProductId.value) return
  
  loading.value = true
  try {
    const response = await axios.get(`${API_URL}/api/san-pham/${selectedProductId.value}/variants`)
    if (response.data && response.data.success) {
      variants.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải biến thể:', error)
    variants.value = []
  } finally {
    loading.value = false
  }
}

const generateMatrix = () => {
  variantMatrix.value = {}
  for (const color of colors.value) {
    for (const size of sizes.value) {
      variantMatrix.value[`${color}-${size}`] = {
        mauSac: color,
        kichCo: size,
        soLuong: 0,
        giaBan: 0
      }
    }
  }
}

const saveVariants = async () => {
  loading.value = true
  try {
    const variantsToSave = Object.values(variantMatrix.value).filter(v => v.soLuong > 0)
    
    for (const variant of variantsToSave) {
      const payload = {
        maSanPham: selectedProductId.value,
        mauSac: variant.mauSac,
        kichCo: variant.kichCo,
        soLuongTon: variant.soLuong,
        giaBan: 100000 // Giá mặc định, có thể chỉnh sau
      }
      await axios.post(`${API_URL}/api/chi-tiet-san-pham`, payload)
    }
    
    alert('Đã lưu biến thể thành công!')
    await loadProductVariants()
    variantMatrix.value = {}
  } catch (error) {
    console.error('Lỗi khi lưu biến thể:', error)
    alert('Lỗi khi lưu biến thể')
  } finally {
    loading.value = false
  }
}

const deleteVariant = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa?')) return
  try {
    await axios.delete(`${API_URL}/api/chi-tiet-san-pham/${id}`)
    await loadProductVariants()
  } catch (error) {
    console.error('Lỗi khi xóa:', error)
  }
}

onMounted(() => {
  fetchProducts()
})
</script>
