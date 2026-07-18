<template>
  <div>
    <!-- Hero Banner -->
    <!-- <div class="position-relative w-100">
      <img src="/banner.png" alt="SportPro Banner" class="w-100" style="height: auto; display: block;">
      <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center text-white" style="background: rgba(0,0,0,0.4);">
        <h1 class="display-3 fw-bold text-uppercase tracking-wider mb-3">SportPro</h1>
        <p class="fs-4 mb-4">Nâng tầm phong cách, bứt phá giới hạn</p>
        <button class="btn btn-light btn-lg px-5 rounded-0 fw-bold text-uppercase" @click="scrollToProducts">Khám phá ngay</button>
      </div>
    </div> -->
    <div class="position-relative w-100 sportpro-banner">
  <!-- Nội dung text và nút bấm đè lên ảnh -->
  <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center text-white" style="background: rgba(0,0,0,0.4);">
    <h1 class="display-3 fw-bold text-uppercase tracking-wider mb-3">SportPro</h1>
    <p class="fs-4 mb-4">Nâng tầm phong cách, bứt phá giới hạn</p>
    <button class="btn btn-light btn-lg px-5 rounded-0 fw-bold text-uppercase" @click="scrollToProducts">Khám phá ngay</button>
  </div>
</div>

    <div class="container mt-5" id="products-section">
      <!-- Bộ lọc -->
    <div class="card mb-4 bg-light shadow-sm border-0">
      <div class="card-body">
        <h5 class="card-title fw-bold text-primary mb-3"><i class="bi bi-funnel"></i> Bộ lọc tìm kiếm</h5>
        <div class="row g-3">
          <div class="col-md-4">
            <input type="text" v-model="searchForm.ten" @input="fetchProducts" class="form-control border-dark" placeholder="Tìm theo tên sản phẩm...">
          </div>
          <div class="col-md-4">
            <select v-model="searchForm.maDanhMuc" @change="fetchProducts" class="form-select border-dark">
              <option value="">Tất cả Danh mục</option>
              <option v-for="cat in categories" :key="cat.maDanhMuc" :value="cat.maDanhMuc">
                {{ cat.tenDanhMuc }}
              </option>
            </select>
          </div>
          <div class="col-md-4">
            <select v-model="searchForm.maThuongHieu" @change="fetchProducts" class="form-select border-dark">
              <option value="">Tất cả Thương hiệu</option>
              <option v-for="brand in brands" :key="brand.maThuongHieu" :value="brand.maThuongHieu">
                {{ brand.tenThuongHieu }}
              </option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="row row-cols-1 row-cols-md-3 row-cols-lg-4 g-4">
      <div class="col" v-for="product in products" :key="product.maSanPham">
        <div class="card h-100 shadow-sm product-card border-0 position-relative">
          <!-- Removed Wishlist Button as requested -->

          <img :src="getMainImage(product)" class="card-img-top object-fit-cover" alt="Product Image" style="height: 200px;">
          
          <div class="card-body d-flex flex-column text-center">
            <h6 class="card-title fw-bold text-truncate">{{ product.tenSanPham }}</h6>
            
            <div class="mt-auto">
              <div class="mb-3">
                <span class="text-danger fw-bold fs-5">{{ formatPrice(product.giaKhuyenMai || product.GiKhuyenMai) }}</span>
                <div v-if="(product.giaGoc || product.GiGoc) > (product.giaKhuyenMai || product.GiKhuyenMai)" class="text-muted text-decoration-line-through small">
                  {{ formatPrice(product.giaGoc || product.GiGoc) }}
                </div>
              </div>
              <router-link :to="'/product/' + product.maSanPham" class="btn btn-outline-primary w-100 fw-bold text-uppercase">
                MUA NGAY
              </router-link>
            </div>
          </div>
        </div>
      </div>
      <div v-if="products.length === 0" class="col-12 text-center text-muted py-5">
        Không tìm thấy sản phẩm nào phù hợp.
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import { API_URL } from '@/config.js'
const router = useRouter()
const products = ref([])
const categories = ref([])
const brands = ref([])
const loading = ref(false)
const user = ref(null)
const wishlist = ref([])

const searchForm = ref({
  ten: '',
  maDanhMuc: '',
  maThuongHieu: ''
})

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const getMainImage = (product) => {
  if (product.hinhAnh && product.hinhAnh.length > 0) {
    const mainImg = product.hinhAnh.find(img => img.laAnhChinh)
    let path = mainImg ? mainImg.duongDanAnh : product.hinhAnh[0].duongDanAnh;
    if (path && path.startsWith('http')) {
      return path;
    }
    // Ảnh lưu trên backend, cần thêm URL prefix
    return `${API_URL}/api/hinh-anh/uploads/${path}`;
  }
  return 'https://via.placeholder.com/300x200?text=No+Image'
}

const fetchFilters = async () => {
  try {
    const [catRes, brandRes] = await Promise.all([
      axios.get(`${API_URL}/api/danh-muc`),
      axios.get(`${API_URL}/api/thuong-hieu`)
    ])
    categories.value = catRes.data.data || catRes.data || []
    brands.value = brandRes.data.data || brandRes.data || []
  } catch (error) {
    console.error('Lỗi tải bộ lọc:', error)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const [res, imgRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham`),
      axios.get(`${API_URL}/api/hinh-anh`).catch(() => ({data: {data: []}}))
    ])
    let allProducts = res.data.data || res.data || []
    let allImages = imgRes.data.data || imgRes.data || []
    
    // Attach images
    allProducts.forEach(p => {
      p.hinhAnh = allImages.filter(img => img.maSanPham === p.maSanPham)
    })
    
    if (searchForm.value.ten) {
      allProducts = allProducts.filter(p => p.tenSanPham.toLowerCase().includes(searchForm.value.ten.toLowerCase()))
    }
    if (searchForm.value.maDanhMuc) {
      allProducts = allProducts.filter(p => p.maDanhMuc === searchForm.value.maDanhMuc)
    }
    if (searchForm.value.maThuongHieu) {
      allProducts = allProducts.filter(p => p.maThuongHieu === searchForm.value.maThuongHieu)
    }
    
    products.value = allProducts
  } catch (error) {
    console.error('Lỗi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const fetchWishlist = async () => {
  if (!user.value) return
  try {
    const res = await axios.get(`${API_URL}/api/yeu-thich/nguoi-dung/${user.value.maNguoiDung}`)
    wishlist.value = res.data.data || res.data || []
  } catch (err) {
    console.error('Lỗi tải wishlist:', err)
  }
}

const isWishlisted = (maSanPham) => {
  return wishlist.value.some(w => w.sanPham?.maSanPham === maSanPham)
}

const toggleWishlist = async (product) => {
  if (!user.value) {
    router.push('/login')
    return
  }
  try {
    await axios.post(`${API_URL}/api/yeu-thich/toggle?maNguoiDung=${user.value.maNguoiDung}&maSanPham=${product.maSanPham}`)
    await fetchWishlist() // Refresh
  } catch (err) {
    console.error('Lỗi toggle wishlist:', err)
  }
}

const scrollToProducts = () => {
  document.getElementById('products-section')?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
    fetchWishlist()
  }
  fetchFilters()
  fetchProducts()
})
</script>

<style scoped>
.tracking-wider {
  letter-spacing: 0.15em;
}
.position-relative > img[alt="SportPro Banner"] {
  height: 50vh !important;
  max-height: 450px !important;
  object-fit: cover !important;
  object-position: center 30% !important;
}

/* Tùy chỉnh chữ trên Banner */
.position-absolute.top-0 h1 {
  background: linear-gradient(135deg, #ffffff, #bae6fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  font-weight: 900 !important;
}

.position-absolute.top-0 p {
  color: #f1f5f9 !important;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.6);
  font-weight: 500;
  letter-spacing: 0.05em;
}

/* Bo tròn nút 'Khám phá ngay' và thêm màu đẹp */
.position-absolute.top-0 button {
  border-radius: 9999px !important; /* Bo tròn hoàn toàn */
  background: linear-gradient(135deg, #0ea5e9, #38bdf8) !important; /* Gradient xanh dương nhạt cho hợp navbar */
  color: #ffffff !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(14, 165, 233, 0.4) !important;
  padding: 0.75rem 2.5rem !important;
  transition: all 0.3s ease !important;
}

.position-absolute.top-0 button:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 25px rgba(14, 165, 233, 0.6) !important;
  background: linear-gradient(135deg, #0284c7, #0ea5e9) !important;
}

.product-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}
.wishlist-btn {
  z-index: 2;
  transition: all 0.2s;
}
.wishlist-btn:hover {
  background-color: #f8f9fa;
  transform: scale(1.1);
}
.sportpro-banner {
  /* Đường dẫn tới file ảnh của bạn */
  background-image: url('/banner.png'); 

  
  /* Căn giữa ảnh và tự động co giãn ôm khít khung mà không bị méo */
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;

  /* Thiết lập chiều cao cho banner */
  /* height: 100vh; Chiếm toàn bộ chiều cao màn hình (Tùy chọn) */
  /* Hoặc bạn có thể dùng chiều cao cố định như bên dưới: */
  height: 600px; 
}

/* Responsive: Tự động giảm chiều cao banner trên thiết bị di động */
@media (max-width: 768px) {
  .sportpro-banner {
    height: 60vh; /* Hoặc 350px */
  }
}
</style>
