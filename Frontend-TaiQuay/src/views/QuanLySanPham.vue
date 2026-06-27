<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const sanPhams = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const formData = ref({ maSanPham: null, tenSanPham: '', giaGoc: 0, moTa: '' })

const loadData = async () => {
  try {
    const res = await api.get('/san-pham')
    sanPhams.value = res.data || []
  } catch (error) {
    alert('Lỗi tải danh sách sản phẩm')
  }
}

onMounted(() => loadData())

const openAdd = () => {
  isEdit.value = false
  formData.value = { maSanPham: null, tenSanPham: '', giaGoc: 0, moTa: '' }
  showModal.value = true
}

const openEdit = (sp) => {
  isEdit.value = true
  formData.value = { ...sp }
  showModal.value = true
}

const save = async () => {
  try {
    if (isEdit.value) {
      await api.put(`/san-pham/${formData.value.maSanPham}`, formData.value)
    } else {
      await api.post('/san-pham', formData.value)
    }
    showModal.value = false
    loadData()
  } catch (e) {
    alert('Lỗi lưu dữ liệu. Vui lòng kiểm tra lại Backend.')
  }
}

const remove = async (id) => {
  if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
    try {
      await api.delete(`/san-pham/${id}`)
      loadData()
    } catch (e) {
      alert('Không thể xóa sản phẩm đang có trong đơn hàng!')
    }
  }
}
</script>

<template>
  <div class="card shadow-sm mt-3">
    <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
      <h5 class="mb-0 fw-bold"><i class="bi bi-box me-2"></i>Quản Lý Sản Phẩm</h5>
      <button class="btn btn-primary" @click="openAdd"><i class="bi bi-plus-lg me-1"></i>Thêm Mới</button>
    </div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th>Mã SP</th>
              <th>Tên Sản Phẩm</th>
              <th>Giá Bán</th>
              <th>Mô Tả</th>
              <th class="text-end">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sanPhams.length === 0">
              <td colspan="5" class="text-center py-4 text-muted">Chưa có sản phẩm nào</td>
            </tr>
            <tr v-for="sp in sanPhams" :key="sp.maSanPham">
              <td>#{{ sp.maSanPham }}</td>
              <td class="fw-bold text-primary">{{ sp.tenSanPham }}</td>
              <td class="text-danger fw-bold">{{ (sp.giaGoc || 0).toLocaleString('vi-VN') }} đ</td>
              <td>{{ sp.moTa || 'Không có mô tả' }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-outline-primary me-2" @click="openEdit(sp)"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-sm btn-outline-danger" @click="remove(sp.maSanPham)"><i class="bi bi-trash"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Form -->
    <div v-if="showModal" class="modal d-block" style="background: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title fw-bold">{{ isEdit ? 'Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}</h5>
            <button type="button" class="btn-close" @click="showModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label fw-bold">Tên sản phẩm <span class="text-danger">*</span></label>
              <input type="text" class="form-control" v-model="formData.tenSanPham" placeholder="Ví dụ: Giày thể thao Nike" />
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">Giá gốc (VNĐ) <span class="text-danger">*</span></label>
              <input type="number" class="form-control" v-model="formData.giaGoc" />
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">Mô tả chi tiết</label>
              <textarea class="form-control" v-model="formData.moTa" rows="3" placeholder="Nhập mô tả sản phẩm..."></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showModal = false">Hủy</button>
            <button class="btn btn-primary" @click="save"><i class="bi bi-save me-2"></i>Lưu lại</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
