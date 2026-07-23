<template>
  <div class="container mt-5">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark" role="status"></div>
    </div>
    
    <div v-else-if="product" class="row">
      <!-- Ảnh sản phẩm -->
      <div class="col-md-6 mb-4">
        <div class="card border-0">
          <img :src="getImageUrl(mainImage)" class="img-fluid rounded-0 object-fit-cover shadow-sm" style="height: 500px; width: 100%;" alt="Product Image">
          <div class="d-flex gap-2 mt-3 overflow-auto">
            <img 
              v-for="img in product.hinhAnh" 
              :key="img.maHinhAnh" 
              :src="getImageUrl(img.duongDanAnh)" 
              class="img-thumbnail rounded-0 cursor-pointer object-fit-cover" 
              style="width: 80px; height: 80px; cursor: pointer"
              :class="{ 'border-dark border-2': mainImage === img.duongDanAnh }"
              @click="mainImage = img.duongDanAnh"
            >
          </div>
        </div>
      </div>

      <!-- Thông tin sản phẩm -->
      <div class="col-md-6">
        <div class="d-flex justify-content-between align-items-start">
          <h2 class="fw-bold">{{ product.tenSanPham }}</h2>
        </div>
        
        <div class="mb-3">
          <span class="badge bg-dark rounded-0 me-2">ID: {{ product.maSanPham }}</span>
          <span class="text-muted">{{ getCategoryName(product.maDanhMuc) }}</span>
        </div>
        
        <div class="mb-4">
          <h3 class="text-dark fw-bold d-inline-block me-3">{{ formatPrice(currentPrice) }}</h3>
          <span v-if="currentOriginalPrice > currentPrice" class="text-muted text-decoration-line-through fs-5">
            {{ formatPrice(currentOriginalPrice) }}
          </span>
        </div>

        <p class="mb-4 text-muted">{{ product.moTa || 'Chưa có mô tả cho sản phẩm này.' }}</p>

        <!-- Chọn Màu sắc -->
        <div class="mb-3">
          <label class="form-label fw-bold text-uppercase" style="letter-spacing: 1px; font-size: 0.85rem">Màu sắc:</label>
          <div class="d-flex flex-wrap gap-2">
            <button 
              v-for="color in availableColors" 
              :key="color"
              class="btn rounded-0"
              :class="selectedColor === color ? 'btn-dark' : 'btn-outline-dark'"
              @click="selectColor(color)"
            >
              {{ color }}
            </button>
          </div>
        </div>

        <!-- Chọn Kích cỡ -->
        <div class="mb-4">
          <label class="form-label fw-bold text-uppercase" style="letter-spacing: 1px; font-size: 0.85rem">Kích cỡ:</label>
          <div class="d-flex flex-wrap gap-2">
            <button 
              v-for="size in availableSizes" 
              :key="size"
              class="btn rounded-0"
              :class="selectedSize === size ? 'btn-dark' : 'btn-outline-dark'"
              @click="selectedSize = size"
              :disabled="!isSizeAvailable(size)"
            >
              {{ size }}
            </button>
          </div>
        </div>

        <!-- Trạng thái kho -->
        <div v-if="selectedVariant">
          <div class="alert alert-success bg-success-subtle text-success border-0 py-2 mb-4" v-if="selectedVariant.soLuongTon > 0">
            Còn <strong>{{ selectedVariant.soLuongTon }}</strong> sản phẩm trong kho
          </div>
          <div class="alert alert-danger bg-danger-subtle text-danger border-0 py-2 mb-4 fw-bold" v-else>
            <i class="bi bi-exclamation-triangle-fill me-1"></i> Sản phẩm tạm thời hết hàng
          </div>
        </div>
        <div class="alert alert-danger bg-danger-subtle text-danger border-0 py-2 mb-4 fw-bold" v-else-if="selectedColor && selectedSize">
          <i class="bi bi-exclamation-triangle-fill me-1"></i> Sản phẩm tạm thời hết hàng
        </div>

        <!-- Chọn số lượng & Thêm giỏ hàng -->
        <div class="d-flex gap-3 align-items-center mb-4">
          <div class="input-group" style="width: 130px;">
            <button class="btn btn-outline-dark rounded-0" @click="quantity > 1 && quantity--">-</button>
            <input type="number" class="form-control text-center border-dark" v-model="quantity" min="1" :max="selectedVariant?.soLuongTon || 1">
            <button class="btn btn-outline-dark rounded-0" @click="quantity < (selectedVariant?.soLuongTon || 99) && quantity++">+</button>
          </div>
          <button 
            class="btn btn-dark btn-lg flex-grow-1 fw-bold rounded-0"
            :disabled="!selectedVariant || selectedVariant.soLuongTon < 1 || loadingCart"
            @click="addToCart"
          >
            <i class="bi bi-cart-plus me-2" v-if="selectedVariant && selectedVariant.soLuongTon > 0"></i>
            {{ loadingCart ? 'ĐANG THÊM...' : (selectedVariant && selectedVariant.soLuongTon > 0 ? 'THÊM VÀO GIỎ' : 'TẠM HẾT HÀNG') }}
          </button>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center py-5">
      <h3>Sản phẩm không tồn tại</h3>
      <router-link to="/" class="btn btn-dark mt-3 rounded-0">Quay về trang chủ</router-link>
    </div>

    <!-- Review Section -->
    <div v-if="product" class="row mt-5 pt-4 border-top">
      <div class="col-12">
        <h4 class="fw-bold mb-4 text-uppercase">Đánh giá sản phẩm</h4>
      </div>
      
      <div class="col-md-5 mb-4">
        <div class="bg-light p-4">
          <h5 class="fw-bold mb-3">Viết đánh giá của bạn</h5>
          <div v-if="!user" class="alert alert-secondary border-0">
            Vui lòng <router-link to="/login" class="fw-bold text-dark">Đăng nhập</router-link> để viết đánh giá.
          </div>
          <form v-else @submit.prevent="submitReview">
            <div class="mb-3">
              <label class="form-label fw-bold">Điểm đánh giá</label>
              <div class="d-flex gap-2">
                <i v-for="star in 5" :key="star" 
                   class="bi fs-4 cursor-pointer" 
                   :class="star <= reviewForm.soSao ? 'bi-star-fill text-warning' : 'bi-star text-secondary'"
                   @click="reviewForm.soSao = star" style="cursor: pointer;"></i>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">Nội dung</label>
              <textarea v-model="reviewForm.noiDung" class="form-control border-dark rounded-0" rows="3" required placeholder="Chia sẻ cảm nhận của bạn về sản phẩm..."></textarea>
            </div>
            <button type="submit" class="btn btn-dark rounded-0 fw-bold px-4" :disabled="reviewLoading || reviewForm.soSao === 0">
              {{ reviewLoading ? 'Đang gửi...' : 'GỬI ĐÁNH GIÁ' }}
            </button>
          </form>
        </div>
      </div>
      
      <div class="col-md-7">
        <div v-if="reviews.length === 0" class="text-muted fst-italic py-4 text-center">
          Chưa có đánh giá nào cho sản phẩm này. Hãy là người đầu tiên!
        </div>
        <div v-else>
          <div v-for="review in reviews" :key="review.maDanhGia" class="mb-4 pb-3 border-bottom">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <div class="fw-bold"><i class="bi bi-person-circle me-2"></i> Khách hàng #{{ review.maNguoiDung }}</div>
              <div class="text-muted small">{{ new Date(review.ngayTao).toLocaleDateString('vi-VN') }}</div>
            </div>
            <div class="mb-2">
              <i v-for="s in 5" :key="s" class="bi" :class="s <= review.soSao ? 'bi-star-fill text-warning' : 'bi-star text-secondary'"></i>
            </div>
            <p class="mb-0">{{ review.noiDung }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { getStoredUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
import { API_URL } from '@/config.js'

const product = ref(null)
const variants = ref([])
const categories = ref([])
const loading = ref(false)
const loadingCart = ref(false)
const mainImage = ref('')
const user = ref(null)

const selectedColor = ref('')
const selectedSize = ref('')
const quantity = ref(1)

const isWishlisted = ref(false)
const wishlistId = ref(null)

const reviews = ref([])
const reviewLoading = ref(false)
const reviewForm = ref({
  soSao: 5,
  noiDung: ''
})

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const getCategoryName = (id) => {
  const cat = categories.value.find(c => c.maDanhMuc === id)
  return cat ? cat.tenDanhMuc : `Danh mục ID: ${id}`
}

const getImageUrl = (path) => {
  if (!path) return 'https://via.placeholder.com/600x600?text=No+Image';
  if (path.startsWith('http') || path.startsWith('/')) return path;
  return '/' + path;
}

const availableColors = computed(() => {
  const colors = new Set()
  variants.value.forEach(v => colors.add(v.mauSac))
  return Array.from(colors)
})

const availableSizes = computed(() => {
  const sizes = new Set()
  variants.value.forEach(v => {
    if (v.mauSac === selectedColor.value) {
      sizes.add(v.kichCo)
    }
  })
  return Array.from(sizes)
})

const isSizeAvailable = (size) => {
  const variant = variants.value.find(v => v.mauSac === selectedColor.value && v.kichCo === size)
  return variant && variant.soLuongTon > 0
}

const selectedVariant = computed(() => {
  if (!selectedColor.value || !selectedSize.value) return null
  return variants.value.find(v => v.mauSac === selectedColor.value && v.kichCo === selectedSize.value)
})

const selectColor = (color) => {
  selectedColor.value = color
  const firstAvailable = variants.value.find(v => v.mauSac === color && v.soLuongTon > 0)
  if (firstAvailable) {
    selectedSize.value = firstAvailable.kichCo
  } else {
    selectedSize.value = ''
  }
  quantity.value = 1
}

const currentPrice = computed(() => {
  if (!product.value) return 0;
  let basePrice = product.value.giaKhuyenMai || product.value.GiKhuyenMai || product.value.giaGoc || product.value.GiGoc || 0;
  if (selectedVariant.value && selectedVariant.value.giaCongThem) {
    basePrice += selectedVariant.value.giaCongThem;
  }
  return basePrice;
})

const currentOriginalPrice = computed(() => {
  if (!product.value) return 0;
  return product.value.giaGoc || product.value.GiGoc || 0;
})

const fetchProductData = async () => {
  loading.value = true
  try {
    const productId = route.params.id
    const [prodRes, varRes, catRes, reviewRes, imgRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham/${productId}`),
      axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${productId}`),
      axios.get(`${API_URL}/api/danh-muc`),
      axios.get(`${API_URL}/api/danh-gia/san-pham/${productId}`).catch(() => ({data: {data: []}})),
      axios.get(`${API_URL}/api/hinh-anh/san-pham/${productId}`).catch(() => ({data: {data: []}}))
    ])
    
    product.value = prodRes.data.data || prodRes.data
    product.value.hinhAnh = imgRes.data.data || imgRes.data || []
    categories.value = catRes.data.data || catRes.data || []
    reviews.value = reviewRes.data.data || reviewRes.data || []
    
    const allVariants = varRes.data.data || varRes.data || []
    variants.value = allVariants

    if (product.value.hinhAnh && product.value.hinhAnh.length > 0) {
      const main = product.value.hinhAnh.find(img => img.laAnhChinh)
      mainImage.value = main ? main.duongDanAnh : product.value.hinhAnh[0].duongDanAnh
    } else {
      mainImage.value = 'https://via.placeholder.com/600x600?text=No+Image'
    }

    if (variants.value.length > 0) {
      const firstAvailable = variants.value.find(v => v.soLuongTon > 0) || variants.value[0]
      selectedColor.value = firstAvailable.mauSac
      selectedSize.value = firstAvailable.kichCo
    }

    checkWishlist()
  } catch (error) {
    console.error('Lỗi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const checkWishlist = async () => {
  if (!user.value || !product.value) return
  try {
    const res = await axios.get(`${API_URL}/api/yeu-thich/nguoi-dung/${user.value.maNguoiDung}`)
    const list = res.data.data || res.data || []
    const wl = list.find(w => w.sanPham?.maSanPham === product.value.maSanPham)
    if (wl) {
      isWishlisted.value = true
      wishlistId.value = wl.maYeuThich
    } else {
      isWishlisted.value = false
    }
  } catch (e) {
    console.error(e)
  }
}

const toggleWishlist = async () => {
  if (!user.value) {
    router.push('/login')
    return
  }
  try {
    await axios.post(`${API_URL}/api/yeu-thich/toggle?maNguoiDung=${user.value.maNguoiDung}&maSanPham=${product.value.maSanPham}`)
    await checkWishlist()
  } catch (err) {
    console.error('Lỗi toggle wishlist:', err)
  }
}

const addToCart = async () => {
  if (user.value && !user.value.maNguoiDung) {
    alert('Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại!')
    localStorage.removeItem('user')
    router.push('/login')
    return
  }

  if (!selectedVariant.value) {
    alert('Vui lòng chọn Màu sắc và Kích cỡ')
    return
  }

  if (quantity.value > selectedVariant.value.soLuongTon) {
    alert('Số lượng yêu cầu vượt quá tồn kho hiện tại!')
    return
  }

  if (!user.value) {
    // KHACH VANG LAI (Guest) -> Luu vao localStorage
    const cart = JSON.parse(localStorage.getItem('guestCart') || '[]')
    const existingItem = cart.find(item => item.maChiTietSp === selectedVariant.value.maChiTietSp)
    
    if (existingItem) {
      if (existingItem.soLuong + quantity.value > selectedVariant.value.soLuongTon) {
        alert('Số lượng vượt quá tồn kho!')
        return
      }
      existingItem.soLuong += quantity.value
    } else {
      cart.push({
        maChiTietSp: selectedVariant.value.maChiTietSp,
        soLuong: quantity.value,
        donGia: currentPrice.value,
        // Save some display info for the cart page so we don't have to fetch everything
        sanPham: product.value,
        chiTietSanPham: selectedVariant.value
      })
    }
    
    localStorage.setItem('guestCart', JSON.stringify(cart))
    alert('Thêm vào giỏ hàng thành công!')
    router.push('/cart')
    return
  }

  loadingCart.value = true
  try {
    const payload = {
      maChiTietSp: selectedVariant.value.maChiTietSp,
      soLuong: quantity.value
    }

    await axios.post(`${API_URL}/api/gio-hang/them/${user.value.maNguoiDung}`, payload)
    alert('Thêm vào giỏ hàng thành công!')
    router.push('/cart')
  } catch (error) {
    console.error('Lỗi thêm giỏ hàng:', error)
    alert('Lỗi: ' + (error.response?.data?.message || 'Không thể thêm vào giỏ hàng'))
  } finally {
    loadingCart.value = false
  }
}

const submitReview = async () => {
  if (!user.value) return
  reviewLoading.value = true
  try {
    const payload = {
      maNguoiDung: user.value.maNguoiDung,
      maSanPham: product.value.maSanPham,
      soSao: reviewForm.value.soSao,
      noiDung: reviewForm.value.noiDung
    }
    await axios.post(`${API_URL}/api/danh-gia`, payload)
    
    // Refresh reviews
    const reviewRes = await axios.get(`${API_URL}/api/danh-gia/san-pham/${product.value.maSanPham}`)
    reviews.value = reviewRes.data.data || reviewRes.data || []
    
    // Reset form
    reviewForm.value.soSao = 5
    reviewForm.value.noiDung = ''
    alert('Cảm ơn bạn đã đánh giá sản phẩm!')
  } catch (err) {
    console.error('Lỗi gửi đánh giá', err)
    alert('Có lỗi xảy ra khi gửi đánh giá')
  } finally {
    reviewLoading.value = false
  }
}

onMounted(() => {
  user.value = getStoredUser()
  fetchProductData()
})
</script>
