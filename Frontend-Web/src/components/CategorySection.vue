<template>
  <section class="category-section py-5">
    <div class="container">
      <div class="section-header text-center mb-4">
        <h2 class="section-title">Danh mục thể thao</h2>
        <p class="section-subtitle text-muted">
          Chọn danh mục từ cửa hàng SportPro
        </p>
      </div>

      <div v-if="loading" class="text-center text-muted py-4">Đang tải danh mục...</div>

      <div v-else class="row g-4">
        <div
          v-for="dm in danhMucList"
          :key="dm.ma_danh_muc"
          class="col-6 col-md-4 col-lg-3"
        >
          <RouterLink
            :to="{ name: 'shop', query: { ma_danh_muc: dm.ma_danh_muc } }"
            class="category-card text-decoration-none"
          >
            <div class="category-icon">
              <AppImage
                v-if="dm.hinh_anh"
                :src="dm.hinh_anh"
                :alt="dm.ten_danh_muc"
                img-class="category-img"
              />
              <i v-else class="bi bi-grid"></i>
            </div>
            <h6 class="category-name">{{ dm.ten_danh_muc }}</h6>
          </RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchDanhMuc } from '../services/catalogService'
import AppImage from './AppImage.vue'

const danhMucList = ref([])
const loading = ref(true)

onMounted(async () => {
  danhMucList.value = await fetchDanhMuc()
  loading.value = false
})
</script>

<style scoped>
.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.25rem 0.5rem;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e2e8f0;
  transition: transform 0.25s, box-shadow 0.25s;
}

.category-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.12);
}

.category-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  margin-bottom: 0.75rem;
  overflow: hidden;
  font-size: 1.5rem;
  color: var(--sportpro-accent);
}

.category-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.category-name {
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  font-size: 0.95rem;
  text-align: center;
}
</style>
