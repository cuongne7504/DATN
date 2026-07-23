export function parseUserFromApiResponse(responseData) {
  if (!responseData) return null

  let user = responseData?.data ?? responseData

  // Trường hợp lưu nhầm cả object ApiResponse vào localStorage
  if (user?.success !== undefined && user?.data) {
    user = user.data
  }

  if (!user?.maNguoiDung) return null
  return user
}

export function getStoredUser() {
  const raw = localStorage.getItem('user')
  if (!raw) return null

  try {
    const parsed = JSON.parse(raw)
    return parseUserFromApiResponse(parsed) ?? (parsed?.maNguoiDung ? parsed : null)
  } catch {
    return null
  }
}

export function saveUser(user) {
  const normalized = parseUserFromApiResponse(user) ?? (user?.maNguoiDung ? user : null)
  if (!normalized?.maNguoiDung) {
    throw new Error('Thiếu mã người dùng (maNguoiDung)')
  }
  localStorage.setItem('user', JSON.stringify(normalized))
  window.dispatchEvent(new Event('storage'))
  return normalized
}
