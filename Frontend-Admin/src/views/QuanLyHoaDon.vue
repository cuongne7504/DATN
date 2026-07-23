<template>
  <div class="container mt-4">
    <PageHeader
      title="Quản lý Hóa đơn"
      subtitle="Xem, in và xuất PDF hóa đơn bán tại quầy & đơn online đã hoàn thành"
    >
      <template #actions>
        <div class="search-box">
          <i class="bi bi-search"></i>
          <input
            v-model="searchQuery"
            type="text"
            class="form-control"
            placeholder="Tìm mã HĐ, mã ĐH, khách hàng..."
          />
        </div>
      </template>
    </PageHeader>

    <div class="row g-3 mb-4">
      <div class="col-md-4 col-sm-6">
        <div class="sp-kpi-card">
          <div class="kpi-label">Tổng hóa đơn</div>
          <div class="kpi-value">{{ invoiceOrders.length }}</div>
        </div>
      </div>
      <div class="col-md-4 col-sm-6">
        <div class="sp-kpi-card">
          <div class="kpi-label">Bán tại quầy</div>
          <div class="kpi-value text-warning">{{ countByType('pos') }}</div>
        </div>
      </div>
      <div class="col-md-4 col-sm-6">
        <div class="sp-kpi-card">
          <div class="kpi-label">Đơn online</div>
          <div class="kpi-value text-primary">{{ countByType('online') }}</div>
        </div>
      </div>
    </div>

    <div class="card invoices-card">
      <div class="card-body p-4">
        <div class="filter-tabs mb-3">
          <button
            v-for="tab in typeTabs"
            :key="tab.value"
            class="filter-tab"
            :class="{ active: currentType === tab.value }"
            @click="currentType = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>

        <div v-if="loading">
          <SkeletonLoader variant="table" :rows="7" />
        </div>

        <EmptyState
          v-else-if="filteredInvoices.length === 0"
          icon="bi bi-receipt-cutoff"
          title="Chưa có hóa đơn"
          description="Hóa đơn sẽ xuất hiện khi có đơn hàng hoàn thành hoặc bán tại quầy"
        />

        <div v-else class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th>Mã HĐ</th>
                <th>Mã ĐH</th>
                <th>Khách hàng</th>
                <th>Ngày lập</th>
                <th>Loại</th>
                <th>Tổng tiền</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in filteredInvoices" :key="order.maDonHang">
                <td class="fw-bold text-primary">{{ invoiceNumber(order.maDonHang) }}</td>
                <td>#{{ order.maDonHang }}</td>
                <td>
                  <div class="fw-semibold">{{ customerName(order) }}</div>
                  <small class="text-muted">{{ customerPhone(order) }}</small>
                </td>
                <td>{{ formatDate(order.ngayDat) }}</td>
                <td>
                  <span class="badge" :class="isPosOrder(order) ? 'bg-warning text-dark' : 'bg-primary'">
                    {{ isPosOrder(order) ? 'Tại quầy' : 'Online' }}
                  </span>
                </td>
                <td class="fw-bold text-danger">{{ formatPrice(order.tongTien) }}</td>
                <td>
                  <div class="action-group">
                    <button class="btn btn-sm btn-outline-primary" @click="openInvoice(order)">
                      <i class="bi bi-eye me-1"></i> Xem
                    </button>
                    <button class="btn btn-sm btn-outline-secondary" @click="quickPrint(order)">
                      <i class="bi bi-printer"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="quickPdf(order)">
                      <i class="bi bi-file-earmark-pdf"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <InvoiceModal
      modal-id="invoiceManageModal"
      :order="selectedOrder"
      :nhan-vien-ten="selectedNhanVienTen"
    />

    <!-- Hidden render area for quick print/pdf without opening modal -->
    <div class="offscreen-render" aria-hidden="true">
      <HoaDonTemplate
        v-if="renderOrder"
        ref="hiddenTemplateRef"
        :order="renderOrder"
        :nhan-vien-ten="renderNhanVienTen"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { Modal } from 'bootstrap'
import { API_URL } from '@/config.js'
import PageHeader from '@/components/PageHeader.vue'
import EmptyState from '@/components/EmptyState.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'
import InvoiceModal from '@/components/InvoiceModal.vue'
import HoaDonTemplate from '@/components/HoaDonTemplate.vue'
import { useToast } from '@/composables/useToast.js'
import {
  formatInvoicePrice,
  formatInvoiceDate,
  invoiceNumber,
  isPosOrder,
  isInvoiceable,
  printInvoiceElement,
  exportInvoicePdf,
  resolveInvoicePaperElement
} from '@/composables/useInvoice.js'

const { success, error } = useToast()

const orders = ref([])
const loading = ref(false)
const searchQuery = ref('')
const currentType = ref('all')
const selectedOrder = ref(null)
const selectedNhanVienTen = ref('')
const renderOrder = ref(null)
const renderNhanVienTen = ref('')
const hiddenTemplateRef = ref(null)
const employeeMap = ref({})

const typeTabs = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Tại quầy', value: 'pos' },
  { label: 'Online', value: 'online' }
]

const formatPrice = formatInvoicePrice
const formatDate = formatInvoiceDate

const invoiceOrders = computed(() => orders.value.filter(isInvoiceable))

const filteredInvoices = computed(() => {
  let list = invoiceOrders.value

  if (currentType.value === 'pos') {
    list = list.filter(isPosOrder)
  } else if (currentType.value === 'online') {
    list = list.filter((o) => !isPosOrder(o))
  }

  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return list

  return list.filter((order) => {
    const name = customerName(order).toLowerCase()
    const phone = customerPhone(order).toLowerCase()
    const inv = invoiceNumber(order.maDonHang).toLowerCase()
    const id = String(order.maDonHang)
    return name.includes(q) || phone.includes(q) || inv.includes(q) || id.includes(q)
  })
})

const countByType = (type) => {
  if (type === 'pos') return invoiceOrders.value.filter(isPosOrder).length
  return invoiceOrders.value.filter((o) => !isPosOrder(o)).length
}

const customerName = (order) => {
  if (!order?.diaChiGiao || isPosOrder(order)) {
    const raw = order?.diaChiGiao || ''
    if (raw.toLowerCase().includes('tại quầy')) return 'Khách mua tại quầy'
    return raw.split(' - ')[0] || 'Khách mua tại quầy'
  }
  return order.diaChiGiao.split(' - ')[0] || 'Khách hàng'
}

const customerPhone = (order) => {
  if (!order?.diaChiGiao) return ''
  const parts = order.diaChiGiao.split(' - ')
  return parts[1] || ''
}

const fetchEmployees = async () => {
  try {
    const res = await axios.get(`${API_URL}/api/nguoi-dung`)
    const users = res.data.data || res.data || []
    const map = {}
    users.forEach((u) => {
      map[u.maNguoiDung] = u.hoTen || u.email
    })
    employeeMap.value = map
  } catch {
    employeeMap.value = {}
  }
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/don-hang`)
    orders.value = res.data.data || res.data || []
    orders.value.sort((a, b) => b.maDonHang - a.maDonHang)
  } catch {
    error('Không tải được danh sách hóa đơn')
  } finally {
    loading.value = false
  }
}

const loadOrderDetail = async (order) => {
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/${order.maDonHang}`)
    return res.data.data || res.data || order
  } catch {
    return order
  }
}

const getNhanVienTen = (order) => employeeMap.value[order?.maNhanVien] || ''

const openInvoice = async (order) => {
  selectedOrder.value = null
  selectedNhanVienTen.value = ''
  const detail = await loadOrderDetail(order)
  selectedOrder.value = detail
  selectedNhanVienTen.value = getNhanVienTen(detail)
  await nextTick()
  const el = document.getElementById('invoiceManageModal')
  if (el) Modal.getOrCreateInstance(el).show()
}

const runHiddenRender = async (order, action) => {
  renderOrder.value = null
  await nextTick()
  renderOrder.value = order
  renderNhanVienTen.value = getNhanVienTen(order)
  await nextTick()
  await new Promise((resolve) => requestAnimationFrame(resolve))

  const paper = resolveInvoicePaperElement(hiddenTemplateRef.value)
  if (!paper) {
    error('Không thể tạo hóa đơn')
    renderOrder.value = null
    return
  }
  if (action === 'print') {
    printInvoiceElement(paper)
    success('Đã mở hộp thoại in')
  } else {
    await exportInvoicePdf(paper, order.maDonHang)
    success('Xuất PDF thành công')
  }
  renderOrder.value = null
}

const quickPrint = async (order) => {
  const detail = await loadOrderDetail(order)
  await runHiddenRender(detail, 'print')
}

const quickPdf = async (order) => {
  const detail = await loadOrderDetail(order)
  try {
    await runHiddenRender(detail, 'pdf')
  } catch {
    error('Xuất PDF thất bại')
  }
}

onMounted(async () => {
  await Promise.all([fetchEmployees(), fetchOrders()])
})
</script>

<style scoped>
.invoices-card {
  border: 1px solid var(--sp-border);
  box-shadow: var(--sp-shadow-sm);
}

.filter-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
}

.filter-tab {
  border: 1px solid var(--sp-border);
  background: #fff;
  color: #64748b;
  border-radius: 999px;
  padding: 0.45rem 0.9rem;
  font-weight: 650;
  font-size: 0.88rem;
  transition: all 0.2s ease;
}

.filter-tab:hover,
.filter-tab.active {
  border-color: rgba(29, 78, 216, 0.3);
  color: var(--sp-blue);
}

.filter-tab.active {
  background: rgba(29, 78, 216, 0.1);
}

.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}

.search-box {
  position: relative;
  min-width: min(320px, 100%);
}

.search-box i {
  position: absolute;
  left: 0.85rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.search-box input {
  padding-left: 2.2rem;
}

.offscreen-render {
  position: fixed;
  left: -9999px;
  top: 0;
  width: 720px;
  pointer-events: none;
  opacity: 0;
}
</style>
