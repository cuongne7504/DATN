import { reactive } from 'vue'
import api from './api'
import { getCurrentUserId } from '../utils/auth'
import { getDonGiaChiTiet } from '../utils/productHelpers'

const FREE_SHIP_THRESHOLD = 500000
const SHIPPING_FEE = 30000

const state = reactive({
  cart: null,
})

function enrichCartItem(item) {
  return {
    ...item,
    don_gia: getDonGiaChiTiet(item, {
      gia_goc: item.gia_goc,
      gia_khuyen_mai: item.gia_khuyen_mai,
    }),
    so_luong_ton: item.so_luong_ton ?? 99,
  }
}

function recalculateCart(cart) {
  const chiTiet = cart.chi_tiet_gio_hang ?? []
  cart.chi_tiet_gio_hang = chiTiet.map(enrichCartItem)
  cart.tam_tinh = cart.chi_tiet_gio_hang.reduce(
    (sum, item) => sum + item.don_gia * item.so_luong,
    0
  )
  cart.so_luong_san_pham = cart.chi_tiet_gio_hang.reduce(
    (sum, item) => sum + item.so_luong,
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

/** GIO_HANG + CHI_TIET_GIO_HANG (JOIN) */
export async function fetchGioHang(ma_nguoi_dung = getCurrentUserId()) {
  const { data } = await api.get(`/gio-hang/${ma_nguoi_dung}`)
  state.cart = recalculateCart(data)
  return state.cart
}

export function getCartSnapshot() {
  return ensureLocalCart()
}

export function updateCartItemQuantity(ma_ct_gio_hang, so_luong) {
  const cart = ensureLocalCart()
  const item = cart.chi_tiet_gio_hang.find(
    (row) => row.ma_ct_gio_hang === ma_ct_gio_hang
  )
  if (!item) return cart

  const max = item.so_luong_ton ?? 99
  const next = Math.min(Math.max(1, Number(so_luong) || 1), max)
  item.so_luong = next
  return recalculateCart(cart)
}

export function removeCartItem(ma_ct_gio_hang) {
  const cart = ensureLocalCart()
  cart.chi_tiet_gio_hang = cart.chi_tiet_gio_hang.filter(
    (row) => row.ma_ct_gio_hang !== ma_ct_gio_hang
  )
  return recalculateCart(cart)
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
