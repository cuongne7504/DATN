<template>
  <div class="home-page">
    <section class="sportpro-banner" ref="bannerRef">
      <div class="banner-bg" :style="bannerParallax"></div>
      <div class="banner-overlay">
        <div class="hero-content hero-stagger">
          <p class="hero-kicker">SportPro Store</p>
          <h1 class="hero-title">SportPro</h1>
          <p class="hero-sub">Nâng tầm phong cách, bứt phá giới hạn với trang phục thể thao chính hãng.</p>
          <div class="hero-actions">
            <button class="btn btn-primary btn-lg px-4 fw-bold hero-cta" @click="scrollToProducts">
              Khám phá ngay <i class="bi bi-arrow-down-short ms-1"></i>
            </button>
            <button class="btn btn-outline-light btn-lg px-4 fw-semibold" @click="scrollToProducts">
              Xem bộ sưu tập
            </button>
          </div>
        </div>
      </div>
      <div class="scroll-hint" @click="scrollToProducts">
        <span></span>
      </div>
    </section>

    <div class="container">
      <div class="trust-strip">
        <div
          v-for="(item, i) in trustItems"
          :key="item.title"
          class="trust-item"
          v-reveal="{ delay: i * 90 }"
        >
          <div class="trust-icon"><i :class="item.icon"></i></div>
          <div>
            <strong>{{ item.title }}</strong>
            <span>{{ item.desc }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="container mt-5 mb-5" id="products-section">
      <div class="section-head" v-reveal>
        <div>
          <p class="page-kicker mb-1">Sản phẩm</p>
          <h2>Bộ sưu tập nổi bật</h2>
          <p>Khám phá giày và trang phục thể thao đang được yêu thích.</p>
        </div>
        <div class="result-count" v-if="!loading">{{ products.length }} sản phẩm</div>
      </div>

      <div class="sp-soft-panel mb-4 filter-panel" v-reveal="{ delay: 80 }">
        <div class="d-flex align-items-center gap-2 mb-3">
          <span class="filter-icon"><i class="bi bi-sliders"></i></span>
          <h5 class="mb-0 fw-bold">Bộ lọc tìm kiếm</h5>
        </div>
        <div class="row g-3">
          <div class="col-md-4">
            <label class="filter-label">Từ khóa</label>
            <div class="filter-field">
              <i class="bi bi-search"></i>
              <input
                type="text"
                v-model="searchForm.ten"
                @input="onSearchInput"
                class="form-control filter-input"
                placeholder="Tìm theo tên sản phẩm..."
              />
            </div>
          </div>
          <div class="col-md-4">
            <label class="filter-label">Danh mục</label>
            <SpSelect
              v-model="searchForm.maDanhMuc"
              :options="categoryOptions"
              placeholder="Tất cả danh mục"
              @change="applyFilters"
            />
          </div>
          <div class="col-md-4">
            <label class="filter-label">Thương hiệu</label>
            <SpSelect
              v-model="searchForm.maThuongHieu"
              :options="brandOptions"
              placeholder="Tất cả thương hiệu"
              @change="applyFilters"
            />
          </div>
        </div>
      </div>

      <div v-if="loading" class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
        <div class="col" v-for="n in 8" :key="n">
          <div class="skeleton-card">
            <div class="skeleton skeleton-media"></div>
            <div class="skeleton skeleton-line"></div>
            <div class="skeleton skeleton-line short"></div>
            <div class="skeleton skeleton-line btn-like"></div>
          </div>
        </div>
      </div>

      <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4 product-grid" v-else>
        <div class="col" v-for="(product, index) in products" :key="product.maSanPham">
          <div
            class="card h-100 product-card"
            v-reveal="index < 8 ? { delay: index * 40 } : false"
          >
            <router-link :to="'/product/' + product.maSanPham" class="product-link">
              <div class="product-media">
                <img
                  :src="getMainImage(product)"
                  class="card-img-top"
                  :alt="product.tenSanPham"
                  loading="lazy"
                  decoding="async"
                  width="400"
                  height="400"
                />
                <span class="quick-view">Xem nhanh</span>
              </div>
            </router-link>
            <div class="card-body d-flex flex-column">
              <h6 class="card-title product-name">{{ product.tenSanPham }}</h6>
              <div class="mt-auto">
                <div class="price-block mb-3">
                  <span class="price-now">{{ formatPrice(product.giaGoc || product.GiGoc) }}</span>
                </div>
                <router-link :to="'/product/' + product.maSanPham" class="btn btn-outline-primary w-100 fw-bold">
                  Xem chi tiết
                </router-link>
              </div>
            </div>
          </div>
        </div>
        <div v-if="products.length === 0" class="col-12">
          <div class="sp-soft-panel text-center py-5 empty-state">
            <i class="bi bi-search text-muted" style="font-size: 2.5rem;"></i>
            <h5 class="mt-3 mb-1">Không tìm thấy sản phẩm</h5>
            <p class="text-muted mb-0">Thử đổi từ khóa hoặc bộ lọc khác.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { API_URL } from '@/config.js'
import SpSelect from '@/components/SpSelect.vue'
import { resolveImageUrl } from '@/utils/image.js'

const products = ref([])
const allProductsCache = ref([])
const categories = ref([])
const brands = ref([])
const loading = ref(false)
const catalogLoaded = ref(false)
const bannerRef = ref(null)
const bannerOffset = ref(0)
let searchTimer = null
let scrollRaf = 0

const categoryOptions = computed(() => [
  { value: '', label: 'Tất cả danh mục' },
  ...categories.value.map((cat) => ({
    value: cat.maDanhMuc,
    label: cat.tenDanhMuc
  }))
])

const brandOptions = computed(() => [
  { value: '', label: 'Tất cả thương hiệu' },
  ...brands.value.map((brand) => ({
    value: brand.maThuongHieu,
    label: brand.tenThuongHieu
  }))
])

const trustItems = [
  { icon: 'bi bi-shield-check', title: 'Hàng chính hãng', desc: 'Cam kết nguồn gốc rõ ràng' },
  { icon: 'bi bi-truck', title: 'Giao hàng nhanh', desc: 'Theo dõi đơn dễ dàng' },
  { icon: 'bi bi-arrow-repeat', title: 'Đổi trả linh hoạt', desc: 'Hỗ trợ sau mua tận tâm' }
]

const bannerParallax = ref({
  transform: 'translateY(0) scale(1.05)'
})

const searchForm = ref({
  ten: '',
  maDanhMuc: '',
  maThuongHieu: ''
})

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const getDiscountPercent = (product) => {
  const original = Number(product.giaGoc || product.GiGoc || 0)
  const sale = Number(product.giaKhuyenMai || product.GiKhuyenMai || 0)
  if (!original || !sale || sale >= original) return 0
  return Math.round(((original - sale) / original) * 100)
}

const getMainImage = (product) => {
  if (product.hinhAnh && product.hinhAnh.length > 0) {
    const mainImg = product.hinhAnh.find(img => img.laAnhChinh)
    const path = mainImg ? mainImg.duongDanAnh : product.hinhAnh[0].duongDanAnh
    return resolveImageUrl(path)
  }
  return resolveImageUrl(null)
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

const fetchProducts = async (force = false) => {
  if (catalogLoaded.value && !force) {
    applyFilters()
    return
  }

  loading.value = true
  try {
    const [res, imgRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham`),
      axios.get(`${API_URL}/api/hinh-anh`).catch(() => ({ data: { data: [] } }))
    ])
    let allProducts = res.data.data || res.data || []
    const allImages = imgRes.data.data || imgRes.data || []

    const imagesByProduct = new Map()
    for (const img of allImages) {
      const key = img.maSanPham
      if (!imagesByProduct.has(key)) imagesByProduct.set(key, [])
      imagesByProduct.get(key).push(img)
    }

    allProducts.forEach((p) => {
      p.hinhAnh = imagesByProduct.get(p.maSanPham) || []
    })

    allProductsCache.value = allProducts
    catalogLoaded.value = true
    applyFilters()
  } catch (error) {
    console.error('Lỗi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  let list = allProductsCache.value
  const { ten, maDanhMuc, maThuongHieu } = searchForm.value

  if (ten) {
    const q = ten.toLowerCase().trim()
    list = list.filter((p) => p.tenSanPham?.toLowerCase().includes(q))
  }
  if (maDanhMuc) {
    list = list.filter((p) => p.maDanhMuc === maDanhMuc)
  }
  if (maThuongHieu) {
    list = list.filter((p) => p.maThuongHieu === maThuongHieu)
  }

  products.value = list
}

const onSearchInput = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(applyFilters, 200)
}

const scrollToProducts = () => {
  document.getElementById('products-section')?.scrollIntoView({ behavior: 'smooth' })
}

const onBannerScroll = () => {
  if (scrollRaf) return
  scrollRaf = requestAnimationFrame(() => {
    scrollRaf = 0
    const y = Math.min(window.scrollY, 420)
    bannerOffset.value = y
    bannerParallax.value = {
      transform: `translateY(${y * 0.28}px) scale(${1.05 + y * 0.00008})`
    }
  })
}

onMounted(() => {
  fetchFilters()
  fetchProducts()
  window.addEventListener('scroll', onBannerScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', onBannerScroll)
  clearTimeout(searchTimer)
  if (scrollRaf) cancelAnimationFrame(scrollRaf)
})
</script>

<style scoped>
.sportpro-banner {
  position: relative;
  height: min(640px, 82vh);
  overflow: hidden;
  isolation: isolate;
}

.banner-bg {
  position: absolute;
  inset: -8% 0;
  background-image: image-set(
    url('/banner.webp') type('image/webp'),
    url('/banner.jpg') type('image/jpeg')
  );
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  will-change: transform;
  transition: transform 0.08s linear;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 2rem 1.25rem;
  background:
    linear-gradient(120deg, rgba(11, 18, 32, 0.8) 0%, rgba(29, 78, 216, 0.45) 55%, rgba(15, 23, 42, 0.58) 100%);
}

.hero-content {
  max-width: 700px;
  position: relative;
  z-index: 1;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  margin: 0 0 0.85rem;
  padding: 0.35rem 0.85rem;
  border-radius: 999px;
  background: rgba(249, 115, 22, 0.2);
  border: 1px solid rgba(253, 186, 116, 0.4);
  color: #fdba74;
  font-size: 0.78rem;
  font-weight: 750;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  backdrop-filter: blur(8px);
}

.hero-title {
  margin: 0 0 0.85rem;
  color: #fff;
  font-size: clamp(2.7rem, 6.2vw, 4.4rem);
  font-weight: 800;
  letter-spacing: -0.045em;
  line-height: 1.02;
  text-shadow: 0 14px 44px rgba(0, 0, 0, 0.4);
}

.hero-sub {
  margin: 0 auto 1.6rem;
  max-width: 540px;
  color: #e2e8f0;
  font-size: clamp(1rem, 2vw, 1.18rem);
  line-height: 1.65;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.75rem;
}

.hero-cta {
  box-shadow: 0 10px 22px rgba(29, 78, 216, 0.28);
}

.btn-outline-light {
  border-width: 1.5px !important;
  color: #fff !important;
  background: rgba(255, 255, 255, 0.08) !important;
  backdrop-filter: blur(8px);
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1), background 0.25s ease !important;
}

.btn-outline-light:hover {
  background: rgba(255, 255, 255, 0.18) !important;
  color: #fff !important;
  transform: translateY(-2px);
}

.scroll-hint {
  position: absolute;
  left: 50%;
  bottom: 1.1rem;
  transform: translateX(-50%);
  width: 26px;
  height: 40px;
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-radius: 14px;
  cursor: pointer;
  z-index: 2;
}

.scroll-hint span {
  display: block;
  width: 4px;
  height: 8px;
  margin: 8px auto 0;
  border-radius: 4px;
  background: #fff;
  animation: softFloat 1.6s ease-in-out infinite;
}

.filter-panel {
  position: relative;
  z-index: 30;
  margin-top: 0.25rem;
  overflow: visible;
}

.filter-panel .row,
.filter-panel [class*="col-"] {
  overflow: visible;
}

.product-grid {
  position: relative;
  z-index: 1;
}

.filter-label {
  display: block;
  margin-bottom: 0.4rem;
  font-size: 0.8rem;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 0.02em;
}

.filter-field {
  position: relative;
}

.filter-field > i {
  position: absolute;
  left: 0.95rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  z-index: 1;
  pointer-events: none;
}

.filter-input {
  padding-left: 2.4rem !important;
  min-height: 48px;
}

.filter-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  background: rgba(29, 78, 216, 0.1);
  color: var(--sp-blue);
}

.result-count {
  font-size: 0.88rem;
  font-weight: 650;
  color: #64748b;
  background: #fff;
  border: 1px solid var(--sp-border, #e8ecf3);
  border-radius: 999px;
  padding: 0.4rem 0.85rem;
  white-space: nowrap;
}

.product-link {
  color: inherit;
  text-decoration: none;
}

.product-media {
  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, #f8fafc, #eef2f7);
  aspect-ratio: 1 / 1;
}

.product-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.55s cubic-bezier(0.22, 1, 0.36, 1);
}

.quick-view {
  position: absolute;
  left: 50%;
  bottom: 14px;
  transform: translate(-50%, 12px);
  opacity: 0;
  background: rgba(15, 23, 42, 0.82);
  color: #fff;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  padding: 0.4rem 0.85rem;
  border-radius: 999px;
  backdrop-filter: blur(8px);
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  pointer-events: none;
}

.product-card:hover .quick-view {
  opacity: 1;
  transform: translate(-50%, 0);
}

.sale-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #f97316, #fb923c);
  color: #fff;
  font-size: 0.75rem;
  font-weight: 750;
  padding: 0.3rem 0.6rem;
  border-radius: 999px;
  box-shadow: 0 8px 18px rgba(249, 115, 22, 0.3);
  z-index: 1;
}

.product-name {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.6em;
  font-size: 0.98rem;
  text-align: left;
}

.price-block {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.45rem;
}

.price-now {
  font-size: 1.15rem;
  font-weight: 800;
  color: var(--sp-orange-deep);
}

.price-old {
  font-size: 0.86rem;
  color: #94a3b8;
  text-decoration: line-through;
}

@keyframes pulseGlow {
  0%, 100% { box-shadow: 0 10px 22px rgba(29, 78, 216, 0.28); }
  50% { box-shadow: 0 14px 34px rgba(29, 78, 216, 0.42); }
}

@media (max-width: 768px) {
  .sportpro-banner {
    height: 500px;
  }

  .quick-view {
    display: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .banner-bg {
    animation: none;
    transition: none;
  }
  .hero-cta {
    animation: none;
  }
}
</style>
