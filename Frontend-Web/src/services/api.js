import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

/** SAN_PHAM + JOIN DANH_MUC, THUONG_HIEU, HINH_ANH_SP, CHI_TIET_SAN_PHAM, DANH_GIA */
export async function fetchSanPham(params = {}) {
  const { data } = await api.get('/san-pham', { params })
  return data
}

export async function fetchSanPhamById(ma_san_pham) {
  const { data } = await api.get(`/san-pham/${ma_san_pham}`)
  return data
}

/** DANH_MUC: ten_danh_muc, hinh_anh */
export async function fetchDanhMuc() {
  const { data } = await api.get('/danh-muc')
  return data
}

/** THUONG_HIEU: ten_thuong_hieu, logo */
export async function fetchThuongHieu() {
  const { data } = await api.get('/thuong-hieu')
  return data
}

/** GIO_HANG + CHI_TIET_GIO_HANG */
export async function fetchGioHang(ma_nguoi_dung) {
  const { data } = await api.get(`/gio-hang/${ma_nguoi_dung}`)
  return data
}

/** DON_HANG: tong_tien, phi_ship, dia_chi_giao, phuong_thuc_tt, trang_thai */
export async function fetchDonHang(ma_don_hang) {
  const { data } = await api.get(`/don-hang/${ma_don_hang}`)
  return data
}

/** POST thanh toán online — redirect URL do backend trả về khi tích hợp thật */
export async function createPayment(payload) {
  const { data } = await api.post('/thanh-toan', payload)
  return data
}

export default api

api.interceptors.response.use((response) => {
    const box = response.data;
    if (box && typeof box.success !== 'undefined') {
        if (box.success) return { data: box.data, message: box.message };
        return Promise.reject(new Error(box.message || 'L?i API'));
    }
    return response;
}, (error) => Promise.reject(error));
