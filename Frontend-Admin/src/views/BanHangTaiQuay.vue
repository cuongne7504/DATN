<template>
  <div class="container mt-4">
    <h2 class="mb-4">POS - Bán tại quầy</h2>

    <div class="row">
      <div class="col-md-4">
        <div class="card">
          <div class="card-body">
            <h5>Thêm sản phẩm vào đơn</h5>
            
            <!-- Quét mã vạch -->
            <div class="mb-3">
              <label class="form-label">Quét mã vạch / Mã biến thể</label>
              <input 
                type="text" 
                v-model="barcodeInput" 
                @keyup.enter="scanBarcode"
                ref="barcodeInputRef"
                class="form-control" 
                placeholder="Quét hoặc nhập mã..."
                autofocus
              >
            </div>
            
            <div class="mb-3">
              <label class="form-label">Số lượng</label>
              <input type="number" v-model="tempForm.soLuong" class="form-control" value="1" min="1">
            </div>
            
            <div v-if="scannedProduct" class="alert alert-info mb-3">
              <strong>{{ scannedProduct.tenSanPham }}</strong><br>
              <small>{{ scannedProduct.mauSac }} / {{ scannedProduct.kichCo }}</small><br>
              <strong>Giá: {{ formatPrice(scannedProduct.giaBan) }}</strong>
            </div>
            
            <button @click="addItem" class="btn btn-primary w-100">Thêm vào đơn</button>
          </div>
        </div>
      </div>

      <div class="col-md-8">
        <div class="card">
          <div class="card-body">
            <h5>Chi tiết đơn hàng</h5>
            <div class="mb-3">
              <label class="form-label">Mã nhân viên</label>
              <input type="number" v-model="posForm.maNhanVien" class="form-control" value="1">
            </div>

            <div v-if="posForm.items.length === 0" class="text-muted mb-3">
              Chưa có sản phẩm nào
            </div>

            <div v-else class="table-responsive mb-3">
              <table class="table table-sm">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Biến thể</th>
                    <th>Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in posForm.items" :key="index">
                    <td>{{ item.tenSanPham }}</td>
                    <td>{{ item.mauSac }} / {{ item.kichCo }}</td>
                    <td>
                      <input 
                        type="number" 
                        v-model="item.soLuong" 
                        class="form-control form-control-sm" 
                        style="width: 60px;"
                        min="1"
                        @change="updateItemTotal(index)"
                      >
                    </td>
                    <td>{{ formatPrice(item.donGia) }}</td>
                    <td>{{ formatPrice(item.soLuong * item.donGia) }}</td>
                    <td>
                      <button @click="removeItem(index)" class="btn btn-sm btn-danger">×</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h4>Tổng: {{ formatPrice(calculateTotal()) }}</h4>
              </div>
              <button @click="createPosOrder" class="btn btn-success btn-lg" :disabled="posForm.items.length === 0 || loading">
                {{ loading ? 'Đang xử lý...' : 'Thanh toán' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080'

const loading = ref(false)
const barcodeInput = ref('')
const barcodeInputRef = ref(null)
const scannedProduct = ref(null)

const posForm = ref({
  maNhanVien: 1,
  items: []
})

const tempForm = ref({
  soLuong: 1
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const calculateTotal = () => {
  return posForm.value.items.reduce((sum, item) => sum + (item.soLuong * item.donGia), 0)
}

const scanBarcode = async () => {
  if (!barcodeInput.value) return
  
  try {
    // Tìm biến thể theo mã vạch hoặc mã chi tiết
    const response = await axios.get(`${API_URL}/api/chi-tiet-san-pham/${barcodeInput.value}`)
    if (response.data && response.data.success) {
      scannedProduct.value = response.data.data
    } else {
      alert('Không tìm thấy sản phẩm với mã này')
      scannedProduct.value = null
    }
  } catch (error) {
    console.error('Lỗi khi quét mã:', error)
    alert('Không tìm thấy sản phẩm với mã này')
    scannedProduct.value = null
  }
}

const addItem = () => {
  if (!scannedProduct.value) {
    alert('Vui lòng quét mã sản phẩm trước')
    return
  }
  
  // Kiểm tra xem sản phẩm đã có trong đơn chưa
  const existingIndex = posForm.value.items.findIndex(
    item => item.maChiTietSp === scannedProduct.value.maChiTietSp
  )
  
  if (existingIndex >= 0) {
    // Cập nhật số lượng nếu đã có
    posForm.value.items[existingIndex].soLuong += parseInt(tempForm.value.soLuong)
  } else {
    // Thêm mới
    posForm.value.items.push({
      maChiTietSp: scannedProduct.value.maChiTietSp,
      tenSanPham: scannedProduct.value.tenSanPham,
      mauSac: scannedProduct.value.mauSac,
      kichCo: scannedProduct.value.kichCo,
      soLuong: parseInt(tempForm.value.soLuong),
      donGia: scannedProduct.value.giaBan
    })
  }

  // Reset
  scannedProduct.value = null
  barcodeInput.value = ''
  tempForm.value.soLuong = 1
  
  // Focus lại vào input để quét tiếp
  nextTick(() => {
    barcodeInputRef.value?.focus()
  })
}

const updateItemTotal = (index) => {
  // Tự động tính lại thành tiền khi thay đổi số lượng
  // Không cần làm gì vì tính toán được thực hiện trong template
}

const removeItem = (index) => {
  posForm.value.items.splice(index, 1)
}

const createPosOrder = async () => {
  if (posForm.value.items.length === 0) {
    alert('Vui lòng thêm sản phẩm vào đơn')
    return
  }

  loading.value = true
  try {
    const payload = {
      maNhanVien: posForm.value.maNhanVien,
      items: posForm.value.items.map(item => ({
        maChiTietSp: item.maChiTietSp,
        soLuong: item.soLuong,
        donGia: item.donGia
      }))
    }
    
    const response = await axios.post(`${API_URL}/api/don-hang/pos`, payload)
    if (response.data && response.data.success) {
      alert('Tạo đơn POS thành công!')
      posForm.value = {
        maNhanVien: 1,
        items: []
      }
    }
  } catch (error) {
    console.error('Lỗi khi tạo đơn POS:', error)
    alert('Lỗi khi tạo đơn POS')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  barcodeInputRef.value?.focus()
})
</script>
