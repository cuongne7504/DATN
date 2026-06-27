<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý đơn hàng</h2>

    <!-- Bộ lọc trạng thái -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-3">
            <label class="form-label">Trạng thái</label>
            <select v-model="filterStatus" @change="fetchOrders" class="form-select">
              <option value="">Tất cả</option>
              <option value="Đang xử lý">Đang xử lý</option>
              <option value="Đang giao hàng">Đang giao hàng</option>
              <option value="Đã giao hàng">Đã giao hàng</option>
              <option value="Đã hủy">Đã hủy</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Tìm kiếm</label>
            <input type="text" v-model="searchKeyword" @keyup.enter="fetchOrders" placeholder="Mã đơn, tên khách..." class="form-control">
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Tổng tiền</th>
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th>Ngày đặt</th>
            <th>Chi tiết</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.maDonHang">
            <td>{{ order.maDonHang }}</td>
            <td>{{ order.maNguoiDung }}</td>
            <td>{{ formatPrice(order.tongTien) }}</td>
            <td>{{ order.diaChiGiao }}</td>
            <td>
              <span class="badge" :class="getStatusClass(order.trangThai)">{{ order.trangThai }}</span>
            </td>
            <td>{{ formatDate(order.ngayDat) }}</td>
            <td>
              <button @click="viewDetails(order)" class="btn btn-sm btn-info">Xem</button>
            </td>
            <td>
              <select @change="updateStatus(order.maDonHang, $event.target.value)" class="form-select form-select-sm">
                <option value="">Cập nhật</option>
                <option value="Đang xử lý">Đang xử lý</option>
                <option value="Đang giao hàng">Đang giao hàng</option>
                <option value="Đã giao hàng">Đã giao hàng</option>
                <option value="Đã hủy">Đã hủy</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal chi tiết đơn hàng -->
    <div v-if="selectedOrder" class="modal fade show" style="display: block;" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết đơn hàng #{{ selectedOrder.maDonHang }}</h5>
            <button @click="selectedOrder = null" class="btn-close"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <strong>Khách hàng:</strong> {{ selectedOrder.maNguoiDung }}
              </div>
              <div class="col-md-6">
                <strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}
              </div>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <strong>Địa chỉ giao:</strong> {{ selectedOrder.diaChiGiao }}
              </div>
              <div class="col-md-6">
                <strong>Trạng thái:</strong> 
                <span class="badge" :class="getStatusClass(selectedOrder.trangThai)">{{ selectedOrder.trangThai }}</span>
              </div>
            </div>
            <hr>
            <h6>Chi tiết sản phẩm</h6>
            <table class="table table-sm">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Biến thể</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in orderDetails" :key="item.maChiTietDonHang">
                  <td>{{ item.tenSanPham }}</td>
                  <td>{{ item.mauSac }} / {{ item.kichCo }}</td>
                  <td>{{ item.soLuong }}</td>
                  <td>{{ formatPrice(item.donGia) }}</td>
                  <td>{{ formatPrice(item.thanhTien) }}</td>
                </tr>
              </tbody>
            </table>
            <div class="text-end">
              <strong>Tổng tiền: {{ formatPrice(selectedOrder.tongTien) }}</strong>
            </div>
          </div>
          <div class="modal-footer">
            <button @click="printInvoice" class="btn btn-primary me-2">In hóa đơn</button>
            <button @click="selectedOrder = null" class="btn btn-secondary">Đóng</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="selectedOrder" class="modal-backdrop fade show" @click="selectedOrder = null"></div>

    <!-- Hóa đơn in -->
    <div v-if="selectedOrder" id="invoice-print" class="d-none">
      <div class="invoice-container">
        <div class="invoice-header">
          <h2>SportPro</h2>
          <p>Địa chỉ: 123 Đường ABC, Quận XYZ, TP.HCM</p>
          <p>Điện thoại: 0123 456 789</p>
        </div>
        <hr>
        <div class="invoice-info">
          <p><strong>Mã đơn hàng:</strong> #{{ selectedOrder.maDonHang }}</p>
          <p><strong>Ngày đặt:</strong> {{ formatDate(selectedOrder.ngayDat) }}</p>
          <p><strong>Khách hàng:</strong> {{ selectedOrder.maNguoiDung }}</p>
          <p><strong>Địa chỉ giao:</strong> {{ selectedOrder.diaChiGiao }}</p>
          <p><strong>Trạng thái:</strong> {{ selectedOrder.trangThai }}</p>
        </div>
        <hr>
        <table class="invoice-table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Biến thể</th>
              <th>Số lượng</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in orderDetails" :key="item.maChiTietDonHang">
              <td>{{ item.tenSanPham }}</td>
              <td>{{ item.mauSac }} / {{ item.kichCo }}</td>
              <td>{{ item.soLuong }}</td>
              <td>{{ formatPrice(item.donGia) }}</td>
              <td>{{ formatPrice(item.thanhTien) }}</td>
            </tr>
          </tbody>
        </table>
        <hr>
        <div class="invoice-total">
          <p><strong>Tổng tiền:</strong> {{ formatPrice(selectedOrder.tongTien) }}</p>
        </div>
        <div class="invoice-footer">
          <p>Cảm ơn quý khách đã mua hàng!</p>
          <p>Ngày in: {{ new Date().toLocaleString('vi-VN') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const orders = ref([])
const loading = ref(false)
const filterStatus = ref('')
const searchKeyword = ref('')
const selectedOrder = ref(null)
const orderDetails = ref([])

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('vi-VN')
}

const getStatusClass = (status) => {
  switch (status) {
    case 'Đã giao hàng': return 'bg-success'
    case 'Đang giao hàng': return 'bg-info'
    case 'Đang xử lý': return 'bg-warning'
    case 'Đã hủy': return 'bg-danger'
    default: return 'bg-secondary'
  }
}

const fetchOrders = async () => {
  loading.value = true
  try {
    let url = `${API_URL}/api/don-hang`
    if (filterStatus.value) {
      url += `?trangThai=${filterStatus.value}`
    }
    const response = await axios.get(url)
    if (response.data && response.data.success) {
      orders.value = response.data.data
      if (searchKeyword.value) {
        orders.value = orders.value.filter(order => 
          order.maDonHang.toString().includes(searchKeyword.value) ||
          order.maNguoiDung.toString().includes(searchKeyword.value)
        )
      }
    }
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
  } finally {
    loading.value = false
  }
}

const viewDetails = async (order) => {
  selectedOrder.value = order
  try {
    const response = await axios.get(`${API_URL}/api/don-hang/${order.maDonHang}/chi-tiet`)
    if (response.data && response.data.success) {
      orderDetails.value = response.data.data
    }
  } catch (error) {
    console.error('Lỗi khi tải chi tiết:', error)
    orderDetails.value = []
  }
}

const updateStatus = async (id, status) => {
  if (!status) return
  try {
    await axios.put(`${API_URL}/api/don-hang/${id}/trang-thai`, null, { params: { trangThai: status } })
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
  }
}

const printInvoice = () => {
  const printContent = document.getElementById('invoice-print')
  if (printContent) {
    printContent.classList.remove('d-none')
    window.print()
    printContent.classList.add('d-none')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
@media print {
  body * {
    visibility: hidden;
  }
  #invoice-print, #invoice-print * {
    visibility: visible;
  }
  #invoice-print {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
  .invoice-container {
    padding: 20px;
    font-family: Arial, sans-serif;
  }
  .invoice-header h2 {
    margin: 0 0 10px 0;
    color: #333;
  }
  .invoice-header p {
    margin: 5px 0;
    font-size: 12px;
  }
  .invoice-info p {
    margin: 5px 0;
    font-size: 12px;
  }
  .invoice-table {
    width: 100%;
    border-collapse: collapse;
    margin: 10px 0;
  }
  .invoice-table th,
  .invoice-table td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
    font-size: 12px;
  }
  .invoice-table th {
    background-color: #f5f5f5;
  }
  .invoice-total {
    text-align: right;
    margin: 10px 0;
    font-size: 14px;
  }
  .invoice-footer {
    margin-top: 20px;
    text-align: center;
    font-size: 12px;
  }
}
</style>
