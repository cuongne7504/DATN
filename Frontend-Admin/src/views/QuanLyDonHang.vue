<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý Đơn hàng</h2>

    <div class="card mb-4 shadow-sm">
      <div class="card-body">
        <ul class="nav nav-pills mb-3">
          <li class="nav-item" v-for="status in statuses" :key="status.value">
            <a class="nav-link" :class="{ active: currentStatus === status.value }" href="#" @click.prevent="currentStatus = status.value; fetchOrders()">
              {{ status.label }}
            </a>
          </li>
        </ul>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>Mã ĐH</th>
                <th>Khách hàng</th>
                <th>Ngày đặt</th>
                <th>Tổng tiền</th>
                <th>Thanh toán</th>
                <th>Loại đơn</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in filteredOrders" :key="order.maDonHang">
                <td class="fw-bold">#{{ order.maDonHang }}</td>
                <td>
                  {{ order.diaChiGiao ? order.diaChiGiao.split(' - ')[0] : 'Khách vãng lai' }}<br>
                  <small class="text-muted">{{ order.diaChiGiao ? order.diaChiGiao.split(' - ')[1] : '' }}</small>
                </td>
                <td>{{ formatDate(order.ngayDat) }}</td>
                <td class="text-danger fw-bold">{{ formatPrice(order.tongTien) }}</td>
                <td>
                  <span class="badge" :class="order.phuongThucTt === 'VNPay' ? 'bg-info text-dark' : 'bg-secondary'">
                    {{ order.phuongThucTt || 'Tiền mặt' }}
                  </span>
                </td>
                <td>
                  <span class="badge" :class="(!order.diaChiGiao || order.diaChiGiao.includes('tại quầy')) ? 'bg-warning text-dark' : 'bg-primary'">
                    {{ (!order.diaChiGiao || order.diaChiGiao.includes('tại quầy')) ? 'Tại quầy' : 'Online' }}
                  </span>
                </td>
                <td>
                  <button @click="viewDetail(order)" class="btn btn-sm btn-outline-info me-1" data-bs-toggle="modal" data-bs-target="#orderDetailModal">Chi tiết</button>
                  <button v-if="order.trangThai === 'Chờ xử lý'" @click="updateStatus(order.maDonHang, 'Đang xử lý')" class="btn btn-sm btn-success me-1">Xác nhận</button>
                  <button v-if="order.trangThai === 'Chờ xử lý'" @click="updateStatus(order.maDonHang, 'Đã hủy')" class="btn btn-sm btn-danger me-1">Hủy</button>
                  <button v-if="order.trangThai === 'Đang xử lý'" @click="openShipperModal(order)" class="btn btn-sm btn-primary me-1" data-bs-toggle="modal" data-bs-target="#shipperModal">Giao cho Shipper</button>
                  <button v-if="order.trangThai === 'Đang xử lý'" @click="sendToGHN(order.maDonHang)" class="btn btn-sm btn-warning me-1">Gửi GHN</button>
                  <button v-if="order.trangThai === 'Đang giao hàng'" @click="updateStatus(order.maDonHang, 'Đã giao hàng')" class="btn btn-sm btn-success">Hoàn thành</button>
                </td>
              </tr>
              <tr v-if="filteredOrders.length === 0">
                <td colspan="7" class="text-center text-muted py-4">Không có đơn hàng nào</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Chi Tiết -->
    <div class="modal fade" id="orderDetailModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết đơn hàng #{{ selectedOrder?.maDonHang }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body" v-if="selectedOrder">
            <div class="row mb-4">
              <div class="col-md-6">
                <h6 class="fw-bold border-bottom pb-2">Thông tin giao hàng</h6>
                <p class="mb-1"><strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}</p>
                <p class="mb-1"><strong>Địa chỉ:</strong> {{ selectedOrder.diaChiGiao || 'Bán tại quầy' }}</p>
              </div>
              <div class="col-md-6">
                <h6 class="fw-bold border-bottom pb-2">Thông tin thanh toán</h6>
                <p class="mb-1"><strong>Phương thức:</strong> {{ selectedOrder.phuongThucTt }}</p>
                <p class="mb-1"><strong>Trạng thái ĐH:</strong> <span class="badge bg-secondary">{{ selectedOrder.trangThai }}</span></p>
                <p class="mb-1"><strong>Mã Khuyến Mãi:</strong> {{ selectedOrder.maKhuyenMai ? 'Có' : 'Không' }}</p>
              </div>
            </div>

            <div class="row mb-4" v-if="selectedOrder.shipperName || selectedOrder.shippingCode">
              <div class="col-12">
                <h6 class="fw-bold border-bottom pb-2">Thông tin Shipper / Giao hàng</h6>
                <p class="mb-1" v-if="selectedOrder.shippingCode"><strong>Mã vận đơn (GHN):</strong> <span class="badge bg-success">{{ selectedOrder.shippingCode }}</span></p>
                <p class="mb-1" v-if="selectedOrder.shipperName"><strong>Tên shipper:</strong> {{ selectedOrder.shipperName }}</p>
                <p class="mb-1" v-if="selectedOrder.shipperPhone"><strong>Số điện thoại:</strong> {{ selectedOrder.shipperPhone }}</p>
                <p class="mb-1" v-if="selectedOrder.shippingNote"><strong>Ghi chú:</strong> {{ selectedOrder.shippingNote }}</p>
              </div>
            </div>

            <h6 class="fw-bold border-bottom pb-2">Sản phẩm</h6>
            <table class="table table-sm align-middle">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.chiTietList" :key="item.maCtDonHang">
                  <td>
                    <div class="fw-bold">{{ item.sanPham?.tenSanPham || 'Sản phẩm ' + item.maChiTietSp }}</div>
                    <div class="small text-muted">Màu: {{ item.chiTietSanPham?.mauSac }} - Size: {{ item.chiTietSanPham?.kichCo }}</div>
                  </td>
                  <td>{{ item.soLuong }}</td>
                  <td>{{ formatPrice(item.donGia) }}</td>
                  <td class="fw-bold">{{ formatPrice(item.soLuong * item.donGia) }}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="3" class="text-end fw-bold">Tổng tiền hàng:</td>
                  <td class="fw-bold text-danger">{{ formatPrice(selectedOrder.tongTien) }}</td>
                </tr>
              </tfoot>
            </table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Giao hàng cho Shipper -->
    <div class="modal fade" id="shipperModal" tabindex="-1" aria-labelledby="shipperModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="shipperModalLabel">Giao hàng cho Shipper - Đơn hàng #{{ shipperOrder?.maDonHang }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form @submit.prevent="submitShipper">
            <div class="modal-body">
              <div class="mb-3">
                <label for="shipperName" class="form-label">Tên shipper <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="shipperName" v-model="shipperForm.shipperName" required placeholder="Nhập tên shipper">
              </div>
              <div class="mb-3">
                <label for="shipperPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="shipperPhone" v-model="shipperForm.shipperPhone" required placeholder="Nhập số điện thoại shipper">
              </div>
              <div class="mb-3">
                <label for="shippingNote" class="form-label">Ghi chú</label>
                <textarea class="form-control" id="shippingNote" v-model="shipperForm.shippingNote" rows="3" placeholder="Nhập ghi chú giao hàng (nếu có)"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" ref="closeShipperModalBtn">Đóng</button>
              <button type="submit" class="btn btn-primary">Xác nhận</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

import { API_URL } from '@/config.js'
const orders = ref([])
const loading = ref(false)
const currentStatus = ref('TatCa')
const selectedOrder = ref(null)

const shipperOrder = ref(null)
const shipperForm = ref({
  shipperName: '',
  shipperPhone: '',
  shippingNote: ''
})
const closeShipperModalBtn = ref(null)

const openShipperModal = (order) => {
  shipperOrder.value = order
  shipperForm.value = {
    shipperName: '',
    shipperPhone: '',
    shippingNote: ''
  }
}

const submitShipper = async () => {
  if (!shipperOrder.value) return
  try {
    await axios.put(`${API_URL}/api/don-hang/${shipperOrder.value.maDonHang}/giao-hang`, shipperForm.value)
    // Close the modal
    if (closeShipperModalBtn.value) {
      closeShipperModalBtn.value.click()
    }
    // Refresh orders list
    await fetchOrders()
  } catch (error) {
    alert('Giao hàng cho shipper thất bại!')
    console.error(error)
  }
}

const sendToGHN = async (id) => {
  if (!confirm('Bạn có muốn gửi thông tin đơn hàng này sang GHN để tạo vận đơn không?')) return
  try {
    const res = await axios.put(`${API_URL}/api/don-hang/${id}/gui-ghn`)
    alert('Gửi đơn hàng sang GHN thành công! Mã vận đơn: ' + (res.data?.data?.shippingCode || ''))
    await fetchOrders()
  } catch (error) {
    alert('Gửi đơn hàng sang GHN thất bại! ' + (error.response?.data?.message || ''))
    console.error(error)
  }
}

const statuses = [
  { label: 'Tất cả', value: 'TatCa' },
  { label: 'Chờ xác nhận', value: 'Chờ xử lý' },
  { label: 'Đã xác nhận', value: 'Đang xử lý' },
  { label: 'Đang giao', value: 'Đang giao hàng' },
  { label: 'Hoàn thành', value: 'Đã giao hàng' },
  { label: 'Đã hủy', value: 'Đã hủy' }
]

const filteredOrders = computed(() => {
  if (currentStatus.value === 'TatCa') return orders.value
  return orders.value.filter(o => o.trangThai === currentStatus.value)
})

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('vi-VN')
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await axios.get(`${API_URL}/api/don-hang`)
    orders.value = res.data.data || res.data || []
    // Sort descending by ID
    orders.value.sort((a, b) => b.maDonHang - a.maDonHang)
  } catch (error) {
    console.error('Lỗi tải đơn hàng:', error)
  } finally {
    loading.value = false
  }
}

const updateStatus = async (id, status) => {
  if (!confirm(`Bạn muốn chuyển sang trạng thái: ${status}?`)) return
  try {
    // Implement API call for update status. 
    await axios.put(`${API_URL}/api/don-hang/${id}/trang-thai`, null, { params: { trangThai: status } })
    await fetchOrders()
  } catch (error) {
    alert('Cập nhật trạng thái thất bại!')
    console.error(error)
  }
}

const viewDetail = async (order) => {
  selectedOrder.value = null
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/${order.maDonHang}`)
    selectedOrder.value = res.data.data || res.data || order
  } catch (error) {
    selectedOrder.value = order
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
