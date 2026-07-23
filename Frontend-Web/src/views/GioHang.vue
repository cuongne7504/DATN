<template>
  <div class="container mt-4">
    <h2 class="mb-4 fw-bold">Giỏ hàng của bạn</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="!cartItems || cartItems.length === 0" class="text-center py-5 bg-light rounded shadow-sm">
      <i class="bi bi-cart-x text-muted" style="font-size: 4rem;"></i>
      <h4 class="mt-3 text-muted">Giỏ hàng trống</h4>
      <p>Có vẻ như bạn chưa thêm sản phẩm nào vào giỏ hàng.</p>
      <router-link to="/" class="btn btn-primary mt-2 fw-bold px-4">Tiếp tục mua sắm</router-link>
    </div>

    <div v-else class="row">
      <div class="col-lg-8 mb-4">
        <div class="card shadow-sm border-0">
          <div class="card-body">
            <div class="table-responsive">
              <table class="table align-middle">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Số lượng</th>
                    <th>Thành tiền</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cartItems" :key="item.maCtGioHang">
                    <td>
                      <div class="d-flex align-items-center">
                        <img v-if="item.hinhAnh" 
                             :src="item.hinhAnh" 
                             class="img-thumbnail me-3 object-fit-cover" 
                             style="width: 80px; height: 80px;" alt="Product">
                        <img v-else src="https://via.placeholder.com/80?text=No+Image" class="img-thumbnail me-3" style="width: 80px; height: 80px;">
                        
                        <div>
                          <h6 class="mb-0 fw-bold">{{ item.tenSanPham || 'Sản phẩm' }}</h6>
                          <small class="text-muted">
                            Màu: {{ item.mauSac }} | Size: {{ item.kichCo }}
                          </small>
                          <div class="text-danger mt-1 fw-bold">{{ formatPrice(item.donGia) }}</div>
                        </div>
                      </div>
                    </td>

                    <td>
                      <div class="input-group input-group-sm quantity-input-group" style="width: 120px;">
                        <button class="btn btn-outline-secondary" @click="updateQuantity(item, item.soLuong - 1)">-</button>
                        <input type="number" class="form-control text-center no-spinners" v-model.number="item.soLuong" @change="updateQuantity(item, item.soLuong)" min="1">
                        <button class="btn btn-outline-secondary" @click="updateQuantity(item, item.soLuong + 1)">+</button>
                      </div>
                    </td>

                    <td class="fw-bold text-primary">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                    <td>
                      <button @click="removeItem(item.maCtGioHang)" class="btn btn-sm btn-outline-danger border-0">
                        <i class="bi bi-trash fs-5"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card shadow-sm border-0 bg-light">
          <div class="card-body p-4">
            <h5 class="fw-bold mb-4">Tổng quan giỏ hàng</h5>
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Tạm tính:</span>
              <span class="fw-bold">{{ formatPrice(totalAmount) }}</span>
            </div>
            <hr>
            <div class="d-flex justify-content-between mb-4">
              <span class="fw-bold fs-5">Tổng cộng:</span>
              <span class="fw-bold fs-4 text-danger">{{ formatPrice(totalAmount) }}</span>
            </div>
            <router-link to="/checkout" class="btn btn-danger btn-lg w-100 fw-bold shadow-sm">
              TIẾN HÀNH THANH TOÁN
            </router-link>
            <router-link to="/" class="btn btn-outline-primary btn-lg w-100 mt-2">
              Mua thêm sản phẩm
            </router-link>
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

import { API_URL } from '@/config.js'
const router = useRouter()

const cartItems = ref([])
const loading = ref(false)

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.soLuong * (item.donGia || 0))
  }, 0)
})

const fetchCart = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  loading.value = true
  try {
    if (!user) {
      // Load từ localStorage
      const guestCart = JSON.parse(localStorage.getItem('guestCart') || '[]')
      const enriched = guestCart.map((item, index) => {
        let hinhAnh = null
        if (item.sanPham.hinhAnh && item.sanPham.hinhAnh.length > 0) {
           const path = item.sanPham.hinhAnh.find(i => i.laAnhChinh)?.duongDanAnh || item.sanPham.hinhAnh[0].duongDanAnh
           if (path) {
             hinhAnh = path.startsWith('http') ? path : (path.startsWith('/') ? `${API_URL}${path}` : `${API_URL}/${path}`)
           }
        }
        return {
          maCtGioHang: 'guest_' + index,
          maChiTietSp: item.maChiTietSp,
          maSanPham: item.sanPham.maSanPham,
          soLuong: item.soLuong,
          tenSanPham: item.sanPham.tenSanPham,
          mauSac: item.chiTietSanPham.mauSac,
          kichCo: item.chiTietSanPham.kichCo,
          soLuongTon: item.chiTietSanPham.soLuongTon,
          donGia: item.donGia,
          hinhAnh: hinhAnh
        }
      })
      cartItems.value = enriched
      return
    }

    // Lấy danh sách chi tiết giỏ hàng
    const res = await axios.get(`${API_URL}/api/gio-hang/cua-toi/${user.maNguoiDung}`)
    const items = res.data.data || res.data || []

    // Với mỗi item, fetch thông tin biến thể + sản phẩm
    const enriched = []
    for (let item of items) {
      const maChiTietSp = item.maChiTietSp
      if (!maChiTietSp) {
        enriched.push(item)
        continue
      }
      try {
        const ctRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/${maChiTietSp}`)
        const ct = ctRes.data.data || ctRes.data
        const maSanPham = ct.maSanPham

        const spRes = await axios.get(`${API_URL}/api/san-pham/${maSanPham}`)
        const sp = spRes.data.data || spRes.data

        let hinhAnh = null
        try {
          const imgRes = await axios.get(`${API_URL}/api/hinh-anh/san-pham/${maSanPham}`)
          const imgs = imgRes.data.data || imgRes.data || []
          if (imgs.length > 0) {
            const path = imgs[0].duongDanAnh
            if (path) {
              hinhAnh = path.startsWith('http') ? path : (path.startsWith('/') ? `${API_URL}${path}` : `${API_URL}/${path}`)
            }
          }
        } catch (e) {}

        enriched.push({
          maCtGioHang: item.maCtGioHang,
          maChiTietSp: maChiTietSp,
          maSanPham: maSanPham,
          soLuong: item.soLuong,
          tenSanPham: sp.tenSanPham,
          mauSac: ct.mauSac,
          kichCo: ct.kichCo,
          soLuongTon: ct.soLuongTon,
          donGia: item.donGia || sp.giaKhuyenMai || sp.giaGoc || 0,
          hinhAnh: hinhAnh
        })
      } catch (e) {
        console.error('Lỗi tải thông tin biến thể:', e)
        enriched.push({ ...item, tenSanPham: 'Sản phẩm', donGia: 0 })
      }
    }
    cartItems.value = enriched
  } catch (error) {
    console.error('Lỗi tải giỏ hàng:', error)
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (item, newQuantity) => {
  if (newQuantity < 1) {
    removeItem(item.maCtGioHang)
    return
  }
  if (newQuantity > item.soLuongTon) {
    alert(`Rất tiếc, kho chỉ còn ${item.soLuongTon} sản phẩm.`)
    item.soLuong = item.soLuongTon
    return
  }
  
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    // Update local storage
    const guestCart = JSON.parse(localStorage.getItem('guestCart') || '[]')
    const idx = parseInt(item.maCtGioHang.split('_')[1])
    if (guestCart[idx]) {
      guestCart[idx].soLuong = newQuantity
      localStorage.setItem('guestCart', JSON.stringify(guestCart))
      item.soLuong = newQuantity
    }
    return
  }
  
  try {
    item.soLuong = newQuantity
    await axios.put(`${API_URL}/api/gio-hang/cap-nhat/${item.maCtGioHang}`, { soLuong: newQuantity })
  } catch (error) {
    console.error('Lỗi cập nhật số lượng:', error)
    alert('Lỗi khi cập nhật số lượng!')
  }
}

const removeItem = async (id) => {
  if (!confirm('Bạn muốn bỏ sản phẩm này khỏi giỏ hàng?')) return
  
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    let guestCart = JSON.parse(localStorage.getItem('guestCart') || '[]')
    const idx = parseInt(id.toString().split('_')[1])
    guestCart.splice(idx, 1)
    localStorage.setItem('guestCart', JSON.stringify(guestCart))
    await fetchCart()
    return
  }

  try {
    await axios.delete(`${API_URL}/api/gio-hang/xoa/${id}`)
    await fetchCart()
  } catch (error) {
    console.error('Lỗi xóa sản phẩm:', error)
    alert('Không thể xóa sản phẩm khỏi giỏ!')
  }
}

onMounted(() => {
  fetchCart()
})
</script>

<style scoped>
/* Ẩn nút mũi tên tăng giảm mặc định của Chrome, Safari, Edge, Opera */
.no-spinners::-webkit-outer-spin-button,
.no-spinners::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Ẩn nút mũi tên tăng giảm mặc định của Firefox */
.no-spinners {
  -moz-appearance: textfield;
}

.quantity-input-group .form-control {
  padding-left: 0.2rem;
  padding-right: 0.2rem;
  font-weight: bold;
}
</style>
