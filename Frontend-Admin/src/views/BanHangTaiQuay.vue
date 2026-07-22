<template>
  <div class="container mt-4">
    <PageHeader
      title="Bán tại quầy"
      subtitle="Quét mã sản phẩm và tạo đơn nhanh tại cửa hàng"
    >
      <template #actions>
        <div class="seller-chip">
          <i class="bi bi-person-circle"></i>
          <span>{{ sellerName }}</span>
        </div>
        <button class="btn btn-primary" type="button" @click="createCart">
          <i class="bi bi-plus-lg me-1"></i> Tạo đơn hàng
        </button>
      </template>
    </PageHeader>

    <div class="order-tabs mb-3">
      <button
        v-for="cart in carts"
        :key="cart.id"
        class="order-tab"
        :class="{ active: cart.id === activeCartId }"
        @click="activeCartId = cart.id"
      >
        <span>Đơn {{ cart.id }} · {{ cart.label }}</span>
        <span class="tab-count" v-if="cart.items.length">{{ cart.items.length }}</span>
        <i
          v-if="carts.length > 1"
          class="bi bi-x tab-close"
          @click.stop="closeCart(cart.id)"
          title="Đóng đơn"
        ></i>
      </button>
    </div>

    <div class="row g-3">
      <div class="col-lg-4">
        <div class="card pos-panel h-100">
          <div class="card-body">
            <div class="panel-title">
              <i class="bi bi-upc-scan"></i>
              <span>Quét / thêm sản phẩm</span>
            </div>

            <div class="mb-3">
              <label class="form-label">Mã vạch / SKU</label>
              <input
                type="text"
                v-model="barcodeInput"
                @keyup.enter="scanBarcode"
                ref="barcodeInputRef"
                class="form-control form-control-lg scan-input"
                placeholder="Quét hoặc nhập mã rồi Enter"
                autofocus
              />
              <div class="form-text">Nhập mã biến thể (SKU) hoặc ID biến thể.</div>
            </div>

            <div class="mb-3">
              <label class="form-label">Số lượng</label>
              <div class="qty-control">
                <button type="button" class="btn btn-ghost" @click="tempForm.soLuong = Math.max(1, Number(tempForm.soLuong) - 1)">−</button>
                <input type="number" v-model="tempForm.soLuong" class="form-control text-center" min="1" />
                <button type="button" class="btn btn-ghost" @click="tempForm.soLuong = Number(tempForm.soLuong) + 1">+</button>
              </div>
            </div>

            <div v-if="scanning" class="scanning-box mb-3">
              <div class="spinner-border spinner-border-sm text-primary me-2"></div>
              Đang tìm sản phẩm...
            </div>

            <div v-else-if="scannedProduct" class="scanned-box mb-3">
              <div class="fw-bold">{{ scannedProduct.tenSanPham || 'Sản phẩm' }}</div>
              <div class="text-muted small mt-1">
                {{ scannedProduct.mauSac }} · Size {{ scannedProduct.kichCo }}
              </div>
              <div class="d-flex justify-content-between align-items-center mt-2">
                <strong class="text-danger">{{ formatPrice(scannedProduct.giaBan || scannedProduct.giaKhuyenMai || scannedProduct.GiBan) }}</strong>
                <span class="badge bg-light text-dark border">Tồn: {{ scannedProduct.soLuongTon }}</span>
              </div>
            </div>

            <button @click="addItem" class="btn btn-primary btn-lg w-100 fw-bold add-btn">
              <i class="bi bi-plus-lg me-1"></i> Thêm sản phẩm
            </button>
          </div>
        </div>
      </div>

      <div class="col-lg-8">
        <div class="card pos-panel h-100">
          <div class="card-body d-flex flex-column">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
              <div class="panel-title mb-0">
                <i class="bi bi-receipt"></i>
                <span>Chi tiết đơn hàng</span>
              </div>
              <span class="stat-mini"><i class="bi bi-box-seam"></i> {{ activeCart.items.length }} sản phẩm</span>
            </div>

            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <label class="form-label">Khách hàng (tuỳ chọn)</label>
                <input type="text" v-model="activeCart.tenNguoiNhan" class="form-control" placeholder="Tên khách hàng" />
              </div>
              <div class="col-md-6">
                <label class="form-label">Số điện thoại</label>
                <input type="text" v-model="activeCart.soDienThoai" class="form-control" placeholder="SĐT khách hàng" />
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Mã giảm giá</label>
              <div class="input-group">
                <input
                  type="text"
                  v-model="activeCart.voucherCode"
                  class="form-control"
                  placeholder="Nhập mã voucher"
                  :disabled="!!activeCart.voucher"
                />
                <button
                  v-if="!activeCart.voucher"
                  @click="applyVoucher"
                  class="btn btn-outline-primary"
                  type="button"
                  :disabled="applyingVoucher || activeCart.items.length === 0"
                >
                  {{ applyingVoucher ? 'Đang kiểm tra...' : 'Áp dụng' }}
                </button>
                <button v-else @click="removeVoucher" class="btn btn-outline-danger" type="button">
                  Hủy
                </button>
              </div>
              <div v-if="activeCart.voucher" class="form-text text-success mt-1">
                <i class="bi bi-patch-check-fill"></i>
                Đã áp dụng <strong>{{ activeCart.voucher.maCode }}</strong> · Giảm {{ activeCart.voucher.phanTramGiam }}%
              </div>
            </div>

            <EmptyState
              v-if="activeCart.items.length === 0"
              icon="bi bi-cart-x"
              title="Chưa có sản phẩm trong đơn"
              description="Quét mã SKU bên trái hoặc tạo đơn mới để bắt đầu"
            />

            <div v-else class="table-responsive mb-3 flex-grow-1">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Biến thể</th>
                    <th style="width: 110px">SL</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in activeCart.items" :key="item.maChiTietSp + '-' + index" class="item-row">
                    <td class="fw-bold">{{ item.tenSanPham }}</td>
                    <td><span class="badge bg-light text-dark border">{{ item.mauSac }} / {{ item.kichCo }}</span></td>
                    <td>
                      <input
                        type="number"
                        v-model="item.soLuong"
                        class="form-control form-control-sm"
                        min="1"
                        @change="updateItemTotal(index)"
                      />
                    </td>
                    <td>{{ formatPrice(item.donGia) }}</td>
                    <td class="fw-bold text-danger">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                    <td>
                      <button @click="removeItem(index)" class="btn btn-sm btn-outline-danger" title="Xóa">
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="checkout-bar mt-auto">
              <div class="summary-lines">
                <div class="d-flex justify-content-between">
                  <span>Tạm tính</span>
                  <strong>{{ formatPrice(subTotal) }}</strong>
                </div>
                <div v-if="activeCart.voucher" class="d-flex justify-content-between text-success">
                  <span>Khuyến mãi ({{ activeCart.voucher.phanTramGiam }}%)</span>
                  <strong>- {{ formatPrice(discountAmount) }}</strong>
                </div>
                <div class="d-flex justify-content-between total-line">
                  <span>Tổng cộng</span>
                  <strong>{{ formatPrice(finalTotal) }}</strong>
                </div>
              </div>
              <button
                @click="createPosOrder"
                class="btn btn-warning btn-lg pay-btn"
                :disabled="activeCart.items.length === 0 || loading"
              >
                <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                {{ loading ? 'Đang xử lý...' : 'Thanh toán' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <InvoiceModal
      modal-id="posInvoiceModal"
      :order="invoiceOrder"
      :nhan-vien-ten="sellerName"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { Modal } from 'bootstrap'
import { API_URL } from '@/config.js'
import PageHeader from '@/components/PageHeader.vue'
import EmptyState from '@/components/EmptyState.vue'
import InvoiceModal from '@/components/InvoiceModal.vue'
import { useToast } from '@/composables/useToast.js'

const { success, error, info } = useToast()

let cartSeed = 1
const createEmptyCart = () => ({
  id: cartSeed++,
  label: `HD-${String(cartSeed - 1).padStart(3, '0')}`,
  tenNguoiNhan: '',
  soDienThoai: '',
  items: [],
  voucher: null,
  voucherCode: ''
})

const carts = ref([createEmptyCart()])
const activeCartId = ref(carts.value[0].id)
const loading = ref(false)
const scanning = ref(false)
const barcodeInput = ref('')
const barcodeInputRef = ref(null)
const scannedProduct = ref(null)
const tempForm = ref({ soLuong: 1 })
const applyingVoucher = ref(false)
const invoiceOrder = ref(null)

const activeCart = computed(() => carts.value.find((c) => c.id === activeCartId.value) || carts.value[0])

const sellerName = computed(() => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    return user?.hoTen || user?.email || 'Nhân viên'
  } catch {
    return 'Nhân viên'
  }
})

const formatPrice = (price) =>
  price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'

const subTotal = computed(() =>
  activeCart.value.items.reduce((sum, item) => sum + item.soLuong * item.donGia, 0)
)

const discountAmount = computed(() => {
  if (!activeCart.value.voucher) return 0
  return (subTotal.value * activeCart.value.voucher.phanTramGiam) / 100
})

const finalTotal = computed(() => Math.max(0, subTotal.value - discountAmount.value))

const createCart = () => {
  const cart = createEmptyCart()
  carts.value.push(cart)
  activeCartId.value = cart.id
  info(`Đã tạo ${cart.label}`)
}

const closeCart = (id) => {
  if (carts.value.length === 1) {
    info('Cần giữ ít nhất 1 đơn')
    return
  }
  carts.value = carts.value.filter((c) => c.id !== id)
  if (activeCartId.value === id) activeCartId.value = carts.value[0].id
}

const applyVoucher = async () => {
  if (!activeCart.value.voucherCode.trim()) return
  applyingVoucher.value = true
  try {
    const res = await axios.get(`${API_URL}/api/khuyen-mai`)
    const vouchers = res.data.data || res.data || []
    const voucher = vouchers.find(
      (v) => v.maCode.toUpperCase() === activeCart.value.voucherCode.trim().toUpperCase()
    )

    if (!voucher) {
      error('Mã khuyến mãi không tồn tại')
      activeCart.value.voucher = null
      return
    }
    if (new Date(voucher.ngayKetThuc) < new Date() || new Date(voucher.ngayBatDau) > new Date()) {
      error('Mã khuyến mãi hết hạn hoặc chưa hiệu lực')
      return
    }
    if (voucher.soLuongDung != null && voucher.soLuongDung <= 0) {
      error('Mã khuyến mãi đã hết lượt sử dụng')
      return
    }
    activeCart.value.voucher = voucher
    success(`Áp dụng mã giảm ${voucher.phanTramGiam}% thành công`)
  } catch {
    error('Lỗi kiểm tra mã khuyến mãi')
  } finally {
    applyingVoucher.value = false
  }
}

const removeVoucher = () => {
  activeCart.value.voucher = null
  activeCart.value.voucherCode = ''
  info('Đã hủy mã giảm giá')
}

const scanBarcode = async () => {
  if (!barcodeInput.value) return
  const input = barcodeInput.value.trim()
  scanning.value = true

  try {
    let data = null
    try {
      const skuRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/sku/${input}`)
      data = skuRes.data.data || skuRes.data
    } catch {
      if (!isNaN(input)) {
        const idRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/${input}`)
        data = idRes.data.data || idRes.data
      }
    }

    if (data && data.maChiTietSp) {
      scannedProduct.value = data
      try {
        const prodRes = await axios.get(`${API_URL}/api/san-pham/${data.maSanPham}`)
        const prodData = prodRes.data.data || prodRes.data
        if (prodData) {
          scannedProduct.value.tenSanPham = prodData.tenSanPham
          scannedProduct.value.giaBan = prodData.giaKhuyenMai || prodData.giaGoc || 0
        }
      } catch {}
    } else {
      error('Không tìm thấy biến thể: ' + input)
      scannedProduct.value = null
    }
  } catch {
    error('Không tìm thấy biến thể: ' + input)
    scannedProduct.value = null
  } finally {
    scanning.value = false
  }
}

const addItem = async () => {
  if (!scannedProduct.value) {
    if (barcodeInput.value) {
      await scanBarcode()
      if (!scannedProduct.value) return
    } else {
      error('Vui lòng quét mã sản phẩm trước')
      return
    }
  }

  if (tempForm.value.soLuong > scannedProduct.value.soLuongTon) {
    error(`Tồn kho không đủ! Chỉ còn ${scannedProduct.value.soLuongTon}`)
    return
  }

  const existingIndex = activeCart.value.items.findIndex(
    (item) => item.maChiTietSp === scannedProduct.value.maChiTietSp
  )

  if (existingIndex >= 0) {
    const newQty = activeCart.value.items[existingIndex].soLuong + parseInt(tempForm.value.soLuong)
    if (newQty > scannedProduct.value.soLuongTon) {
      error('Số lượng cộng dồn vượt quá tồn kho')
      return
    }
    activeCart.value.items[existingIndex].soLuong = newQty
  } else {
    activeCart.value.items.push({
      maChiTietSp: scannedProduct.value.maChiTietSp,
      tenSanPham: scannedProduct.value.tenSanPham || 'Sản phẩm ID ' + scannedProduct.value.maSanPham,
      mauSac: scannedProduct.value.mauSac,
      kichCo: scannedProduct.value.kichCo,
      soLuong: parseInt(tempForm.value.soLuong),
      donGia: scannedProduct.value.giaBan || scannedProduct.value.giaKhuyenMai || scannedProduct.value.GiBan || 0,
      tonKho: scannedProduct.value.soLuongTon
    })
  }

  scannedProduct.value = null
  barcodeInput.value = ''
  tempForm.value.soLuong = 1
  success('Đã thêm sản phẩm vào đơn')
  nextTick(() => barcodeInputRef.value?.focus())
}

const updateItemTotal = (index) => {
  const item = activeCart.value.items[index]
  if (item.soLuong > item.tonKho) {
    error('Vượt quá số lượng tồn kho')
    item.soLuong = item.tonKho
  }
}

const removeItem = (index) => {
  activeCart.value.items.splice(index, 1)
}

const createPosOrder = async () => {
  if (activeCart.value.items.length === 0) {
    error('Vui lòng thêm sản phẩm vào đơn')
    return
  }

  loading.value = true
  try {
    const user = JSON.parse(localStorage.getItem('user'))
    const payload = {
      maNhanVien: user ? user.maNguoiDung : 1,
      tenNguoiNhan: activeCart.value.tenNguoiNhan || 'Khách Mua Tại Quầy',
      soDienThoai: activeCart.value.soDienThoai || '0000000000',
      loaiDonHang: 'TaiQuay',
      phuongThucThanhToan: 'TienMat',
      trangThai: 'HoanThanh',
      maKhuyenMai: activeCart.value.voucher ? activeCart.value.voucher.maKhuyenMai : null,
      items: activeCart.value.items.map((item) => ({
        maChiTietSp: item.maChiTietSp,
        soLuong: item.soLuong,
        donGia: item.donGia
      }))
    }

    const response = await axios.post(`${API_URL}/api/don-hang/pos`, payload)
    const created = response.data.data
    success('Thanh toán thành công! Mã đơn: ' + (created?.maDonHang || ''))

    // Reset current cart after pay
    const idx = carts.value.findIndex((c) => c.id === activeCartId.value)
    const fresh = createEmptyCart()
    fresh.id = activeCart.value.id
    fresh.label = activeCart.value.label
    carts.value[idx] = fresh

    // Invoice preview is optional; never treat its failure as payment failure.
    if (created?.maDonHang) {
      try {
        const detailRes = await axios.get(`${API_URL}/api/don-hang/${created.maDonHang}`)
        invoiceOrder.value = detailRes.data.data || detailRes.data || created
      } catch {
        invoiceOrder.value = created
      }
      await nextTick()
      const modalEl = document.getElementById('posInvoiceModal')
      if (modalEl) {
        Modal.getOrCreateInstance(modalEl).show()
      }
    }
  } catch (e) {
    error(e.response?.data?.message || e.message || 'Thanh toán thất bại')
  } finally {
    loading.value = false
  }
}

onMounted(() => barcodeInputRef.value?.focus())
</script>

<style scoped>
.seller-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.45rem 0.85rem;
  border-radius: 999px;
  background: #fff;
  border: 1px solid var(--sp-border);
  color: #475569;
  font-weight: 650;
  font-size: 0.9rem;
}

.order-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.order-tab {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.55rem 0.9rem;
  border-radius: 12px;
  border: 1px solid var(--sp-border);
  background: #fff;
  color: #64748b;
  font-weight: 650;
  transition: all 0.2s ease;
}

.order-tab:hover {
  border-color: rgba(29, 78, 216, 0.3);
  color: var(--sp-blue);
}

.order-tab.active {
  background: rgba(29, 78, 216, 0.1);
  border-color: rgba(29, 78, 216, 0.22);
  color: var(--sp-blue);
  box-shadow: 0 6px 16px rgba(29, 78, 216, 0.08);
}

.tab-count {
  background: rgba(15, 23, 42, 0.06);
  border-radius: 999px;
  padding: 0.05rem 0.4rem;
  font-size: 0.75rem;
}

.tab-close {
  opacity: 0.55;
}

.tab-close:hover {
  opacity: 1;
  color: #dc2626;
}

.pos-panel {
  border: 1px solid var(--sp-border) !important;
  box-shadow: var(--sp-shadow-sm) !important;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 0.55rem;
  font-weight: 750;
  color: #0f172a;
  margin-bottom: 1rem;
}

.panel-title i {
  color: var(--sp-blue);
}

.scan-input {
  border-color: rgba(29, 78, 216, 0.35) !important;
  font-weight: 600;
}

.qty-control {
  display: grid;
  grid-template-columns: 42px 1fr 42px;
  gap: 0.45rem;
}

.scanning-box,
.scanned-box {
  border-radius: 14px;
  padding: 0.9rem 1rem;
  border: 1px solid rgba(29, 78, 216, 0.18);
}

.scanning-box {
  display: flex;
  align-items: center;
  background: #f8fbff;
  color: #475569;
  font-weight: 600;
}

.scanned-box {
  background: linear-gradient(135deg, #eff6ff, #fff7ed);
  animation: pop 0.25s ease;
}

.add-btn {
  box-shadow: 0 10px 22px rgba(29, 78, 216, 0.22);
}

.item-row {
  animation: pop 0.2s ease;
}

.checkout-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  justify-content: space-between;
  padding-top: 1rem;
  border-top: 1px solid var(--sp-border);
}

.summary-lines {
  flex: 1;
  min-width: 220px;
  display: grid;
  gap: 0.35rem;
  color: #64748b;
}

.total-line {
  color: #0f172a;
  font-size: 1.15rem;
  margin-top: 0.25rem;
}

.total-line strong {
  color: var(--sp-orange-deep);
}

.pay-btn {
  min-width: 180px;
  font-weight: 780 !important;
}

@keyframes pop {
  from {
    opacity: 0;
    transform: translateY(6px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
