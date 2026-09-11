<template>
  <div class="container mt-4">
    <PageHeader title="Quản lý Sản phẩm chi tiết" subtitle="Quản lý chi tiết từng biến thể kích cỡ, màu sắc và số lượng tồn kho theo mã SKU">
      <template #actions>
        <router-link to="/admin/products" class="btn btn-outline-secondary btn-sm">
          <i class="bi bi-arrow-left me-1"></i>Về DS Sản phẩm
        </router-link>
      </template>
    </PageHeader>

    <div class="card mb-4 shadow-sm border-0">
      <div class="card-body p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="mb-0 fw-semibold text-dark">
            <i :class="isEditing ? 'bi bi-pencil-square text-warning me-2' : 'bi bi-plus-circle text-primary me-2'"></i>
            {{ isEditing ? 'Cập nhật Sản phẩm chi tiết' : 'Thêm mới Sản phẩm chi tiết' }}
          </h5>
          <!-- Nút hủy khi đang ở chế độ chỉnh sửa -->
          <button type="button" v-if="isEditing" @click="resetForm" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-x-circle me-1"></i>Hủy chỉnh sửa
          </button>
        </div>

        <!-- FORM THÊM MỚI / CHỈNH SỬA BIẾN THỂ -->
        <form @submit.prevent="saveVariant">
          <div class="row g-3">
            <!-- 1. CHỌN SẢN PHẨM -->
            <div class="col-md-3">
              <label class="form-label fw-semibold">Sản phẩm <span class="text-danger">*</span></label>
              <select v-model="form.maSanPham" class="form-select" @change="fetchVariants" required>
                <option value="">-- Chọn sản phẩm --</option>
                <option v-for="p in products" :key="p.maSanPham" :value="p.maSanPham">
                  {{ p.tenSanPham }}
                </option>
              </select>
            </div>

            <!-- 2. CHỌN MÀU SẮC (Dropdown có nút Thêm nhanh như ở trang Sản phẩm) -->
            <div class="col-md-2">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <label class="form-label mb-0 fw-semibold">Màu sắc <span class="text-danger">*</span></label>
                <button 
                  type="button" 
                  class="btn btn-sm btn-outline-primary py-0 px-2" 
                  @click="showQuickColorModal = true" 
                  style="font-size: 0.78rem;" 
                  title="Thêm màu mới vào danh sách"
                >
                  <i class="bi bi-plus-circle me-1"></i>Thêm
                </button>
              </div>
              <select v-model="form.mauSac" class="form-select" required>
                <option value="">-- Chọn màu --</option>
                <option v-for="color in availableColors" :key="color" :value="color">
                  {{ color }}
                </option>
              </select>
            </div>

            <!-- 3. CHỌN KÍCH CỠ (Dropdown có nút Thêm nhanh như ở trang Sản phẩm) -->
            <div class="col-md-2">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <label class="form-label mb-0 fw-semibold">Kích cỡ <span class="text-danger">*</span></label>
                <button 
                  type="button" 
                  class="btn btn-sm btn-outline-primary py-0 px-2" 
                  @click="showQuickSizeModal = true" 
                  style="font-size: 0.78rem;" 
                  title="Thêm kích cỡ mới vào danh sách"
                >
                  <i class="bi bi-plus-circle me-1"></i>Thêm
                </button>
              </div>
              <select v-model="form.kichCo" class="form-select" required>
                <option value="">-- Chọn kích cỡ --</option>
                <option v-for="size in availableSizes" :key="size" :value="size">
                  {{ size }}
                </option>
              </select>
            </div>

            <!-- 4. GIÁ BÁN (Định dạng phân cách hàng nghìn kiểu 10,000 để biết rõ bao nhiêu tiền) -->
            <div class="col-md-2">
              <label class="form-label fw-semibold">Giá bán (VNĐ) <span class="text-danger">*</span></label>
              <input 
                type="text" 
                :value="displayPrice" 
                @input="onPriceInput" 
                class="form-control" 
                placeholder="VD: 250,000" 
                required 
              />
            </div>

            <!-- 5. SỐ LƯỢNG TỒN KHO -->
            <div class="col-md-2">
              <label class="form-label fw-semibold">Số lượng tồn <span class="text-danger">*</span></label>
              <input 
                type="number" 
                v-model="form.soLuongTon" 
                class="form-control" 
                min="0" 
                placeholder="0" 
                required 
              />
            </div>

            <!-- 6. NÚT SUBMIT -->
            <div class="col-md-1 d-flex align-items-end">
              <button type="submit" class="btn btn-primary w-100" :disabled="loading">
                <span v-if="loading" class="spinner-border spinner-border-sm me-1"></span>
                {{ loading ? '...' : (isEditing ? 'Lưu' : 'Thêm') }}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- VÒNG XOAY ĐANG TẢI DỮ LIỆU -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="text-muted mt-2">Đang tải danh sách sản phẩm chi tiết...</p>
    </div>

    <!-- BẢNG DANH SÁCH SẢN PHẨM CHI TIẾT -->
    <div v-else class="table-responsive bg-white rounded shadow-sm p-3 border">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th class="text-nowrap">Mã SP chi tiết</th>
            <th class="text-nowrap">Sản phẩm</th>
            <th class="text-nowrap">Màu sắc</th>
            <th class="text-nowrap">Kích cỡ</th>
            <th class="text-nowrap">Giá bán</th>
            <th class="text-nowrap">Tồn kho</th>
            <th class="text-nowrap">Trạng thái</th>
            <th class="text-nowrap">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="variant in variants" :key="variant.maChiTietSp">
            <!-- Mã vạch SKU / Mã chi tiết (Click để copy nhanh) -->
            <td class="text-nowrap">
              <button
                type="button"
                class="btn btn-sm btn-outline-dark font-monospace fw-bold text-nowrap"
                @click="copyText(variant.maVachSku || ('CTSP' + String(variant.maChiTietSp).padStart(2, '0')))"
                :title="'Click để copy mã: ' + (variant.maVachSku || ('CTSP' + String(variant.maChiTietSp).padStart(2, '0')))"
              >
                <i class="bi bi-upc-scan me-1"></i>
                <span>{{ variant.maVachSku || ('CTSP' + String(variant.maChiTietSp).padStart(2, '0')) }}</span>
              </button>
            </td>

            <!-- Tên sản phẩm cha -->
            <td>
              <div class="fw-bold text-dark">{{ getProductName(variant.maSanPham) }}</div>
            </td>

            <!-- Màu sắc -->
            <td class="text-nowrap">
              <span class="badge bg-light text-dark border px-2 py-1">
                {{ variant.mauSac }}
              </span>
            </td>

            <!-- Kích cỡ -->
            <td class="text-nowrap">
              <span class="badge bg-light text-dark border px-2 py-1">
                {{ variant.kichCo }}
              </span>
            </td>

            <!-- CỘT GIÁ BÁN Ở SẢN PHẨM CHI TIẾT -->
            <td class="text-nowrap">
              <span class="fw-bold text-primary fs-6">
                {{ formatPrice(variant.giaCongThem || 0) }}
              </span>
            </td>

            <!-- Tồn kho -->
            <td class="text-nowrap">
              <span class="badge text-nowrap" :class="variant.soLuongTon > 0 ? 'bg-success' : 'bg-danger'">
                {{ variant.soLuongTon > 0 ? variant.soLuongTon : 'Hết hàng' }}
              </span>
            </td>

            <!-- Trạng thái kinh doanh -->
            <td class="text-nowrap">
              <span
                class="badge text-nowrap"
                :class="isPaused(variant.trangThai) ? 'bg-secondary' : 'bg-success'"
                style="cursor: pointer;"
                @click="toggleVariantStatus(variant)"
                :title="'Click để ' + (isPaused(variant.trangThai) ? 'Mở bán lại' : 'Tạm ngừng')"
              >
                <i :class="isPaused(variant.trangThai) ? 'bi bi-pause-circle me-1' : 'bi bi-check-circle me-1'"></i>
                {{ isPaused(variant.trangThai) ? 'Tạm ngừng' : 'Đang bán' }}
              </span>
            </td>

            <!-- Các nút thao tác -->
            <td class="text-nowrap">
              <div class="d-flex flex-nowrap gap-1">
                <button @click="printBarcode(variant)" class="btn btn-sm btn-outline-dark" title="In tem mã vạch">
                  <i class="bi bi-printer me-1"></i>In mã
                </button>
                <button @click="editVariant(variant)" class="btn btn-sm btn-outline-primary" title="Sửa biến thể">
                  <i class="bi bi-pencil me-1"></i>Sửa
                </button>
                <button
                  v-if="isPaused(variant.trangThai)"
                  @click="toggleVariantStatus(variant)"
                  class="btn btn-sm btn-outline-success"
                  title="Đổi trạng thái sang Đang bán"
                >
                  <i class="bi bi-play-circle me-1"></i>Bán lại
                </button>
                <button
                  v-else
                  @click="toggleVariantStatus(variant)"
                  class="btn btn-sm btn-outline-warning"
                  title="Đổi trạng thái sang Tạm ngừng"
                >
                  <i class="bi bi-pause-circle me-1"></i>Tạm ngừng
                </button>
              </div>
            </td>
          </tr>

          <!-- Khi danh sách trống -->
          <tr v-if="variants.length === 0">
            <td colspan="8" class="text-center text-muted py-4">Chưa có dữ liệu biến thể sản phẩm</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- MODAL THÊM NHANH MÀU SẮC (Tương tự Thêm nhanh Danh mục ở Sản phẩm) -->
    <div class="modal fade show d-block" tabindex="-1" v-if="showQuickColorModal" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
          <div class="modal-header">
            <h5 class="modal-title fw-bold text-primary">
              <i class="bi bi-palette-fill me-2"></i>Thêm Màu sắc mới
            </h5>
            <button type="button" class="btn-close" @click="showQuickColorModal = false"></button>
          </div>
          <div class="modal-body">
            <label class="form-label fw-semibold">Tên màu sắc mới <span class="text-danger">*</span></label>
            <input 
              type="text" 
              class="form-control" 
              v-model="quickColorName" 
              placeholder="Ví dụ: Tím than, Xanh rêu, Đỏ đô..." 
              @keyup.enter="saveQuickColor" 
              autofocus 
            />
            <div class="form-text text-muted">Màu này sẽ được lưu vào danh sách chọn để dùng ngay.</div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showQuickColorModal = false">Hủy</button>
            <button type="button" class="btn btn-primary" :disabled="!quickColorName.trim()" @click="saveQuickColor">
              Lưu & Chọn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL THÊM NHANH KÍCH CỠ (Tương tự Thêm nhanh Thương hiệu ở Sản phẩm) -->
    <div class="modal fade show d-block" tabindex="-1" v-if="showQuickSizeModal" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
          <div class="modal-header">
            <h5 class="modal-title fw-bold text-primary">
              <i class="bi bi-rulers me-2"></i>Thêm Kích cỡ mới
            </h5>
            <button type="button" class="btn-close" @click="showQuickSizeModal = false"></button>
          </div>
          <div class="modal-body">
            <label class="form-label fw-semibold">Tên kích cỡ mới <span class="text-danger">*</span></label>
            <input 
              type="text" 
              class="form-control" 
              v-model="quickSizeName" 
              placeholder="Ví dụ: 4XL, Size 6, 46, Oversize..." 
              @keyup.enter="saveQuickSize" 
              autofocus 
            />
            <div class="form-text text-muted">Kích cỡ này sẽ được lưu vào danh sách chọn để dùng ngay.</div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showQuickSizeModal = false">Hủy</button>
            <button type="button" class="btn btn-primary" :disabled="!quickSizeName.trim()" @click="saveQuickSize">
              Lưu & Chọn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL IN MÃ VẠCH BIẾN THỂ -->
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
              <!-- Hiển thị trực tiếp giá bán của biến thể chi tiết -->
              <div class="fw-bold fs-5 text-danger mt-1">
                {{ formatPrice(selectedBarcodeVariant.giaCongThem || 0) }}
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
// =============================================================================
// 1. IMPORT CÁC THƯ VIỆN & TIỆN ÍCH CẦN THIẾT
// =============================================================================
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import PageHeader from '@/components/PageHeader.vue'
import { useToast } from '@/composables/useToast.js'
import { API_URL } from '@/config.js'
import { generateBarcodeSVG } from '@/utils/barcode.js'

// Lấy router params/query để xem có chuyển từ trang sản phẩm sang không (VD: ?productId=20)
const route = useRoute()
// Hook hiển thị thông báo toast (success: xanh, error: đỏ)
const { success, error } = useToast()

// =============================================================================
// 2. KHAI BÁO CÁC STATE (BIẾN PHẢN ỨNG CỦA VUE)
// =============================================================================
const variants = ref([])         // Danh sách các biến thể chi tiết lấy từ API
const products = ref([])         // Danh sách tất cả sản phẩm cha để chọn trong dropdown
const loading = ref(false)       // Cờ theo dõi trạng thái đang tải dữ liệu (spinner)
const isEditing = ref(false)     // Cờ cho biết đang ở chế độ 'Sửa' (true) hay 'Thêm mới' (false)
const editingId = ref(null)      // Lưu ID của biến thể đang được sửa

// State cho Modal In Tem Mã Vạch (Barcode)
const showBarcodeModal = ref(false)
const selectedBarcodeVariant = ref(null)
const barcodeSvg = ref('')

// State cho Modal Thêm nhanh Màu sắc
const showQuickColorModal = ref(false)
const quickColorName = ref('')

// State cho Modal Thêm nhanh Kích cỡ
const showQuickSizeModal = ref(false)
const quickSizeName = ref('')

// =============================================================================
// 3. DANH SÁCH MÀU SẮC & KÍCH CỠ CHO PHÉP CHỌN (TƯƠNG TỰ NHƯ Ở TRANG SẢN PHẨM)
// =============================================================================

// Danh sách các màu thông dụng trong đồ thể thao
const customColors = ref([
  'Đen', 'Trắng', 'Đỏ', 'Xanh lam', 'Xanh navy', 'Xanh lá',
  'Vàng', 'Cam', 'Hồng', 'Xám', 'Tím', 'Nâu',
  'Trắng Xanh', 'Đỏ Xanh', 'Xanh Đen', 'Vàng Xanh', 'Trắng Đỏ', 'Hồng Vàng', 'Trắng Đa Sắc'
])

// Danh sách các kích cỡ thông dụng (quần áo, giày, phụ kiện)
const customSizes = ref([
  // Size quần áo
  'XS', 'S', 'M', 'L', 'XL', 'XXL', '3XL', 'FreeSize',
  // Size giày thể thao
  '36', '37', '38', '39', '40', '41', '42', '43', '44', '45',
  // Size phụ kiện & bóng
  'Size 4', 'Size 5', 'Tiêu chuẩn'
])

// Computed availableColors: Tự động gom các màu mẫu + các màu đã có sẵn trong database
// Nhờ dùng Set nên danh sách sẽ không bao giờ bị trùng lặp màu
const availableColors = computed(() => {
  const set = new Set(customColors.value)
  variants.value.forEach(v => {
    if (v.mauSac && v.mauSac.trim()) {
      set.add(v.mauSac.trim())
    }
  })
  return Array.from(set)
})

// Computed availableSizes: Tự động gom các size mẫu + các size đã có sẵn trong database
const availableSizes = computed(() => {
  const set = new Set(customSizes.value)
  variants.value.forEach(v => {
    if (v.kichCo && v.kichCo.trim()) {
      set.add(v.kichCo.trim())
    }
  })
  return Array.from(set)
})

// Hàm lưu nhanh màu sắc mới từ modal
const saveQuickColor = () => {
  const name = quickColorName.value.trim()
  if (!name) return
  if (!customColors.value.includes(name)) {
    customColors.value.push(name)
  }
  form.value.mauSac = name // Tự động chọn luôn màu mới thêm
  quickColorName.value = ''
  showQuickColorModal.value = false
  success(`Đã thêm màu "${name}" vào danh sách`)
}

// Hàm lưu nhanh kích cỡ mới từ modal
const saveQuickSize = () => {
  const name = quickSizeName.value.trim()
  if (!name) return
  if (!customSizes.value.includes(name)) {
    customSizes.value.push(name)
  }
  form.value.kichCo = name // Tự động chọn luôn size mới thêm
  quickSizeName.value = ''
  showQuickSizeModal.value = false
  success(`Đã thêm kích cỡ "${name}" vào danh sách`)
}

// =============================================================================
// 4. MODEL DỮ LIỆU CỦA FORM THÊM / SỬA SẢN PHẨM CHI TIẾT
// =============================================================================
// Chuỗi hiển thị giá trong ô input có dấu phẩy phân cách hàng nghìn (VD: "10,000", "20,000,000")
const displayPrice = ref('')

// Hàm định dạng số hiển thị có dấu phẩy phân cách hàng nghìn (VD: 10000 -> "10,000")
const formatInputNumber = (val) => {
  if (val === null || val === undefined || val === '') return ''
  const cleanDigits = String(val).replace(/\D/g, '')
  if (!cleanDigits) return ''
  return new Intl.NumberFormat('en-US').format(Number(cleanDigits))
}

// Hàm xử lý sự kiện khi gõ vào ô nhập giá: tự động format dấu phẩy kiểu 10,000
const onPriceInput = (event) => {
  // Loại bỏ mọi ký tự không phải chữ số
  const rawDigits = event.target.value.replace(/\D/g, '')
  if (!rawDigits) {
    form.value.giaCongThem = ''
    displayPrice.value = ''
    event.target.value = ''
    return
  }
  const num = Number(rawDigits)
  form.value.giaCongThem = num
  displayPrice.value = new Intl.NumberFormat('en-US').format(num)
  event.target.value = displayPrice.value
}

// Chú ý quan trọng:
// Giá được quản lý ở Sản phẩm chi tiết và được lưu vào trường `giaCongThem` trong DB
const form = ref({
  maSanPham: '',      // ID của sản phẩm cha
  mauSac: '',         // Màu sắc (chọn từ dropdown)
  kichCo: '',         // Kích cỡ (chọn từ dropdown)
  giaCongThem: '',    // Giá bán của biến thể chi tiết này (VNĐ)
  soLuongTon: 0       // Số lượng hàng tồn kho
})

// =============================================================================
// 5. CÁC HÀM TIỆN ÍCH
// =============================================================================

// Định dạng tiền tệ Việt Nam (VD: 250000 -> "250.000 ₫")
const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

// Lấy tên sản phẩm cha từ ID để hiển thị lên bảng
const getProductName = (id) => {
  const p = products.value.find(x => x.maSanPham === id)
  return p ? p.tenSanPham : id
}

// Sao chép nhanh mã SKU vào clipboard
const copyText = (text) => {
  navigator.clipboard.writeText(String(text)).then(() => success('Đã copy mã SKU'))
}

// Kiểm tra xem biến thể có đang bị tạm ngừng kinh doanh hay không
const isPaused = (status) => {
  if (!status) return false
  const s = String(status).toLowerCase()
  return s.includes('tạm') || s.includes('tam') || s.includes('ngừng') || s.includes('ngung')
}

// =============================================================================
// 6. GỌI API BACKEND (CRUD SẢN PHẨM CHI TIẾT)
// =============================================================================

// Lấy danh sách toàn bộ sản phẩm cha để đổ vào thẻ <select>
const fetchData = async () => {
  loading.value = true
  try {
    const prodRes = await axios.get(`${API_URL}/api/san-pham`)
    products.value = prodRes.data.data || prodRes.data || []
    await fetchVariants()
  } catch (err) {
    console.error('Lỗi khi tải dữ liệu sản phẩm:', err)
  } finally {
    loading.value = false
  }
}

// Lấy danh sách sản phẩm chi tiết (lọc theo sản phẩm nếu người dùng đã chọn sản phẩm cụ thể)
const fetchVariants = async () => {
  try {
    let url = `${API_URL}/api/chi-tiet-san-pham`
    if (form.value.maSanPham) {
      url = `${API_URL}/api/chi-tiet-san-pham/san-pham/${form.value.maSanPham}`
    }
    const res = await axios.get(url)
    variants.value = res.data.data || res.data || []
  } catch (err) {
    console.error('Lỗi khi tải biến thể:', err)
    variants.value = []
  }
}

// Lưu biến thể: Gọi API Thêm mới (POST) hoặc Cập nhật (PUT)
const saveVariant = async () => {
  // Validate kiểm tra form
  if (!form.value.maSanPham) {
    alert('Vui lòng chọn sản phẩm trước khi thêm biến thể!')
    return
  }
  if (!form.value.mauSac) {
    alert('Vui lòng chọn màu sắc!')
    return
  }
  if (!form.value.kichCo) {
    alert('Vui lòng chọn kích cỡ!')
    return
  }

  loading.value = true
  try {
    // Chuẩn bị payload gửi lên Backend
    const payload = {
      maSanPham: Number(form.value.maSanPham),
      mauSac: form.value.mauSac,
      kichCo: form.value.kichCo,
      soLuongTon: Number(form.value.soLuongTon),
      giaCongThem: Number(form.value.giaCongThem || 0) // Lưu giá bán ở sản phẩm chi tiết
    }

    if (isEditing.value) {
      // Gọi API cập nhật biến thể
      await axios.put(`${API_URL}/api/chi-tiet-san-pham/${editingId.value}`, payload)
      success('Cập nhật biến thể sản phẩm chi tiết thành công')
    } else {
      // Gọi API thêm mới biến thể
      await axios.post(`${API_URL}/api/chi-tiet-san-pham`, payload)
      success('Thêm mới biến thể sản phẩm chi tiết thành công')
    }
    
    // Đưa form về trạng thái ban đầu và tải lại bảng
    resetForm()
    await fetchVariants()
  } catch (err) {
    alert('Lỗi: ' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

// Đổ dữ liệu của 1 dòng lên form khi bấm nút "Sửa"
const editVariant = (variant) => {
  isEditing.value = true
  editingId.value = variant.maChiTietSp
  const price = (variant.giaCongThem !== null && variant.giaCongThem !== undefined) ? variant.giaCongThem : 0
  form.value = {
    maSanPham: variant.maSanPham,
    mauSac: variant.mauSac,
    kichCo: variant.kichCo,
    giaCongThem: price,
    soLuongTon: variant.soLuongTon
  }
  // Hiển thị giá có định dạng phân cách hàng nghìn (VD: "20,000,000")
  displayPrice.value = price ? formatInputNumber(price) : ''
}

// Đổi trạng thái kinh doanh của biến thể (Đang bán <-> Tạm ngừng)
const toggleVariantStatus = async (variant) => {
  const currentlyPaused = isPaused(variant.trangThai)
  const actionText = currentlyPaused ? 'mở bán lại' : 'tạm ngừng'
  const skuLabel = variant.maVachSku || ('CTSP' + String(variant.maChiTietSp).padStart(2, '0'))
  if (!confirm(`Bạn có chắc muốn ${actionText} biến thể "${skuLabel}"?`)) return

  const targetStatus = currentlyPaused ? 'Đang bán' : 'Tạm ngừng'
  try {
    const res = await axios.put(`${API_URL}/api/chi-tiet-san-pham/${variant.maChiTietSp}/trang-thai?trangThai=${encodeURIComponent(targetStatus)}`)
    variant.trangThai = res.data.data?.trangThai || targetStatus
    success(`Đã cập nhật trạng thái: "${skuLabel}" -> ${variant.trangThai}`)
  } catch (err) {
    try {
      const res = await axios.patch(`${API_URL}/api/chi-tiet-san-pham/${variant.maChiTietSp}/trang-thai`)
      variant.trangThai = res.data.data?.trangThai || targetStatus
      success(`Đã cập nhật trạng thái: "${skuLabel}" -> ${variant.trangThai}`)
    } catch (e) {
      error('Không thể cập nhật trạng thái biến thể: ' + (e.response?.data?.message || e.message))
    }
  }
}

// Reset form về trạng thái trống (vẫn giữ mã sản phẩm đang chọn để tiện thêm nhiều biến thể liên tiếp)
const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  const currentMaSP = form.value.maSanPham
  form.value = {
    maSanPham: currentMaSP,
    mauSac: '',
    kichCo: '',
    giaCongThem: '',
    soLuongTon: 0
  }
  displayPrice.value = ''
}

// =============================================================================
// 7. CHỨC NĂNG IN TEM MÃ VẠCH (BARCODE)
// =============================================================================
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

// =============================================================================
// 8. LIFECYCLE VÀ WATCHER
// =============================================================================
// Khi component vừa khởi tạo (onMounted)
onMounted(async () => {
  if (route.query.productId) {
    form.value.maSanPham = Number(route.query.productId)
  }
  await fetchData()
})

// Theo dõi nếu URL thay đổi ?productId=...
watch(() => route.query.productId, (newId) => {
  if (newId) {
    form.value.maSanPham = Number(newId)
  } else {
    form.value.maSanPham = ''
  }
  fetchVariants()
})
</script>
