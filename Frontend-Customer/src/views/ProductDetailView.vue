<template>
  <div v-if="sanPham" class="product-detail-page">
    <div class="container py-4">
      <!-- Breadcrumb -->
      <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb mb-0 small">
          <li class="breadcrumb-item">
            <RouterLink to="/">Trang chủ</RouterLink>
          </li>
          <li class="breadcrumb-item">
            <RouterLink to="/shop">Cửa hàng</RouterLink>
          </li>
          <li v-if="sanPham.ten_danh_muc" class="breadcrumb-item">
            <RouterLink :to="{ name: 'shop', query: { ma_danh_muc: sanPham.ma_danh_muc } }">
              {{ sanPham.ten_danh_muc }}
            </RouterLink>
          </li>
          <li class="breadcrumb-item active" aria-current="page">
            {{ sanPham.ten_san_pham }}
          </li>
        </ol>
      </nav>

      <div class="row g-4 g-lg-5">
        <!-- Gallery -->
        <div class="col-12 col-lg-6">
          <div class="gallery-card rounded-4 overflow-hidden">
            <div v-if="coGiamGia" class="sale-ribbon">Giảm giá</div>
            <div class="main-image-wrap">
              <AppImage
                :src="activeImagePath"
                :alt="sanPham.ten_san_pham"
                img-class="main-image"
                loading="eager"
              />
            </div>
            <div v-if="galleryImages.length > 1" class="thumb-row">
              <button
                v-for="(img, index) in galleryImages"
                :key="img.ma_hinh_anh ?? index"
                type="button"
                class="thumb-btn"
                :class="{ active: activeImagePath === img.duong_dan_anh }"
                :aria-label="`Ảnh ${index + 1}`"
                @click="activeImagePath = img.duong_dan_anh"
              >
                <AppImage
                  :src="img.duong_dan_anh"
                  :alt="`${sanPham.ten_san_pham} ${index + 1}`"
                  img-class="thumb-img"
                />
              </button>
            </div>
          </div>
        </div>

        <!-- Product info -->
        <div class="col-12 col-lg-6">
          <div class="info-panel">
            <div class="d-flex flex-wrap align-items-center gap-2 mb-2">
              <span v-if="sanPham.ten_danh_muc" class="category-badge">
                {{ sanPham.ten_danh_muc }}
              </span>
              <div class="d-flex align-items-center gap-2 ms-auto">
                <AppImage
                  v-if="sanPham.logo"
                  :src="sanPham.logo"
                  :alt="sanPham.ten_thuong_hieu"
                  img-class="brand-logo"
                />
                <span class="brand-name">{{ sanPham.ten_thuong_hieu }}</span>
              </div>
            </div>

            <h1 class="product-title">{{ sanPham.ten_san_pham }}</h1>

            <div
              v-if="sanPham.so_luong_danh_gia > 0"
              class="rating-row d-flex align-items-center gap-2 mb-3"
            >
              <span class="rating-stars">
                <i
                  v-for="n in 5"
                  :key="n"
                  class="bi bi-star-fill"
                  :class="{ muted: n > sanPham.diem_trung_binh }"
                ></i>
              </span>
              <span class="rating-text">
                {{ sanPham.diem_trung_binh }}/5
                <span class="text-muted">({{ sanPham.so_luong_danh_gia }} đánh giá)</span>
              </span>
            </div>

            <div class="price-block mb-3">
              <span class="price-current">{{ formatPrice(giaHienThi) }}</span>
              <span v-if="coGiamGia" class="price-old">
                {{ formatPrice(sanPham.gia_goc) }}
              </span>
              <span v-if="salePercent" class="sale-tag">-{{ salePercent }}%</span>
            </div>

            <p class="product-desc">{{ sanPham.mo_ta }}</p>

            <!-- Variant picker -->
            <div v-if="hasVariants" class="variant-panel rounded-4">
              <div class="variant-panel-header">
                <i class="bi bi-sliders2"></i>
                <span>Chọn phiên bản</span>
                <span class="variant-steps text-muted">
                  {{ variantStepLabel }}
                </span>
              </div>

              <!-- Màu sắc -->
              <div v-if="mauSacOptions.length" class="variant-section">
                <div class="variant-label-row">
                  <span class="variant-label">Màu sắc</span>
                  <span v-if="selectedColor" class="variant-selected">{{ selectedColor }}</span>
                  <span v-else class="variant-hint">Chọn màu</span>
                </div>
                <div class="color-options">
                  <button
                    v-for="mau in mauSacOptions"
                    :key="mau.ten"
                    type="button"
                    class="color-option"
                    :class="{
                      active: selectedColor === mau.ten,
                      unavailable: !mau.con_hang,
                    }"
                    :disabled="!mau.con_hang"
                    :title="mau.con_hang ? mau.ten : `${mau.ten} — Hết hàng`"
                    @click="selectColor(mau.ten)"
                  >
                    <span
                      class="color-dot"
                      :class="{ light: mau.isLight }"
                      :style="{ backgroundColor: mau.hex }"
                    ></span>
                    <span class="color-name">{{ mau.ten }}</span>
                    <i v-if="selectedColor === mau.ten" class="bi bi-check-lg check-icon"></i>
                  </button>
                </div>
              </div>

              <!-- Kích cỡ -->
              <div v-if="mauSacOptions.length" class="variant-section">
                <div class="variant-label-row">
                  <span class="variant-label">Kích cỡ</span>
                  <span v-if="selectedSize" class="variant-selected">{{ selectedSize }}</span>
                  <span v-else-if="!selectedColor" class="variant-hint">Chọn màu trước</span>
                  <span v-else class="variant-hint">Chọn size</span>
                </div>
                <div class="size-grid">
                  <button
                    v-for="opt in kichCoOptions"
                    :key="opt.kich_co"
                    type="button"
                    class="size-option"
                    :class="{
                      active: selectedSize === opt.kich_co,
                      disabled: !opt.con_hang,
                    }"
                    :disabled="!selectedColor || !opt.con_hang"
                    @click="selectSize(opt.kich_co)"
                  >
                    <span class="size-label">{{ opt.kich_co }}</span>
                    <span v-if="opt.con_hang" class="size-stock">Còn {{ opt.so_luong_ton }}</span>
                    <span v-else class="size-stock out">Hết</span>
                  </button>
                </div>
              </div>

              <!-- Tóm tắt biến thể đã chọn -->
              <Transition name="fade">
                <div v-if="selectedChiTiet" class="variant-summary">
                  <div class="summary-row">
                    <span class="summary-key">Phiên bản</span>
                    <span class="summary-val">
                      {{ selectedColor }} · {{ selectedSize }}
                    </span>
                  </div>
                  <div v-if="selectedChiTiet.ma_vach_sku" class="summary-row">
                    <span class="summary-key">SKU</span>
                    <span class="summary-val font-monospace">{{ selectedChiTiet.ma_vach_sku }}</span>
                  </div>
                  <div class="summary-row">
                    <span class="summary-key">Tồn kho</span>
                    <span
                      class="summary-val"
                      :class="selectedChiTiet.so_luong_ton <= 5 ? 'text-warning' : 'text-success'"
                    >
                      {{ stockLabel }}
                    </span>
                  </div>
                  <div v-if="selectedChiTiet.gia_cong_them > 0" class="summary-row">
                    <span class="summary-key">Phụ thu</span>
                    <span class="summary-val">+{{ formatPrice(selectedChiTiet.gia_cong_them) }}</span>
                  </div>
                </div>
              </Transition>
            </div>

            <!-- Số lượng -->
            <div class="qty-section">
              <span class="variant-label">Số lượng</span>
              <div class="qty-control">
                <button
                  type="button"
                  class="qty-btn"
                  :disabled="quantity <= 1"
                  aria-label="Giảm số lượng"
                  @click="decreaseQty"
                >
                  <i class="bi bi-dash"></i>
                </button>
                <input
                  v-model.number="quantity"
                  type="number"
                  class="qty-input"
                  min="1"
                  :max="maxQuantity"
                  aria-label="Số lượng"
                  @blur="clampQuantity"
                />
                <button
                  type="button"
                  class="qty-btn"
                  :disabled="quantity >= maxQuantity"
                  aria-label="Tăng số lượng"
                  @click="increaseQty"
                >
                  <i class="bi bi-plus"></i>
                </button>
              </div>
            </div>

            <div v-if="validationMessage" class="alert alert-warning py-2 small mb-3">
              <i class="bi bi-exclamation-triangle me-1"></i>{{ validationMessage }}
            </div>

            <div class="action-row">
              <button
                type="button"
                class="btn btn-accent btn-action"
                :disabled="!canPurchase"
                @click="handleAddToCart"
              >
                <i class="bi bi-cart-plus"></i>
                Thêm vào giỏ hàng
              </button>
              <button
                type="button"
                class="btn btn-dark btn-action"
                :disabled="!canPurchase"
                @click="handleBuyNow"
              >
                Mua ngay
              </button>
            </div>

            <ul class="trust-list">
              <li><i class="bi bi-truck"></i> Giao hàng toàn quốc</li>
              <li><i class="bi bi-arrow-repeat"></i> Đổi trả trong 7 ngày</li>
              <li><i class="bi bi-shield-check"></i> Hàng chính hãng 100%</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else-if="loading" class="container py-5 text-center text-muted">
    <div class="spinner-border text-secondary mb-3" role="status"></div>
    <p class="mb-0">Đang tải sản phẩm...</p>
  </div>

  <div v-else class="container py-5 text-center">
    <i class="bi bi-box-seam display-1 text-muted"></i>
    <h3 class="mt-3">Không tìm thấy sản phẩm</h3>
    <RouterLink to="/shop" class="btn btn-accent rounded-pill mt-3">Quay lại cửa hàng</RouterLink>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchSanPhamById } from '../services/catalogService'
import AppImage from '../components/AppImage.vue'
import {
  formatPrice,
  getGiaHienThi,
  coKhuyenMai,
  getMauSacList,
  sizeConHang,
  getDonGiaChiTiet,
} from '../utils/productHelpers'

const COLOR_MAP = {
  Đen: '#1e293b',
  Trắng: '#f8fafc',
  'Xanh dương': '#2563eb',
  Đỏ: '#dc2626',
  Hồng: '#ec4899',
  Xám: '#94a3b8',
}

const LIGHT_COLORS = new Set(['Trắng', 'Vàng', 'Kem'])

const route = useRoute()
const sanPham = ref(null)
const loading = ref(true)
const selectedColor = ref(null)
const selectedSize = ref(null)
const quantity = ref(1)
const validationMessage = ref('')
const activeImagePath = ref('')

const hasVariants = computed(() => (sanPham.value?.chi_tiet?.length ?? 0) > 0)

const giaHienThi = computed(() => {
  if (!sanPham.value) return 0
  const ct = selectedChiTiet.value
  if (ct) return getDonGiaChiTiet(ct, sanPham.value)
  return getGiaHienThi(sanPham.value)
})

const coGiamGia = computed(() => (sanPham.value ? coKhuyenMai(sanPham.value) : false))

const salePercent = computed(() => {
  if (!coGiamGia.value || !sanPham.value) return 0
  return Math.round(
    ((Number(sanPham.value.gia_goc) - Number(sanPham.value.gia_khuyen_mai)) /
      Number(sanPham.value.gia_goc)) *
      100
  )
})

const galleryImages = computed(() => sanPham.value?.hinh_anh_sp ?? [])

const mauSacOptions = computed(() => {
  if (!sanPham.value?.chi_tiet?.length) return []
  const colors = getMauSacList(sanPham.value)
  return colors.map((ten) => {
    const items = sanPham.value.chi_tiet.filter((ct) => ct.mau_sac === ten)
    const totalStock = items.reduce((sum, ct) => sum + ct.so_luong_ton, 0)
    return {
      ten,
      hex: COLOR_MAP[ten] || '#64748b',
      isLight: LIGHT_COLORS.has(ten),
      con_hang: totalStock > 0,
      so_luong_ton: totalStock,
    }
  })
})

const selectedChiTiet = computed(() => {
  if (!sanPham.value?.chi_tiet?.length) return null
  if (!selectedColor.value || !selectedSize.value) return null
  return (
    sanPham.value.chi_tiet.find(
      (ct) =>
        ct.mau_sac === selectedColor.value && ct.kich_co === selectedSize.value
    ) ?? null
  )
})

const kichCoOptions = computed(() => {
  if (!sanPham.value?.chi_tiet?.length || !selectedColor.value) return []
  const filtered = sanPham.value.chi_tiet.filter(
    (ct) => ct.mau_sac === selectedColor.value
  )

  const unique = new Map()
  filtered.forEach((ct) => {
    if (!unique.has(ct.kich_co)) {
      unique.set(ct.kich_co, {
        kich_co: ct.kich_co,
        con_hang: sizeConHang(ct),
        so_luong_ton: ct.so_luong_ton,
        ma_vach_sku: ct.ma_vach_sku,
        ma_chi_tiet_sp: ct.ma_chi_tiet_sp,
      })
    }
  })
  return [...unique.values()]
})

const maxQuantity = computed(() => {
  if (selectedChiTiet.value) return Math.max(selectedChiTiet.value.so_luong_ton, 1)
  return 99
})

const canPurchase = computed(() => {
  if (!hasVariants.value) return true
  return Boolean(selectedChiTiet.value?.so_luong_ton)
})

const stockLabel = computed(() => {
  const stock = selectedChiTiet.value?.so_luong_ton ?? 0
  if (stock <= 0) return 'Hết hàng'
  if (stock <= 5) return `Chỉ còn ${stock} sản phẩm`
  return `Còn ${stock} sản phẩm`
})

const variantStepLabel = computed(() => {
  if (!selectedColor.value) return 'Bước 1/2'
  if (!selectedSize.value) return 'Bước 2/2'
  return 'Đã chọn xong'
})

function getMainImagePath(product) {
  const main =
    product?.hinh_anh_sp?.find((h) => h.la_anh_chinh) || product?.hinh_anh_sp?.[0]
  return main?.duong_dan_anh ?? product?.duong_dan_anh ?? null
}

function selectColor(ten) {
  selectedColor.value = ten
  selectedSize.value = null
  validationMessage.value = ''
}

function selectSize(kichCo) {
  selectedSize.value = kichCo
  validationMessage.value = ''
  quantity.value = 1
}

function clampQuantity() {
  if (!Number.isFinite(quantity.value) || quantity.value < 1) {
    quantity.value = 1
  } else if (quantity.value > maxQuantity.value) {
    quantity.value = maxQuantity.value
  }
}

async function loadProduct() {
  loading.value = true
  sanPham.value = await fetchSanPhamById(route.params.id)
  if (sanPham.value) {
    activeImagePath.value = getMainImagePath(sanPham.value)
    selectedColor.value = null
    selectedSize.value = null
    quantity.value = 1
    validationMessage.value = ''
  }
  loading.value = false
}

onMounted(loadProduct)
watch(() => route.params.id, loadProduct)

watch(maxQuantity, clampQuantity)

function decreaseQty() {
  if (quantity.value > 1) quantity.value--
}

function increaseQty() {
  if (quantity.value < maxQuantity.value) quantity.value++
}

function validateSelection() {
  validationMessage.value = ''
  if (!hasVariants.value) return true

  if (!selectedColor.value) {
    validationMessage.value = 'Vui lòng chọn màu sắc trước khi tiếp tục.'
    return false
  }
  if (!selectedSize.value) {
    validationMessage.value = 'Vui lòng chọn kích cỡ trước khi tiếp tục.'
    return false
  }
  if (!selectedChiTiet.value?.so_luong_ton) {
    validationMessage.value = 'Phiên bản này đã hết hàng.'
    return false
  }
  return true
}

function handleAddToCart() {
  if (!validateSelection()) return
  const ct = selectedChiTiet.value
  alert(
    `Đã thêm vào giỏ: ${sanPham.value.ten_san_pham}` +
      (selectedColor.value ? ` — ${selectedColor.value}` : '') +
      (selectedSize.value ? ` / Size ${selectedSize.value}` : '') +
      ` × ${quantity.value}` +
      (ct?.ma_vach_sku ? ` (SKU: ${ct.ma_vach_sku})` : '') +
      ` — ${formatPrice(getDonGiaChiTiet(ct, sanPham.value) * quantity.value)}`
  )
}

function handleBuyNow() {
  if (!validateSelection()) return
  const ct = selectedChiTiet.value
  const donGia = getDonGiaChiTiet(ct, sanPham.value)
  alert(
    `Mua ngay thành công! ${sanPham.value.ten_san_pham} × ${quantity.value} — ` +
      `Tổng: ${formatPrice(donGia * quantity.value)}`
  )
}
</script>

<style scoped>
.product-detail-page {
  background: var(--sportpro-bg);
}

.breadcrumb-item a {
  color: var(--sportpro-muted);
  text-decoration: none;
}

.breadcrumb-item a:hover {
  color: var(--sportpro-accent);
}

.breadcrumb-item.active {
  color: #334155;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Gallery */
.gallery-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  position: relative;
}

.sale-ribbon {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 2;
  padding: 0.3rem 0.85rem;
  border-radius: 50rem;
  background: var(--sportpro-accent);
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.main-image-wrap {
  background: #f8fafc;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 1rem;
}

.thumb-row {
  display: flex;
  gap: 0.5rem;
  padding: 0.75rem 1rem 1rem;
  overflow-x: auto;
}

.thumb-btn {
  flex: 0 0 72px;
  padding: 0;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  background: #f8fafc;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.thumb-btn.active {
  border-color: var(--sportpro-accent);
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.2);
}

.thumb-img {
  width: 72px;
  height: 72px;
  object-fit: cover;
  display: block;
}

/* Info panel */
.info-panel {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 1rem;
  padding: 1.5rem;
}

.category-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 50rem;
  background: rgba(249, 115, 22, 0.1);
  color: var(--sportpro-accent);
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}

.brand-logo {
  height: 22px;
  object-fit: contain;
}

.brand-name {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--sportpro-muted);
}

.product-title {
  font-size: clamp(1.35rem, 3vw, 1.85rem);
  font-weight: 800;
  color: #0f172a;
  line-height: 1.35;
  margin-bottom: 0.5rem;
}

.rating-stars {
  color: #f59e0b;
  font-size: 0.9rem;
}

.rating-stars .muted {
  color: #e2e8f0;
}

.rating-text {
  font-size: 0.875rem;
  font-weight: 600;
  color: #334155;
}

.price-block {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.65rem;
}

.price-current {
  font-size: 1.75rem;
  font-weight: 800;
  color: #dc2626;
}

.price-old {
  font-size: 1rem;
  color: #94a3b8;
  text-decoration: line-through;
}

.sale-tag {
  padding: 0.15rem 0.5rem;
  border-radius: 6px;
  background: #fef2f2;
  color: #dc2626;
  font-size: 0.8rem;
  font-weight: 700;
}

.product-desc {
  color: var(--sportpro-muted);
  line-height: 1.65;
  margin-bottom: 1.25rem;
  font-size: 0.95rem;
}

/* Variant panel */
.variant-panel {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 1.1rem;
  margin-bottom: 1.25rem;
}

.variant-panel-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  font-size: 0.9rem;
  color: #0f172a;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.variant-panel-header i {
  color: var(--sportpro-accent);
}

.variant-steps {
  margin-left: auto;
  font-size: 0.8rem;
  font-weight: 500;
}

.variant-section + .variant-section {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px dashed #e2e8f0;
}

.variant-label-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.65rem;
}

.variant-label {
  font-weight: 700;
  font-size: 0.85rem;
  color: #334155;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.variant-selected {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--sportpro-accent);
}

.variant-hint {
  margin-left: auto;
  font-size: 0.8rem;
  color: #94a3b8;
}

/* Color options */
.color-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.color-option {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.4rem 0.75rem 0.4rem 0.45rem;
  border: 2px solid #e2e8f0;
  border-radius: 50rem;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s, box-shadow 0.2s;
  position: relative;
}

.color-option:hover:not(:disabled) {
  border-color: #cbd5e1;
}

.color-option.active {
  border-color: var(--sportpro-accent);
  background: rgba(249, 115, 22, 0.06);
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.15);
}

.color-option.unavailable {
  opacity: 0.45;
  cursor: not-allowed;
  text-decoration: line-through;
}

.color-dot {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.08);
}

.color-dot.light {
  box-shadow: inset 0 0 0 1px #cbd5e1;
}

.color-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: #334155;
}

.check-icon {
  font-size: 0.85rem;
  color: var(--sportpro-accent);
  margin-left: 0.1rem;
}

/* Size grid */
.size-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 0.5rem;
}

.size-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.15rem;
  min-height: 58px;
  padding: 0.5rem 0.35rem;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.size-option:hover:not(:disabled) {
  border-color: #94a3b8;
}

.size-option.active {
  border-color: #0f172a;
  background: #0f172a;
  color: #fff;
}

.size-option.active .size-stock {
  color: rgba(255, 255, 255, 0.75);
}

.size-option.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #f1f5f9;
}

.size-label {
  font-weight: 700;
  font-size: 0.9rem;
}

.size-stock {
  font-size: 0.65rem;
  color: #22c55e;
  font-weight: 600;
}

.size-stock.out {
  color: #ef4444;
}

.size-option.active .size-stock.out {
  color: #fca5a5;
}

/* Variant summary */
.variant-summary {
  margin-top: 1rem;
  padding: 0.85rem 1rem;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  font-size: 0.85rem;
  padding: 0.2rem 0;
}

.summary-row + .summary-row {
  margin-top: 0.35rem;
}

.summary-key {
  color: #94a3b8;
  font-weight: 500;
}

.summary-val {
  font-weight: 600;
  color: #334155;
  text-align: right;
}

/* Quantity */
.qty-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.qty-control {
  display: inline-flex;
  align-items: center;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}

.qty-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #f8fafc;
  color: #334155;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.qty-btn:hover:not(:disabled) {
  background: #e2e8f0;
}

.qty-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-input {
  width: 52px;
  height: 40px;
  border: none;
  border-left: 1.5px solid #e2e8f0;
  border-right: 1.5px solid #e2e8f0;
  text-align: center;
  font-weight: 700;
  font-size: 1rem;
  color: #0f172a;
  -moz-appearance: textfield;
}

.qty-input::-webkit-outer-spin-button,
.qty-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

/* Actions */
.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
}

.btn-action {
  flex: 1 1 160px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border-radius: 50rem;
  padding: 0.7rem 1.25rem;
  font-weight: 700;
}

.btn-action:disabled {
  opacity: 0.55;
}

.btn-accent {
  background: var(--sportpro-accent);
  border: none;
  color: #fff;
}

.btn-accent:hover:not(:disabled) {
  background: var(--sportpro-accent-dark);
  color: #fff;
}

.trust-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem 1.25rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.trust-list li {
  font-size: 0.8rem;
  color: var(--sportpro-muted);
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.trust-list i {
  color: var(--sportpro-accent);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 575.98px) {
  .info-panel {
    padding: 1.1rem;
  }

  .size-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .action-row .btn-action {
    flex: 1 1 100%;
  }
}
</style>
