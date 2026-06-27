/** Nhãn & cấu hình trạng thái DON_HANG */

export const ORDER_STATUS = {
  ChoXuLy: {
    label: 'Chờ xử lý',
    color: '#f59e0b',
    bg: '#fffbeb',
    icon: 'bi-hourglass-split',
  },
  DangChuanBi: {
    label: 'Đang chuẩn bị',
    color: '#3b82f6',
    bg: '#eff6ff',
    icon: 'bi-box-seam',
  },
  DangGiao: {
    label: 'Đang giao hàng',
    color: '#8b5cf6',
    bg: '#f5f3ff',
    icon: 'bi-truck',
  },
  HoanThanh: {
    label: 'Hoàn thành',
    color: '#16a34a',
    bg: '#f0fdf4',
    icon: 'bi-check-circle',
  },
  DaHuy: {
    label: 'Đã hủy',
    color: '#ef4444',
    bg: '#fef2f2',
    icon: 'bi-x-circle',
  },
}

/** Thứ tự bước theo dõi (đơn thường) */
export const TRACKING_STEPS = [
  { key: 'ChoXuLy', label: 'Đặt hàng', icon: 'bi-receipt' },
  { key: 'DangChuanBi', label: 'Chuẩn bị', icon: 'bi-box-seam' },
  { key: 'DangGiao', label: 'Đang giao', icon: 'bi-truck' },
  { key: 'HoanThanh', label: 'Hoàn thành', icon: 'bi-house-check' },
]

export const PAYMENT_LABELS = {
  cod: 'Thanh toán khi nhận hàng (COD)',
  COD: 'Thanh toán khi nhận hàng (COD)',
  bank: 'Chuyển khoản ngân hàng',
  momo: 'Ví MoMo',
}

export function getStatusConfig(trang_thai) {
  return ORDER_STATUS[trang_thai] ?? {
    label: trang_thai,
    color: '#64748b',
    bg: '#f1f5f9',
    icon: 'bi-info-circle',
  }
}

export function getPaymentLabel(phuong_thuc_tt) {
  return PAYMENT_LABELS[phuong_thuc_tt] ?? phuong_thuc_tt
}

export function getActiveStepIndex(trang_thai) {
  if (trang_thai === 'DaHuy') return -1
  const idx = TRACKING_STEPS.findIndex((s) => s.key === trang_thai)
  return idx >= 0 ? idx : 0
}

export function formatOrderDate(iso) {
  if (!iso) return ''
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(iso))
}

export function formatOrderId(ma_don_hang) {
  return `#SP${String(ma_don_hang).padStart(6, '0')}`
}
