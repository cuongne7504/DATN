<template>
  <aside class="shop-filter-sidebar">
    <div class="filter-card rounded-4 shadow-sm">
      <div class="filter-header d-flex justify-content-between align-items-center">
        <h5 class="filter-title mb-0">
          <i class="bi bi-funnel me-2"></i>Bộ lọc
        </h5>
        <button
          v-if="hasActiveFilters"
          type="button"
          class="btn btn-link btn-sm text-accent p-0"
          @click="$emit('reset')"
        >
          Xóa tất cả
        </button>
      </div>

      <div class="filter-group">
        <h6 class="filter-group-title">Danh mục</h6>
        <div v-for="dm in filterOptions.categories" :key="dm.ma_danh_muc" class="form-check">
          <input
            :id="`cat-${dm.ma_danh_muc}`"
            class="form-check-input"
            type="checkbox"
            :checked="filters.ma_danh_muc.includes(dm.ma_danh_muc)"
            @change="toggleFilter('ma_danh_muc', dm.ma_danh_muc)"
          />
          <label class="form-check-label" :for="`cat-${dm.ma_danh_muc}`">
            {{ dm.ten_danh_muc }}
          </label>
        </div>
      </div>

      <div class="filter-group">
        <h6 class="filter-group-title">Kích cỡ / Size</h6>
        <div class="size-grid">
          <label
            v-for="size in filterOptions.sizes"
            :key="size"
            class="size-chip"
            :class="{ active: filters.kich_co.includes(size) }"
          >
            <input
              type="checkbox"
              class="visually-hidden"
              :checked="filters.kich_co.includes(size)"
              @change="toggleFilter('kich_co', size)"
            />
            {{ size }}
          </label>
        </div>
      </div>

      <div class="filter-group">
        <h6 class="filter-group-title">Khoảng giá</h6>
        <div
          v-for="range in filterOptions.priceRanges"
          :key="range.id"
          class="form-check"
        >
          <input
            :id="`price-${range.id}`"
            class="form-check-input"
            type="checkbox"
            :checked="filters.priceRanges.includes(range.id)"
            @change="toggleFilter('priceRanges', range.id)"
          />
          <label class="form-check-label" :for="`price-${range.id}`">
            {{ range.label }}
          </label>
        </div>
      </div>

      <div class="filter-group mb-0">
        <h6 class="filter-group-title">
          Giá tối đa: {{ formatPrice(filters.maxPrice) }}
        </h6>
        <input
          type="range"
          class="form-range price-range"
          :min="0"
          :max="maxPrice"
          :step="50000"
          :value="filters.maxPrice"
          @input="updateMaxPrice($event.target.value)"
        />
        <div class="d-flex justify-content-between small text-muted">
          <span>0đ</span>
          <span>{{ formatPrice(maxPrice) }}</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { formatPrice } from '../utils/productHelpers'

defineProps({
  filters: { type: Object, required: true },
  filterOptions: { type: Object, required: true },
  maxPrice: { type: Number, required: true },
  hasActiveFilters: { type: Boolean, default: false },
})

const emit = defineEmits(['toggle', 'update-max-price', 'reset'])

function toggleFilter(group, value) {
  emit('toggle', { group, value })
}

function updateMaxPrice(value) {
  emit('update-max-price', Number(value))
}
</script>

<style scoped>
.filter-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 1.25rem;
  position: sticky;
  top: var(--sticky-offset);
}

.filter-header {
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.filter-title {
  font-weight: 700;
  font-size: 1rem;
  color: #0f172a;
}

.text-accent {
  color: var(--sportpro-accent);
  text-decoration: none;
}

.filter-group {
  margin-bottom: 1.25rem;
  padding-bottom: 1.25rem;
  border-bottom: 1px solid #f1f5f9;
}

.filter-group-title {
  font-weight: 700;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  color: #475569;
  margin-bottom: 0.75rem;
}

.form-check {
  margin-bottom: 0.4rem;
}

.form-check-label {
  font-size: 0.9rem;
  color: #334155;
  cursor: pointer;
}

.form-check-input:checked {
  background-color: var(--sportpro-accent);
  border-color: var(--sportpro-accent);
}

.size-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.size-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 42px;
  height: 36px;
  padding: 0 0.5rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.size-chip:hover {
  border-color: var(--sportpro-accent);
  color: var(--sportpro-accent);
}

.size-chip.active {
  background: var(--sportpro-accent);
  border-color: var(--sportpro-accent);
  color: #fff;
}

.price-range {
  accent-color: var(--sportpro-accent);
}

@media (max-width: 991.98px) {
  .filter-card {
    position: static;
  }
}
</style>
