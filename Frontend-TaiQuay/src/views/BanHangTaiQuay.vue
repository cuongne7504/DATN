<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'

const sanPhamList = ref([])
const gioHang = ref([])

onMounted(async () => {
  try {
    const res = await api.get('/san-pham')
    sanPhamList.value = res.data || []
  } catch (err) {
    console.error('Lỗi khi tải sản phẩm:', err)
  }
})

const themVaoGio = (sp) => {
  const item = gioHang.value.find(i => i.maSanPham === sp.maSanPham)
  if (item) {
    item.soLuong++
  } else {
    gioHang.value.push({ ...sp, soLuong: 1 })
  }
}

const xoaKhoiGio = (idx) => {
  gioHang.value.splice(idx, 1)
}

const tongTien = computed(() => {
  return gioHang.value.reduce((total, item) => total + ((item.giaGoc || 0) * item.soLuong), 0)
})

const thanhToan = () => {
  alert('Tính năng thanh toán đang được hoàn thiện!')
}
</script>

<template>
  <div class="row">
    <!-- Cột Sản Phẩm -->
    <div class="col-md-7 border-end">
      <h4><i class="bi bi-upc-scan me-2"></i>Sản phẩm (Quét mã)</h4>
      <input type="text" class="form-control mb-3" placeholder="Quét mã vạch hoặc nhập tên sản phẩm..." />
      <div class="row g-3" style="max-height: 70vh; overflow-y: auto;">
        <div v-for="(sp, idx) in sanPhamList" :key="idx" class="col-4">
          <div class="card h-100 product-card" @click="themVaoGio(sp)">
            <div class="card-body p-2 text-center d-flex flex-column justify-content-between">
              <h6 class="card-title text-truncate" :title="sp.tenSanPham">{{ sp.tenSanPham || 'Sản phẩm' }}</h6>
              <p class="text-danger fw-bold mb-0">{{ (sp.giaGoc || 0).toLocaleString('vi-VN') }} đ</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Cột Giỏ Hàng -->
    <div class="col-md-5">
      <h4><i class="bi bi-cart me-2"></i>Đơn Hàng Mới</h4>
      <div class="table-responsive" style="height: 50vh; overflow-y: auto;">
        <table class="table table-sm align-middle">
          <thead class="table-light sticky-top">
            <tr>
              <th>Sản phẩm</th>
              <th width="80">SL</th>
              <th>Thành tiền</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="gioHang.length === 0">
              <td colspan="4" class="text-center text-muted py-4">Chưa có sản phẩm nào</td>
            </tr>
            <tr v-for="(item, idx) in gioHang" :key="idx">
              <td>{{ item.tenSanPham || 'Sản phẩm' }}</td>
              <td>
                <input type="number" class="form-control form-control-sm" v-model.number="item.soLuong" min="1" />
              </td>
              <td>{{ ((item.giaGoc || 0) * item.soLuong).toLocaleString('vi-VN') }} đ</td>
              <td>
                <button class="btn btn-sm btn-outline-danger" @click="xoaKhoiGio(idx)">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="card bg-light mt-3">
        <div class="card-body">
          <h5 class="d-flex justify-content-between">
            <span>Tổng tiền:</span>
            <span class="text-danger fw-bold">{{ tongTien.toLocaleString('vi-VN') }} đ</span>
          </h5>
          <button class="btn btn-primary w-100 mt-2 py-3 fs-5 fw-bold" @click="thanhToan" :disabled="gioHang.length === 0">
            <i class="bi bi-cash-coin me-2"></i>THANH TOÁN
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  cursor: pointer;
  transition: all 0.2s;
}
.product-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
  border-color: #0d6efd;
}
</style>
