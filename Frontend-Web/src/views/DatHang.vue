<template>
  <div class="checkout-page">
    <div class="page-header">
      <div class="container">
        <nav aria-label="breadcrumb" class="checkout-steps mb-3">
          <RouterLink :to="{ name: 'cart' }" class="step done">
            <i class="bi bi-cart-check"></i> Giỏ hàng
          </RouterLink>
          <i class="bi bi-chevron-right step-divider"></i>
          <span class="step active"><i class="bi bi-geo-alt"></i> Giao hàng</span>
          <i class="bi bi-chevron-right step-divider"></i>
          <span class="step muted">Hoàn tất</span>
        </nav>
        <h1>Thông tin giao hàng</h1>
        <p>Điền thông tin nhận hàng để hoàn tất đơn</p>
      </div>
    </div>

    <div class="container pb-5">
      <div v-if="cartLoading" class="text-center py-5 text-muted">Đang tải...</div>

      <div v-else-if="!gioHang.chi_tiet_gio_hang.length" class="text-center py-5">
        <i class="bi bi-cart-x display-1 text-muted"></i>
        <h4 class="mt-3">Giỏ hàng trống</h4>
        <RouterLink to="/shop" class="btn btn-accent rounded-pill px-4 mt-2">
          Mua sắm ngay
        </RouterLink>
      </div>

      <div v-else class="row g-4">
        <!-- Form giao hàng -->
        <div class="col-lg-7">
          <form class="checkout-form rounded-4" @submit.prevent="handleSubmit">
            <section class="form-section">
              <h2 class="section-heading">
                <span class="section-num">1</span>
                Thông tin người nhận
              </h2>
              <div class="row g-3">
                <div class="col-12">
                  <label class="form-label" for="hoTen">Họ và tên <span class="text-danger">*</span></label>
                  <input
                    id="hoTen"
                    v-model="form.ho_ten"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': errors.ho_ten }"
                    placeholder="Nguyễn Văn A"
                  />
                  <div v-if="errors.ho_ten" class="invalid-feedback">{{ errors.ho_ten }}</div>
                </div>
                <div class="col-md-6">
                  <label class="form-label" for="sdt">Số điện thoại <span class="text-danger">*</span></label>
                  <input
                    id="sdt"
                    v-model="form.so_dien_thoai"
                    type="tel"
                    class="form-control"
                    :class="{ 'is-invalid': errors.so_dien_thoai }"
                    placeholder="09xx xxx xxx"
                  />
                  <div v-if="errors.so_dien_thoai" class="invalid-feedback">{{ errors.so_dien_thoai }}</div>
                </div>
                <div class="col-md-6">
                  <label class="form-label" for="email">Email</label>
                  <input
                    id="email"
                    v-model="form.email"
                    type="email"
                    class="form-control"
                    :class="{ 'is-invalid': errors.email }"
                    placeholder="email@example.com"
                  />
                  <div v-if="errors.email" class="invalid-feedback">{{ errors.email }}</div>
                </div>
              </div>
            </section>

            <section class="form-section">
              <h2 class="section-heading">
                <span class="section-num">2</span>
                Địa chỉ giao hàng
              </h2>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label" for="tinh">Tỉnh / Thành phố <span class="text-danger">*</span></label>
                  <select
                    id="tinh"
                    v-model="form.tinh_thanh"
                    class="form-select"
                    :class="{ 'is-invalid': errors.tinh_thanh }"
                  >
                    <option value="">-- Chọn tỉnh/thành --</option>
                    <option v-for="t in tinhThanhList" :key="t" :value="t">{{ t }}</option>
                  </select>
                  <div v-if="errors.tinh_thanh" class="invalid-feedback d-block">{{ errors.tinh_thanh }}</div>
                </div>
                <div class="col-md-6">
                  <label class="form-label" for="quan">Quận / Huyện <span class="text-danger">*</span></label>
                  <input
                    id="quan"
                    v-model="form.quan_huyen"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': errors.quan_huyen }"
                    placeholder="Quận 1"
                  />
                  <div v-if="errors.quan_huyen" class="invalid-feedback">{{ errors.quan_huyen }}</div>
                </div>
                <div class="col-md-6">
                  <label class="form-label" for="phuong">Phường / Xã <span class="text-danger">*</span></label>
                  <input
                    id="phuong"
                    v-model="form.phuong_xa"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': errors.phuong_xa }"
                    placeholder="Phường Bến Nghé"
                  />
                  <div v-if="errors.phuong_xa" class="invalid-feedback">{{ errors.phuong_xa }}</div>
                </div>
                <div class="col-12">
                  <label class="form-label" for="diaChi">Địa chỉ chi tiết <span class="text-danger">*</span></label>
                  <input
                    id="diaChi"
                    v-model="form.dia_chi_chi_tiet"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': errors.dia_chi_chi_tiet }"
                    placeholder="Số nhà, tên đường, tòa nhà..."
                  />
                  <div v-if="errors.dia_chi_chi_tiet" class="invalid-feedback">{{ errors.dia_chi_chi_tiet }}</div>
                </div>
                <div class="col-12">
                  <label class="form-label" for="ghiChu">Ghi chú đơn hàng</label>
                  <textarea
                    id="ghiChu"
                    v-model="form.ghi_chu"
                    class="form-control"
                    rows="2"
                    placeholder="Giao giờ hành chính, gọi trước khi giao..."
                  ></textarea>
                </div>
              </div>
            </section>

            <section class="form-section">
              <h2 class="section-heading">
                <span class="section-num">3</span>
                Phương thức thanh toán
              </h2>
              <div class="payment-options">
                <label
                  v-for="opt in paymentMethods"
                  :key="opt.value"
                  class="payment-option"
                  :class="{ active: form.phuong_thuc_thanh_toan === opt.value }"
                >
                  <input
                    v-model="form.phuong_thuc_thanh_toan"
                    type="radio"
                    :value="opt.value"
                    class="d-none"
                  />
                  <i :class="opt.icon"></i>
                  <div>
                    <span class="payment-label">{{ opt.label }}</span>
                    <small class="payment-desc">{{ opt.desc }}</small>
                  </div>
                </label>
              </div>
            </section>

            <div class="form-actions d-flex flex-wrap gap-2">
              <RouterLink :to="{ name: 'cart' }" class="btn btn-outline-secondary rounded-pill px-4">
                <i class="bi bi-arrow-left me-1"></i> Quay lại giỏ hàng
              </RouterLink>
              <button
                type="submit"
                class="btn btn-accent rounded-pill px-4 fw-bold flex-grow-1"
                :disabled="submitting"
              >
                <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                {{ submitLabel }}
                <i v-if="!submitting" class="bi bi-check2-circle ms-1"></i>
              </button>
            </div>
          </form>
        </div>

        <!-- Tóm tắt đơn -->
        <div class="col-lg-5">
          <div class="order-summary rounded-4 sticky-summary">
            <h5 class="summary-title">Đơn hàng của bạn</h5>

            <div class="order-items">
              <div
                v-for="item in gioHang.chi_tiet_gio_hang"
                :key="item.ma_ct_gio_hang"
                class="order-item"
              >
                <AppImage
                  :src="item.duong_dan_anh"
                  :alt="item.ten_san_pham"
                  img-class="order-thumb"
                />
                <div class="order-item-info">
                  <p class="order-item-name mb-0">{{ item.ten_san_pham }}</p>
                  <small class="text-muted">
                    {{ item.mau_sac }} / {{ item.kich_co }} × {{ item.so_luong }}
                  </small>
                </div>
                <span class="order-item-price">{{ formatPrice(item.don_gia * item.so_luong) }}</span>
              </div>
            </div>

            <hr class="summary-divider" />

            <div class="summary-row">
              <span>Tạm tính</span>
              <span>{{ formatPrice(gioHang.tam_tinh) }}</span>
            </div>
            <div class="summary-row">
              <span>Phí vận chuyển</span>
              <span>
                {{ gioHang.phi_van_chuyen === 0 ? 'Miễn phí' : formatPrice(gioHang.phi_van_chuyen) }}
              </span>
            </div>
            <div class="summary-row summary-total">
              <span>Tổng thanh toán</span>
              <span class="total-price">{{ formatPrice(gioHang.tong_thanh_toan) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchGioHang, getCartSnapshot } from '../services/cartService'
import { initiateCheckout } from '../services/paymentService'
import { formatPrice } from '../utils/productHelpers'
import AppImage from '../components/AppImage.vue'

const router = useRouter()

const tinhThanhList = [
  'Hà Nội',
  'TP. Hồ Chí Minh',
  'Đà Nẵng',
  'Hải Phòng',
  'Cần Thơ',
  'Bình Dương',
  'Đồng Nai',
  'Khánh Hòa',
]

const paymentMethods = [
  {
    value: 'cod',
    label: 'Thanh toán khi nhận hàng (COD)',
    desc: 'Trả tiền mặt khi nhận hàng',
    icon: 'bi bi-cash-coin',
  },
  {
    value: 'bank',
    label: 'Chuyển khoản ngân hàng',
    desc: 'Chuyển khoản trước khi giao',
    icon: 'bi bi-bank',
  },
  {
    value: 'momo',
    label: 'Ví MoMo',
    desc: 'Thanh toán qua ví điện tử',
    icon: 'bi bi-phone',
  },
]

const cartLoading = ref(true)
const submitting = ref(false)
const gioHang = ref({
  chi_tiet_gio_hang: [],
  tam_tinh: 0,
  phi_van_chuyen: 0,
  tong_thanh_toan: 0,
})

const form = reactive({
  ho_ten: '',
  so_dien_thoai: '',
  email: '',
  tinh_thanh: '',
  quan_huyen: '',
  phuong_xa: '',
  dia_chi_chi_tiet: '',
  ghi_chu: '',
  phuong_thuc_thanh_toan: 'cod',
})

const errors = reactive({})

const submitLabel = computed(() => {
  if (submitting.value) return 'Đang xử lý...'
  if (form.phuong_thuc_thanh_toan === 'cod') return 'Xác nhận đặt hàng'
  return 'Thanh toán online'
})

function validateForm() {
  Object.keys(errors).forEach((k) => delete errors[k])

  if (!form.ho_ten.trim()) errors.ho_ten = 'Vui lòng nhập họ tên'
  if (!form.so_dien_thoai.trim()) {
    errors.so_dien_thoai = 'Vui lòng nhập số điện thoại'
  } else if (!/^0\d{9,10}$/.test(form.so_dien_thoai.replace(/\s/g, ''))) {
    errors.so_dien_thoai = 'Số điện thoại không hợp lệ'
  }
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Email không hợp lệ'
  }
  if (!form.tinh_thanh) errors.tinh_thanh = 'Vui lòng chọn tỉnh/thành'
  if (!form.quan_huyen.trim()) errors.quan_huyen = 'Vui lòng nhập quận/huyện'
  if (!form.phuong_xa.trim()) errors.phuong_xa = 'Vui lòng nhập phường/xã'
  if (!form.dia_chi_chi_tiet.trim()) errors.dia_chi_chi_tiet = 'Vui lòng nhập địa chỉ chi tiết'

  return Object.keys(errors).length === 0
}

async function handleSubmit() {
  if (!validateForm()) return
  submitting.value = true
  try {
    const response = await initiateCheckout(form)
    if (!response.ok) {
      router.push({ name: 'cart' })
      return
    }
    if (response.flow === 'cod') {
      router.push({ name: 'payment-success' })
    } else {
      router.push(response.route)
    }
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await fetchGioHang()
  gioHang.value = { ...getCartSnapshot() }
  cartLoading.value = false
})
</script>

<style scoped>
.step {
  text-decoration: none;
  color: inherit;
}

.step.done:hover {
  color: #bbf7d0;
}

.checkout-form {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 1.5rem;
}

.form-section + .form-section {
  margin-top: 1.75rem;
  padding-top: 1.75rem;
  border-top: 1px solid #e2e8f0;
}

.section-heading {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  font-size: 1rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 1rem;
}

.section-num {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--sportpro-accent);
  color: #fff;
  font-size: 0.85rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.form-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #334155;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.payment-option {
  display: flex;
  align-items: flex-start;
  gap: 0.85rem;
  padding: 0.85rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}

.payment-option i {
  font-size: 1.35rem;
  color: var(--sportpro-accent);
  margin-top: 0.1rem;
}

.payment-option.active {
  border-color: var(--sportpro-accent);
  background: rgba(249, 115, 22, 0.05);
}

.payment-label {
  display: block;
  font-weight: 700;
  font-size: 0.9rem;
  color: #0f172a;
}

.payment-desc {
  color: #94a3b8;
  font-size: 0.8rem;
}

.form-actions {
  margin-top: 1.75rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.order-summary {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 1.5rem;
}

.summary-title {
  font-weight: 800;
  margin-bottom: 1rem;
  color: #0f172a;
}

.order-items {
  max-height: 280px;
  overflow-y: auto;
  margin-bottom: 0.5rem;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.65rem 0;
}

.order-item + .order-item {
  border-top: 1px solid #f1f5f9;
}

.order-thumb {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid #e2e8f0;
}

.order-item-info {
  flex: 1;
  min-width: 0;
}

.order-item-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: #334155;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.order-item-price {
  font-size: 0.85rem;
  font-weight: 700;
  color: #dc2626;
  white-space: nowrap;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  color: #64748b;
  margin-bottom: 0.5rem;
}

.summary-divider {
  margin: 0.75rem 0;
  opacity: 0.15;
}

.summary-total {
  font-weight: 700;
  color: #0f172a;
  font-size: 1rem;
  margin-bottom: 0;
}

.total-price {
  font-size: 1.2rem;
  font-weight: 800;
  color: #dc2626;
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

@media (max-width: 767.98px) {
  .checkout-form,
  .order-summary {
    padding: 1rem;
  }

  .form-actions {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
  }

  .form-actions .btn {
    width: 100%;
  }
}
</style>
