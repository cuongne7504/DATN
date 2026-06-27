<template>
  <div class="payment-result-page">
    <div class="container py-5">
      <div v-if="!result" class="text-center py-5 text-muted">Đang tải...</div>

      <div v-else class="result-card rounded-4 text-center mx-auto">
        <div class="result-icon failed mx-auto mb-3">
          <i class="bi bi-x-lg"></i>
        </div>

        <h1 class="result-title">Thanh toán thất bại</h1>
        <p class="result-desc text-muted">
          {{ result.message || 'Giao dịch không hoàn tất. Đơn hàng chưa được tạo.' }}
        </p>

        <div class="result-details rounded-3 text-start">
          <div v-if="result.ma_don_hang" class="detail-row">
            <span>Mã tham chiếu</span>
            <strong>{{ formatOrderId(result.ma_don_hang) }}</strong>
          </div>
          <div class="detail-row">
            <span>Phương thức</span>
            <strong>{{ getPaymentLabel(result.phuong_thuc_tt) }}</strong>
          </div>
          <div class="detail-row">
            <span>Số tiền</span>
            <strong class="text-danger">{{ formatPrice(result.tong_thanh_toan) }}</strong>
          </div>
        </div>

        <div class="help-box rounded-3 text-start mt-3">
          <h6 class="fw-bold mb-2"><i class="bi bi-lightbulb me-1"></i> Gợi ý</h6>
          <ul class="mb-0 small text-muted">
            <li>Kiểm tra số dư tài khoản / ví điện tử</li>
            <li>Thử lại với phương thức COD hoặc chuyển khoản</li>
            <li>Liên hệ hotline <strong>1900 6688</strong> nếu cần hỗ trợ</li>
          </ul>
        </div>

        <div class="result-actions d-flex flex-wrap gap-2 justify-content-center mt-4">
          <RouterLink :to="{ name: 'checkout' }" class="btn btn-accent rounded-pill px-4">
            <i class="bi bi-arrow-repeat me-1"></i> Thử thanh toán lại
          </RouterLink>
          <RouterLink :to="{ name: 'cart' }" class="btn btn-outline-dark rounded-pill px-4">
            Về giỏ hàng
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
import { formatOrderId, getPaymentLabel } from '../utils/orderHelpers'

const router = useRouter()
const result = ref(null)

onMounted(() => {
  const data = getPaymentResult()
  if (!data || data.status !== 'failed') {
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

.result-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: 700;
}

.result-icon.failed {
  background: #fef2f2;
  color: #dc2626;
}

.result-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 0.5rem;
}

.result-desc {
  font-size: 0.95rem;
  margin-bottom: 1.25rem;
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

.help-box {
  background: #fffbeb;
  border: 1px solid #fde68a;
  padding: 0.85rem 1rem;
}

.help-box ul {
  padding-left: 1.1rem;
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
