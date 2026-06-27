<template>
  <div class="container mt-4">
    <div class="row mb-4">
      <div class="col-md-12">
        <h1 class="text-primary fw-bold">SportPro</h1>
        <p class="text-muted">Cửa hàng thể thao chuyên nghiệp</p>
      </div>
    </div>

    <!-- Bộ lọc tìm kiếm -->
    <div class="card mb-4 bg-light">
      <div class="card-body">
        <h5 class="card-title">Bộ lọc sản phẩm</h5>
        <div class="row">
          <div class="col-md-3 mb-2">
            <input type="text" v-model="searchForm.ten" class="form-control" placeholder="Tên sản phẩm">
          </div>
          <div class="col-md-2 mb-2">
            <select v-model="searchForm.maDanhMuc" class="form-select">
              <option value="">Tất cả danh mục</option>
              <option v-for="cat in categories" :key="cat.maDanhMuc" :value="cat.maDanhMuc">
                {{ cat.tenDanhMuc }}
              </option>
            </select>
          </div>
          <div class="col-md-2 mb-2">
            <select v-model="searchForm.maThuongHieu" class="form-select">
              <option value="">Tất cả thương hiệu</option>
              <option v-for="brand in brands" :key="brand.maThuongHieu" :value="brand.maThuongHieu">
                {{ brand.tenThuongHieu }}
              </option>
            </select>
          </div>
          <div class="col-md-2 mb-2">
            <input type="number" v-model="searchForm.minGia" class="form-control" placeholder="Giá min">
          </div>
          <div class="col-md-2 mb-2">
            <input type="number" v-model="searchForm.maxGia" class="form-control" placeholder="Giá max">
          </div>
          <div class="col-md-1 mb-2">
            <button @click="searchProducts" class="btn btn-primary w-100">Tìm</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Danh sách sản phẩm -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tải...</p>
    </div>

    <div v-else-if="products.length === 0" class="text-center py-5">
      <p class="text-muted">Không có sản phẩm nào</p>
    </div>

    <div v-else class="row">
      <div v-for="product in products" :key="product.maSanPham" class="col-md-4 mb-4">
        <div class="card h-100 shadow-sm">
          <img 
            :src="getProductImage(product.maSanPham)" 
            class="card-img-top" 
            :alt="product.tenSanPham"
            style="height: 200px; object-fit: cover;"
          >
          <div class="card-body">
            <h5 class="card-title">{{ product.tenSanPham }}</h5>
            <p class="card-text text-muted small">{{ product.moTa }}</p>
            <p class="card-text">
              <span class="text-decoration-line-through text-muted">{{ formatPrice(product.giaGoc) }}</span>
              <span class="text-danger fw-bold ms-2">{{ formatPrice(product.giaKhuyenMai) }}</span>
            </p>
            <router-link :to="`/product/${product.maSanPham}`" class="btn btn-primary w-100">Xem chi tiết</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const products = ref([])
const categories = ref([])
const brands = ref([])
const productImages = ref({})
const loading = ref(false)
const searchForm = ref({
  ten: '',
  maDanhMuc: '',
  maThuongHieu: '',
  minGia: '',
  maxGia: ''
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const fetchCategories = async () => {
  try {
    const response = await axios.get(`${API_URL}/api/danh-muc`)
    if (response.data && response.data.success) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải danh mục:', error)
  }
}

const fetchBrands = async () => {
  try {
    const response = await axios.get(`${API_URL}/api/thuong-hieu`)
    if (response.data && response.data.success) {
      brands.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải thương hiệu:', error)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_URL}/api/san-pham`)
    if (response.data && response.data.success) {
      products.value = response.data.data
      // Lấy ảnh cho từng sản phẩm
      for (const product of products.value) {
        await fetchProductImages(product.maSanPham)
      }
    }
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const fetchProductImages = async (maSanPham) => {
  try {
    const response = await axios.get(`${API_URL}/api/hinh-anh/san-pham/${maSanPham}`)
    if (response.data && response.data.success) {
      const images = response.data.data
      // Tìm ảnh chính hoặc lấy ảnh đầu tiên
      const mainImage = images.find(img => img.laAnhChinh === true) || images[0]
      if (mainImage) {
        productImages.value[maSanPham] = mainImage.duongDanAnh
      }
    }
  } catch (error) {
    console.error('Lỗi khi tải ảnh sản phẩm:', error)
  }
}

const getProductImage = (maSanPham) => {
  return productImages.value[maSanPham] || 'https://placehold.co/300x200?text=No+Image'
}

const searchProducts = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.value.ten) params.ten = searchForm.value.ten
    if (searchForm.value.maDanhMuc) params.maDanhMuc = Number(searchForm.value.maDanhMuc)
    if (searchForm.value.maThuongHieu) params.maThuongHieu = Number(searchForm.value.maThuongHieu)
    if (searchForm.value.minGia) params.minGia = Number(searchForm.value.minGia)
    if (searchForm.value.maxGia) params.maxGia = Number(searchForm.value.maxGia)

    const response = await axios.get(`${API_URL}/api/san-pham/search`, { params })
    if (response.data && response.data.success) {
      products.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tìm kiếm:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchBrands()
  fetchProducts()
})
</script>
