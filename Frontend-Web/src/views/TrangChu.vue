<template>
  <div class="home-page">
    <Banner />
    <BenefitsBar />
    <CategorySection />

    <section class="py-5 bg-light">
      <div class="container">
        <div class="section-header d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
          <div>
            <h2 class="section-title mb-1">Sản phẩm nổi bật</h2>
            <p class="section-subtitle text-muted mb-0">
              Được yêu thích nhất tại SportPro
            </p>
          </div>
          <RouterLink to="/shop" class="btn btn-outline-dark rounded-pill px-3 px-md-4 w-100 w-sm-auto">
            Xem tất cả <i class="bi bi-arrow-right ms-1"></i>
          </RouterLink>
        </div>

        <div v-if="loading" class="text-center text-muted py-4">Đang tải...</div>
        <div v-else class="row g-4">
          <div
            v-for="product in featuredProducts"
            :key="product.ma_san_pham"
            class="col-12 col-sm-6 col-lg-4"
          >
            <ProductCard :product="product" />
          </div>
        </div>
      </div>
    </section>

    <section class="promo-banner py-5">
      <div class="container">
        <div class="promo-card rounded-4 overflow-hidden">
          <div class="row g-0 align-items-center">
            <div class="col-lg-6 p-4 p-lg-5 text-white">
              <span class="promo-label">Giảm giá</span>
              <h2 class="promo-title">Sản phẩm đang khuyến mãi</h2>
              <p class="promo-desc opacity-90">
                Khám phá các sản phẩm có giá khuyến mãi hấp dẫn từ SportPro.
              </p>
              <RouterLink to="/giam-gia" class="btn btn-light rounded-pill px-4 fw-bold">
                Săn deal ngay
              </RouterLink>
            </div>
            <div class="col-lg-6">
              <AppImage
                :src="promoImageSrc"
                alt="Giày thể thao"
                img-class="promo-image w-100"
              />
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="brands-section py-5 bg-light">
      <div class="container text-center">
        <h2 class="section-title mb-4">Thương hiệu đối tác</h2>
        <div class="row g-4 align-items-center justify-content-center">
          <div v-for="brand in brands" :key="brand.ma_thuong_hieu" class="col-6 col-sm-4 col-md-3">
            <div class="brand-logo">
              <AppImage
                :src="brand.logo"
                :alt="brand.ten_thuong_hieu"
                img-class="brand-img"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import Banner from '../components/Banner.vue'
import BenefitsBar from '../components/BenefitsBar.vue'
import CategorySection from '../components/CategorySection.vue'
import ProductCard from '../components/ProductCard.vue'
import AppImage from '../components/AppImage.vue'
import { fetchSanPham, fetchThuongHieu } from '../services/catalogService'

const loading = ref(true)
const products = ref([])
const brands = ref([])

const featuredProducts = computed(() =>
  [...products.value]
    .sort((a, b) => (b.so_luong_danh_gia || 0) - (a.so_luong_danh_gia || 0))
    .slice(0, 3)
)

const promoImageSrc = computed(() => {
  const first = products.value[0]
  if (!first) return null
  const main = first.hinh_anh_sp?.find((h) => h.la_anh_chinh) || first.hinh_anh_sp?.[0]
  return main?.duong_dan_anh ?? first.duong_dan_anh ?? null
})

onMounted(async () => {
  const [productList, brandList] = await Promise.all([
    fetchSanPham(),
    fetchThuongHieu(),
  ])
  products.value = productList
  brands.value = brandList
  loading.value = false
})
</script>

<style scoped>
.section-subtitle {
  font-size: clamp(0.9rem, 2.5vw, 1rem);
}

.promo-card {
  background: linear-gradient(135deg, #0f172a 0%, #334155 100%);
}

.promo-label {
  display: inline-block;
  padding: 0.3rem 0.85rem;
  margin-bottom: 1rem;
  border-radius: 50rem;
  background: var(--sportpro-accent);
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
}

.promo-title {
  font-size: clamp(1.5rem, 3vw, 2.25rem);
  font-weight: 800;
  margin-bottom: 1rem;
}

.promo-desc {
  margin-bottom: 1.5rem;
  max-width: 420px;
}

.promo-image {
  height: 100%;
  min-height: 260px;
  object-fit: cover;
}

.brand-logo {
  padding: 1rem;
}

.brand-img {
  max-height: 40px;
  object-fit: contain;
  filter: grayscale(1);
  opacity: 0.75;
  transition: opacity 0.2s;
}

.brand-logo:hover .brand-img {
  opacity: 1;
  filter: grayscale(0);
}
</style>
