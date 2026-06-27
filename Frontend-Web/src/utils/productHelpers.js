/**
 * Helpers ánh xạ dữ liệu sản phẩm / đơn hàng theo schema DB SportPro
 */

const IMAGE_BASE = import.meta.env.VITE_IMAGE_BASE_URL || '/images/'

export const PLACEHOLDER_IMAGE =
  'https://placehold.co/600x600/e2e8f0/64748b?text=SportPro'

/** Fallback URL khi file local chưa có (chủ yếu logo thương hiệu) */
export const DEMO_IMAGE_URLS = {
  'nike-logo.png':
    'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Logo_NIKE.svg/256px-Logo_NIKE.svg.png',
  'adidas-logo.png':
    'https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Adidas_Logo.svg/256px-Adidas_Logo.svg.png',
  'puma-logo.png':
    'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Puma_logo.svg/256px-Puma_logo.svg.png',
}

export function getDemoImageUrl(duong_dan_anh) {
  if (!duong_dan_anh) return null
  return DEMO_IMAGE_URLS[duong_dan_anh] ?? null
}

export function formatPrice(value) {
  if (value == null) return ''
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(Number(value))
}

export function resolveImageUrl(duong_dan_anh) {
  if (!duong_dan_anh) return PLACEHOLDER_IMAGE
  if (duong_dan_anh.startsWith('http')) return duong_dan_anh
  // Ưu tiên ảnh local trong public/images/ (khớp tên file DB)
  return `${IMAGE_BASE}${duong_dan_anh}`
}

/** Ảnh chính từ HINH_ANH_SP */
export function getAnhChinh(sanPham) {
  if (sanPham?.duong_dan_anh) return resolveImageUrl(sanPham.duong_dan_anh)
  const list = sanPham?.hinh_anh_sp || []
  const main = list.find((h) => h.la_anh_chinh) || list[0]
  return main ? resolveImageUrl(main.duong_dan_anh) : resolveImageUrl(null)
}

/** Giá hiển thị: ưu tiên gia_khuyen_mai */
export function getGiaHienThi(sanPham) {
  if (!sanPham) return 0
  return Number(sanPham.gia_khuyen_mai ?? sanPham.gia_goc ?? 0)
}

/** Đơn giá (don_gia) = giá SP + gia_cong_them của biến thể */
export function getDonGiaChiTiet(chiTiet, sanPham) {
  const base = getGiaHienThi(sanPham)
  const them = Number(chiTiet?.gia_cong_them ?? 0)
  return base + them
}

export function getDonGia(item) {
  if (item.don_gia != null) return Number(item.don_gia)
  const sp = { gia_goc: item.gia_goc, gia_khuyen_mai: item.gia_khuyen_mai }
  return getDonGiaChiTiet(item, sp)
}

export function coKhuyenMai(sanPham) {
  return (
    sanPham?.gia_khuyen_mai != null &&
    Number(sanPham.gia_khuyen_mai) < Number(sanPham.gia_goc)
  )
}

export function getKichCoList(sanPham) {
  if (!sanPham?.chi_tiet?.length) return []
  return [...new Set(sanPham.chi_tiet.map((ct) => ct.kich_co))]
}

export function getMauSacList(sanPham) {
  if (!sanPham?.chi_tiet?.length) return []
  return [...new Set(sanPham.chi_tiet.map((ct) => ct.mau_sac))]
}

export function sizeConHang(chiTiet) {
  return chiTiet.so_luong_ton > 0
}

export function getBadge(sanPham) {
  if (!coKhuyenMai(sanPham)) return null
  const percent = Math.round(
    ((Number(sanPham.gia_goc) - Number(sanPham.gia_khuyen_mai)) /
      Number(sanPham.gia_goc)) *
      100
  )
  return percent > 0 ? `-${percent}%` : 'Sale'
}
