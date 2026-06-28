<template>
  <div class="container mt-4">
    <h2 class="mb-4 fw-bold">Thanh Toán</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="cartItems.length === 0" class="text-center py-5">
      <h4 class="text-muted">Không có sản phẩm nào để thanh toán</h4>
      <router-link to="/" class="btn btn-primary mt-3">Tiếp tục mua sắm</router-link>
    </div>

    <div v-else class="row">
      <!-- Thông tin nhận hàng -->
      <div class="col-md-7 mb-4">
        <div class="card shadow-sm border-0">
          <div class="card-body">
            <h5 class="fw-bold mb-4 border-bottom pb-2">Thông tin giao hàng</h5>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
              <input type="text" v-model="form.tenNguoiNhan" class="form-control" required placeholder="Nhập họ tên người nhận">
            </div>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
              <input type="tel" v-model="form.soDienThoai" class="form-control" required placeholder="Nhập số điện thoại">
            </div>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Địa chỉ giao hàng <span class="text-danger">*</span></label>
              <textarea v-model="form.diaChiGiaoHang" class="form-control" rows="2" required placeholder="Nhập địa chỉ chi tiết"></textarea>
            </div>
            
            <div class="mb-3">
              <label class="form-label fw-semibold">Ghi chú (Tùy chọn)</label>
              <textarea v-model="form.ghiChu" class="form-control" rows="2" placeholder="Ghi chú thêm cho đơn hàng..."></textarea>
            </div>

            <h5 class="fw-bold mb-3 mt-4 border-bottom pb-2">Phương thức thanh toán</h5>
            <div class="form-check border p-3 rounded mb-2" :class="{'border-primary bg-light': form.phuongThucThanhToan === 'TienMat'}">
              <input class="form-check-input ms-1" type="radio" v-model="form.phuongThucThanhToan" value="TienMat" id="ptTienMat">
              <label class="form-check-label fw-semibold ms-2 w-100" for="ptTienMat" style="cursor: pointer;">
                Thanh toán khi nhận hàng (COD)
              </label>
            </div>
            <div class="form-check border p-3 rounded" :class="{'border-primary bg-light': form.phuongThucThanhToan === 'VNPay'}">
              <input class="form-check-input ms-1" type="radio" v-model="form.phuongThucThanhToan" value="VNPay" id="ptVNPay">
              <label class="form-check-label fw-semibold ms-2 w-100 text-primary" for="ptVNPay" style="cursor: pointer;">
                Thanh toán trực tuyến qua VNPay
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- Đơn hàng -->
      <div class="col-md-5">
        <div class="card shadow-sm border-0 bg-light">
          <div class="card-body p-4">
            <h5 class="fw-bold mb-4">Chi tiết đơn hàng</h5>
            
            <div class="d-flex justify-content-between align-items-center mb-3 border-bottom pb-2" v-for="item in cartItems" :key="item.maCtGioHang">
              <div>
                <div class="fw-bold">{{ item.tenSanPham || 'Sản phẩm' }}</div>
                <small class="text-muted">SL: {{ item.soLuong }} | Size: {{ item.kichCo }} - Màu: {{ item.mauSac }}</small>
              </div>
              <div class="fw-bold text-end">{{ formatPrice(item.soLuong * (item.donGia || 0)) }}</div>
            </div>

            <!-- Khuyến Mãi -->
            <div class="mt-4 mb-3 pb-3 border-bottom">
              <label class="form-label fw-semibold">Mã khuyến mãi (Voucher)</label>
              <div class="input-group">
                <input type="text" v-model="voucherCodeInput" class="form-control" placeholder="Nhập mã..." :disabled="appliedVoucher">
                <button v-if="!appliedVoucher" @click="applyVoucher" class="btn btn-outline-primary fw-bold" type="button" :disabled="applyingVoucher">Áp dụng</button>
                <button v-else @click="removeVoucher" class="btn btn-outline-danger fw-bold" type="button">Xóa mã</button>
              </div>
              <div v-if="appliedVoucher" class="text-success small mt-1">
                <i class="bi bi-check-circle-fill"></i> Đã áp dụng giảm {{ appliedVoucher.phanTramGiam }}%
              </div>
            </div>

            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Tổng tiền hàng:</span>
              <span class="fw-bold">{{ formatPrice(subTotal) }}</span>
            </div>
            
            <div v-if="discountAmount > 0" class="d-flex justify-content-between mb-2 text-success">
              <span>Khuyến mãi giảm:</span>
              <span class="fw-bold">-{{ formatPrice(discountAmount) }}</span>
            </div>
            
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Phí giao hàng:</span>
              <span class="fw-bold">Miễn phí</span>
            </div>
            
            <hr>
            <div class="d-flex justify-content-between mb-4 align-items-end">
              <span class="fw-bold fs-5">Thành tiền:</span>
              <span class="fw-bold fs-3 text-danger">{{ formatPrice(finalTotal) }}</span>
            </div>
            
            <button @click="submitOrder" class="btn btn-danger btn-lg w-100 fw-bold shadow-sm" :disabled="loadingSubmit">
              {{ loadingSubmit ? 'Đang xử lý...' : (form.phuongThucThanhToan === 'VNPay' ? 'THANH TOÁN VNPAY' : 'ĐẶT HÀNG NGAY') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const API_URL = 'http://localhost:8080'
const router = useRouter()

const cartItems = ref([])
const loading = ref(false)
const loadingSubmit = ref(false)
const applyingVoucher = ref(false)
const user = ref(null)

const voucherCodeInput = ref('')
const appliedVoucher = ref(null)

const form = ref({
  tenNguoiNhan: '',
  soDienThoai: '',
  diaChiGiaoHang: '',
  ghiChu: '',
  phuongThucThanhToan: 'TienMat'
})

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const getItemPrice = (item) => {
  let price = item.chiTietSanPham?.sanPham?.giaKhuyenMai || item.chiTietSanPham?.sanPham?.giaGoc || 0;
  if (item.chiTietSanPham?.giaCongThem) {
    price += item.chiTietSanPham.giaCongThem;
  }
  return price;
}

const subTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.soLuong * (item.donGia || 0))
  }, 0)
})

const discountAmount = computed(() => {
  if (!appliedVoucher.value) return 0
  return (subTotal.value * appliedVoucher.value.phanTramGiam) / 100
})

const finalTotal = computed(() => {
  return Math.max(0, subTotal.value - discountAmount.value)
})

const fetchCartAndUser = async () => {
  const userData = localStorage.getItem('user')
  if (!userData) {
    router.push('/login')
    return
  }
  user.value = JSON.parse(userData)
  form.value.tenNguoiNhan = user.value.hoTen || ''
  form.value.soDienThoai = user.value.soDienThoai || ''
  form.value.diaChiGiaoHang = user.value.diaChi || ''

  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/gio-hang/cua-toi/${user.value.maNguoiDung}`)
    const items = res.data.data || res.data || []

    // Enrich từng item với đầy đủ thông tin
    const enriched = []
    for (let item of items) {
      const maChiTietSp = item.maChiTietSp
      if (!maChiTietSp) { enriched.push(item); continue }
      try {
        const ctRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/${maChiTietSp}`)
        const ct = ctRes.data.data || ctRes.data
        const spRes = await axios.get(`${API_URL}/api/san-pham/${ct.maSanPham}`)
        const sp = spRes.data.data || spRes.data
        enriched.push({
          maCtGioHang: item.maCtGioHang,
          maChiTietSp: maChiTietSp,
          soLuong: item.soLuong,
          tenSanPham: sp.tenSanPham,
          mauSac: ct.mauSac,
          kichCo: ct.kichCo,
          donGia: sp.giaKhuyenMai || sp.giaGoc || 0,
          chiTietSanPham: { maChiTietSp, mauSac: ct.mauSac, kichCo: ct.kichCo, sanPham: sp }
        })
      } catch (e) {
        console.error('Lỗi fetch biến thể', e)
        enriched.push(item)
      }
    }

    cartItems.value = enriched
    if (cartItems.value.length === 0) {
      router.push('/cart')
    }
  } catch (error) {
    console.error('Lỗi tải giỏ hàng:', error)
  } finally {
    loading.value = false
  }
}

const applyVoucher = async () => {
  if (!voucherCodeInput.value.trim()) return
  
  applyingVoucher.value = true
  try {
    // API Check KhuyenMai 
    // Assuming backend endpoint exists or we fetch all and check
    const res = await axios.get(`${API_URL}/api/khuyen-mai`)
    const vouchers = res.data.data || res.data || []
    
    const voucher = vouchers.find(v => v.maCode.toUpperCase() === voucherCodeInput.value.toUpperCase())
    
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
    } else {
      alert('Mã khuyến mãi không tồn tại!')
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

const submitOrder = async () => {
  if (!form.value.tenNguoiNhan || !form.value.soDienThoai || !form.value.diaChiGiaoHang) {
    alert('Vui lòng nhập đầy đủ thông tin giao hàng (Tên, SĐT, Địa chỉ)!')
    return
  }

  loadingSubmit.value = true
  try {
    // Payload Order
    const diaChiGop = `${form.value.tenNguoiNhan} - ${form.value.soDienThoai} - ${form.value.diaChiGiaoHang}` + (form.value.ghiChu ? ` (Ghi chú: ${form.value.ghiChu})` : '')
    const payload = {
      maNguoiDung: user.value.maNguoiDung,
      maKhuyenMai: appliedVoucher.value ? appliedVoucher.value.maKhuyenMai : null,
      diaChiGiao: diaChiGop,
      phuongThucTt: form.value.phuongThucThanhToan,
      phiShip: 0,
      items: cartItems.value.map(item => {
        return {
          maChiTietSp: item.maChiTietSp,
          soLuong: item.soLuong,
          donGia: item.donGia || 0
        }
      })
    }

    const orderRes = await axios.post(`${API_URL}/api/don-hang`, payload)
    const orderData = orderRes.data.data || orderRes.data
    const orderId = orderData.maDonHang

    // Clear cart since order is placed
    for (let item of cartItems.value) {
      await axios.delete(`${API_URL}/api/gio-hang/xoa/${item.maCtGioHang}`)
    }

    if (form.value.phuongThucThanhToan === 'VNPay') {
      // VNPay Integration
      const vnpayRes = await axios.get(`${API_URL}/api/payment/create-url?order_id=${orderId}&amout_vnd=${finalTotal.value}`)
      const vnpayUrl = vnpayRes.data.data || vnpayRes.data
      if (typeof vnpayUrl === 'string' && vnpayUrl.includes('http')) {
        window.location.href = vnpayUrl
      } else {
        alert('Không thể kết nối VNPay. Vui lòng thanh toán khi nhận hàng.')
        router.push('/history')
      }
    } else {
      alert('Đặt hàng thành công!')
      router.push('/history')
    }
  } catch (error) {
    console.error('Lỗi đặt hàng:', error)
    alert('Lỗi: ' + (error.response?.data?.message || 'Không thể tạo đơn hàng'))
  } finally {
    loadingSubmit.value = false
  }
}

onMounted(() => {
  fetchCartAndUser()
})
</script>
