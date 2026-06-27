<template>
  <div class="container mt-4">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tải...</p>
    </div>

    <div v-else-if="product" class="row">
      <div class="col-md-6">
        <div class="card">
          <div class="card-body text-center">
            <h3>{{ product.tenSanPham }}</h3>
            <p class="text-muted">{{ product.moTa }}</p>
            <h2 class="text-danger">{{ formatPrice(product.giaKhuyenMai) }}</h2>
            <p class="text-decoration-line-through text-muted">{{ formatPrice(product.giaGoc) }}</p>
          </div>
        </div>

        <!-- Biến thể sản phẩm -->
        <div class="card mt-3">
          <div class="card-body">
            <h5>Biến thể</h5>
            <div v-if="variants.length === 0" class="text-muted">Không có biến thể</div>
            <div v-else>
              <div v-for="v in variants" :key="v.maChiTietSp" class="border p-2 mb-2">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <strong>{{ v.maVachSku }}</strong> - {{ v.mauSac }} / {{ v.kichCo }}
                    <span class="badge bg-info ms-2">Tồn: {{ v.soLuongTon }}</span>
                  </div>
                  <div>
                    <span class="text-success">+{{ formatPrice(v.giaCongThem) }}</span>
                    <button @click="addToCart(v.maChiTietSp)" class="btn btn-sm btn-primary ms-2">Thêm vào giỏ</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="card">
          <div class="card-body">
            <h5>Thông tin sản phẩm</h5>
            <p><strong>Mã sản phẩm:</strong> {{ product.maSanPham }}</p>
            <p><strong>Danh mục:</strong> {{ product.maDanhMuc }}</p>
            <p><strong>Thương hiệu:</strong> {{ product.maThuongHieu }}</p>
            <p><strong>Ngày tạo:</strong> {{ formatDate(product.ngayTao) }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-5">
      <p class="text-muted">Không tìm thấy sản phẩm</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const API_URL = 'http://localhost:8080'
const route = useRoute()

const product = ref(null)
const variants = ref([])
const loading = ref(false)

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('vi-VN')
}

const fetchProduct = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_URL}/api/san-pham/${route.params.id}`)
    if (response.data && response.data.success) {
      product.value = response.data.data
      await fetchVariants()
    }
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const fetchVariants = async () => {
  try {
    const response = await axios.get(`${API_URL}/api/biens-the/san-pham/${product.value.maSanPham}`)
    if (response.data && response.data.success) {
      variants.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải biến thể:', error)
  }
}

const addToCart = async (maChiTietSp) => {
  try {
    await axios.post(`${API_URL}/api/gio-hang/1/items`, {
      maChiTietSp,
      soLuong: 1
    })
    alert('Đã thêm vào giỏ hàng!')
  } catch (error) {
    console.error('Lỗi khi thêm vào giỏ:', error)
    alert('Lỗi khi thêm vào giỏ hàng')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>
