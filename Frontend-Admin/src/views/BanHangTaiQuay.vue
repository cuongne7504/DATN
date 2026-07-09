<template>
  <div class="container mt-4">
    <h2 class="mb-4 text-warning fw-bold">POS - Bán tại quầy</h2>

    <div class="row">
      <div class="col-md-4">
        <div class="card shadow-sm border-0">
          <div class="card-body bg-light rounded">
            <h5 class="fw-bold text-primary mb-3"><i class="bi bi-cart-plus"></i> Thêm sản phẩm vào đơn</h5>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Quét mã vạch / Mã biến thể</label>
              <input 
                type="text" 
                v-model="barcodeInput" 
                @keyup.enter="scanBarcode"
                ref="barcodeInputRef"
                class="form-control form-control-lg border-primary" 
                placeholder="Quét hoặc nhập mã..."
                autofocus
              >
              <div class="form-text">Nhập mã biến thể (SKU) hoặc ID và nhấn Enter.</div>
            </div>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Số lượng</label>
              <input type="number" v-model="tempForm.soLuong" class="form-control" value="1" min="1">
            </div>
            
            <div v-if="scannedProduct" class="alert alert-info border-info mb-3">
              <strong>{{ scannedProduct.tenSanPham || 'Sản phẩm' }}</strong><br>
              <small>Màu: {{ scannedProduct.mauSac }} - Size: {{ scannedProduct.kichCo }}</small><br>
              <strong>Giá: <span class="text-danger">{{ formatPrice(scannedProduct.giaBan || scannedProduct.giaKhuyenMai || scannedProduct.GiBan) }}</span></strong><br>
              <small>Tồn kho: {{ scannedProduct.soLuongTon }}</small>
            </div>
            
            <button @click="addItem" class="btn btn-primary btn-lg w-100 fw-bold">Thêm vào đơn</button>
          </div>
        </div>
      </div>

      <div class="col-md-8">
        <div class="card shadow-sm border-0 h-100">
          <div class="card-body">
            <h5 class="fw-bold mb-3"><i class="bi bi-receipt"></i> Chi tiết đơn hàng mới</h5>
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label fw-semibold">Khách hàng (Tùy chọn)</label>
                <input type="text" v-model="posForm.tenNguoiNhan" class="form-control" placeholder="Tên khách hàng">
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Số điện thoại</label>
                <input type="text" v-model="posForm.soDienThoai" class="form-control" placeholder="SĐT khách hàng">
              </div>
            </div>

            <!-- Thêm ô áp dụng mã giảm giá -->
            <div class="row mb-4">
              <div class="col-md-12">
                <label class="form-label fw-semibold">Mã giảm giá (Voucher)</label>
                <div class="input-group">
                  <input type="text" v-model="voucherCodeInput" class="form-control" placeholder="Nhập mã voucher" :disabled="appliedVoucher">
                  <button v-if="!appliedVoucher" @click="applyVoucher" class="btn btn-outline-primary" type="button" :disabled="applyingVoucher || posForm.items.length === 0">
                    {{ applyingVoucher ? 'Đang check...' : 'Áp dụng' }}
                  </button>
                  <button v-else @click="removeVoucher" class="btn btn-outline-danger" type="button">
                    Hủy áp dụng
                  </button>
                </div>
                <div v-if="appliedVoucher" class="form-text text-success mt-1">
                  <i class="bi bi-patch-check-fill"></i> Đã áp dụng mã <strong>{{ appliedVoucher.maCode }}</strong>: Giảm {{ appliedVoucher.phanTramGiam }}% tổng đơn.
                </div>
              </div>
            </div>

            <div v-if="posForm.items.length === 0" class="text-center text-muted p-5 bg-light rounded border border-dashed">
              <i class="bi bi-cart-x" style="font-size: 2rem;"></i><br>
              Chưa có sản phẩm nào trong đơn
            </div>

            <div v-else class="table-responsive mb-3">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Biến thể</th>
                    <th style="width: 100px">Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in posForm.items" :key="index">
                    <td class="fw-bold">{{ item.tenSanPham }}</td>
                    <td>{{ item.mauSac }} / {{ item.kichCo }}</td>
                    <td>
                      <input 
                        type="number" 
                        v-model="item.soLuong" 
                        class="form-control form-control-sm" 
                        min="1"
                        @change="updateItemTotal(index)"
                      >
                    </td>
                    <td>{{ formatPrice(item.donGia) }}</td>
                    <td class="fw-bold text-danger">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                    <td>
                      <button @click="removeItem(index)" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> Xóa</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Hiển thị tóm tắt chi phí thanh toán -->
            <div class="border-top pt-3">
              <div class="d-flex justify-content-between mb-2">
                <span class="text-muted fw-semibold">Tạm tính:</span>
                <span class="fw-bold">{{ formatPrice(subTotal) }}</span>
              </div>
              <div v-if="appliedVoucher" class="d-flex justify-content-between mb-2 text-success">
                <span class="fw-semibold">Khuyến mãi ({{ appliedVoucher.phanTramGiam }}%):</span>
                <span class="fw-bold">- {{ formatPrice(discountAmount) }}</span>
              </div>
              <div class="d-flex justify-content-between align-items-center mb-1">
                <h4 class="fw-bold mb-0">Tổng cộng: <span class="text-danger">{{ formatPrice(finalTotal) }}</span></h4>
                <button @click="createPosOrder" class="btn btn-success btn-lg px-5 fw-bold" :disabled="posForm.items.length === 0 || loading">
                  {{ loading ? 'Đang xử lý...' : 'THANH TOÁN' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { API_URL } from '@/config.js'

const loading = ref(false)
const barcodeInput = ref('')
const barcodeInputRef = ref(null)
const scannedProduct = ref(null)

const posForm = ref({
  maNhanVien: 1, // ID của nhân viên bán hàng (từ localStorage)
  tenNguoiNhan: '',
  soDienThoai: '',
  items: []
})

const tempForm = ref({
  soLuong: 1
})

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

// === CẤU HÌNH VOUCHER TRÊN POS ===
const voucherCodeInput = ref('')
const appliedVoucher = ref(null)
const applyingVoucher = ref(false)

const subTotal = computed(() => {
  return posForm.value.items.reduce((sum, item) => sum + (item.soLuong * item.donGia), 0)
})

const discountAmount = computed(() => {
  if (!appliedVoucher.value) return 0
  return (subTotal.value * appliedVoucher.value.phanTramGiam) / 100
})

const finalTotal = computed(() => {
  return Math.max(0, subTotal.value - discountAmount.value)
})

const applyVoucher = async () => {
  if (!voucherCodeInput.value.trim()) return
  
  applyingVoucher.value = true
  try {
    const res = await axios.get(`${API_URL}/api/khuyen-mai`)
    const vouchers = res.data.data || res.data || []
    
    const voucher = vouchers.find(v => v.maCode.toUpperCase() === voucherCodeInput.value.trim().toUpperCase())
    
    if (voucher) {
      if (new Date(voucher.ngayKetThuc) < new Date() || new Date(voucher.ngayBatDau) > new Date()) {
        alert('Mã khuyến mãi đã hết hạn hoặc chưa có hiệu lực!')
        return
      }
      if (voucher.soLuongDung != null && voucher.soLuongDung <= 0) {
        alert('Mã khuyến mãi đã hết lượt sử dụng!')
        return
      }
      appliedVoucher.value = voucher
      alert(`Áp dụng mã giảm giá thành công! Giảm ${voucher.phanTramGiam}%`)
    } else {
      alert('Mã khuyến mãi không tồn tại!')
      appliedVoucher.value = null
    }
  } catch (error) {
    console.error('Lỗi check voucher', error)
    alert('Lỗi kiểm tra mã khuyến mãi')
  } finally {
    applyingVoucher.value = false
  }
}

const removeVoucher = () => {
  appliedVoucher.value = null
  voucherCodeInput.value = ''
}

const scanBarcode = async () => {
  if (!barcodeInput.value) return
  const input = barcodeInput.value.trim()
  
  try {
    let data = null
    
    // Thử tìm bằng mã SKU trước
    try {
      const skuRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/sku/${input}`)
      data = skuRes.data.data || skuRes.data
    } catch (e) {
      // Nếu không tìm thấy bằng SKU, thử bằng ID số
      if (!isNaN(input)) {
        const idRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/${input}`)
        data = idRes.data.data || idRes.data
      }
    }
    
    if (data && data.maChiTietSp) {
      scannedProduct.value = data
      
      // Lấy tên sản phẩm và giá
      try {
        const prodRes = await axios.get(`${API_URL}/api/san-pham/${data.maSanPham}`)
        const prodData = prodRes.data.data || prodRes.data
        if (prodData) {
          scannedProduct.value.tenSanPham = prodData.tenSanPham
          scannedProduct.value.giaBan = prodData.giaKhuyenMai || prodData.giaGoc || 0
        }
      } catch (e) {}

    } else {
      alert('Không tìm thấy biến thể với mã: ' + input)
      scannedProduct.value = null
    }
  } catch (error) {
    console.error('Lỗi khi quét mã:', error)
    alert('Không tìm thấy biến thể với mã: ' + input)
    scannedProduct.value = null
  }
}

const addItem = async () => {
  if (!scannedProduct.value) {
    if (barcodeInput.value) {
      await scanBarcode()
      if (!scannedProduct.value) return // If still null after scan, stop
    } else {
      alert('Vui lòng quét mã và tìm sản phẩm trước')
      return
    }
  }
  
  if (tempForm.value.soLuong > scannedProduct.value.soLuongTon) {
    alert(`Tồn kho không đủ! Chỉ còn ${scannedProduct.value.soLuongTon} sản phẩm.`)
    return
  }

  const existingIndex = posForm.value.items.findIndex(
    item => item.maChiTietSp === scannedProduct.value.maChiTietSp
  )
  
  if (existingIndex >= 0) {
    const newQty = posForm.value.items[existingIndex].soLuong + parseInt(tempForm.value.soLuong)
    if (newQty > scannedProduct.value.soLuongTon) {
      alert('Số lượng cộng dồn vượt quá tồn kho!')
      return
    }
    posForm.value.items[existingIndex].soLuong = newQty
  } else {
    posForm.value.items.push({
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
  
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
}

const updateItemTotal = (index) => {
  const item = posForm.value.items[index]
  if (item.soLuong > item.tonKho) {
    alert('Vượt quá số lượng tồn kho!')
    item.soLuong = item.tonKho
  }
}

const removeItem = (index) => {
  posForm.value.items.splice(index, 1)
}

const createPosOrder = async () => {
  if (posForm.value.items.length === 0) {
    alert('Vui lòng thêm sản phẩm vào đơn')
    return
  }

  loading.value = true
  try {
    const user = JSON.parse(localStorage.getItem('user'))
    const payload = {
      maNhanVien: user ? user.maNguoiDung : 1, // ID của nv
      tenNguoiNhan: posForm.value.tenNguoiNhan || 'Khách Mua Tại Quầy',
      soDienThoai: posForm.value.soDienThoai || '0000000000',
      loaiDonHang: 'TaiQuay',
      phuongThucThanhToan: 'TienMat',
      trangThai: 'HoanThanh', // Đơn POS thì hoàn thành luôn
      maKhuyenMai: appliedVoucher.value ? appliedVoucher.value.maKhuyenMai : null, // Gửi mã giảm giá lên backend
      items: posForm.value.items.map(item => ({
        maChiTietSp: item.maChiTietSp,
        soLuong: item.soLuong,
        donGia: item.donGia
      }))
    }
    
    // Gửi POST tới API backend
    const response = await axios.post(`${API_URL}/api/don-hang/pos`, payload)
    alert('Thanh toán thành công! Mã đơn: ' + (response.data.data?.maDonHang || ''))
    
    // Reset form
    posForm.value = {
      maNhanVien: user ? user.maNguoiDung : 1,
      tenNguoiNhan: '',
      soDienThoai: '',
      items: []
    }
    // Reset voucher
    appliedVoucher.value = null
    voucherCodeInput.value = ''
  } catch (error) {
    console.error('Lỗi khi thanh toán:', error)
    alert('Lỗi: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  barcodeInputRef.value?.focus()
})
</script>

<style scoped>
.border-dashed {
  border-style: dashed !important;
  border-width: 2px !important;
}
</style>
