<template>
  <div class="payment-result-page">
    <div class="container py-5">
      <div v-if="!result" class="text-center py-5 text-muted">Đang tải...</div>

      <div v-else class="result-card rounded-4 text-center mx-auto">
        <nav aria-label="breadcrumb" class="checkout-steps mb-4 justify-content-center">
          <span class="step done"><i class="bi bi-cart-check"></i> Giỏ hàng</span>
          <i class="bi bi-chevron-right step-divider"></i>
          <span class="step done"><i class="bi bi-geo-alt"></i> Giao hàng</span>
          <i class="bi bi-chevron-right step-divider"></i>
          <span class="step active"><i class="bi bi-check-circle"></i> Hoàn tất</span>
        </nav>

        <div class="result-icon success mx-auto mb-3">
          <i class="bi bi-check-lg"></i>
        </div>

        <h1 class="result-title">Thanh toán thành công!</h1>
        <p class="result-desc text-muted">
          Cảm ơn <strong>{{ result.ho_ten }}</strong>. Đơn hàng của bạn đã được ghi nhận.
        </p>

        <div class="result-details rounded-3 text-start">
          <div class="detail-row">
            <span>Mã đơn hàng</span>
            <strong>{{ formatOrderId(result.ma_don_hang) }}</strong>
          </div>
          <div v-if="result.ma_giao_dich" class="detail-row">
            <span>Mã giao dịch</span>
            <strong class="font-monospace">{{ result.ma_giao_dich }}</strong>
          </div>
          <div class="detail-row">
            <span>Phương thức</span>
            <strong>{{ getPaymentLabel(result.phuong_thuc_tt) }}</strong>
          </div>
          <div class="detail-row">
            <span>Tổng thanh toán</span>
            <strong class="text-danger">{{ formatPrice(result.tong_thanh_toan) }}</strong>
          </div>
          <div class="detail-row">
            <span>Thời gian</span>
            <strong>{{ formatOrderDate(result.ngay_dat) }}</strong>
          </div>
        </div>

        <div class="result-actions d-flex flex-wrap gap-2 justify-content-center mt-4">
          <RouterLink
            :to="{ name: 'order-detail', params: { id: result.ma_don_hang } }"
            class="btn btn-accent rounded-pill px-4"
          >
            <i class="bi bi-bag-check me-1"></i> Xem đơn hàng
          </RouterLink>
          <RouterLink to="/shop" class="btn btn-outline-dark rounded-pill px-4">
            Tiếp tục mua sắm
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPaymentResult } from '../services/paymentService'
import { formatPrice } from '../utils/productHelpers'
import { formatOrderId, formatOrderDate, getPaymentLabel } from '../utils/orderHelpers'

const router = useRouter()
const result = ref(null)

onMounted(() => {
  const data = getPaymentResult()
  if (!data || data.status !== 'success') {
    router.replace({ name: 'shop' })
    return
  }
  result.value = data
})
</script>

<style scoped>
.payment-result-page {
  min-height: 60vh;
  background: var(--sportpro-bg);
}

.result-card {
  max-width: 520px;
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 2rem 1.75rem;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.step {
  color: #94a3b8;
}

.step.done {
  color: #16a34a;
}

.result-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.25rem;
  font-weight: 700;
}

.result-icon.success {
  background: #dcfce7;
  color: #16a34a;
}

.result-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 0.5rem;
}

.result-desc {
  font-size: 0.95rem;
  margin-bottom: 1.5rem;
}

.result-details {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 1rem 1.25rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  font-size: 0.88rem;
  padding: 0.4rem 0;
  color: #64748b;
}

.detail-row strong {
  color: #0f172a;
  text-align: right;
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
</style>
