<template>
  <div class="container mt-4 mb-5 product-detail-page">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="product">
      <nav class="detail-breadcrumb" aria-label="breadcrumb">
        <router-link to="/">Trang chủ</router-link>
        <i class="bi bi-chevron-right"></i>
        <span>{{ getCategoryName(product.maDanhMuc) }}</span>
        <i class="bi bi-chevron-right"></i>
        <strong>{{ product.tenSanPham }}</strong>
      </nav>

      <div class="row g-4 align-items-start">
        <div class="col-lg-6">
          <div class="gallery-card">
            <div class="main-image-wrap">
              <img :src="getImageUrl(mainImage)" :key="mainImage" class="main-image" :alt="product.tenSanPham">
              <span v-if="currentOriginalPrice > currentPrice" class="sale-pill">
                Giảm {{ Math.round((1 - currentPrice / currentOriginalPrice) * 100) }}%
              </span>
            </div>
            <div class="thumb-row" v-if="product.hinhAnh?.length">
              <button
                v-for="img in product.hinhAnh"
                :key="img.maHinhAnh"
                type="button"
                class="thumb-btn"
                :class="{ active: mainImage === img.duongDanAnh }"
                @click="mainImage = img.duongDanAnh"
              >
                <img :src="getImageUrl(img.duongDanAnh)" class="thumb-image" alt="">
              </button>
            </div>
          </div>
        </div>

        <div class="col-lg-6">
          <div class="buy-panel">
            <div class="page-kicker">Chi tiết sản phẩm</div>
            <h1 class="detail-title">{{ product.tenSanPham }}</h1>

            <div class="meta-row">
              <span class="meta-chip">{{ getCategoryName(product.maDanhMuc) }}</span>
              <span class="meta-code">SKU #{{ product.maSanPham }}</span>
            </div>

            <div class="price-row">
              <span class="price-now">{{ formatPrice(currentPrice) }}</span>
              <span v-if="currentOriginalPrice > currentPrice" class="price-old">
                {{ formatPrice(currentOriginalPrice) }}
              </span>
            </div>

            <p class="product-desc">{{ product.moTa || 'Chưa có mô tả cho sản phẩm này.' }}</p>

            <div class="option-block">
              <div class="option-head">
                <span>Màu sắc</span>
                <strong v-if="selectedColor">{{ selectedColor }}</strong>
              </div>
              <div class="option-list">
                <button
                  v-for="color in availableColors"
                  :key="color"
                  type="button"
                  class="option-chip"
                  :class="{ active: selectedColor === color }"
                  @click="selectColor(color)"
                >
                  {{ color }}
                </button>
              </div>
            </div>

            <div class="option-block">
              <div class="option-head">
                <span>Kích cỡ</span>
                <strong v-if="selectedSize">{{ selectedSize }}</strong>
              </div>
              <div class="option-list">
                <button
                  v-for="size in availableSizes"
                  :key="size"
                  type="button"
                  class="option-chip"
                  :class="{ active: selectedSize === size, disabled: !isSizeAvailable(size) }"
                  :disabled="!isSizeAvailable(size)"
                  @click="selectedSize = size"
                >
                  {{ size }}
                </button>
              </div>
            </div>

            <div
              v-if="selectedVariant"
              class="stock-chip"
              :class="selectedVariant.soLuongTon > 0 ? 'in-stock' : 'out-stock'"
            >
              <i :class="selectedVariant.soLuongTon > 0 ? 'bi bi-check-circle-fill' : 'bi bi-x-circle-fill'"></i>
              <span v-if="selectedVariant.soLuongTon > 0">
                Còn <strong>{{ selectedVariant.soLuongTon }}</strong> sản phẩm
              </span>
              <span v-else>Tạm hết hàng</span>
            </div>
            <div
              v-else-if="selectedColor && selectedSize"
              class="stock-chip out-stock"
            >
              <i class="bi bi-x-circle-fill"></i>
              <span>Tạm hết hàng</span>
            </div>

            <div class="cart-row">
              <div class="qty-box">
                <button type="button" @click="quantity > 1 && quantity--">−</button>
                <input type="number" v-model="quantity" min="1" :max="selectedVariant?.soLuongTon || 1">
                <button type="button" @click="quantity < (selectedVariant?.soLuongTon || 99) && quantity++">+</button>
              </div>
              <button
                type="button"
                class="btn btn-primary btn-lg cart-btn"
                :disabled="!selectedVariant || selectedVariant.soLuongTon < 1 || loadingCart"
                @click="addToCart"
              >
                <i class="bi bi-bag-plus me-2" v-if="selectedVariant && selectedVariant.soLuongTon > 0"></i>
                {{ loadingCart ? 'Đang thêm...' : (selectedVariant && selectedVariant.soLuongTon > 0 ? 'Thêm vào giỏ' : 'Tạm hết hàng') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="row mt-5">
        <div class="col-12 mb-3">
          <div class="section-head">
            <div>
              <p class="page-kicker mb-1">Feedback</p>
              <h2>Đánh giá sản phẩm</h2>
            </div>
          </div>
        </div>

        <div class="col-md-5 mb-4">
          <div class="sp-soft-panel h-100">
            <h5 class="fw-bold mb-3">Viết đánh giá của bạn</h5>
            <div v-if="!user" class="alert alert-secondary border-0">
              Vui lòng <router-link to="/login" class="fw-bold">Đăng nhập</router-link> để viết đánh giá.
            </div>
            <form v-else @submit.prevent="submitReview">
              <div class="mb-3">
                <label class="form-label">Điểm đánh giá</label>
                <div class="d-flex gap-2">
                  <i
                    v-for="star in 5"
                    :key="star"
                    class="bi fs-4"
                    :class="star <= reviewForm.soSao ? 'bi-star-fill text-warning' : 'bi-star text-secondary'"
                    style="cursor: pointer;"
                    @click="reviewForm.soSao = star"
                  ></i>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">Nội dung</label>
                <textarea
                  v-model="reviewForm.noiDung"
                  class="form-control"
                  rows="3"
                  required
                  placeholder="Chia sẻ cảm nhận của bạn về sản phẩm..."
                ></textarea>
              </div>
              <button type="submit" class="btn btn-primary fw-bold px-4" :disabled="reviewLoading || reviewForm.soSao === 0">
                {{ reviewLoading ? 'Đang gửi...' : 'Gửi đánh giá' }}
              </button>
            </form>
          </div>
        </div>

        <div class="col-md-7">
          <div class="sp-soft-panel h-100">
            <div v-if="reviews.length === 0" class="text-muted py-4 text-center">
              Chưa có đánh giá nào cho sản phẩm này. Hãy là người đầu tiên!
            </div>
            <div v-else>
              <div v-for="review in reviews" :key="review.maDanhGia" class="review-item">
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
    </div>

    <div v-else class="text-center py-5 sp-soft-panel">
      <h3>Sản phẩm không tồn tại</h3>
      <router-link to="/" class="btn btn-primary mt-3">Quay về trang chủ</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { getStoredUser } from '../utils/auth'
import { useToast } from '@/composables/useToast.js'
import { API_URL } from '@/config.js'
import { resolveImageUrl } from '@/utils/image.js'

const route = useRoute()
const router = useRouter()
const toast = useToast()

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

const getImageUrl = (path) => resolveImageUrl(path)

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
    // Ưu tiên dữ liệu mua hàng trước; danh mục + đánh giá tải sau
    const [prodRes, varRes, imgRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham/${productId}`),
      axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${productId}`),
      axios.get(`${API_URL}/api/hinh-anh/san-pham/${productId}`).catch(() => ({ data: { data: [] } }))
    ])

    product.value = prodRes.data.data || prodRes.data
    product.value.hinhAnh = imgRes.data.data || imgRes.data || []

    const allVariants = varRes.data.data || varRes.data || []
    variants.value = allVariants

    if (product.value.hinhAnh && product.value.hinhAnh.length > 0) {
      const main = product.value.hinhAnh.find(img => img.laAnhChinh)
      mainImage.value = main ? main.duongDanAnh : product.value.hinhAnh[0].duongDanAnh
    } else {
      mainImage.value = ''
    }

    if (variants.value.length > 0) {
      const firstAvailable = variants.value.find(v => v.soLuongTon > 0) || variants.value[0]
      selectedColor.value = firstAvailable.mauSac
      selectedSize.value = firstAvailable.kichCo
    }

    loading.value = false

    Promise.all([
      axios.get(`${API_URL}/api/danh-muc`),
      axios.get(`${API_URL}/api/danh-gia/san-pham/${productId}`).catch(() => ({ data: { data: [] } }))
    ]).then(([catRes, reviewRes]) => {
      categories.value = catRes.data.data || catRes.data || []
      reviews.value = reviewRes.data.data || reviewRes.data || []
    }).catch(() => {})

    checkWishlist()
  } catch (error) {
    console.error('Lỗi tải sản phẩm:', error)
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
    toast.error('Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.')
    localStorage.removeItem('user')
    router.push('/login')
    return
  }

  if (!selectedVariant.value) {
    toast.warning('Vui lòng chọn màu sắc và kích cỡ trước khi thêm vào giỏ.')
    return
  }

  if (quantity.value > selectedVariant.value.soLuongTon) {
    toast.warning(`Số lượng vượt quá tồn kho. Hiện còn ${selectedVariant.value.soLuongTon} sản phẩm.`)
    return
  }

  const notifyAdded = () => {
    toast.success('Sản phẩm đã được thêm vào giỏ hàng của bạn.', {
      title: 'Thêm vào giỏ thành công',
      duration: 4200,
      actionLabel: 'Xem giỏ hàng',
      onAction: () => router.push('/cart')
    })
  }

  if (!user.value) {
    const cart = JSON.parse(localStorage.getItem('guestCart') || '[]')
    const existingItem = cart.find(item => item.maChiTietSp === selectedVariant.value.maChiTietSp)

    if (existingItem) {
      if (existingItem.soLuong + quantity.value > selectedVariant.value.soLuongTon) {
        toast.warning(`Số lượng vượt quá tồn kho. Hiện còn ${selectedVariant.value.soLuongTon} sản phẩm.`)
        return
      }
      existingItem.soLuong += quantity.value
    } else {
      cart.push({
        maChiTietSp: selectedVariant.value.maChiTietSp,
        soLuong: quantity.value,
        donGia: currentPrice.value,
        sanPham: product.value,
        chiTietSanPham: selectedVariant.value
      })
    }

    localStorage.setItem('guestCart', JSON.stringify(cart))
    notifyAdded()
    return
  }

  loadingCart.value = true
  try {
    const payload = {
      maChiTietSp: selectedVariant.value.maChiTietSp,
      soLuong: quantity.value
    }

    await axios.post(`${API_URL}/api/gio-hang/them/${user.value.maNguoiDung}`, payload)
    notifyAdded()
  } catch (error) {
    console.error('Lỗi thêm giỏ hàng:', error)
    toast.error(error.response?.data?.message || 'Không thể thêm vào giỏ hàng. Vui lòng thử lại.')
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

<style scoped>
.detail-breadcrumb {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.45rem;
  margin-bottom: 1.25rem;
  font-size: 0.88rem;
  color: #94a3b8;
}

.detail-breadcrumb a {
  color: #64748b;
  font-weight: 600;
}

.detail-breadcrumb a:hover {
  color: #1d4ed8;
}

.detail-breadcrumb i {
  font-size: 0.7rem;
}

.detail-breadcrumb strong {
  color: #0f172a;
  font-weight: 700;
  max-width: 280px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.gallery-card {
  background: #fff;
  border: 1px solid var(--sp-border, #e8ecf3);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
  animation: fadeUp 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.main-image-wrap {
  position: relative;
  background: linear-gradient(180deg, #f8fafc, #eef2f7);
  aspect-ratio: 1 / 1;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  animation: imgFade 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.sale-pill {
  position: absolute;
  top: 14px;
  left: 14px;
  background: linear-gradient(135deg, #f97316, #fb923c);
  color: #fff;
  font-size: 0.78rem;
  font-weight: 750;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  box-shadow: 0 8px 18px rgba(249, 115, 22, 0.28);
}

.thumb-row {
  display: flex;
  gap: 0.65rem;
  padding: 0.9rem;
  overflow-x: auto;
}

.thumb-btn {
  border: none;
  padding: 0;
  background: transparent;
  border-radius: 12px;
  flex-shrink: 0;
}

.thumb-image {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 12px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1), border-color 0.2s ease, box-shadow 0.2s ease;
  display: block;
}

.thumb-btn:hover .thumb-image {
  transform: translateY(-2px);
}

.thumb-btn.active .thumb-image,
.thumb-btn:hover .thumb-image {
  border-color: #1d4ed8;
  box-shadow: 0 0 0 3px rgba(29, 78, 216, 0.12);
}

.buy-panel {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--sp-border, #e8ecf3);
  border-radius: 20px;
  padding: 1.5rem 1.55rem 1.4rem;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
  animation: fadeUp 0.55s cubic-bezier(0.22, 1, 0.36, 1) 0.08s both;
}

.detail-title {
  margin: 0 0 0.85rem;
  font-size: clamp(1.45rem, 2.4vw, 1.9rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0b1220;
  line-height: 1.25;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.55rem;
  margin-bottom: 1rem;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  background: rgba(29, 78, 216, 0.08);
  color: #1d4ed8;
  font-size: 0.8rem;
  font-weight: 700;
}

.meta-code {
  color: #94a3b8;
  font-size: 0.84rem;
  font-weight: 600;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-bottom: 0.95rem;
}

.price-now {
  font-size: 1.85rem;
  font-weight: 800;
  color: #ea580c;
  letter-spacing: -0.02em;
}

.price-old {
  font-size: 1.05rem;
  color: #94a3b8;
  text-decoration: line-through;
}

.product-desc {
  color: #64748b;
  margin-bottom: 1.35rem;
  line-height: 1.7;
}

.option-block {
  margin-bottom: 1.15rem;
}

.option-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.55rem;
  font-size: 0.88rem;
  font-weight: 700;
  color: #475569;
}

.option-head strong {
  color: #0f172a;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.option-chip {
  min-width: 52px;
  min-height: 42px;
  padding: 0.45rem 0.9rem;
  border-radius: 12px;
  border: 1.5px solid #dbe3ef;
  background: #fff;
  color: #334155;
  font-weight: 650;
  font-size: 0.9rem;
  transition: all 0.2s cubic-bezier(0.22, 1, 0.36, 1);
}

.option-chip:hover:not(:disabled) {
  border-color: rgba(29, 78, 216, 0.45);
  color: #1d4ed8;
  transform: translateY(-1px);
}

.option-chip.active {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  border-color: transparent;
  color: #fff;
  box-shadow: 0 8px 18px rgba(29, 78, 216, 0.25);
}

.option-chip:disabled,
.option-chip.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  text-decoration: line-through;
}

.stock-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.45rem 0.8rem;
  border-radius: 999px;
  font-size: 0.86rem;
  font-weight: 650;
  margin-bottom: 1.15rem;
}

.stock-chip.in-stock {
  background: #ecfdf5;
  color: #059669;
}

.stock-chip.out-stock {
  background: #fef2f2;
  color: #dc2626;
}

.cart-row {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.qty-box {
  display: inline-flex;
  align-items: center;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  background: #f8fafc;
}

.qty-box button {
  width: 40px;
  height: 46px;
  border: none;
  background: transparent;
  color: #475569;
  font-size: 1.15rem;
  font-weight: 700;
  transition: background 0.15s ease, color 0.15s ease;
}

.qty-box button:hover {
  background: #eff6ff;
  color: #1d4ed8;
}

.qty-box input {
  width: 48px;
  height: 46px;
  border: none;
  border-left: 1px solid #e2e8f0;
  border-right: 1px solid #e2e8f0;
  text-align: center;
  font-weight: 750;
  color: #0f172a;
  background: #fff;
  outline: none;
}

.cart-btn {
  flex: 1;
  min-height: 46px;
  border-radius: 12px !important;
}

.review-item {
  padding-bottom: 1rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid #eef2f7;
}

.review-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

@keyframes imgFade {
  from { opacity: 0.5; transform: scale(1.025); }
  to { opacity: 1; transform: scale(1); }
}

@media (max-width: 576px) {
  .cart-row {
    flex-direction: column;
    align-items: stretch;
  }

  .qty-box {
    width: 100%;
    justify-content: space-between;
  }

  .qty-box input {
    flex: 1;
  }
}
</style>
