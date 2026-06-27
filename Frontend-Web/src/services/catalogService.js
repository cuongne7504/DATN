import api from './api'
import { PRICE_RANGES } from '../constants/catalog'
import { getGiaHienThi } from '../utils/productHelpers'

export async function fetchDanhMuc() {
  const { data } = await api.get('/danh-muc')
  return data
}

export async function fetchThuongHieu() {
  const { data } = await api.get('/thuong-hieu')
  return data
}

export async function fetchSanPham(params = {}) {
  const { data } = await api.get('/san-pham', { params })
  const list = Array.isArray(data) ? data : data?.items ?? data
  return Array.isArray(list) ? list : []
}

export async function fetchSanPhamById(ma_san_pham) {
  const { data } = await api.get(`/san-pham/${ma_san_pham}`)
  return data
}

export async function fetchKichCoList() {
  const products = await fetchSanPham()
  const sizes = new Set()
  products.forEach((p) => {
    p.chi_tiet?.forEach((ct) => sizes.add(ct.kich_co))
  })
  return [...sizes]
}

export async function buildFilterOptions() {
  const [categories, sizes] = await Promise.all([
    fetchDanhMuc(),
    fetchKichCoList(),
  ])
  const products = await fetchSanPham()
  const maxPrice = Math.max(...products.map(getGiaHienThi), 0)

  return {
    categories,
    sizes,
    priceRanges: PRICE_RANGES,
    maxPrice,
  }
}

export function getMaxPriceFromProducts(products) {
  if (!products.length) return 0
  return Math.max(...products.map(getGiaHienThi))
}
