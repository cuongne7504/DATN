<template>
  <div class="cart-page">
    <div class="page-header">
      <div class="container">
        <nav aria-label="breadcrumb" class="checkout-steps mb-3">
          <span class="step active"><i class="bi bi-cart3"></i> Giỏ hàng</span>
          <i class="bi bi-chevron-right step-divider"></i>
          <span class="step muted">Giao hàng</span>
          <i class="bi bi-chevron-right step-divider"></i>
          <span class="step muted">Hoàn tất</span>
        </nav>
        <h1>Giỏ hàng</h1>
        <p v-if="!loading && gioHang.chi_tiet_gio_hang.length">
          {{ gioHang.so_luong_san_pham }} sản phẩm · Tạm tính {{ formatPrice(gioHang.tam_tinh) }}
        </p>
        <p v-else-if="!loading">Chưa có sản phẩm nào trong giỏ</p>
      </div>
    </div>

    <div class="container pb-5">
      <div v-if="loading" class="text-center py-5 text-muted">
        <div class="spinner-border text-secondary mb-3" role="status"></div>
        <p class="mb-0">Đang tải giỏ hàng...</p>
      </div>

      <div v-else-if="gioHang.chi_tiet_gio_hang.length" class="row g-4">
        <div class="col-lg-8">
          <div class="cart-card rounded-4">
            <div class="cart-card-header">
              <span class="fw-bold">Sản phẩm ({{ gioHang.chi_tiet_gio_hang.length }} dòng)</span>
              <RouterLink to="/shop" class="btn btn-sm btn-link text-decoration-none">
                <i class="bi bi-plus-lg me-1"></i>Thêm sản phẩm
              </RouterLink>
            </div>

            <TransitionGroup name="cart-item" tag="div">
              <article
                v-for="item in gioHang.chi_tiet_gio_hang"
                :key="item.ma_ct_gio_hang"
                class="cart-item"
              >
                <RouterLink
                  :to="`/product/${item.ma_san_pham}`"
                  class="cart-item-image"
                >
                  <AppImage
                    :src="item.duong_dan_anh"
                    :alt="item.ten_san_pham"
                    img-class="cart-thumb"
                  />
                </RouterLink>

                <div class="cart-item-body">
                  <div class="d-flex justify-content-between align-items-start gap-2">
                    <div>
                      <RouterLink
                        :to="`/product/${item.ma_san_pham}`"
                        class="cart-item-name"
                      >
                        {{ item.ten_san_pham }}
                      </RouterLink>
                      <div class="cart-item-variant">
                        <span class="variant-chip">{{ item.mau_sac }}</span>
                        <span class="variant-chip">{{ item.kich_co }}</span>
                        <span v-if="item.ma_vach_sku" class="sku-text">
                          SKU: {{ item.ma_vach_sku }}
                        </span>
                      </div>
                    </div>
                    <button
                      type="button"
                      class="btn-remove"
                      title="Xóa sản phẩm"
                      aria-label="Xóa sản phẩm"
                      @click="handleRemove(item)"
                    >
                      <i class="bi bi-trash3"></i>
                    </button>
                  </div>

                  <div class="cart-item-footer">
                    <div class="qty-control">
                      <button
                        type="button"
                        class="qty-btn"
                        :disabled="item.so_luong <= 1"
                        aria-label="Giảm số lượng"
                        @click="changeQty(item, item.so_luong - 1)"
                      >
                        <i class="bi bi-dash"></i>
                      </button>
                      <input
                        :value="item.so_luong"
                        type="number"
                        class="qty-input"
                        min="1"
                        :max="item.so_luong_ton"
                        aria-label="Số lượng"
                        @change="onQtyInput(item, $event)"
                      />
                      <button
                        type="button"
                        class="qty-btn"
                        :disabled="item.so_luong >= item.so_luong_ton"
                        aria-label="Tăng số lượng"
                        @click="changeQty(item, item.so_luong + 1)"
                      >
                        <i class="bi bi-plus"></i>
                      </button>
                    </div>

                    <div class="cart-item-prices">
                      <span class="line-total">{{ formatPrice(lineTotal(item)) }}</span>
                      <span class="unit-price">{{ formatPrice(item.don_gia) }} / sp</span>
                    </div>
                  </div>

                  <p v-if="item.so_luong >= item.so_luong_ton" class="stock-warning mb-0">
                    <i class="bi bi-exclamation-circle me-1"></i>
                    Đã đạt số lượng tối đa ({{ item.so_luong_ton }})
                  </p>
                </div>
              </article>
            </TransitionGroup>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="summary-card rounded-4 sticky-summary">
            <h5 class="summary-title">Tóm tắt đơn hàng</h5>

            <div class="summary-row">
              <span>Tạm tính</span>
              <span class="fw-semibold">{{ formatPrice(gioHang.tam_tinh) }}</span>
            </div>
            <div class="summary-row">
              <span>Phí vận chuyển</span>
              <span :class="gioHang.phi_van_chuyen === 0 ? 'text-success fw-semibold' : 'fw-semibold'">
                {{ gioHang.phi_van_chuyen === 0 ? 'Miễn phí' : formatPrice(gioHang.phi_van_chuyen) }}
              </span>
            </div>

            <div v-if="gioHang.phi_van_chuyen > 0" class="free-ship-hint">
              <i class="bi bi-truck me-1"></i>
              Mua thêm
              <strong>{{ formatPrice(remainingForFreeShip) }}</strong>
              để được miễn phí ship
            </div>

            <hr class="summary-divider" />

            <div class="summary-row summary-total">
              <span>Tổng cộng</span>
              <span class="total-price">{{ formatPrice(gioHang.tong_thanh_toan) }}</span>
            </div>

            <RouterLink
              :to="{ name: 'checkout' }"
              class="btn btn-accent w-100 rounded-pill py-2 fw-bold mt-3"
            >
              Tiến hành thanh toán
              <i class="bi bi-arrow-right ms-1"></i>
            </RouterLink>

            <RouterLink to="/shop" class="btn btn-outline-secondary w-100 rounded-pill py-2 mt-2">
              Tiếp tục mua sắm
            </RouterLink>
          </div>
        </div>
      </div>

      <div v-else class="empty-cart text-center py-5">
        <div class="empty-icon mx-auto mb-3">
          <i class="bi bi-cart-x"></i>
        </div>
        <h4>Giỏ hàng trống</h4>
        <p class="text-muted">Hãy thêm sản phẩm yêu thích vào giỏ nhé!</p>
        <RouterLink to="/shop" class="btn btn-accent rounded-pill px-4 mt-2">
          Mua sắm ngay
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  fetchGioHang,
  getCartSnapshot,
  updateCartItemQuantity,
  removeCartItem,
  FREE_SHIP_THRESHOLD,
} from '../services/cartService'
import { formatPrice } from '../utils/productHelpers'
import AppImage from '../components/AppImage.vue'

const loading = ref(true)
const gioHang = ref({
  chi_tiet_gio_hang: [],
  tam_tinh: 0,
  so_luong_san_pham: 0,
  phi_van_chuyen: 0,
  tong_thanh_toan: 0,
})

const remainingForFreeShip = computed(() =>
  Math.max(0, FREE_SHIP_THRESHOLD - (gioHang.value.tam_tinh ?? 0))
)

function syncCart() {
  gioHang.value = { ...getCartSnapshot() }
}

function lineTotal(item) {
  return item.don_gia * item.so_luong
}

function changeQty(item, qty) {
  updateCartItemQuantity(item.ma_ct_gio_hang, qty)
  syncCart()
}

function onQtyInput(item, event) {
  changeQty(item, Number(event.target.value))
}

function handleRemove(item) {
  if (!confirm(`Xóa "${item.ten_san_pham}" khỏi giỏ hàng?`)) return
  removeCartItem(item.ma_ct_gio_hang)
  syncCart()
}

onMounted(async () => {
  await fetchGioHang()
  syncCart()
  loading.value = false
})
</script>

<style scoped>
.cart-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.cart-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.cart-item {
  display: flex;
  gap: 1rem;
  padding: 1.25rem;
  border-bottom: 1px solid #e2e8f0;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-image {
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.cart-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-body {
  flex: 1;
  min-width: 0;
}

.cart-item-name {
  font-weight: 700;
  color: #0f172a;
  text-decoration: none;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cart-item-name:hover {
  color: var(--sportpro-accent);
}

.cart-item-variant {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.35rem;
  margin-top: 0.35rem;
}

.variant-chip {
  padding: 0.15rem 0.5rem;
  border-radius: 50rem;
  background: #f1f5f9;
  font-size: 0.75rem;
  font-weight: 600;
  color: #475569;
}

.sku-text {
  font-size: 0.72rem;
  color: #94a3b8;
}

.btn-remove {
  border: none;
  background: transparent;
  color: #94a3b8;
  padding: 0.25rem;
  cursor: pointer;
  transition: color 0.2s;
  flex-shrink: 0;
}

.btn-remove:hover {
  color: #ef4444;
}

.cart-item-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 0.85rem;
  flex-wrap: wrap;
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
  width: 34px;
  height: 34px;
  border: none;
  background: #f8fafc;
  color: #334155;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.qty-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-input {
  width: 44px;
  height: 34px;
  border: none;
  border-left: 1.5px solid #e2e8f0;
  border-right: 1.5px solid #e2e8f0;
  text-align: center;
  font-weight: 700;
  font-size: 0.9rem;
  -moz-appearance: textfield;
}

.qty-input::-webkit-outer-spin-button,
.qty-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

.cart-item-prices {
  text-align: right;
}

.line-total {
  display: block;
  font-weight: 800;
  color: #dc2626;
  font-size: 1.05rem;
}

.unit-price {
  font-size: 0.75rem;
  color: #94a3b8;
}

.stock-warning {
  margin-top: 0.5rem;
  font-size: 0.78rem;
  color: #d97706;
}

.summary-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 1.5rem;
}

.summary-title {
  font-weight: 800;
  margin-bottom: 1rem;
  color: #0f172a;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  font-size: 0.9rem;
  margin-bottom: 0.65rem;
  color: #64748b;
}

.summary-divider {
  margin: 1rem 0;
  opacity: 0.15;
}

.summary-total {
  font-size: 1rem;
  color: #0f172a;
  font-weight: 700;
  margin-bottom: 0;
}

.total-price {
  font-size: 1.35rem;
  font-weight: 800;
  color: #dc2626;
}

.free-ship-hint {
  font-size: 0.8rem;
  color: #64748b;
  background: #fff7ed;
  border: 1px solid #fed7aa;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
  margin-bottom: 0.5rem;
}

.btn-accent {
  background: var(--sportpro-accent);
  border: none;
  color: #fff;
}

.btn-accent:hover {
  background: var(--sportpro-accent-dark);
  color: #fff;
}

.empty-icon {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  color: #94a3b8;
}

.cart-item-enter-active,
.cart-item-leave-active {
  transition: all 0.25s ease;
}

.cart-item-enter-from,
.cart-item-leave-to {
  opacity: 0;
  transform: translateX(-12px);
}

@media (max-width: 575.98px) {
  .cart-item {
    flex-direction: column;
  }

  .cart-item-image {
    width: 100%;
    height: 160px;
  }

  .cart-item-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .cart-item-prices {
    text-align: left;
  }
}
</style>
