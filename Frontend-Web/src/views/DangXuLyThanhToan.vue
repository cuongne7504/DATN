<template>
  <div class="payment-processing-page">
    <div class="container py-5">
      <div class="processing-card rounded-4 text-center mx-auto">
        <div class="gateway-icon mb-3">
          <i :class="gatewayIcon"></i>
        </div>
        <h2 class="processing-title">Đang chuyển hướng thanh toán</h2>
        <p class="text-muted mb-4">
          Vui lòng không đóng trình duyệt. Hệ thống đang kết nối
          <strong>{{ gatewayLabel }}</strong>...
        </p>

        <div class="spinner-border text-warning mb-4" role="status">
          <span class="visually-hidden">Đang xử lý...</span>
        </div>

        <div class="progress-steps">
          <div class="pstep" :class="{ done: step >= 1, active: step === 1 }">
            <i class="bi bi-shield-lock"></i> Bảo mật
          </div>
          <div class="pstep" :class="{ done: step >= 2, active: step === 2 }">
            <i class="bi bi-bank"></i> Cổng thanh toán
          </div>
          <div class="pstep" :class="{ done: step >= 3, active: step === 3 }">
            <i class="bi bi-check2"></i> Xác nhận
          </div>
        </div>

        <p v-if="pending" class="amount-line mt-4 mb-0">
          Số tiền: <strong>{{ formatPrice(pending.tong_thanh_toan) }}</strong>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPendingPayment,
  processOnlinePayment,
} from '../services/paymentService'
import { formatPrice } from '../utils/productHelpers'
import { getPaymentLabel } from '../utils/orderHelpers'

const router = useRouter()
const pending = ref(null)
const step = ref(1)
let stepTimer = null

const gatewayLabel = computed(() =>
  pending.value ? getPaymentLabel(pending.value.phuong_thuc_tt) : 'cổng thanh toán'
)

const gatewayIcon = computed(() => {
  if (pending.value?.phuong_thuc_tt === 'momo') return 'bi bi-phone-fill'
  return 'bi bi-bank2'
})

onMounted(async () => {
  pending.value = getPendingPayment()
  if (!pending.value) {
    router.replace({ name: 'cart' })
    return
  }

  stepTimer = setInterval(() => {
    if (step.value < 3) step.value += 1
  }, 800)

  const result = await processOnlinePayment(pending.value)

  if (stepTimer) clearInterval(stepTimer)

  if (result.status === 'success') {
    router.replace({ name: 'payment-success' })
  } else {
    router.replace({ name: 'payment-failure' })
  }
})

onUnmounted(() => {
  if (stepTimer) clearInterval(stepTimer)
})
</script>

<style scoped>
.payment-processing-page {
  min-height: 60vh;
  background: var(--sportpro-bg);
}

.processing-card {
  max-width: 480px;
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 2.5rem 2rem;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.gateway-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto;
  border-radius: 50%;
  background: rgba(249, 115, 22, 0.12);
  color: var(--sportpro-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.processing-title {
  font-size: 1.35rem;
  font-weight: 800;
  color: #0f172a;
}

.progress-steps {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.pstep {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  padding: 0.35rem 0.65rem;
  border-radius: 50rem;
  background: #f8fafc;
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  transition: all 0.3s;
}

.pstep.active {
  background: rgba(249, 115, 22, 0.15);
  color: var(--sportpro-accent);
}

.pstep.done {
  background: #dcfce7;
  color: #16a34a;
}

.amount-line {
  font-size: 0.95rem;
  color: #64748b;
}

.amount-line strong {
  color: #dc2626;
  font-size: 1.1rem;
}
</style>
