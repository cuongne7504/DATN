<template>
  <div class="container-fluid">
    <PageHeader title="Quản lý Kho" subtitle="Theo dõi tồn kho và cảnh báo hết hàng" />

    <SkeletonLoader v-if="loading" variant="kpi" :rows="3" class="mb-4" />
    <div v-else class="row mb-4 g-3">
      <div class="col-md-4">
        <div class="sp-kpi-card sp-kpi-orange">
          <div class="kpi-label"><i class="bi bi-exclamation-octagon me-1"></i>Hết hàng</div>
          <p class="kpi-value">{{ outOfStockCount }}</p>
        </div>
      </div>
      <div class="col-md-4">
        <div class="sp-kpi-card sp-kpi-cyan">
          <div class="kpi-label"><i class="bi bi-exclamation-triangle me-1"></i>Sắp hết (≤ 5)</div>
          <p class="kpi-value">{{ lowStockCount }}</p>
        </div>
      </div>
      <div class="col-md-4">
        <div class="sp-kpi-card sp-kpi-green">
          <div class="kpi-label"><i class="bi bi-check2-circle me-1"></i>Còn hàng</div>
          <p class="kpi-value">{{ inStockCount }}</p>
        </div>
      </div>
    </div>

    <!-- Bộ lọc -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-6">
            <input type="text" class="form-control" v-model="searchQuery" placeholder="Tìm theo tên sản phẩm hoặc mã SKU..." />
          </div>
          <div class="col-md-4">
            <select class="form-select" v-model="filterStatus">
              <option value="all">Tất cả trạng thái</option>
              <option value="in-stock">Còn hàng</option>
              <option value="low-stock">Sắp hết hàng</option>
              <option value="out-of-stock">Hết hàng</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng dữ liệu -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover table-striped mb-0 align-middle">
            <thead class="table-light">
              <tr>
                <th>Mã SKU</th>
                <th>Tên sản phẩm</th>
                <th>Phân loại</th>
                <th>Tồn kho</th>
                <th>Trạng thái</th>
                <th class="text-end">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="6" class="text-center py-4">
                  <div class="spinner-border text-primary" role="status"></div>
                </td>
              </tr>
              <tr v-else-if="filteredInventory.length === 0">
                <td colspan="6" class="text-center py-4 text-muted">Không tìm thấy dữ liệu tồn kho</td>
              </tr>
              <tr v-for="item in filteredInventory" :key="item.maChiTietSp">
                <td><span class="badge bg-secondary">{{ item.maVachSku || 'SKU-' + item.maChiTietSp }}</span></td>
                <td class="fw-semibold">{{ item.tenSanPham }}</td>
                <td>{{ item.mauSac }} - {{ item.kichCo }}</td>
                <td>
                  <span class="fw-bold fs-5">{{ item.soLuongTon }}</span>
                </td>
                <td>
                  <span v-if="item.soLuongTon === 0" class="badge bg-danger">Hết hàng</span>
                  <span v-else-if="item.soLuongTon <= 5" class="badge bg-warning text-dark">Sắp hết</span>
                  <span v-else class="badge bg-success">Còn hàng</span>
                </td>
                <td class="text-end">
                  <button class="btn btn-sm btn-primary" @click="openUpdateModal(item)">
                    <i class="bi bi-pencil-square"></i> Sửa số lượng
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Cập nhật số lượng -->
    <div class="modal fade" id="updateStockModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Cập nhật số lượng tồn kho</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Sản phẩm</label>
              <input type="text" class="form-control" :value="currentItem?.tenSanPham" disabled />
            </div>
            <div class="mb-3">
              <label class="form-label">Phân loại</label>
              <input type="text" class="form-control" :value="currentItem ? `${currentItem.mauSac} - ${currentItem.kichCo}` : ''" disabled />
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold text-primary">Số lượng tồn kho mới</label>
              <input type="number" class="form-control form-control-lg" v-model="newStock" min="0" />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="updateStock" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
              Lưu thay đổi
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
// Sử dụng Bootstrap Javascript bundle cho modal
import * as bootstrap from 'bootstrap'
import PageHeader from '@/components/PageHeader.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'

import { API_URL } from '@/config.js'
const inventory = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterStatus = ref('all')

const currentItem = ref(null)
const newStock = ref(0)
const saving = ref(false)
let modalInstance = null

const fetchInventory = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/chi-tiet-san-pham/ton-kho`)
    inventory.value = res.data.data || []
  } catch (error) {
    console.error('Lỗi khi tải tồn kho:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchInventory()
  modalInstance = new bootstrap.Modal(document.getElementById('updateStockModal'))
})

// Tính toán thống kê
const outOfStockCount = computed(() => inventory.value.filter(i => i.soLuongTon === 0).length)
const lowStockCount = computed(() => inventory.value.filter(i => i.soLuongTon > 0 && i.soLuongTon <= 5).length)
const inStockCount = computed(() => inventory.value.filter(i => i.soLuongTon > 5).length)

// Bộ lọc danh sách
const filteredInventory = computed(() => {
  let result = inventory.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(i => 
      (i.tenSanPham && i.tenSanPham.toLowerCase().includes(query)) || 
      (i.maVachSku && i.maVachSku.toLowerCase().includes(query))
    )
  }

  if (filterStatus.value === 'out-of-stock') {
    result = result.filter(i => i.soLuongTon === 0)
  } else if (filterStatus.value === 'low-stock') {
    result = result.filter(i => i.soLuongTon > 0 && i.soLuongTon <= 5)
  } else if (filterStatus.value === 'in-stock') {
    result = result.filter(i => i.soLuongTon > 5)
  }

  return result
})

const openUpdateModal = (item) => {
  currentItem.value = item
  newStock.value = item.soLuongTon
  modalInstance.show()
}

const updateStock = async () => {
  if (!currentItem.value) return
  if (newStock.value < 0) {
    alert('Số lượng không thể âm!')
    return
  }

  saving.value = true
  try {
    // Để cập nhật số lượng, ta phải gọi API PUT của chi-tiet-san-pham (API này yêu cầu truyền toàn bộ object)
    // Đầu tiên ta get chi tiết gốc
    const detailRes = await axios.get(`${API_URL}/api/chi-tiet-san-pham/${currentItem.value.maChiTietSp}`)
    const fullDetail = detailRes.data.data
    
    // Cập nhật số lượng
    fullDetail.soLuongTon = parseInt(newStock.value)
    
    // Lưu lại
    await axios.put(`${API_URL}/api/chi-tiet-san-pham/${currentItem.value.maChiTietSp}`, fullDetail)
    
    alert('Cập nhật số lượng thành công!')
    modalInstance.hide()
    fetchInventory() // Tải lại danh sách
  } catch (error) {
    console.error('Lỗi khi cập nhật:', error)
    alert('Cập nhật thất bại!')
  } finally {
    saving.value = false
  }
}
</script>
