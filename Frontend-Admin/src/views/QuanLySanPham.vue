<template>
  <div class="container mt-4">
    <PageHeader title="Quản lý Sản phẩm" subtitle="Quản lý danh mục sản phẩm, giá và hình ảnh" />

    <div class="card mb-4 shadow-sm">
      <div class="card-body p-4">
        <h5 class="mb-3 fw-semibold">{{ isEditing ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm mới' }}</h5>
        <form @submit.prevent="saveProduct">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
              <input type="text" v-model="form.tenSanPham" class="form-control" required />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Mô tả</label>
              <textarea v-model="form.moTa" class="form-control" rows="1"></textarea>
            </div>
          </div>
          <div class="row">
            <!-- Chọn Danh mục (có nút thêm nhanh) -->
            <div :class="isEditing ? 'col-md-4 mb-3' : 'col-md-6 mb-3'">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <label class="form-label mb-0 fw-semibold">Danh mục <span class="text-danger">*</span></label>
                <button type="button" class="btn btn-sm btn-outline-primary py-0 px-2" @click="openQuickCategoryModal" style="font-size: 0.78rem;">
                  <i class="bi bi-plus-circle me-1"></i>Thêm
                </button>
              </div>
              <select v-model="form.maDanhMuc" class="form-select" required>
                <option value="">Chọn danh mục</option>
                <option v-for="c in categories" :key="c.maDanhMuc" :value="c.maDanhMuc">{{ c.tenDanhMuc }}</option>
              </select>
            </div>

            <!-- Chọn Thương hiệu (có nút thêm nhanh) -->
            <div :class="isEditing ? 'col-md-4 mb-3' : 'col-md-6 mb-3'">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <label class="form-label mb-0 fw-semibold">Thương hiệu <span class="text-danger">*</span></label>
                <button type="button" class="btn btn-sm btn-outline-primary py-0 px-2" @click="openQuickBrandModal" style="font-size: 0.78rem;">
                  <i class="bi bi-plus-circle me-1"></i>Thêm
                </button>
              </div>
              <select v-model="form.maThuongHieu" class="form-select" required>
                <option value="">Chọn thương hiệu</option>
                <option v-for="b in brands" :key="b.maThuongHieu" :value="b.maThuongHieu">{{ b.tenThuongHieu }}</option>
              </select>
            </div>

            <!-- Trạng thái kinh doanh (chỉ hiển thị khi đang sửa sản phẩm) -->
            <div class="col-md-4 mb-3" v-if="isEditing">
              <label class="form-label fw-semibold">Trạng thái kinh doanh</label>
              <select v-model="form.trangThai" class="form-select">
                <option value="Đang bán">Đang bán</option>
                <option value="Tạm ngừng">Tạm ngừng</option>
              </select>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Hình ảnh sản phẩm</label>
              <input type="file" class="form-control" accept="image/*" @change="uploadImage" />
              <div class="form-text text-muted">
                <i class="bi bi-info-circle me-1"></i>Chỉ chấp nhận file ảnh (PNG, JPG, JPEG).
              </div>
              <div v-if="uploadingImg" class="small text-primary mt-1">Đang tải ảnh...</div>
            </div>
            <div class="col-md-6 mb-3 d-flex align-items-center">
              <div v-if="form.hinhAnh" class="preview-box">
                <img :src="imageUrl(form.hinhAnh)" alt="preview" />
              </div>
              <div v-else class="preview-empty">
                <i class="bi bi-image"></i>
              </div>
            </div>
          </div>
          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
            </button>
            <button type="button" v-if="isEditing" @click="resetForm" class="btn btn-secondary">Hủy</button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    <div v-else>
      <!-- Tìm kiếm và bộ lọc -->
      <div class="card mb-3 shadow-sm border-0">
        <div class="card-body p-3 bg-light rounded-3">
          <div class="row align-items-center g-2">
            <div class="col-md-4">
              <div class="input-group">
                <span class="input-group-text bg-white border-2 border-end-0"><i class="bi bi-search text-muted"></i></span>
                <input type="text" v-model="searchQuery" class="form-control border-2 border-start-0" placeholder="Tìm sản phẩm theo tên..." />
              </div>
            </div>
            <div class="col-md-3">
              <select v-model="filterCategory" class="form-select border-2">
                <option value="">Tất cả danh mục</option>
                <option v-for="c in categories" :key="c.maDanhMuc" :value="c.maDanhMuc">{{ c.tenDanhMuc }}</option>
              </select>
            </div>
            <div class="col-md-3">
              <select v-model="filterBrand" class="form-select border-2">
                <option value="">Tất cả thương hiệu</option>
                <option v-for="b in brands" :key="b.maThuongHieu" :value="b.maThuongHieu">{{ b.tenThuongHieu }}</option>
              </select>
            </div>
            <div class="col-md-2">
              <select v-model="filterStatus" class="form-select border-2">
                <option value="">Tất cả trạng thái</option>
                <option value="Đang bán">Đang bán</option>
                <option value="Tạm ngừng">Tạm ngừng</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Bảng sản phẩm -->
      <div class="table-responsive bg-white rounded shadow-sm">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th class="text-nowrap">Mã SP</th>
              <th class="text-nowrap">Tên sản phẩm</th>
              <th class="text-nowrap">Danh mục</th>
              <th class="text-nowrap">Thương hiệu</th>
              <th class="text-nowrap">Trạng thái</th>
              <th class="text-nowrap">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in filteredProducts" :key="p.maSanPham">
              <td class="text-nowrap"><span class="badge bg-primary text-white fw-bold">SPPRO{{ String(p.maSanPham).padStart(3, '0') }}</span></td>
              <td>
                <div class="fw-bold text-dark">{{ p.tenSanPham }}</div>
                <div class="small mt-1" v-if="getProductVariantSummary(p.maSanPham).count > 0">
                  <span class="badge bg-light text-dark border me-1">
                    <i class="bi bi-layers me-1 text-primary"></i>{{ getProductVariantSummary(p.maSanPham).count }} biến thể
                  </span>
                  <span
                    class="badge"
                    :class="getProductVariantSummary(p.maSanPham).totalStock > 0 ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-danger-subtle text-danger border border-danger-subtle'"
                  >
                    Tồn kho: {{ getProductVariantSummary(p.maSanPham).totalStock }}
                  </span>
                </div>
                <div class="small mt-1" v-else>
                  <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle">
                    <i class="bi bi-exclamation-circle me-1"></i>Chưa có chi tiết SP
                  </span>
                </div>
              </td>
              <td class="text-nowrap">{{ getCategoryName(p.maDanhMuc) }}</td>
              <td class="text-nowrap">{{ getBrandName(p.maThuongHieu) }}</td>
              <td class="text-nowrap">
                <div class="d-flex flex-column gap-1 align-items-start">
                  <span
                    class="badge text-nowrap"
                    :class="isPaused(p.trangThai) ? 'bg-secondary' : 'bg-success'"
                    style="cursor: pointer;"
                    @click="toggleStatus(p)"
                    :title="'Click để ' + (isPaused(p.trangThai) ? 'Mở bán lại' : 'Tạm ngừng bán')"
                  >
                    <i :class="isPaused(p.trangThai) ? 'bi bi-pause-circle me-1' : 'bi bi-check-circle me-1'"></i>
                    {{ isPaused(p.trangThai) ? 'Tạm ngừng' : 'Đang bán' }}
                  </span>
                  <span
                    v-if="!isPaused(p.trangThai) && getProductVariantSummary(p.maSanPham).count > 0 && getProductVariantSummary(p.maSanPham).totalStock === 0"
                    class="badge bg-danger-subtle text-danger border border-danger-subtle text-nowrap"
                    title="Tất cả biến thể của sản phẩm này đã hết hàng"
                  >
                    <i class="bi bi-x-circle me-1"></i>Hết hàng
                  </span>
                </div>
              </td>
              <td class="text-nowrap">
                <div class="d-flex flex-nowrap gap-1">
                  <router-link
                    :to="'/admin/variants?productId=' + p.maSanPham"
                    class="btn btn-sm btn-primary text-white"
                    title="Quản lý chi tiết biến thể theo SKU"
                  >
                    <i class="bi bi-box-seam me-1"></i>Chi tiết SP
                  </router-link>
                  <button
                    @click="editProduct(p)"
                    class="btn btn-sm btn-outline-secondary"
                    title="Sửa thông tin sản phẩm"
                  >
                    <i class="bi bi-pencil me-1"></i>Sửa
                  </button>
                  <button
                    v-if="isPaused(p.trangThai)"
                    @click="toggleStatus(p)"
                    class="btn btn-sm btn-outline-success"
                    title="Đổi trạng thái sang Đang bán"
                  >
                    <i class="bi bi-play-circle me-1"></i>Bán lại
                  </button>
                  <button
                    v-else
                    @click="toggleStatus(p)"
                    class="btn btn-sm btn-outline-warning"
                    title="Đổi trạng thái sang Tạm ngừng"
                  >
                    <i class="bi bi-pause-circle me-1"></i>Tạm ngừng
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredProducts.length === 0">
              <td colspan="6" class="text-center text-muted py-4">Chưa có dữ liệu sản phẩm</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="modal fade" id="variantModal" tabindex="-1">
      <div class="modal-dialog modal-xl modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Biến thể · {{ selectedProduct?.tenSanPham }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="card mb-4 shadow-sm border-0 bg-light">
              <div class="card-body">
                <h6 class="mb-3">{{ isEditingVariant ? 'Cập nhật Biến thể' : 'Thêm Biến thể mới' }}</h6>
                <form @submit.prevent="saveVariant">
                  <div class="row">
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Mã SKU</label>
                      <input type="text" v-model="variantForm.maVachSku" class="form-control" placeholder="Tự sinh" />
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Màu sắc <span class="text-danger">*</span></label>
                      <input type="text" v-model="variantForm.mauSac" class="form-control" required placeholder="VD: Đỏ" />
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Kích cỡ <span class="text-danger">*</span></label>
                      <input type="text" v-model="variantForm.kichCo" class="form-control" required placeholder="VD: XL" />
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Tồn kho <span class="text-danger">*</span></label>
                      <input type="number" v-model="variantForm.soLuongTon" class="form-control" min="0" required />
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label fw-semibold">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                      <input 
                        type="text" 
                        :value="displayVariantPrice" 
                        @input="onVariantPriceInput" 
                        class="form-control" 
                        placeholder="VD: 250,000" 
                        required 
                      />
                    </div>
                    <div class="col-md-2 mb-3 d-flex align-items-end">
                      <button type="submit" class="btn btn-primary w-100" :disabled="loadingVariant">
                        {{ loadingVariant ? 'Đang lưu...' : (isEditingVariant ? 'Cập nhật' : 'Thêm') }}
                      </button>
                    </div>
                  </div>
                  <button type="button" v-if="isEditingVariant" @click="resetVariantForm" class="btn btn-secondary btn-sm">
                    Hủy sửa
                  </button>
                </form>
              </div>
            </div>

            <div v-if="loadingVariant" class="text-center py-4">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                  <tr>
                    <th>Mã vạch (SKU)</th>
                    <th>Màu sắc</th>
                    <th>Kích cỡ</th>
                    <th>Tồn kho</th>
                    <th>Giá bán</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="variant in productVariants" :key="variant.maChiTietSp">
                    <td>
                      <button
                        type="button"
                        class="btn btn-sm btn-outline-secondary font-monospace"
                        @click="copyText(variant.maVachSku || variant.maChiTietSp)"
                        :title="'Click để copy mã SKU: ' + (variant.maVachSku || variant.maChiTietSp)"
                      >
                        <i class="bi bi-clipboard me-1"></i>
                        <span class="fw-bold">#{{ variant.maChiTietSp }}</span>
                        <span v-if="variant.maVachSku"> / {{ variant.maVachSku }}</span>
                      </button>
                    </td>
                    <td>{{ variant.mauSac }}</td>
                    <td>{{ variant.kichCo }}</td>
                    <td>
                      <span class="badge" :class="variant.soLuongTon > 0 ? 'bg-success' : 'bg-danger'">
                        {{ variant.soLuongTon > 0 ? variant.soLuongTon : 'Hết hàng' }}
                      </span>
                    </td>
                    <td class="text-primary fw-bold">{{ formatPrice(variant.giaCongThem || 0) }}</td>
                    <td>
                      <button @click="printBarcode(variant)" class="btn btn-sm btn-outline-dark me-1" title="In tem mã vạch">
                        <i class="bi bi-printer me-1"></i>In mã
                      </button>
                      <button @click="editVariant(variant)" class="btn btn-sm btn-outline-primary me-1">Sửa</button>
                      <button @click="deleteVariant(variant.maChiTietSp)" class="btn btn-sm btn-outline-danger">Xóa</button>
                    </td>
                  </tr>
                  <tr v-if="productVariants.length === 0">
                    <td colspan="6" class="text-center text-muted py-3">Sản phẩm này chưa có biến thể nào</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
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
              <div class="fw-bold fs-6 text-dark text-truncate mb-1">{{ selectedProduct?.tenSanPham }}</div>
              <div class="small text-secondary mb-2">
                Phân loại: <strong>Màu {{ selectedBarcodeVariant.mauSac }} - Size {{ selectedBarcodeVariant.kichCo }}</strong>
              </div>
              <div class="my-2 d-flex justify-content-center" v-html="barcodeSvg"></div>
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

    <!-- Modal Thêm nhanh Danh mục -->
    <div class="modal fade show d-block" tabindex="-1" v-if="showQuickCatModal" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title fw-bold text-primary"><i class="bi bi-tags-fill me-2"></i>Thêm nhanh Danh mục</h5>
            <button type="button" class="btn-close" @click="showQuickCatModal = false"></button>
          </div>
          <div class="modal-body">
            <label class="form-label fw-semibold">Tên danh mục mới <span class="text-danger">*</span></label>
            <input type="text" class="form-control" v-model="quickCatName" placeholder="Ví dụ: Áo khoác thể thao..." @keyup.enter="saveQuickCategory" autofocus />
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showQuickCatModal = false">Hủy</button>
            <button type="button" class="btn btn-primary" :disabled="!quickCatName.trim() || savingCat" @click="saveQuickCategory">
              {{ savingCat ? 'Đang lưu...' : 'Lưu & Chọn' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Thêm nhanh Thương hiệu -->
    <div class="modal fade show d-block" tabindex="-1" v-if="showQuickBrandModal" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title fw-bold text-primary"><i class="bi bi-award-fill me-2"></i>Thêm nhanh Thương hiệu</h5>
            <button type="button" class="btn-close" @click="showQuickBrandModal = false"></button>
          </div>
          <div class="modal-body">
            <label class="form-label fw-semibold">Tên thương hiệu mới <span class="text-danger">*</span></label>
            <input type="text" class="form-control" v-model="quickBrandName" placeholder="Ví dụ: Mizuno, Kappa..." @keyup.enter="saveQuickBrand" autofocus />
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showQuickBrandModal = false">Hủy</button>
            <button type="button" class="btn btn-primary" :disabled="!quickBrandName.trim() || savingBrand" @click="saveQuickBrand">
              {{ savingBrand ? 'Đang lưu...' : 'Lưu & Chọn' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import PageHeader from '@/components/PageHeader.vue'
import { useToast } from '@/composables/useToast.js'
import { API_URL } from '@/config.js'
import { generateBarcodeSVG } from '@/utils/barcode.js'

const { success, error } = useToast()
const products = ref([])
const categories = ref([])
const brands = ref([])
const allVariants = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const uploadingImg = ref(false)

const getProductVariantSummary = (productId) => {
  const vars = allVariants.value.filter(v => v.maSanPham === productId)
  const count = vars.length
  const totalStock = vars.reduce((sum, v) => sum + (Number(v.soLuongTon) || 0), 0)
  return { count, totalStock }
}

// Bộ lọc tìm kiếm
const searchQuery = ref('')
const filterCategory = ref('')
const filterBrand = ref('')
const filterStatus = ref('')

const isPaused = (status) => {
  if (!status) return false
  const s = String(status).toLowerCase()
  return s.includes('tạm') || s.includes('tam') || s.includes('ngừng') || s.includes('ngung')
}

const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchesSearch = p.tenSanPham.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesCategory = filterCategory.value === '' || p.maDanhMuc === Number(filterCategory.value)
    const matchesBrand = filterBrand.value === '' || p.maThuongHieu === Number(filterBrand.value)
    const matchesStatus = filterStatus.value === ''
      || (filterStatus.value === 'Tạm ngừng' && isPaused(p.trangThai))
      || (filterStatus.value === 'Đang bán' && !isPaused(p.trangThai))
    return matchesSearch && matchesCategory && matchesBrand && matchesStatus
  })
})
const copyText = (text) => {
  navigator.clipboard.writeText(String(text)).then(() => success('Đã copy mã SKU'))
}

// Khởi tạo form dữ liệu sản phẩm (Giá đã được chuyển sang quản lý ở Sản phẩm chi tiết)
const form = ref({
  maDanhMuc: '',
  maThuongHieu: '',
  tenSanPham: '',
  moTa: '',
  giaGoc: 0, // Giá chuyển sang quản lý tại Biến thể chi tiết
  trangThai: 'Đang bán',
  hinhAnh: ''
})
const imageUrl = (path) => {
  if (!path) return ''
  const raw = String(path).trim()
  if (raw.startsWith('http://') || raw.startsWith('https://')) return raw
  if (raw.startsWith('/uploads/')) return `${API_URL}${raw}`
  if (raw.startsWith('uploads/')) return `${API_URL}/${raw}`
  return `${API_URL}/uploads/${raw}`
}

const uploadImage = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  uploadingImg.value = true
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await axios.post(`${API_URL}/api/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.value.hinhAnh = res.data.data
    success('Tải ảnh lên thành công')
  } catch (e) {
    error('Không thể tải ảnh lên')
  } finally {
    uploadingImg.value = false
  }
}
const formatPrice = (price) =>
  price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'

const getCategoryName = (id) => categories.value.find((c) => c.maDanhMuc === id)?.tenDanhMuc || id
const getBrandName = (id) => brands.value.find((b) => b.maThuongHieu === id)?.tenThuongHieu || id

const fetchData = async () => {
  loading.value = true
  try {
    const [prodRes, catRes, brandRes, variantRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham`),
      axios.get(`${API_URL}/api/danh-muc`),
      axios.get(`${API_URL}/api/thuong-hieu`),
      axios.get(`${API_URL}/api/chi-tiet-san-pham`).catch(() => ({ data: { data: [] } }))
    ])
    products.value = prodRes.data.data || prodRes.data || []
    categories.value = catRes.data.data || catRes.data || []
    brands.value = brandRes.data.data || brandRes.data || []
    allVariants.value = variantRes.data.data || variantRes.data || []
  } catch (e) {
    error('Không tải được dữ liệu sản phẩm')
  } finally {
    loading.value = false
  }
}

// Lưu thông tin sản phẩm (Thêm mới hoặc Cập nhật)
const saveProduct = async () => {
  loading.value = true
  try {
    // Giá gốc mặc định gửi 0 vì giá chi tiết từng phân loại được quản lý ở Sản phẩm chi tiết
    const payload = {
      maDanhMuc: Number(form.value.maDanhMuc),
      maThuongHieu: Number(form.value.maThuongHieu),
      tenSanPham: form.value.tenSanPham,
      moTa: form.value.moTa,
      giaGoc: 0, // Giá chuyển sang quản lý tại Biến thể chi tiết
      trangThai: form.value.trangThai || 'Đang bán'
    }

    if (isEditing.value) {
      await axios.put(`${API_URL}/api/san-pham/${editingId.value}`, payload)
      if (form.value.hinhAnh) {
        try {
          const imgRes = await axios.get(`${API_URL}/api/hinh-anh/san-pham/${editingId.value}`)
          const existingImgs = imgRes.data.data || []
          if (existingImgs.length > 0) {
            await axios.put(`${API_URL}/api/hinh-anh/${existingImgs[0].maHinhAnh}`, {
              maSanPham: editingId.value,
              duongDanAnh: form.value.hinhAnh,
              laAnhChinh: true
            })
          } else {
            await axios.post(`${API_URL}/api/hinh-anh`, {
              maSanPham: editingId.value,
              duongDanAnh: form.value.hinhAnh,
              laAnhChinh: true
            })
          }
        } catch {}
      }
      success('Cập nhật sản phẩm thành công')
    } else {
      const res = await axios.post(`${API_URL}/api/san-pham`, payload)
      const newProduct = res.data.data
      if (form.value.hinhAnh && newProduct) {
        try {
          await axios.post(`${API_URL}/api/hinh-anh`, {
            maSanPham: newProduct.maSanPham,
            duongDanAnh: form.value.hinhAnh,
            laAnhChinh: true
          })
        } catch {}
      }
      success('Thêm sản phẩm thành công')
    }

    resetForm()
    await fetchData()
  } catch (e) {
    const rawMsg = String(e.response?.data?.message || e.message || '')
    error(rawMsg || 'Lỗi khi lưu sản phẩm')
  } finally {
    loading.value = false
  }
}

// Đổ dữ liệu sản phẩm lên form để sửa
const editProduct = async (product) => {
  isEditing.value = true
  editingId.value = product.maSanPham
  let hinhAnh = ''
  try {
    const res = await axios.get(`${API_URL}/api/hinh-anh/san-pham/${product.maSanPham}`)
    if (res.data.data?.length > 0) hinhAnh = res.data.data[0].duongDanAnh
  } catch {}
  form.value = {
    maDanhMuc: product.maDanhMuc,
    maThuongHieu: product.maThuongHieu,
    tenSanPham: product.tenSanPham,
    moTa: product.moTa || '',
    giaGoc: 0,
    trangThai: isPaused(product.trangThai) ? 'Tạm ngừng' : 'Đang bán',
    hinhAnh
  }
}

const toggleStatus = async (product) => {
  const currentlyPaused = isPaused(product.trangThai)
  const actionText = currentlyPaused ? 'mở bán lại' : 'tạm ngừng bán'
  if (!confirm(`Bạn có chắc muốn ${actionText} sản phẩm "${product.tenSanPham}"?`)) return

  const targetStatus = currentlyPaused ? 'Đang bán' : 'Tạm ngừng'
  try {
    const res = await axios.put(`${API_URL}/api/san-pham/${product.maSanPham}/trang-thai?trangThai=${encodeURIComponent(targetStatus)}`)
    product.trangThai = res.data.data?.trangThai || targetStatus
    success(`Đã cập nhật: "${product.tenSanPham}" -> ${product.trangThai}`)
  } catch (e) {
    try {
      const res = await axios.patch(`${API_URL}/api/san-pham/${product.maSanPham}/trang-thai`)
      product.trangThai = res.data.data?.trangThai || targetStatus
      success(`Đã cập nhật: "${product.tenSanPham}" -> ${product.trangThai}`)
    } catch (err) {
      error('Không thể cập nhật trạng thái sản phẩm: ' + (err.response?.data?.message || err.message))
    }
  }
}

// Reset form sản phẩm
const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    maDanhMuc: '',
    maThuongHieu: '',
    tenSanPham: '',
    moTa: '',
    giaGoc: 0,
    trangThai: 'Đang bán',
    hinhAnh: ''
  }
}

const selectedProduct = ref(null)
const productVariants = ref([])
const loadingVariant = ref(false)
const isEditingVariant = ref(false)
const editingVariantId = ref(null)
const displayVariantPrice = ref('')

const onVariantPriceInput = (event) => {
  const rawDigits = event.target.value.replace(/\D/g, '')
  if (!rawDigits) {
    variantForm.value.giaCongThem = 0
    displayVariantPrice.value = ''
    event.target.value = ''
    return
  }
  const num = Number(rawDigits)
  variantForm.value.giaCongThem = num
  displayVariantPrice.value = new Intl.NumberFormat('en-US').format(num)
  event.target.value = displayVariantPrice.value
}

const variantForm = ref({
  maVachSku: '',
  mauSac: '',
  kichCo: '',
  soLuongTon: 0,
  giaCongThem: 0
})

const openVariantModal = async (product) => {
  selectedProduct.value = product
  resetVariantForm()
  await fetchVariants()
}

const fetchVariants = async () => {
  if (!selectedProduct.value) return
  loadingVariant.value = true
  try {
    const res = await axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${selectedProduct.value.maSanPham}`)
    productVariants.value = res.data.data || res.data || []
  } catch {
    productVariants.value = []
  } finally {
    loadingVariant.value = false
  }
}

const saveVariant = async () => {
  if (!selectedProduct.value) return
  loadingVariant.value = true
  try {
    const payload = {
      maSanPham: selectedProduct.value.maSanPham,
      maVachSku: variantForm.value.maVachSku,
      mauSac: variantForm.value.mauSac,
      kichCo: variantForm.value.kichCo,
      soLuongTon: Number(variantForm.value.soLuongTon),
      giaCongThem: Number(variantForm.value.giaCongThem || 0)
    }
    if (isEditingVariant.value) {
      await axios.put(`${API_URL}/api/chi-tiet-san-pham/${editingVariantId.value}`, payload)
    } else {
      await axios.post(`${API_URL}/api/chi-tiet-san-pham`, payload)
    }
    resetVariantForm()
    await fetchVariants()
    success('Lưu biến thể thành công')
  } catch (e) {
    error(e.response?.data?.message || e.message)
  } finally {
    loadingVariant.value = false
  }
}

const editVariant = (variant) => {
  isEditingVariant.value = true
  editingVariantId.value = variant.maChiTietSp
  const price = variant.giaCongThem || 0
  variantForm.value = {
    maVachSku: variant.maVachSku || '',
    mauSac: variant.mauSac,
    kichCo: variant.kichCo,
    soLuongTon: variant.soLuongTon,
    giaCongThem: price
  }
  displayVariantPrice.value = price ? new Intl.NumberFormat('en-US').format(price) : ''
}

const deleteVariant = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa biến thể này?')) return
  try {
    await axios.delete(`${API_URL}/api/chi-tiet-san-pham/${id}`)
    await fetchVariants()
    productSkuMap.value[selectedProduct.value.maSanPham] = [...productVariants.value]
    success('Đã xóa biến thể')
  } catch {
    error('Không thể xóa biến thể này')
  }
}

const resetVariantForm = () => {
  isEditingVariant.value = false
  editingVariantId.value = null
  variantForm.value = {
    maVachSku: '',
    mauSac: '',
    kichCo: '',
    soLuongTon: 0,
    giaCongThem: 0
  }
  displayVariantPrice.value = ''
}

const showBarcodeModal = ref(false)
const selectedBarcodeVariant = ref(null)
const barcodeSvg = ref('')

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

// Thêm nhanh Danh mục
const showQuickCatModal = ref(false)
const quickCatName = ref('')
const savingCat = ref(false)

const openQuickCategoryModal = () => {
  quickCatName.value = ''
  showQuickCatModal.value = true
}

const saveQuickCategory = async () => {
  if (!quickCatName.value.trim()) return
  savingCat.value = true
  try {
    const res = await axios.post(`${API_URL}/api/danh-muc`, { tenDanhMuc: quickCatName.value.trim() })
    success('Thêm danh mục thành công')
    await fetchData()
    if (res.data?.data?.maDanhMuc) {
      form.value.maDanhMuc = res.data.data.maDanhMuc
    }
    showQuickCatModal.value = false
  } catch (err) {
    error('Lỗi khi thêm danh mục: ' + (err.response?.data?.message || err.message))
  } finally {
    savingCat.value = false
  }
}

// Thêm nhanh Thương hiệu
const showQuickBrandModal = ref(false)
const quickBrandName = ref('')
const savingBrand = ref(false)

const openQuickBrandModal = () => {
  quickBrandName.value = ''
  showQuickBrandModal.value = true
}

const saveQuickBrand = async () => {
  if (!quickBrandName.value.trim()) return
  savingBrand.value = true
  try {
    const res = await axios.post(`${API_URL}/api/thuong-hieu`, { tenThuongHieu: quickBrandName.value.trim() })
    success('Thêm thương hiệu thành công')
    await fetchData()
    if (res.data?.data?.maThuongHieu) {
      form.value.maThuongHieu = res.data.data.maThuongHieu
    }
    showQuickBrandModal.value = false
  } catch (err) {
    error('Lỗi khi thêm thương hiệu: ' + (err.response?.data?.message || err.message))
  } finally {
    savingBrand.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.preview-box,
.preview-empty {
  width: 88px;
  height: 88px;
  border-radius: 14px;
  border: 1px solid var(--sp-border);
  overflow: hidden;
  display: grid;
  place-items: center;
  background: #f8fafc;
}

.preview-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-empty {
  color: #94a3b8;
  font-size: 1.8rem;
}
</style>
