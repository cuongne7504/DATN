import { reactive } from 'vue'
import api from './api'
import { getCurrentUserId } from '../utils/auth'

const FREE_SHIP_THRESHOLD = 500000
const SHIPPING_FEE = 30000

const state = reactive({
  cart: null,
})

function recalculateCart(cart) {
  const chiTiet = cart.chi_tiet_gio_hang ?? []
  
  cart.tam_tinh = chiTiet.reduce(
    (sum, item) => sum + (item.don_gia || 0) * (item.so_luong || 0),
    0
  )
  cart.so_luong_san_pham = chiTiet.reduce(
    (sum, item) => sum + (item.so_luong || 0),
    0
  )
  cart.phi_van_chuyen =
    cart.tam_tinh >= FREE_SHIP_THRESHOLD || cart.tam_tinh === 0 ? 0 : SHIPPING_FEE
  cart.tong_thanh_toan = cart.tam_tinh + cart.phi_van_chuyen
  return cart
}

function createEmptyCart(ma_nguoi_dung = getCurrentUserId()) {
  return recalculateCart({
    ma_gio_hang: null,
    ma_nguoi_dung,
    chi_tiet_gio_hang: [],
    tam_tinh: 0,
    so_luong_san_pham: 0,
    phi_van_chuyen: 0,
    tong_thanh_toan: 0,
  })
}

function ensureLocalCart() {
  if (!state.cart) {
    state.cart = createEmptyCart()
  }
  return state.cart
}

export async function fetchGioHang(ma_nguoi_dung = getCurrentUserId()) {
  try {
    // Gọi API chuẩn của Spring Boot
    const { data } = await api.get(`/gio-hang/cua-toi/${ma_nguoi_dung}`)
    
    // Spring Boot trả về 1 mảng ChiTietGioHang
    const arr = Array.isArray(data) ? data : (data?.data || [])
    
    // Map mảng ChiTietGioHang (camelCase) sang chuẩn snake_case của Frontend Khôi
    const chi_tiet_gio_hang = arr.map(item => {
      const sp = item.chiTietSanPham?.sanPham || {}
      return {
        ma_ct_gio_hang: item.maCtGioHang,
        ma_chi_tiet_sp: item.chiTietSanPham?.maChiTietSp,
        ten_san_pham: sp.tenSanPham || 'Sản phẩm',
        hinh_anh: (sp.hinhAnhSps && sp.hinhAnhSps.length > 0) ? sp.hinhAnhSps[0].duongDanAnh : 'giay.jpg',
        kich_co: item.chiTietSanPham?.kichCo,
        mau_sac: item.chiTietSanPham?.mauSac,
        so_luong: item.soLuong,
        so_luong_ton: item.chiTietSanPham?.soLuongTon || 99,
        don_gia: sp.giaKhuyenMai || sp.giaGoc || 0
      }
    })

    const cart = {
      ma_gio_hang: arr.length > 0 ? arr[0].gioHang?.maGioHang : null,
      ma_nguoi_dung,
      chi_tiet_gio_hang,
    }
    state.cart = recalculateCart(cart)
  } catch (error) {
    console.error("Lỗi tải giỏ hàng:", error)
    state.cart = createEmptyCart(ma_nguoi_dung)
  }
  return state.cart
}

export function getCartSnapshot() {
  return ensureLocalCart()
}

export async function updateCartItemQuantity(ma_ct_gio_hang, so_luong) {
  try {
    await api.put(`/gio-hang/cap-nhat/${ma_ct_gio_hang}`, { soLuong })
  } catch (e) {
    console.error("Lỗi cập nhật số lượng:", e)
  }
  return fetchGioHang()
}

export async function removeCartItem(ma_ct_gio_hang) {
  try {
    await api.delete(`/gio-hang/xoa/${ma_ct_gio_hang}`)
  } catch (e) {
    console.error("Lỗi xóa sản phẩm:", e)
  }
  return fetchGioHang()
}

export function clearCart() {
  state.cart = createEmptyCart()
  return state.cart
}

export async function fetchCartItemCount(ma_nguoi_dung = getCurrentUserId()) {
  const cart = await fetchGioHang(ma_nguoi_dung)
  return cart.so_luong_san_pham ?? 0
}

export function getShippingFee(tamTinh) {
  return tamTinh >= FREE_SHIP_THRESHOLD || tamTinh === 0 ? 0 : SHIPPING_FEE
}

export { FREE_SHIP_THRESHOLD, SHIPPING_FEE }
