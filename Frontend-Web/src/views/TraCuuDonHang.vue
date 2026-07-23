<template>
  <div class="container mt-5 mb-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <!-- Card tìm kiếm -->
        <div class="card shadow border-0 mb-4 rounded-4 p-3 bg-light">
          <div class="card-body text-center">
            <h2 class="fw-bold mb-3 text-primary text-uppercase">Tra cứu đơn hàng</h2>
            <p class="text-muted">Nhập mã đơn hàng (được gửi trong email của bạn) để theo dõi trạng thái và lộ trình giao hàng.</p>
            
            <form @submit.prevent="searchOrder" class="d-flex gap-2 mt-4 justify-content-center">
              <div class="position-relative flex-grow-1" style="max-width: 450px;">
                <input 
                  type="text" 
                  class="form-control form-control-lg rounded-pill ps-4 border-2" 
                  placeholder="Nhập mã đơn hàng của bạn (Ví dụ: 3, 5...)"
                  v-model.trim="searchQuery"
                  required
                />
              </div>
              <button type="submit" class="btn btn-primary btn-lg rounded-pill px-4 fw-bold shadow-sm d-flex align-items-center gap-2">
                <i class="bi bi-search"></i> Tra cứu
              </button>
            </form>
          </div>
        </div>

        <!-- Trạng thái Loading -->
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="text-muted mt-2">Đang tìm kiếm thông tin đơn hàng...</p>
        </div>

        <!-- Không tìm thấy kết quả -->
        <div v-else-if="searched && !order" class="card shadow-sm border-0 rounded-4 text-center py-5">
          <div class="card-body">
            <i class="bi bi-search-heart text-muted" style="font-size: 4rem;"></i>
            <h4 class="mt-3 text-dark fw-bold">Không tìm thấy đơn hàng</h4>
            <p class="text-muted">Không tìm thấy đơn hàng nào có mã <strong>#{{ searchQuery }}</strong>. Vui lòng kiểm tra lại mã trong email của bạn.</p>
          </div>
        </div>

        <!-- Kết quả tìm kiếm -->
        <div v-else-if="order" class="card shadow border-0 rounded-4 overflow-hidden animate__animated animate__fadeIn">
          <!-- Header Đơn hàng -->
          <div class="card-header bg-primary text-white pt-4 pb-4 px-4 border-0">
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
              <div>
                <h4 class="fw-bold mb-1">Chi tiết đơn hàng #{{ order.maDonHang }}</h4>
                <p class="mb-0 text-white-50"><i class="bi bi-calendar-event me-1"></i> Ngày đặt: {{ formatDate(order.ngayDat) }}</p>
              </div>
              <div>
                <span class="badge px-4 py-2 fs-6 rounded-pill" :class="getStatusBadgeClass(order.trangThai)">
                  {{ getStatusText(order.trangThai) }}
                </span>
              </div>
            </div>
          </div>

          <div class="card-body p-4">
            <!-- Thông tin giao hàng & thanh toán -->
            <div class="row g-4 mb-4">
              <div class="col-md-6">
                <h5 class="fw-bold border-bottom pb-2 mb-3 text-dark"><i class="bi bi-geo-alt-fill text-danger me-2"></i>Thông tin nhận hàng</h5>
                <p class="mb-2"><strong>Địa chỉ giao:</strong> {{ order.diaChiGiao || 'Nhận tại quầy' }}</p>
                <p class="mb-2"><strong>Phương thức nhận:</strong> {{ order.diaChiGiao ? 'Giao tận nơi' : 'Nhận tại quầy' }}</p>
              </div>
              <div class="col-md-6">
                <h5 class="fw-bold border-bottom pb-2 mb-3 text-dark"><i class="bi bi-credit-card-fill text-primary me-2"></i>Thanh toán & Vận chuyển</h5>
                <p class="mb-2"><strong>Phương thức thanh toán:</strong> {{ order.phuongThucTt }}</p>
                
                <!-- Hiển thị nếu giao hàng qua GHN -->
                <div class="mb-2" v-if="order.shippingCode">
                  <strong>Đơn vị vận chuyển:</strong> Giao Hàng Nhanh (GHN) <br>
                  <strong>Mã vận đơn:</strong> <span class="badge bg-success fs-6 mt-1">{{ order.shippingCode }}</span>
                </div>
                
                <!-- Hiển thị nếu giao hàng qua Shipper tự do -->
                <div class="mb-2" v-else-if="order.shipperName">
                  <strong>Hình thức giao:</strong> Shipper nội bộ <br>
                  <strong>Tên shipper:</strong> <span class="text-primary fw-bold">{{ order.shipperName }}</span> <br>
                  <span v-if="order.shipperPhone"><strong>Số điện thoại:</strong> {{ order.shipperPhone }}</span>
                  <p class="small text-muted mt-1 mb-0" v-if="order.shippingNote"><strong>Ghi chú:</strong> {{ order.shippingNote }}</p>
                </div>
              </div>
            </div>

            <!-- Nút liên kết tra cứu hành trình GHN nếu có -->
            <div class="alert alert-info rounded-4 border-0 p-3 d-flex align-items-center justify-content-between mb-4" v-if="order.shippingCode">
              <div class="d-flex align-items-center gap-2">
                <i class="bi bi-truck fs-3 text-primary animate-bounce"></i>
                <div>
                  <h6 class="fw-bold mb-0">Đơn hàng đang được giao bởi GHN</h6>
                  <small class="text-muted">Bạn có thể theo dõi vị trí thực tế của gói hàng trên bản đồ GHN.</small>
                </div>
              </div>
              <a :href="'https://5sao.ghn.dev/tracking?order_code=' + order.shippingCode" target="_blank" class="btn btn-primary rounded-pill btn-sm px-3 fw-bold">
                Xem bản đồ GHN <i class="bi bi-box-arrow-up-right ms-1"></i>
              </a>
            </div>

            <!-- Danh sách sản phẩm mua -->
            <h5 class="fw-bold border-bottom pb-2 mb-3 text-dark"><i class="bi bi-box-seam-fill text-warning me-2"></i>Sản phẩm đã mua</h5>
            <div class="table-responsive">
              <table class="table align-middle">
                <thead>
                  <tr class="table-light">
                    <th>Sản phẩm</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Đơn giá</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in order.chiTietList" :key="item.maCtDonHang">
                    <td>
                      <div class="fw-bold">{{ item.sanPham?.tenSanPham || 'Sản phẩm ' + item.maChiTietSp }}</div>
                      <div class="small text-muted" v-if="item.chiTietSanPham">
                        Màu: {{ item.chiTietSanPham.mauSac }} - Size: {{ item.chiTietSanPham.kichCo }}
                      </div>
                    </td>
                    <td class="text-center">{{ item.soLuong }}</td>
                    <td class="text-end fw-bold text-primary">{{ formatPrice(item.donGia) }}</td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="2" class="text-end fw-bold text-muted">Phí vận chuyển:</td>
                    <td class="text-end text-muted">{{ formatPrice(order.phiShip || 0) }}</td>
                  </tr>
                  <tr v-if="order.khuyenMai">
                    <td colspan="2" class="text-end fw-bold text-success">Khuyến mãi:</td>
                    <td class="text-end text-success fw-bold">
                      -{{ formatPrice(order.chiTietList.reduce((sum, item) => sum + (item.soLuong * item.donGia), 0) + (order.phiShip || 0) - order.tongTien) }}
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2" class="text-end fw-bold fs-5 text-dark">Tổng cộng:</td>
                    <td class="text-end text-danger fs-4 fw-bold">{{ formatPrice(order.tongTien) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { API_URL } from '@/config.js'

const route = useRoute()
const searchQuery = ref('')
const loading = ref(false)
const searched = ref(false)
const order = ref(null)

const searchOrder = async () => {
  if (!searchQuery.value) return
  
  // Tự động loại bỏ ký tự '#' và khoảng trắng nếu người dùng nhập dạng '#7'
  const cleanId = searchQuery.value.toString().replace(/#/g, '').trim()
  if (!cleanId) return

  loading.value = true
  searched.value = true
  order.value = null
  try {
    const res = await axios.get(`${API_URL}/api/don-hang/${cleanId}`)
    order.value = res.data.data || res.data
  } catch (error) {
    console.error('Lỗi khi tra cứu đơn hàng:', error)
  } finally {
    loading.value = false
  }
}

const formatPrice = (price) => {
  return price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '0 ₫'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('vi-VN')
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'Chờ xử lý': return 'bg-warning text-dark'
    case 'Đang xử lý': return 'bg-info text-dark'
    case 'Đang giao hàng': return 'bg-primary'
    case 'Đã giao hàng': return 'bg-success'
    case 'Đã hủy': return 'bg-danger'
    default: return 'bg-light text-dark border'
  }
}

const getStatusText = (status) => {
  return status || 'Không rõ'
}

// Kiểm tra xem trên URL có sẵn param mã đơn không (Ví dụ: /lookup?code=5)
onMounted(() => {
  const code = route.query.code
  if (code) {
    searchQuery.value = code
    searchOrder()
  }
})
</script>

<style scoped>
.animate-bounce {
  animation: bounce 1.5s infinite;
}
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}
</style>
