<template>
  <div class="order-detail-page">
    <div class="page-header">
      <div class="container">
        <RouterLink :to="{ name: 'order-history' }" class="back-link">
          <i class="bi bi-arrow-left me-1"></i> Quay lại đơn hàng
        </RouterLink>
        <h1 v-if="order">{{ formatOrderId(order.ma_don_hang) }}</h1>
        <p v-if="order">Chi tiết & theo dõi trạng thái đơn hàng</p>
      </div>
    </div>

    <div class="container pb-5">
      <div v-if="loading" class="text-center py-5 text-muted">Đang tải...</div>

      <div v-else-if="!order" class="text-center py-5">
        <h4>Không tìm thấy đơn hàng</h4>
        <RouterLink :to="{ name: 'order-history' }" class="btn btn-accent rounded-pill mt-3">
          Về lịch sử đơn hàng
        </RouterLink>
      </div>

      <div v-else class="row g-4">
        <div class="col-lg-3">
          <AccountSidebar />
        </div>

        <div class="col-lg-9">
          <!-- Trạng thái -->
          <div class="detail-card rounded-4 mb-4">
            <div class="detail-card-header">
              <h2 class="card-title mb-0">Theo dõi đơn hàng</h2>
              <span
                class="status-badge"
                :style="{
                  color: statusConfig.color,
                  background: statusConfig.bg,
                }"
              >
                <i :class="statusConfig.icon"></i>
                {{ statusConfig.label }}
              </span>
            </div>
            <div class="detail-card-body">
              <OrderStatusTracker
                :trang_thai="order.trang_thai"
                :lich_su_trang_thai="order.lich_su_trang_thai"
              />
            </div>
          </div>

          <div class="row g-4">
            <!-- Sản phẩm -->
            <div class="col-md-7">
              <div class="detail-card rounded-4 h-100">
                <div class="detail-card-header">
                  <h2 class="card-title mb-0">Sản phẩm đã đặt</h2>
                </div>
                <div class="detail-card-body p-0">
                  <div
                    v-for="item in order.chi_tiet"
                    :key="item.ma_ct_don_hang"
                    class="product-row"
                  >
                    <AppImage
                      :src="item.duong_dan_anh"
                      :alt="item.ten_san_pham"
                      img-class="product-thumb"
                    />
                    <div class="product-info flex-grow-1">
                      <p class="product-name mb-1">{{ item.ten_san_pham }}</p>
                      <small class="text-muted d-block">
                        {{ item.mau_sac }} / {{ item.kich_co }}
                      </small>
                      <span class="product-qty">× {{ item.so_luong }}</span>
                    </div>
                    <div class="product-price text-end">
                      <strong>{{ formatPrice(item.don_gia * item.so_luong) }}</strong>
                      <small class="text-muted d-block">{{ formatPrice(item.don_gia) }}/sp</small>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Thông tin giao hàng & thanh toán -->
            <div class="col-md-5">
              <div class="detail-card rounded-4 mb-4">
                <div class="detail-card-header">
                  <h2 class="card-title mb-0">Giao hàng</h2>
                </div>
                <div class="detail-card-body">
                  <ul class="info-list">
                    <li>
                      <i class="bi bi-geo-alt"></i>
                      <span>{{ order.dia_chi_giao }}</span>
                    </li>
                    <li>
                      <i class="bi bi-calendar3"></i>
                      <span>Đặt ngày {{ formatOrderDate(order.ngay_dat) }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <div class="detail-card rounded-4">
                <div class="detail-card-header">
                  <h2 class="card-title mb-0">Thanh toán</h2>
                </div>
                <div class="detail-card-body">
                  <p class="payment-method mb-3">
                    <i class="bi bi-credit-card me-2"></i>
                    {{ getPaymentLabel(order.phuong_thuc_tt) }}
                  </p>
                  <div class="summary-row">
                    <span>Tạm tính</span>
                    <span>{{ formatPrice(order.tong_tien) }}</span>
                  </div>
                  <div class="summary-row">
                    <span>Phí vận chuyển</span>
                    <span>
                      {{ order.phi_ship === 0 ? 'Miễn phí' : formatPrice(order.phi_ship) }}
                    </span>
                  </div>
                  <hr />
                  <div class="summary-row total">
                    <span>Tổng cộng</span>
                    <span class="total-price">
                      {{ formatPrice(order.tong_tien + order.phi_ship) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchDonHangById } from '../services/orderService'
import { formatPrice } from '../utils/productHelpers'
import {
  getStatusConfig,
  getPaymentLabel,
  formatOrderDate,
  formatOrderId,
} from '../utils/orderHelpers'
import AccountSidebar from '../components/AccountSidebar.vue'
import AppImage from '../components/AppImage.vue'
import OrderStatusTracker from '../components/OrderStatusTracker.vue'

const route = useRoute()
const loading = ref(true)
const order = ref(null)

const statusConfig = computed(() =>
  order.value ? getStatusConfig(order.value.trang_thai) : {}
)

async function loadOrder() {
  loading.value = true
  order.value = await fetchDonHangById(route.params.id)
  loading.value = false
}

onMounted(loadOrder)
watch(() => route.params.id, loadOrder)
</script>

<style scoped>
.back-link {
  display: inline-flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.9rem;
  margin-bottom: 0.75rem;
}

.back-link:hover {
  color: var(--sportpro-accent);
}

.detail-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.detail-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.25rem;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  flex-wrap: wrap;
}

.card-title {
  font-size: 1rem;
  font-weight: 800;
  color: #0f172a;
}

.detail-card-body {
  padding: 1.25rem;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.3rem 0.75rem;
  border-radius: 50rem;
  font-size: 0.78rem;
  font-weight: 700;
}

.product-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #f1f5f9;
}

.product-row:last-child {
  border-bottom: none;
}

.product-thumb {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.product-name {
  font-weight: 700;
  font-size: 0.9rem;
  color: #334155;
}

.product-qty {
  font-size: 0.82rem;
  font-weight: 600;
  color: #64748b;
}

.product-price strong {
  color: #dc2626;
  font-size: 0.95rem;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  display: flex;
  gap: 0.75rem;
  font-size: 0.88rem;
  color: #475569;
  line-height: 1.5;
}

.info-list li + li {
  margin-top: 0.75rem;
}

.info-list i {
  color: var(--sportpro-accent);
  flex-shrink: 0;
  margin-top: 0.15rem;
}

.payment-method {
  font-size: 0.88rem;
  font-weight: 600;
  color: #334155;
  padding: 0.65rem 0.85rem;
  background: #f8fafc;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.88rem;
  color: #64748b;
  margin-bottom: 0.5rem;
}

.summary-row.total {
  font-weight: 700;
  color: #0f172a;
  font-size: 1rem;
  margin-bottom: 0;
}

.total-price {
  color: #dc2626;
  font-size: 1.15rem;
}

.btn-accent {
  background: var(--sportpro-accent);
  border: none;
  color: #fff;
}
</style>
