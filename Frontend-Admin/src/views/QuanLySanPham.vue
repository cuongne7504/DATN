<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý Sản phẩm</h2>

    <!-- Form thêm/sửa sản phẩm -->
    <div class="card mb-4 shadow-sm">
      <div class="card-body">
        <h5 class="mb-3">{{ isEditing ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm mới' }}</h5>
        <form @submit.prevent="saveProduct">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
              <input type="text" v-model="form.tenSanPham" class="form-control" required>
            <!-- Modal Biến Thể -->
    <div class="modal fade" id="variantModal" tabindex="-1">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Biến thể - {{ selectedProduct?.tenSanPham }}</h5>
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
                      <input type="text" v-model="variantForm.maVachSku" class="form-control" placeholder="Tự sinh">
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Màu sắc <span class="text-danger">*</span></label>
                      <input type="text" v-model="variantForm.mauSac" class="form-control" required placeholder="VD: Đỏ">
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Kích cỡ <span class="text-danger">*</span></label>
                      <input type="text" v-model="variantForm.kichCo" class="form-control" required placeholder="VD: XL">
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Tồn kho <span class="text-danger">*</span></label>
                      <input type="number" v-model="variantForm.soLuongTon" class="form-control" min="0" required>
                    </div>
                    <div class="col-md-2 mb-3">
                      <label class="form-label">Giá cộng thêm</label>
                      <input type="number" v-model="variantForm.giaCongThem" class="form-control" min="0">
                    </div>
                    <div class="col-md-2 mb-3 d-flex align-items-end">
                      <button type="submit" class="btn btn-primary w-100" :disabled="loadingVariant">
                        {{ loadingVariant ? 'Đang lưu...' : (isEditingVariant ? 'Cập nhật' : 'Thêm') }}
                      </button>
                    </div>
                  </div>
                  <button type="button" v-if="isEditingVariant" @click="resetVariantForm" class="btn btn-secondary btn-sm">Hủy sửa</button>
                </form>
              </div>
            </div>

            <div v-if="loadingVariant" class="text-center py-4">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
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
                    <td class="fw-bold text-secondary">{{ variant.maChiTietSp }} <span v-if="variant.maVachSku">/ {{ variant.maVachSku }}</span></td>
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
              <input type="number" v-model="form.giaGoc" class="form-control" min="0" required>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label">Giá khuyến mãi <span class="text-danger">*</span></label>
              <input type="number" v-model="form.giaKhuyenMai" class="form-control" min="0" required>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12 mb-3">
              <label class="form-label fw-semibold">Ảnh sản phẩm</label>
              <div class="d-flex align-items-center gap-3">
                <div v-if="form.hinhAnh" class="position-relative border rounded" style="width: 120px; height: 120px; overflow: hidden;">
                  <img :src="form.hinhAnh.startsWith('http') ? form.hinhAnh : `${API_URL}/api/hinh-anh/uploads/${form.hinhAnh}`" class="w-100 h-100" style="object-fit: cover;">
                  <button @click="form.hinhAnh = ''" type="button" class="btn-close position-absolute top-0 end-0 bg-white m-1 p-2 shadow-sm rounded-circle" style="opacity: 0.9;" aria-label="Xóa"></button>
                </div>
                <div v-else class="bg-light border rounded d-flex align-items-center justify-content-center text-muted" style="width: 120px; height: 120px;">
                  <i class="bi bi-image" style="font-size: 2.5rem;"></i>
                </div>
                <div class="flex-grow-1">
                  <input type="file" @change="uploadImage" class="form-control form-control-lg" accept="image/*" :disabled="uploadingImg">
                  <div class="form-text mt-2 text-muted"><i class="bi bi-info-circle me-1"></i>Chỉ chấp nhận file ảnh (PNG, JPG, JPEG).</div>
                </div>
              </div>
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Đang lưu...' : (isEditing ? 'Cập nhật' : 'Thêm mới') }}
          </button>
          <button type="button" v-if="isEditing" @click="resetForm" class="btn btn-secondary ms-2">Hủy</button>
        </form>
      </div>
    </div>

    <!-- Danh sách sản phẩm -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="table-responsive bg-white rounded shadow-sm p-3">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>ID</th>
            <th>Tên sản phẩm</th>
            <th>Danh mục</th>
            <th>Thương hiệu</th>
            <th>Giá gốc</th>
            <th>Giá KM</th>
            <th>Mã biến thể</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.maSanPham">
            <td>{{ product.maSanPham }}</td>
            <td class="fw-bold">{{ product.tenSanPham }}</td>
            <td>{{ getCategoryName(product.maDanhMuc) }}</td>
            <td>{{ getBrandName(product.maThuongHieu) }}</td>
            <td>{{ formatPrice(product.giaGoc) }}</td>
            <td><span class="text-danger fw-bold">{{ formatPrice(product.giaKhuyenMai) }}</span></td>
            <td>
              <div v-if="productSkuMap[product.maSanPham] && productSkuMap[product.maSanPham].length > 0">
                <div v-for="sku in productSkuMap[product.maSanPham]" :key="sku.maChiTietSp" class="d-flex align-items-center gap-1 mb-1">
                  <code class="bg-light border rounded px-1 small">{{ sku.maChiTietSp }}{{ sku.maVachSku ? ' / ' + sku.maVachSku : '' }}</code>
                  <button @click="copyText(sku.maChiTietSp)" class="btn btn-xs btn-outline-secondary py-0 px-1" style="font-size:11px;" title="Copy mã">
                    <i class="bi bi-clipboard"></i>
                  </button>
                </div>
              </div>
              <span v-else class="text-muted small">Chưa có biến thể</span>
            </td>
            <td>
              <button @click="openVariantModal(product)" class="btn btn-sm btn-outline-info me-1" data-bs-toggle="modal" data-bs-target="#variantModal">Biến thể</button>
              <button @click="editProduct(product)" class="btn btn-sm btn-outline-primary me-1">Sửa</button>
              <button @click="deleteProduct(product.maSanPham)" class="btn btn-sm btn-outline-danger">Xóa</button>
            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="7" class="text-center text-muted py-3">Chưa có dữ liệu sản phẩm</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

import { API_URL } from '@/config.js'

const products = ref([])
const categories = ref([])
const brands = ref([])
const loading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const uploadingImg = ref(false)
const productSkuMap = ref({})

const copyText = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    // Optionally you can show a toast here, but simple alert is annoying. Let's just rely on the click.
  })
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

const uploadImage = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  uploadingImg.value = true
  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await axios.post(`${API_URL}/api/hinh-anh/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.value.hinhAnh = res.data.data // filename returned from backend
    alert('Tải ảnh lên thành công!')
  } catch (error) {
    console.error('Lỗi upload ảnh:', error)
    alert('Không thể tải ảnh lên!')
  } finally {
    uploadingImg.value = false
  }
}

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const getCategoryName = (id) => {
  const cat = categories.value.find(c => c.maDanhMuc === id)
  return cat ? cat.tenDanhMuc : id
}

const getBrandName = (id) => {
  const brand = brands.value.find(b => b.maThuongHieu === id)
  return brand ? brand.tenThuongHieu : id
}

const fetchData = async () => {
  loading.value = true
  try {
    const [prodRes, catRes, brandRes] = await Promise.all([
      axios.get(`${API_URL}/api/san-pham`),
      axios.get(`${API_URL}/api/danh-muc`),
      axios.get(`${API_URL}/api/thuong-hieu`)
    ])
    
    // Spring Boot returns raw list or ApiResponse depending on how I merged it. 
    // Handle both cases.
    products.value = prodRes.data.data || prodRes.data || []
    categories.value = catRes.data.data || catRes.data || []
    brands.value = brandRes.data.data || brandRes.data || []
    // Fetch SKUs for each product
    const skuMap = {}
    for (let p of products.value) {
      try {
        const ctRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/san-pham/${p.maSanPham}`)
        skuMap[p.maSanPham] = ctRes.data.data || ctRes.data || []
      } catch (e) {
        skuMap[p.maSanPham] = []
      }
    }
    productSkuMap.value = skuMap
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu:', error)
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
      // Save or update image
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
        } catch(e) {}
      }
      alert('Cập nhật sản phẩm thành công!')
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
        } catch(e) {}
      }
      alert('Thêm sản phẩm thành công!')
    }
    
    resetForm()
    await fetchData()
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error)
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
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
    if (res.data.data && res.data.data.length > 0) {
      hinhAnh = res.data.data[0].duongDanAnh
    }
  } catch(e) {}

  form.value = {
    maDanhMuc: product.maDanhMuc,
    maThuongHieu: product.maThuongHieu,
    tenSanPham: product.tenSanPham,
    moTa: product.moTa || '',
    giaGoc: product.giaGoc || product.GiGoc || 0, // Fallback if backend casing is weird
    giaKhuyenMai: product.giaKhuyenMai || product.GiKhuyenMai || 0,
    hinhAnh: hinhAnh
  }
}

const deleteProduct = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return
  try {
    await axios.delete(`${API_URL}/api/san-pham/${id}`)
    await fetchData()
  } catch (error) {
    console.error('Lỗi khi xóa:', error)
    alert('Không thể xóa sản phẩm này!')
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
    giaNhap: '',
    giaGoc: '',
    giaKhuyenMai: '',
    hinhAnh: ''
  }
}

// --- VARIANT MANAGEMENT LOGIC ---
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
  } catch (error) {
    console.error('Lỗi khi tải biến thể:', error)
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
    // Cập nhật lại list biến thể ngoài màn hình chính
    productSkuMap.value[selectedProduct.value.maSanPham] = [...productVariants.value]
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
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
    // Cập nhật lại list biến thể ngoài màn hình chính
    productSkuMap.value[selectedProduct.value.maSanPham] = [...productVariants.value]
  } catch (error) {
    alert('Không thể xóa biến thể này!')
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


onMounted(() => {
  fetchData()
})
</script>
