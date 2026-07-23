<template>
  <div class="invoice-paper" ref="paperRef">
    <div class="invoice-header">
      <div class="invoice-brand">
        <img src="/logo.png" alt="SportPro" crossorigin="anonymous" />
        <div>
          <h1>SportPro</h1>
          <p>Cửa hàng thể thao chính hãng</p>
          <p>Hotline: 1900 6868 · sportpro.com</p>
        </div>
      </div>
      <div class="invoice-meta">
        <strong>{{ invoiceNo }}</strong>
        <div>Ngày lập: {{ formattedDate }}</div>
        <div>
          <span class="badge-type" :class="pos ? 'badge-pos' : 'badge-online'">
            {{ pos ? 'Bán tại quầy' : 'Đơn online' }}
          </span>
        </div>
      </div>
    </div>

    <div class="invoice-grid">
      <div class="invoice-box">
        <h3>Thông tin khách hàng</h3>
        <p><strong>{{ customer.name }}</strong></p>
        <p v-if="customer.phone">SĐT: {{ customer.phone }}</p>
        <p v-if="!pos">Địa chỉ: {{ customer.address }}</p>
      </div>
      <div class="invoice-box">
        <h3>Thông tin thanh toán</h3>
        <p>Phương thức: <strong>{{ order?.phuongThucTt || 'Tiền mặt' }}</strong></p>
        <p>Trạng thái: <strong>{{ order?.trangThai || 'Đã giao hàng' }}</strong></p>
        <p v-if="nhanVienTen">Nhân viên: <strong>{{ nhanVienTen }}</strong></p>
        <p v-else-if="order?.maNhanVien">Mã NV: <strong>#{{ order.maNhanVien }}</strong></p>
      </div>
    </div>

    <table>
      <thead>
        <tr>
          <th style="width: 36px">#</th>
          <th>Sản phẩm</th>
          <th class="text-center" style="width: 56px">SL</th>
          <th class="text-end" style="width: 110px">Đơn giá</th>
          <th class="text-end" style="width: 120px">Thành tiền</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in order?.chiTietList || []" :key="item.maCtDonHang || index">
          <td class="text-center">{{ index + 1 }}</td>
          <td>
            <strong>{{ item.sanPham?.tenSanPham || 'Sản phẩm' }}</strong>
            <div class="item-variant">
              {{ item.chiTietSanPham?.mauSac || '—' }} · Size {{ item.chiTietSanPham?.kichCo || '—' }}
              <span v-if="item.chiTietSanPham?.maVachSku"> · SKU: {{ item.chiTietSanPham.maVachSku }}</span>
            </div>
          </td>
          <td class="text-center">{{ item.soLuong }}</td>
          <td class="text-end">{{ formatPrice(item.donGia) }}</td>
          <td class="text-end"><strong>{{ formatPrice(item.soLuong * item.donGia) }}</strong></td>
        </tr>
      </tbody>
    </table>

    <div class="invoice-totals">
      <div class="totals-row">
        <span>Tạm tính</span>
        <strong>{{ formatPrice(subtotal) }}</strong>
      </div>
      <div class="totals-row" v-if="discount > 0">
        <span>Giảm giá</span>
        <strong class="text-success">- {{ formatPrice(discount) }}</strong>
      </div>
      <div class="totals-row" v-if="!pos && Number(order?.phiShip) > 0">
        <span>Phí vận chuyển</span>
        <strong>{{ formatPrice(order.phiShip) }}</strong>
      </div>
      <div class="totals-row grand">
        <span>Tổng thanh toán</span>
        <strong>{{ formatPrice(order?.tongTien) }}</strong>
      </div>
    </div>

    <div class="invoice-footer">
      <p>Cảm ơn quý khách đã mua sắm tại SportPro!</p>
      <p>Hóa đơn được tạo tự động từ hệ thống · Mã đơn hàng #{{ order?.maDonHang }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import {
  formatInvoicePrice,
  formatInvoiceDate,
  invoiceNumber,
  isPosOrder,
  parseCustomerInfo,
  calcInvoiceSubtotal,
  calcInvoiceDiscount
} from '@/composables/useInvoice.js'

const props = defineProps({
  order: { type: Object, default: null },
  nhanVienTen: { type: String, default: '' }
})

const paperRef = ref(null)

const getPaperElement = () => paperRef.value

const formatPrice = formatInvoicePrice
const pos = computed(() => isPosOrder(props.order))
const customer = computed(() => parseCustomerInfo(props.order))
const invoiceNo = computed(() => invoiceNumber(props.order?.maDonHang))
const formattedDate = computed(() => formatInvoiceDate(props.order?.ngayDat))
const subtotal = computed(() => calcInvoiceSubtotal(props.order))
const discount = computed(() => calcInvoiceDiscount(props.order))

defineExpose({ getPaperElement })
</script>

<style scoped>
.invoice-paper {
  background: #fff;
  border: 1px solid var(--sp-border, #e2e8f0);
  border-radius: 16px;
  padding: 1.35rem 1.5rem;
  color: #0f172a;
}

.invoice-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.1rem;
  flex-wrap: wrap;
}

.invoice-brand {
  display: flex;
  gap: 0.85rem;
  align-items: center;
}

.invoice-brand img {
  width: 54px;
  height: 54px;
  object-fit: contain;
}

.invoice-brand h1 {
  margin: 0;
  font-size: 1.35rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.invoice-brand p {
  margin: 0.15rem 0 0;
  color: #64748b;
  font-size: 0.82rem;
}

.invoice-meta {
  text-align: right;
  font-size: 0.86rem;
  color: #475569;
}

.invoice-meta strong {
  display: block;
  color: var(--sp-blue, #1d4ed8);
  font-size: 1.15rem;
  margin-bottom: 0.35rem;
}

.invoice-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.invoice-box {
  border: 1px solid var(--sp-border, #e2e8f0);
  border-radius: 12px;
  padding: 0.85rem 0.95rem;
  background: #f8fafc;
}

.invoice-box h3 {
  margin: 0 0 0.55rem;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #64748b;
  font-weight: 700;
}

.invoice-box p {
  margin: 0 0 0.2rem;
  font-size: 0.86rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.86rem;
}

th,
td {
  border: 1px solid #e2e8f0;
  padding: 0.55rem 0.65rem;
}

th {
  background: #f1f5f9;
  font-weight: 700;
}

.item-variant {
  color: #64748b;
  font-size: 0.78rem;
  margin-top: 0.15rem;
}

.text-end {
  text-align: right;
}

.text-center {
  text-align: center;
}

.text-success {
  color: #059669;
}

.invoice-totals {
  margin-left: auto;
  width: min(320px, 100%);
  margin-top: 0.75rem;
}

.invoice-totals .totals-row {
  display: flex;
  justify-content: space-between;
  padding: 0.3rem 0;
  color: #475569;
  font-size: 0.88rem;
}

.invoice-totals .grand {
  border-top: 2px solid #0f172a;
  margin-top: 0.35rem;
  padding-top: 0.65rem;
  font-size: 1rem;
  color: #0f172a;
}

.invoice-totals .grand strong {
  color: var(--sp-orange-deep, #ea580c);
}

.invoice-footer {
  margin-top: 1.1rem;
  padding-top: 0.85rem;
  border-top: 1px dashed #cbd5e1;
  text-align: center;
  color: #64748b;
  font-size: 0.8rem;
}

.invoice-footer p {
  margin: 0.15rem 0;
}

.badge-type {
  display: inline-block;
  padding: 0.2rem 0.65rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
}

.badge-pos {
  background: #fef3c7;
  color: #92400e;
}

.badge-online {
  background: #dbeafe;
  color: #1d4ed8;
}

@media (max-width: 640px) {
  .invoice-grid {
    grid-template-columns: 1fr;
  }

  .invoice-meta {
    text-align: left;
  }
}
</style>
