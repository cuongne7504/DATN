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

            <div class="mb-3" v-if="!user">
              <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
              <input type="email" v-model="form.email" class="form-control" required placeholder="Nhập địa chỉ email để nhận thông báo">
            </div>
            
            <div class="row">
              <div class="col-md-4 mb-3">
                <label class="form-label fw-semibold">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                <select class="form-select" v-model="selectedProvince" @change="onProvinceChange" required>
                  <option value="">Chọn Tỉnh/Thành phố</option>
                  <option v-for="prov in provinces" :key="prov.ProvinceID" :value="prov.ProvinceID">
                    {{ prov.ProvinceName }}
                  </option>
                </select>
              </div>
              
              <div class="col-md-4 mb-3">
                <label class="form-label fw-semibold">Quận/Huyện <span class="text-danger">*</span></label>
                <select class="form-select" v-model="selectedDistrict" @change="onDistrictChange" :disabled="!selectedProvince" required>
                  <option value="">Chọn Quận/Huyện</option>
                  <option v-for="dist in districts" :key="dist.DistrictID" :value="dist.DistrictID">
                    {{ dist.DistrictName }}
                  </option>
                </select>
              </div>

              <div class="col-md-4 mb-3">
                <label class="form-label fw-semibold">Phường/Xã <span class="text-danger">*</span></label>
                <select class="form-select" v-model="selectedWard" @change="onWardChange" :disabled="!selectedDistrict" required>
                  <option value="">Chọn Phường/Xã</option>
                  <option v-for="w in wards" :key="w.WardCode" :value="w.WardCode">
                    {{ w.WardName }}
                  </option>
                </select>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">Địa chỉ chi tiết (Số nhà, tên đường...) <span class="text-danger">*</span></label>
              <input type="text" v-model="form.diaChiChiTiet" class="form-control" required placeholder="Ví dụ: 123 Đường Lê Lợi">
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
              <span class="fw-bold text-primary">
                <span v-if="calculatingFee" class="spinner-border spinner-border-sm" role="status"></span>
                <span v-else-if="phiShip > 0">{{ formatPrice(phiShip) }}</span>
                <span v-else class="text-muted">Chưa tính (chọn địa chỉ)</span>
              </span>
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

    <!-- OTP Modal -->
    <div v-if="showOtpModal" class="otp-modal-overlay">
      <div class="otp-modal">
        <h4 class="fw-bold mb-3">Xác thực số điện thoại</h4>
        <p class="text-muted mb-4">
          Mã xác thực đã được gửi qua số điện thoại <br>
          <strong class="text-primary fs-5">{{ form.soDienThoai }}</strong>
        </p>
        <div class="mb-4">
          <input type="text" v-model="otpCode" class="form-control form-control-lg text-center fw-bold fs-4" placeholder="Nhập mã 6 số" maxlength="6" style="letter-spacing: 5px;">
        </div>
        <button @click="confirmOrder" class="btn btn-primary btn-lg w-100 fw-bold mb-3" :disabled="loadingSubmit || otpCode.length !== 6">
          {{ loadingSubmit ? 'Đang xác thực...' : 'Xác nhận' }}
        </button>
        <button @click="showOtpModal = false" class="btn btn-outline-secondary w-100">Đổi số điện thoại khác</button>
        <p class="text-muted mt-3 mb-0" style="font-size: 14px;">
          Không nhận được mã? <span v-if="countdown > 0">{{ countdown }} giây</span>
          <a href="#" v-else @click.prevent="requestOtp" class="text-decoration-none fw-bold">Gửi lại mã</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import { API_URL } from '@/config.js'
const router = useRouter()

const cartItems = ref([])
const loading = ref(false)
const loadingSubmit = ref(false)
const applyingVoucher = ref(false)
const user = ref(null)

const voucherCodeInput = ref('')
const appliedVoucher = ref(null)

const provinces = ref([])
const districts = ref([])
const wards = ref([])

const selectedProvince = ref('')
const selectedDistrict = ref('')
const selectedWard = ref('')
const phiShip = ref(0)
const calculatingFee = ref(false)

const form = ref({
  tenNguoiNhan: '',
  soDienThoai: '',
  email: '',
  diaChiChiTiet: '',
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
  return Math.max(0, subTotal.value - discountAmount.value + (phiShip.value || 0))
})

const fetchCartAndUser = async () => {
  const userData = localStorage.getItem('user')
  
  if (userData) {
    user.value = JSON.parse(userData)
    form.value.tenNguoiNhan = user.value.hoTen || ''
    form.value.soDienThoai = user.value.soDienThoai || ''
    form.value.email = user.value.email || ''
    form.value.diaChiChiTiet = user.value.diaChi || ''
  }

  loading.value = true
  try {
    if (!user.value) {
      // Guest
      const guestCart = JSON.parse(localStorage.getItem('guestCart') || '[]')
      if (guestCart.length === 0) {
        router.push('/cart')
        return
      }
      cartItems.value = guestCart.map((item, index) => ({
        maCtGioHang: 'guest_' + index,
        maChiTietSp: item.maChiTietSp,
        soLuong: item.soLuong,
        tenSanPham: item.sanPham.tenSanPham,
        mauSac: item.chiTietSanPham.mauSac,
        kichCo: item.chiTietSanPham.kichCo,
        donGia: item.donGia,
        chiTietSanPham: item.chiTietSanPham
      }))
      return
    }

    // Logged in user
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
          donGia: item.donGia || sp.giaKhuyenMai || sp.giaGoc || 0,
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

const fetchProvinces = async () => {
  try {
    const res = await axios.get(`${API_URL}/api/ghn/provinces`)
    const data = res.data.data || res.data
    provinces.value = data.data || []
  } catch (e) {
    console.error('Lỗi fetch provinces', e)
  }
}

const onProvinceChange = async () => {
  districts.value = []
  wards.value = []
  selectedDistrict.value = ''
  selectedWard.value = ''
  phiShip.value = 0
  if (!selectedProvince.value) return
  try {
    const res = await axios.get(`${API_URL}/api/ghn/districts?provinceId=${selectedProvince.value}`)
    const data = res.data.data || res.data
    districts.value = data.data || []
  } catch (e) {
    console.error('Lỗi fetch districts', e)
  }
}

const onDistrictChange = async () => {
  wards.value = []
  selectedWard.value = ''
  phiShip.value = 0
  if (!selectedDistrict.value) return
  try {
    const res = await axios.get(`${API_URL}/api/ghn/wards?districtId=${selectedDistrict.value}`)
    const data = res.data.data || res.data
    wards.value = data.data || []
  } catch (e) {
    console.error('Lỗi fetch wards', e)
  }
}

const onWardChange = async () => {
  if (!selectedDistrict.value || !selectedWard.value) {
    phiShip.value = 0
    return
  }
  calculatingFee.value = true
  try {
    const res = await axios.post(`${API_URL}/api/ghn/fee`, {
      to_district_id: Number(selectedDistrict.value),
      to_ward_code: selectedWard.value
    })
    const data = res.data.data || res.data
    if (data.code === 200 || data.data) {
      const feeData = data.data || {}
      phiShip.value = feeData.total || feeData.service_fee || 0
    } else {
      phiShip.value = 0
    }
  } catch (e) {
    console.error('Lỗi tính phí ship', e)
    phiShip.value = 0
  } finally {
    calculatingFee.value = false
  }
}

const getAddressText = () => {
  const p = provinces.value.find(x => x.ProvinceID === selectedProvince.value)
  const d = districts.value.find(x => x.DistrictID === selectedDistrict.value)
  const w = wards.value.find(x => x.WardCode === selectedWard.value)
  
  const provinceName = p ? p.ProvinceName : ''
  const districtName = d ? d.DistrictName : ''
  const wardName = w ? w.WardName : ''
  
  return `${form.value.diaChiChiTiet}, ${wardName}, ${districtName}, ${provinceName}`
}

const submitOrder = async () => {
  // 1. Kiểm tra các trường bắt buộc không được để trống
  if (!form.value.tenNguoiNhan || !form.value.soDienThoai || !selectedProvince.value || !selectedDistrict.value || !selectedWard.value || !form.value.diaChiChiTiet || (!user.value && !form.value.email)) {
    alert('Vui lòng nhập đầy đủ thông tin giao hàng và chọn địa chỉ (Tỉnh, Quận, Phường)!')
    return
  }

  // 2. Validate số điện thoại (đúng định dạng di động Việt Nam: 10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09)
  const phoneRegex = /^(0[35789])[0-9]{8}$/
  if (!phoneRegex.test(form.value.soDienThoai.trim())) {
    alert('Số điện thoại không hợp lệ! Vui lòng nhập số điện thoại Việt Nam gồm 10 chữ số (Ví dụ: 0982337504).')
    return
  }

  // 3. Validate email
  const emailToValidate = form.value.email ? form.value.email.trim() : ''
  if (!user.value || emailToValidate) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(emailToValidate)) {
      alert('Địa chỉ email không hợp lệ! Vui lòng nhập đúng định dạng email (Ví dụ: example@domain.com).')
      return
    }
  }

  requestOtp()
}

const showOtpModal = ref(false)
const otpCode = ref('')
const countdown = ref(0)
let timer = null

const requestOtp = async () => {
  loadingSubmit.value = true
  try {
    await axios.post(`${API_URL}/api/otp/send`, { soDienThoai: form.value.soDienThoai })
    showOtpModal.value = true
    otpCode.value = ''
    startCountdown(60) // 60 giây
  } catch (error) {
    console.error('Lỗi yêu cầu OTP:', error)
    alert('Lỗi: Không thể gửi mã OTP. Vui lòng kiểm tra lại số điện thoại.')
  } finally {
    loadingSubmit.value = false
  }
}

const startCountdown = (seconds) => {
  countdown.value = seconds
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const confirmOrder = async () => {
  if (!otpCode.value || otpCode.value.length !== 6) {
    alert('Vui lòng nhập đủ 6 số mã OTP!')
    return
  }

  loadingSubmit.value = true
  try {
    const fullAddress = getAddressText()
    const diaChiGop = `${fullAddress} | [GHN:${selectedWard.value}:${selectedDistrict.value}]` + (form.value.ghiChu ? ` (Ghi chú: ${form.value.ghiChu})` : '')
    
    let orderId = null;

    if (!user.value) {
      // Guest Checkout
      const payload = {
        hoTen: form.value.tenNguoiNhan,
        soDienThoai: form.value.soDienThoai,
        email: form.value.email,
        diaChiGiao: diaChiGop,
        phuongThucTt: form.value.phuongThucThanhToan,
        phiShip: phiShip.value,
        maKhuyenMai: appliedVoucher.value ? appliedVoucher.value.maKhuyenMai : null,
        otpCode: otpCode.value,
        items: cartItems.value.map(item => ({
          maChiTietSp: item.maChiTietSp,
          soLuong: item.soLuong,
          donGia: item.donGia || 0
        }))
      }
      const orderRes = await axios.post(`${API_URL}/api/don-hang/guest`, payload)
      const orderData = orderRes.data.data || orderRes.data
      orderId = orderData.maDonHang
      
      // Xóa localStorage
      localStorage.removeItem('guestCart')
    } else {
      // Normal Checkout
      const payload = {
        maNguoiDung: user.value.maNguoiDung,
        maKhuyenMai: appliedVoucher.value ? appliedVoucher.value.maKhuyenMai : null,
        diaChiGiao: `${form.value.tenNguoiNhan} - ${form.value.soDienThoai} - ${diaChiGop}`,
        phuongThucTt: form.value.phuongThucThanhToan,
        phiShip: phiShip.value,
        otpCode: otpCode.value,
        items: cartItems.value.map(item => ({
          maChiTietSp: item.maChiTietSp,
          soLuong: item.soLuong,
          donGia: item.donGia || 0
        }))
      }
      const orderRes = await axios.post(`${API_URL}/api/don-hang`, payload)
      const orderData = orderRes.data.data || orderRes.data
      orderId = orderData.maDonHang

      // Clear cart
      for (let item of cartItems.value) {
        await axios.delete(`${API_URL}/api/gio-hang/xoa/${item.maCtGioHang}`)
      }
    }

    if (form.value.phuongThucThanhToan === 'VNPay') {
      const vnpayRes = await axios.get(`${API_URL}/api/payment/create-url?order_id=${orderId}&amout_vnd=${finalTotal.value}`)
      const vnpayUrl = vnpayRes.data.data || vnpayRes.data
      if (typeof vnpayUrl === 'string' && vnpayUrl.includes('http')) {
        window.location.href = vnpayUrl
      } else {
        alert('Không thể kết nối VNPay. Vui lòng thanh toán khi nhận hàng.')
        router.push(user.value ? '/history' : '/')
      }
    } else {
      alert('Đặt hàng thành công!')
      router.push(user.value ? '/history' : '/')
    }
  } catch (error) {
    console.error('Lỗi đặt hàng:', error)
    alert('Lỗi: ' + (error.response?.data?.message || 'Không thể tạo đơn hàng hoặc sai OTP'))
  } finally {
    loadingSubmit.value = false
    showOtpModal.value = false
  }
}

onMounted(() => {
  fetchCartAndUser()
  fetchProvinces()
})
</script>

<style scoped>
.otp-modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.otp-modal {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  width: 90%;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}
</style>
