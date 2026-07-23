<template>
  <div class="modal fade" :id="modalId" tabindex="-1" aria-hidden="true" ref="modalEl">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content invoice-modal">
        <div class="modal-header">
          <div>
            <h5 class="modal-title mb-1">Hóa đơn {{ invoiceNo }}</h5>
            <p class="modal-subtitle mb-0">Xem trước, in hoặc tải PDF</p>
          </div>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
        </div>

        <div class="modal-body" v-if="order">
          <HoaDonTemplate ref="templateRef" :order="order" :nhan-vien-ten="nhanVienTen" />
        </div>

        <div class="modal-body text-center py-5" v-else>
          <div class="spinner-border text-primary" role="status"></div>
        </div>

        <div class="modal-footer" v-if="order">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          <button type="button" class="btn btn-outline-primary" :disabled="printing" @click="handlePrint">
            <i class="bi bi-printer me-1"></i>
            {{ printing ? 'Đang in...' : 'In hóa đơn' }}
          </button>
          <button type="button" class="btn btn-primary" :disabled="exporting" @click="handleExportPdf">
            <i class="bi bi-file-earmark-pdf me-1"></i>
            {{ exporting ? 'Đang xuất...' : 'Xuất PDF' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import HoaDonTemplate from '@/components/HoaDonTemplate.vue'
import {
  invoiceNumber,
  printInvoiceElement,
  exportInvoicePdf,
  resolveInvoicePaperElement
} from '@/composables/useInvoice.js'
import { useToast } from '@/composables/useToast.js'

const props = defineProps({
  modalId: { type: String, default: 'invoiceModal' },
  order: { type: Object, default: null },
  nhanVienTen: { type: String, default: '' }
})

const { error } = useToast()
const templateRef = ref(null)
const printing = ref(false)
const exporting = ref(false)

const invoiceNo = computed(() => invoiceNumber(props.order?.maDonHang))

const getPaperElement = () => resolveInvoicePaperElement(templateRef.value)

const handlePrint = async () => {
  const el = getPaperElement()
  if (!el) {
    error('Không thể tạo hóa đơn để in')
    return
  }
  printing.value = true
  try {
    printInvoiceElement(el)
  } finally {
    printing.value = false
  }
}

const handleExportPdf = async () => {
  const el = getPaperElement()
  if (!el || !props.order?.maDonHang) {
    error('Không thể tạo hóa đơn PDF')
    return
  }
  exporting.value = true
  try {
    await exportInvoicePdf(el, props.order.maDonHang)
  } finally {
    exporting.value = false
  }
}
</script>

<style scoped>
.invoice-modal .modal-title {
  font-weight: 780;
}

.modal-subtitle {
  color: #64748b;
  font-size: 0.86rem;
  font-weight: 500;
}
</style>
