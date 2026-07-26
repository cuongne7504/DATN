import { API_URL } from '@/config.js'

/** Fallback local khi sản phẩm chưa có ảnh trong DB */
export const NO_IMAGE = '/no-image.svg'

/**
 * Ghép URL ảnh từ backend (/uploads/...) — không dùng URL ngoài fix cứng.
 */
export function resolveImageUrl(path) {
  if (!path || String(path).startsWith('http://via.placeholder') || String(path).startsWith('https://via.placeholder')) {
    return NO_IMAGE
  }

  const raw = String(path).trim()
  if (!raw) return NO_IMAGE

  // Chỉ nhận ảnh từ backend uploads hoặc relative path đã lưu trong DB
  if (raw.startsWith('/uploads/')) return `${API_URL}${raw}`
  if (raw.startsWith('uploads/')) return `${API_URL}/${raw}`

  // Filename thuần (UUID.ext) do API upload trả về / lưu DB
  if (!raw.includes('://') && !raw.startsWith('/')) {
    return `${API_URL}/uploads/${raw}`
  }

  // URL http(s) ngoài (unsplash/pexels...) — bỏ, dùng no-image
  if (raw.startsWith('http://') || raw.startsWith('https://')) {
    return NO_IMAGE
  }

  return NO_IMAGE
}
