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
            <div class="col-md-3 mb-3">
              <label class="form-label">Danh mục <span class="text-danger">*</span></label>
              <select v-model="form.maDanhMuc" class="form-select" required>
                <option value="">Chọn danh mục</option>
                <option v-for="c in categories" :key="c.maDanhMuc" :value="c.maDanhMuc">{{ c.tenDanhMuc }}</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Thương hiệu <span class="text-danger">*</span></label>
              <select v-model="form.maThuongHieu" class="form-select" required>
                <option value="">Chọn thương hiệu</option>
                <option v-for="b in brands" :key="b.maThuongHieu" :value="b.maThuongHieu">{{ b.tenThuongHieu }}</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Giá gốc <span class="text-danger">*</span></label>
              <input type="number" v-model="form.giaGoc" class="form-control" min="0" required />
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Giá khuyến mãi</label>
              <input type="number" v-model="form.giaKhuyenMai" class="form-control" min="0" />
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

    <div v-else class="table-responsive bg-white rounded shadow-sm">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>ID</th>
            <th>Tên sản phẩm</th>
            <th>Danh mục</th>
            <th>Thương hiệu</th>
            <th>Giá</th>
            <th>SKU / Biến thể</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.maSanPham">
            <td>#{{ p.maSanPham }}</td>
            <td class="fw-bold">{{ p.tenSanPham }}</td>
            <td>{{ getCategoryName(p.maDanhMuc) }}</td>
            <td>{{ getBrandName(p.maThuongHieu) }}</td>
            <td>
              <div class="fw-semibold text-danger">{{ formatPrice(p.giaKhuyenMai || p.giaGoc) }}</div>
              <small v-if="p.giaKhuyenMai" class="text-muted text-decoration-line-through">{{ formatPrice(p.giaGoc) }}</small>
            </td>
            <td>
              <div class="d-flex flex-wrap gap-1">
                <button
                  v-for="v in (productSkuMap[p.maSanPham] || []).slice(0, 3)"
                  :key="v.maChiTietSp"
                  class="btn btn-sm btn-outline-secondary"
                  type="button"
                  @click="copyText(v.maVachSku || v.maChiTietSp)"
                  :title="'Copy ' + (v.maVachSku || v.maChiTietSp)"
                >
                  <i class="bi bi-clipboard me-1"></i>{{ v.maVachSku || v.maChiTietSp }}
                </button>
                <span v-if="(productSkuMap[p.maSanPham] || []).length > 3" class="badge bg-light text-dark border">
                  +{{ (productSkuMap[p.maSanPham] || []).length - 3 }}
                </span>
              </div>
            </td>
            <td>
              <div class="d-flex flex-wrap gap-1">
                <button
                  class="btn btn-sm btn-outline-primary"
                  data-bs-toggle="modal"
                  data-bs-target="#variantModal"
                  @click="openVariantModal(p)"
                >
                  Biến thể
                </button>
                <button @click="editProduct(p)" class="btn btn-sm btn-outline-secondary">Sửa</button>
                <button @click="deleteProduct(p.maSanPham)" class="btn btn-sm btn-outline-danger">Xóa</button>
              </div>
            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="7" class="text-center text-muted py-4">Chưa có dữ liệu sản phẩm</td>
          </tr>
        </tbody>
      </table>
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
                      <label class="form-label">Giá cộng thêm</label>
                      <input type="number" v-model="variantForm.giaCongThem" class="form-control" min="0" />
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
                    <th>Giá (+)</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="variant in productVariants" :key="variant.maChiTietSp">
                    <td class="fw-bold text-secondary">
                      {{ variant.maChiTietSp }}
                      <span v-if="variant.maVachSku"> / {{ variant.maVachSku }}</span>
                    </td>
                    <td>{{ variant.mauSac }}</td>
                    <td>{{ variant.kichCo }}</td>
                    <td>
                      <span class="badge" :class="variant.soLuongTon > 0 ? 'bg-success' : 'bg-danger'">
                        {{ variant.soLuongTon > 0 ? variant.soLuongTon : 'Hết hàng' }}
                      </span>
                    </td>
                    <td class="text-danger fw-semibold">+{{ formatPrice(variant.giaCongThem || 0) }}</td>
                    <td>
                      <button @click="editVariant(variant)" class="btn btn-sm btn-outline-primary me-2">Sửa</button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import PageHeader from '@/components/PageHeader.vue'
import { useToast } from '@/composables/useToast.js'
import { API_URL } from '@/config.js'

const { success, error } = useToast()
const products = ref([])
const categories = ref([])
const brands = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const uploadingImg = ref(false)
const productSkuMap = ref({})

const copyText = (text) => {
  navigator.clipboard.writeText(String(text)).then(() => success('Đã copy mã SKU'))
}

const form = ref({
  maDanhMuc: '',
  maThuongHieu: '',
  tenSanPham: '',
  moTa: '',
  giaGoc: '',
  giaKhuyenMai: '',
  hinhAnh: ''
})
const imageUrl = (path) => {
  if (!path) return ''
  if (String(path).startsWith('http')) return path
  if (String(path).startsWith('/uploads/')) return `${API_URL}${path}`
  if (String(path).startsWith('uploads/')) return `${API_URL}/${path}`
  return `${API_URL}/uploads/${path}`
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
    const [prodRes, catRes, brandRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham`),
      axios.get(`${API_URL}/api/danh-muc`),
      axios.get(`${API_URL}/api/thuong-hieu`)
    ])
    products.value = prodRes.data.data || prodRes.data || []
    categories.value = catRes.data.data || catRes.data || []
    brands.value = brandRes.data.data || brandRes.data || []
    const skuMap = {}
    for (const p of products.value) {
      try {
        const ctRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${p.maSanPham}`)
        skuMap[p.maSanPham] = ctRes.data.data || ctRes.data || []
      } catch {
        skuMap[p.maSanPham] = []
      }
    }
    productSkuMap.value = skuMap
  } catch (e) {
    error('Không tải được dữ liệu sản phẩm')
  } finally {
    loading.value = false
  }
}

const saveProduct = async () => {
  loading.value = true
  try {
    const payload = {
      maDanhMuc: Number(form.value.maDanhMuc),
      maThuongHieu: Number(form.value.maThuongHieu),
      tenSanPham: form.value.tenSanPham,
      moTa: form.value.moTa,
      giaGoc: Number(form.value.giaGoc),
      giaKhuyenMai: Number(form.value.giaKhuyenMai)
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
    error(e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}

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
    giaGoc: product.giaGoc || product.GiGoc || 0,
    giaKhuyenMai: product.giaKhuyenMai || product.GiKhuyenMai || 0,
    hinhAnh
  }
}

const deleteProduct = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return
  try {
    await axios.delete(`${API_URL}/api/san-pham/${id}`)
    success('Đã xóa sản phẩm')
    await fetchData()
  } catch {
    error('Không thể xóa sản phẩm này')
  }
}

const resetForm = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    maDanhMuc: '',
    maThuongHieu: '',
    tenSanPham: '',
    moTa: '',
    giaGoc: '',
    giaKhuyenMai: '',
    hinhAnh: ''
  }
}

const selectedProduct = ref(null)
const productVariants = ref([])
const loadingVariant = ref(false)
const isEditingVariant = ref(false)
const editingVariantId = ref(null)
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
    productSkuMap.value[selectedProduct.value.maSanPham] = [...productVariants.value]
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
  variantForm.value = {
    maVachSku: variant.maVachSku || '',
    mauSac: variant.mauSac,
    kichCo: variant.kichCo,
    soLuongTon: variant.soLuongTon,
    giaCongThem: variant.giaCongThem || 0
  }
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
