import api from './api'
import { createDonHang } from './orderService'
import { clearCart, getCartSnapshot } from './cartService'
import { getCurrentUserId } from '../utils/auth'

const PENDING_KEY = 'sportpro_pending_payment'
const RESULT_KEY = 'sportpro_last_payment_result'

function buildDiaChiGiao(form) {
  return `${form.dia_chi_chi_tiet}, ${form.phuong_xa}, ${form.quan_huyen}, ${form.tinh_thanh}`
}

function buildPendingOrder(form, cart) {
  const ma_don_hang = Number(String(Date.now()).slice(-6))
  return {
    ma_don_hang,
    ma_nguoi_dung: getCurrentUserId(),
    ngay_dat: new Date().toISOString(),
    ho_ten: form.ho_ten.trim(),
    so_dien_thoai: form.so_dien_thoai.replace(/\s/g, ''),
    email: form.email?.trim() || '',
    tinh_thanh: form.tinh_thanh,
    quan_huyen: form.quan_huyen.trim(),
    phuong_xa: form.phuong_xa.trim(),
    dia_chi_chi_tiet: form.dia_chi_chi_tiet.trim(),
    ghi_chu: form.ghi_chu?.trim() || '',
    dia_chi_giao: buildDiaChiGiao(form),
    phuong_thuc_tt: form.phuong_thuc_thanh_toan,
    tam_tinh: cart.tam_tinh,
    phi_ship: cart.phi_van_chuyen,
    tong_thanh_toan: cart.tong_thanh_toan,
    chi_tiet: cart.chi_tiet_gio_hang.map((item) => ({
      ma_ct_don_hang: item.ma_ct_gio_hang,
      ma_chi_tiet_sp: item.ma_chi_tiet_sp,
      ten_san_pham: item.ten_san_pham,
      mau_sac: item.mau_sac,
      kich_co: item.kich_co,
      so_luong: item.so_luong,
      don_gia: item.don_gia,
      duong_dan_anh: item.duong_dan_anh,
    })),
  }
}

function buildOrderFromPending(pending, transactionId) {
  return {
    ma_don_hang: pending.ma_don_hang,
    ma_nguoi_dung: pending.ma_nguoi_dung,
    ngay_dat: pending.ngay_dat,
    tong_tien: pending.tam_tinh,
    phi_ship: pending.phi_ship,
    dia_chi_giao: pending.dia_chi_giao,
    phuong_thuc_tt: pending.phuong_thuc_tt,
    trang_thai: pending.phuong_thuc_tt === 'cod' ? 'ChoXuLy' : 'DangChuanBi',
    ma_giao_dich: transactionId ?? null,
    lich_su_trang_thai: [
      {
        trang_thai: 'ChoXuLy',
        thoi_gian: pending.ngay_dat,
        mo_ta:
          pending.phuong_thuc_tt === 'cod'
            ? 'Đơn COD — chờ xác nhận'
            : 'Thanh toán online thành công — đang chuẩn bị hàng',
      },
    ],
    chi_tiet: pending.chi_tiet.map((item, index) => ({
      ...item,
      ma_ct_don_hang: item.ma_ct_don_hang ?? index + 1,
    })),
  }
}

export function savePendingPayment(pending) {
  sessionStorage.setItem(PENDING_KEY, JSON.stringify(pending))
}

export function getPendingPayment() {
  try {
    const raw = sessionStorage.getItem(PENDING_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function clearPendingPayment() {
  sessionStorage.removeItem(PENDING_KEY)
}

export function savePaymentResult(result) {
  sessionStorage.setItem(RESULT_KEY, JSON.stringify(result))
}

export function getPaymentResult() {
  try {
    const raw = sessionStorage.getItem(RESULT_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function clearPaymentResult() {
  sessionStorage.removeItem(RESULT_KEY)
}

/** COD → hoàn tất ngay; online → chuyển màn xử lý */
export async function initiateCheckout(form) {
  const cart = getCartSnapshot()
  if (!cart.chi_tiet_gio_hang?.length) {
    return { ok: false, error: 'empty_cart' }
  }

  const pending = buildPendingOrder(form, cart)
  savePendingPayment(pending)

  if (pending.phuong_thuc_tt === 'cod') {
    const result = await completePaymentSuccess(pending)
    return { ok: true, flow: 'cod', result }
  }

  return { ok: true, flow: 'online', route: { name: 'payment-processing' } }
}

/** Xử lý thanh toán online qua cổng backend */
export async function processOnlinePayment(pending) {
  try {
    const { data } = await api.post('/thanh-toan', pending)
    return completePaymentSuccess(pending, data?.transaction_id ?? data?.ma_giao_dich)
  } catch (err) {
    return completePaymentFailure(
      pending,
      err.response?.data?.message ?? 'Không thể kết nối cổng thanh toán. Vui lòng thử lại sau.'
    )
  }
}

async function completePaymentSuccess(pending, transactionId) {
  const order = buildOrderFromPending(pending, transactionId)
  try {
    await createDonHang(order)
  } catch (err) {
    return completePaymentFailure(
      pending,
      err.response?.data?.message ?? 'Không thể tạo đơn hàng. Vui lòng thử lại.'
    )
  }
  clearCart()
  clearPendingPayment()

  const result = {
    status: 'success',
    ma_don_hang: order.ma_don_hang,
    ma_giao_dich: transactionId ?? null,
    tong_thanh_toan: pending.tong_thanh_toan,
    phuong_thuc_tt: pending.phuong_thuc_tt,
    ho_ten: pending.ho_ten,
    ngay_dat: pending.ngay_dat,
  }
  savePaymentResult(result)
  return result
}

function completePaymentFailure(pending, message) {
  clearPendingPayment()
  const result = {
    status: 'failed',
    ma_don_hang: pending.ma_don_hang,
    tong_thanh_toan: pending.tong_thanh_toan,
    phuong_thuc_tt: pending.phuong_thuc_tt,
    message,
  }
  savePaymentResult(result)
  return result
}

export const ONLINE_PAYMENT_METHODS = new Set(['bank', 'momo'])
