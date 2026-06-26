const STORAGE_KEY = 'sportpro_user'

const defaultUserId = Number(import.meta.env.VITE_DEFAULT_USER_ID) || 3

const defaultUser = {
  ma_nguoi_dung: defaultUserId,
  ho_ten: 'Khách hàng SportPro',
  email: '',
}

export function getCurrentUserId() {
  const stored = readStoredUser()
  return stored?.ma_nguoi_dung ?? defaultUserId
}

export function getCurrentUser() {
  return readStoredUser() ?? { ...defaultUser }
}

export function setCurrentUser(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
}

export function clearCurrentUser() {
  localStorage.removeItem(STORAGE_KEY)
}

function readStoredUser() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}
