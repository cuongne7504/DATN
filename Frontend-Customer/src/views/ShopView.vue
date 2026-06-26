<template>
  <div class="shop-page">
    <div class="page-header">
      <div class="container">
        <h1>{{ pageTitle }}</h1>
        <p v-if="searchQuery">Kết quả tìm kiếm: "{{ searchQuery }}"</p>
        <p v-else>Tìm đồ thể thao phù hợp — lọc theo danh mục, size và giá</p>
      </div>
    </div>

    <div class="container pb-5">
      <div v-if="loading" class="text-center py-5 text-muted">Đang tải sản phẩm...</div>

      <div v-else class="row g-4">
        <div class="col-12 col-lg-3">
          <button
            class="btn btn-filter-toggle d-lg-none w-100"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#shopFilterCollapse"
            aria-expanded="false"
            aria-controls="shopFilterCollapse"
          >
            <i class="bi bi-funnel me-2"></i>
            Bộ lọc sản phẩm
            <span v-if="hasActiveFilters" class="filter-active-dot"></span>
          </button>
          <div id="shopFilterCollapse" class="collapse filter-collapse">
            <ShopFilterSidebar
              :filters="filters"
              :filter-options="filterOptions"
              :max-price="filterOptions.maxPrice"
              :has-active-filters="hasActiveFilters"
              @toggle="handleToggleFilter"
              @update-max-price="filters.maxPrice = $event"
              @reset="resetFilters"
            />
          </div>
        </div>

        <div class="col-12 col-lg-9">
          <div class="shop-toolbar d-flex flex-wrap justify-content-between align-items-center gap-2 gap-md-3 mb-4">
            <p class="result-count mb-0">
              <strong>{{ filteredProducts.length }}</strong>
              <span class="d-none d-sm-inline"> / {{ products.length }} sản phẩm</span>
            </p>

            <div class="d-flex align-items-center gap-2 w-100 w-sm-auto">
              <label for="sortSelect" class="text-muted small mb-0 text-nowrap">Sắp xếp:</label>
              <select
                id="sortSelect"
                v-model="sortBy"
                class="form-select form-select-sm sort-select rounded-pill flex-grow-1 flex-sm-grow-0"
              >
                <option value="newest">Mới nhất</option>
                <option value="price-asc">Giá tăng dần</option>
                <option value="price-desc">Giá giảm dần</option>
              </select>
            </div>
          </div>

          <div v-if="filteredProducts.length" class="row g-4">
            <div
              v-for="product in filteredProducts"
              :key="product.ma_san_pham"
              class="col-6 col-sm-4 col-md-4"
            >
              <ProductCard :product="product" />
            </div>
          </div>

          <div v-else class="empty-state text-center py-5 rounded-4">
            <i class="bi bi-sliders display-4 text-muted"></i>
            <h5 class="mt-3 fw-bold">Không tìm thấy sản phẩm phù hợp</h5>
            <p class="text-muted mb-3">Thử điều chỉnh bộ lọc hoặc xóa một số tiêu chí.</p>
            <button type="button" class="btn btn-accent rounded-pill px-4" @click="resetFilters">
              Xóa bộ lọc
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import ShopFilterSidebar from '../components/ShopFilterSidebar.vue'
import { fetchSanPham, buildFilterOptions } from '../services/catalogService'
import { getGiaHienThi, getKichCoList, coKhuyenMai } from '../utils/productHelpers'

const route = useRoute()
const sortBy = ref('newest')
const loading = ref(true)
const products = ref([])
const filterOptions = ref({
  categories: [],
  sizes: [],
  priceRanges: [],
  maxPrice: 0,
})

const filters = reactive({
  ma_danh_muc: [],
  kich_co: [],
  priceRanges: [],
  maxPrice: 0,
})

function createDefaultFilters(maxPrice) {
  return {
    ma_danh_muc: [],
    kich_co: [],
    priceRanges: [],
    maxPrice,
  }
}

onMounted(async () => {
  const [productList, options] = await Promise.all([
    fetchSanPham(),
    buildFilterOptions(),
  ])
  products.value = productList
  filterOptions.value = options
  Object.assign(filters, createDefaultFilters(options.maxPrice))
  syncQueryToFilters()
  loading.value = false
})

const pageTitle = computed(() => route.meta.title || 'Cửa hàng SportPro')
const searchQuery = computed(() => route.query.q || '')
const isSalePage = computed(() => route.meta.sale === true)

function syncQueryToFilters() {
  const maDanhMuc = Number(route.query.ma_danh_muc)
  if (maDanhMuc) {
    filters.ma_danh_muc = [maDanhMuc]
  }
}

watch(() => route.query, syncQueryToFilters)

const hasActiveFilters = computed(() => {
  return (
    filters.ma_danh_muc.length > 0 ||
    filters.kich_co.length > 0 ||
    filters.priceRanges.length > 0 ||
    filters.maxPrice < filterOptions.value.maxPrice
  )
})

function handleToggleFilter({ group, value }) {
  const list = filters[group]
  const index = list.indexOf(value)
  if (index === -1) list.push(value)
  else list.splice(index, 1)
}

function resetFilters() {
  Object.assign(filters, createDefaultFilters(filterOptions.value.maxPrice))
}

function matchesPriceRange(gia, rangeId) {
  const range = filterOptions.value.priceRanges.find((r) => r.id === rangeId)
  if (!range) return true
  return gia >= range.min && gia < range.max
}

const filteredProducts = computed(() => {
  let result = [...products.value]

  if (isSalePage.value) {
    result = result.filter((p) => coKhuyenMai(p))
  }

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(
      (p) =>
        p.ten_san_pham.toLowerCase().includes(q) ||
        p.ten_danh_muc?.toLowerCase().includes(q) ||
        p.ten_thuong_hieu?.toLowerCase().includes(q)
    )
  }

  if (filters.ma_danh_muc.length) {
    result = result.filter((p) => filters.ma_danh_muc.includes(p.ma_danh_muc))
  }

  if (filters.kich_co.length) {
    result = result.filter((p) =>
      getKichCoList(p).some((s) => filters.kich_co.includes(s))
    )
  }

  if (filters.priceRanges.length) {
    result = result.filter((p) =>
      filters.priceRanges.some((rangeId) =>
        matchesPriceRange(getGiaHienThi(p), rangeId)
      )
    )
  }

  result = result.filter((p) => getGiaHienThi(p) <= filters.maxPrice)

  switch (sortBy.value) {
    case 'price-asc':
      result.sort((a, b) => getGiaHienThi(a) - getGiaHienThi(b))
      break
    case 'price-desc':
      result.sort((a, b) => getGiaHienThi(b) - getGiaHienThi(a))
      break
    case 'newest':
    default:
      result.sort((a, b) => new Date(b.ngay_tao) - new Date(a.ngay_tao))
      break
  }

  return result
})
</script>

<style scoped>
.shop-toolbar {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.85rem 1.25rem;
}

.result-count {
  font-size: 0.95rem;
  color: #64748b;
}

.sort-select {
  min-width: 160px;
  border-color: #e2e8f0;
  font-size: 0.9rem;
}

.empty-state {
  background: #fff;
  border: 1px dashed #cbd5e1;
}

.btn-filter-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.35rem;
  padding: 0.65rem 1rem;
  margin-bottom: 0.75rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  font-weight: 700;
  color: #334155;
}

.filter-active-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--sportpro-accent);
  margin-left: auto;
}

@media (max-width: 575.98px) {
  .shop-toolbar {
    flex-direction: column;
    align-items: stretch !important;
  }

  .sort-select {
    min-width: 0 !important;
    width: 100%;
  }
}
</style>
