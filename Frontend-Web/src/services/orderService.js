import api from './api'
import { getCurrentUserId } from '../utils/auth'

/** Danh sách DON_HANG theo khách hàng */
export async function fetchDonHangList(ma_nguoi_dung = getCurrentUserId()) {
  const { data } = await api.get(`/don-hang/khach-hang/${ma_nguoi_dung}`)
  return Array.isArray(data) ? data : data?.items ?? []
}

/** Chi tiết một đơn hàng */
export async function fetchDonHangById(ma_don_hang, ma_nguoi_dung = getCurrentUserId()) {
  const { data } = await api.get(`/don-hang/${ma_don_hang}`)
  if (data?.ma_nguoi_dung != null && data.ma_nguoi_dung !== ma_nguoi_dung) {
    return null
  }
  return data
}

/** Tạo đơn hàng mới sau thanh toán */
export async function createDonHang(order) {
  const { data } = await api.post('/don-hang', order)
  return data
}
