<template>
  <div class="container mt-4">
    <PageHeader title="Quản lý Biến thể" subtitle="Quản lý màu sắc, kích cỡ và tồn kho theo SKU" />

    <div class="card mb-4 shadow-sm">
      <div class="card-body p-4">
        <h5 class="mb-3 fw-semibold">{{ isEditing ? 'Cập nhật Biến thể' : 'Thêm Biến thể mới' }}</h5>
        <form @submit.prevent="saveVariant">
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">Sản phẩm <span class="text-danger">*</span></label>
              <select v-model="form.maSanPham" class="form-select" @change="fetchVariants" required>
                <option value="">Chọn sản phẩm</option>
                <option v-for="p in products" :key="p.maSanPham" :value="p.maSanPham">{{ p.tenSanPham }}</option>
              </select>
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Màu sắc <span class="text-danger">*</span></label>
              <input type="text" v-model="form.mauSac" class="form-control" required placeholder="VD: Đỏ">
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Kích cỡ <span class="text-danger">*</span></label>
              <input type="text" v-model="form.kichCo" class="form-control" required placeholder="VD: XL">
            </div>
            <div class="col-md-2 mb-3">
              <label class="form-label">Số lượng tồn <span class="text-danger">*</span></label>
              <input type="number" v-model="form.soLuongTon" class="form-control" min="0" required>
            </div>
            <div class="col-md-2 mb-3 d-flex align-items-end">
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm') }}
              </button>
            </div>
          </div>
          <button type="button" v-if="isEditing" @click="resetForm" class="btn btn-secondary">Hủy</button>
        </form>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="table-responsive bg-white rounded shadow-sm p-3">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>ID (Mã vạch)</th>
            <th>Sản phẩm</th>
            <th>Màu sắc</th>
            <th>Kích cỡ</th>
            <th>Tồn kho</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="variant in variants" :key="variant.maChiTietSp">
            <td>
              <button
                type="button"
                class="btn btn-sm btn-outline-secondary font-monospace"
                @click="copyText(variant.maVachSku || variant.maChiTietSp)"
                :title="'Click để copy mã: ' + (variant.maVachSku || variant.maChiTietSp)"
              >
                <i class="bi bi-clipboard me-1"></i>
                <span class="fw-bold">#{{ variant.maChiTietSp }}</span>
                <span v-if="variant.maVachSku"> / {{ variant.maVachSku }}</span>
              </button>
            </td>
            <td class="fw-bold">{{ getProductName(variant.maSanPham) }}</td>
            <td>{{ variant.mauSac }}</td>
            <td>{{ variant.kichCo }}</td>
            <td>
              <span class="badge" :class="variant.soLuongTon > 0 ? 'bg-success' : 'bg-danger'">
                {{ variant.soLuongTon > 0 ? variant.soLuongTon : 'Hết hàng' }}
              </span>
            </td>
            <td>
              <button @click="printBarcode(variant)" class="btn btn-sm btn-outline-dark me-1" title="In tem mã vạch">
                <i class="bi bi-printer me-1"></i>In mã
              </button>
              <button @click="editVariant(variant)" class="btn btn-sm btn-outline-primary me-1">Sửa</button>
              <button @click="deleteVariant(variant.maChiTietSp)" class="btn btn-sm btn-outline-danger">Xóa</button>
            </td>
          </tr>
          <tr v-if="variants.length === 0">
            <td colspan="6" class="text-center text-muted py-3">Chưa có dữ liệu biến thể</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal In Mã Vạch Biến Thể -->
    <div v-if="showBarcodeModal" class="modal fade show d-block" style="background: rgba(0,0,0,0.6); z-index: 1070;" @click.self="showBarcodeModal = false">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0 rounded-3">
          <div class="modal-header">
            <h5 class="modal-title fw-bold"><i class="bi bi-printer me-2"></i>In Tem Mã Vạch Biến Thể</h5>
            <button type="button" class="btn-close" @click="showBarcodeModal = false"></button>
          </div>
          <div class="modal-body text-center" v-if="selectedBarcodeVariant">
            <div id="printableBarcodeArea" class="barcode-card p-3 border rounded shadow-sm bg-white mx-auto" style="max-width: 320px;">
              <div class="fw-bold text-uppercase small text-muted">SPORTPRO ATHLETICS</div>
              <div class="fw-bold fs-6 text-dark text-truncate mb-1">{{ getProductName(selectedBarcodeVariant.maSanPham) }}</div>
              <div class="small text-secondary mb-2">
                Phân loại: <strong>Màu {{ selectedBarcodeVariant.mauSac }} - Size {{ selectedBarcodeVariant.kichCo }}</strong>
              </div>
              <div class="my-2 d-flex justify-content-center" v-html="barcodeSvg"></div>
              <div class="fw-bold fs-5 text-danger mt-1">
                {{ formatPrice(getProductPrice(selectedBarcodeVariant.maSanPham) + (selectedBarcodeVariant.giaCongThem || 0)) }}
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showBarcodeModal = false">Đóng</button>
            <button type="button" class="btn btn-primary fw-bold" @click="triggerPrintBarcode">
              <i class="bi bi-printer-fill me-1"></i>In tem ngay
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import PageHeader from '@/components/PageHeader.vue'
import { useToast } from '@/composables/useToast.js'
import { API_URL } from '@/config.js'
import { generateBarcodeSVG } from '@/utils/barcode.js'

const { success } = useToast()
const variants = ref([])
const products = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const showBarcodeModal = ref(false)
const selectedBarcodeVariant = ref(null)
const barcodeSvg = ref('')

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const getProductPrice = (id) => {
  const p = products.value.find(x => x.maSanPham === id)
  return p ? (p.giaGoc || 0) : 0
}

const printBarcode = (variant) => {
  selectedBarcodeVariant.value = variant
  const code = variant.maVachSku || variant.maChiTietSp
  barcodeSvg.value = generateBarcodeSVG(code, { width: 2, height: 50, fontSize: 13 })
  showBarcodeModal.value = true
}

const triggerPrintBarcode = () => {
  const area = document.getElementById('printableBarcodeArea')
  if (!area) return
  const printContents = area.innerHTML
  const printWindow = window.open('', '', 'height=500,width=600')
  printWindow.document.write('<html><head><title>In tem mã vạch</title>')
  printWindow.document.write('<style>body{font-family:sans-serif;text-align:center;padding:20px;margin:0;}.barcode-card{border:1px dashed #333;padding:15px;border-radius:6px;max-width:280px;margin:auto;}svg{max-width:100%;height:auto;}</style>')
  printWindow.document.write('</head><body>')
  printWindow.document.write('<div class="barcode-card">' + printContents + '</div>')
  printWindow.document.write('</body></html>')
  printWindow.document.close()
  printWindow.focus()
  setTimeout(() => {
    printWindow.print()
    printWindow.close()
  }, 300)
}

const copyText = (text) => {
  navigator.clipboard.writeText(String(text)).then(() => success('Đã copy mã SKU'))
}

const form = ref({
  maSanPham: '',
  mauSac: '',
  kichCo: '',
  soLuongTon: 0
})

const getProductName = (id) => {
  const p = products.value.find(x => x.maSanPham === id)
  return p ? p.tenSanPham : id
}

const fetchData = async () => {
  loading.value = true
  try {
    const prodRes = await axios.get(`${API_URL}/api/san-pham`)
    products.value = prodRes.data.data || prodRes.data || []
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const fetchVariants = async () => {
  if (!form.value.maSanPham) {
    variants.value = []
    return
  }
  try {
    const res = await axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${form.value.maSanPham}`)
    variants.value = res.data.data || res.data || []
  } catch (error) {
    console.error('Lỗi khi tải biến thể:', error)
    variants.value = []
  }
}

const saveVariant = async () => {
  loading.value = true
  try {
    const payload = {
      maSanPham: Number(form.value.maSanPham),
      mauSac: form.value.mauSac,
      kichCo: form.value.kichCo,
      soLuongTon: Number(form.value.soLuongTon)
    }

    if (isEditing.value) {
      await axios.put(`${API_URL}/api/chi-tiet-san-pham/${editingId.value}`, payload)
      success('Cập nhật biến thể thành công')
    } else {
      await axios.post(`${API_URL}/api/chi-tiet-san-pham`, payload)
      success('Thêm biến thể mới thành công')
    }
    
    resetForm()
    await fetchVariants()
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const editVariant = (variant) => {
  isEditing.value = true
  editingId.value = variant.maChiTietSp
  form.value = {
    maSanPham: variant.maSanPham,
    mauSac: variant.mauSac,
    kichCo: variant.kichCo,
    soLuongTon: variant.soLuongTon
  }
}

const deleteVariant = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa biến thể này?')) return
  try {
    await axios.delete(`${API_URL}/api/chi-tiet-san-pham/${id}`)
    await fetchVariants()
  } catch (error) {
    alert('Không thể xóa biến thể này!')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  const currentMaSP = form.value.maSanPham
  form.value = {
    maSanPham: currentMaSP,
    mauSac: '',
    kichCo: '',
    soLuongTon: 0
  }
}

onMounted(() => {
  fetchData()
})
</script>
