<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const donHangs = ref([])

const loadData = async () => {
  try {
    const res = await api.get('/don-hang')
    donHangs.value = res.data || []
  } catch (error) {
    alert('Lỗi tải danh sách đơn hàng')
  }
}

onMounted(() => loadData())

const duyetDon = async (id) => {
  if (confirm('Bạn muốn duyệt đơn hàng này?')) {
    try {
      await api.put(`/don-hang/${id}/trang-thai?trangThai=DaDuyet`)
      loadData()
    } catch (e) {
      alert('Không thể duyệt đơn')
    }
  }
}
</script>

<template>
  <div class="card shadow-sm mt-3">
    <div class="card-header bg-white py-3">
      <h5 class="mb-0 fw-bold"><i class="bi bi-receipt me-2"></i>Quản Lý Đơn Hàng</h5>
    </div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th>Mã Đơn</th>
              <th>Khách Hàng</th>
              <th>Ngày Đặt</th>
              <th>Tổng Tiền</th>
              <th>Trạng Thái</th>
              <th class="text-end">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="donHangs.length === 0">
              <td colspan="6" class="text-center py-4 text-muted">Chưa có đơn hàng nào</td>
            </tr>
            <tr v-for="dh in donHangs" :key="dh.maDonHang">
              <td>#{{ dh.maDonHang }}</td>
              <td class="fw-bold">{{ dh.tenNguoiNhan || 'Khách lẻ' }}</td>
              <td>{{ new Date(dh.ngayDat).toLocaleString('vi-VN') }}</td>
              <td class="text-danger fw-bold">{{ (dh.tongTien || 0).toLocaleString('vi-VN') }} đ</td>
              <td>
                <span class="badge" :class="dh.trangThai === 'ChoDuyet' ? 'bg-warning text-dark' : 'bg-success'">
                  {{ dh.trangThai === 'ChoDuyet' ? 'Chờ Duyệt' : 'Đã Duyệt' }}
                </span>
              </td>
              <td class="text-end">
                <button v-if="dh.trangThai === 'ChoDuyet'" class="btn btn-sm btn-success me-2" @click="duyetDon(dh.maDonHang)">
                  <i class="bi bi-check-circle me-1"></i>Duyệt
                </button>
                <button class="btn btn-sm btn-outline-info"><i class="bi bi-eye"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
