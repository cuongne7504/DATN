<template>
  <div class="product-card h-100">
    <RouterLink :to="`/product/${product.ma_san_pham}`" class="text-decoration-none">
      <div class="product-image-wrap">
        <AppImage
          :src="imageSrc"
          :alt="product.ten_san_pham"
          img-class="product-image"
        />
        <span v-if="badge" class="product-badge">{{ badge }}</span>
        <button type="button" class="btn-wishlist" aria-label="Yêu thích" @click.prevent>
          <i class="bi bi-heart"></i>
        </button>
      </div>
    </RouterLink>
    <div class="product-body">
      <span class="product-category">{{ product.ten_danh_muc }}</span>
      <RouterLink :to="`/product/${product.ma_san_pham}`" class="text-decoration-none">
        <h6 class="product-name">{{ product.ten_san_pham }}</h6>
      </RouterLink>
      <div v-if="product.so_luong_danh_gia > 0" class="product-rating">
        <i
          v-for="n in 5"
          :key="n"
          class="bi bi-star-fill"
          :class="{ muted: n > product.diem_trung_binh }"
        ></i>
        <span class="rating-count">({{ product.so_luong_danh_gia }})</span>
      </div>
      <div class="product-price">
        <span class="price-current">{{ formatPrice(giaHienThi) }}</span>
        <span v-if="coGiamGia" class="price-old">
          {{ formatPrice(product.gia_goc) }}
        </span>
      </div>
      <button type="button" class="btn btn-accent w-100 rounded-pill mt-2">
        <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import AppImage from './AppImage.vue'
import {
  formatPrice,
  getGiaHienThi,
  coKhuyenMai,
  getBadge,
  getAnhChinh,
} from '../utils/productHelpers'

const props = defineProps({
  product: { type: Object, required: true },
})

const imageSrc = computed(() => {
  const p = props.product
  if (p?.duong_dan_anh) return p.duong_dan_anh
  const main = p?.hinh_anh_sp?.find((h) => h.la_anh_chinh) || p?.hinh_anh_sp?.[0]
  return main?.duong_dan_anh ?? null
})
const giaHienThi = computed(() => getGiaHienThi(props.product))
const coGiamGia = computed(() => coKhuyenMai(props.product))
const badge = computed(() => getBadge(props.product))
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  transition: transform 0.25s, box-shadow 0.25s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.1);
}

.product-image-wrap {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f1f5f9;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.product-card:hover .product-image {
  transform: scale(1.06);
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 0.25rem 0.65rem;
  border-radius: 50rem;
  background: var(--sportpro-accent);
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
}

.btn-wishlist {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.95);
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-body {
  padding: 1rem 1.1rem 1.25rem;
}

.product-category {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--sportpro-accent);
}

.product-name {
  font-weight: 700;
  color: #1e293b;
  margin: 0.35rem 0 0.5rem;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-rating {
  font-size: 0.8rem;
  color: #f59e0b;
  margin-bottom: 0.5rem;
}

.product-rating .muted {
  color: #cbd5e1;
}

.rating-count {
  color: #94a3b8;
  margin-left: 0.25rem;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.price-current {
  font-size: 1.05rem;
  font-weight: 800;
  color: #dc2626;
}

.price-old {
  font-size: 0.85rem;
  color: #94a3b8;
  text-decoration: line-through;
}

.btn-accent {
  background: var(--sportpro-accent);
  border: none;
  color: #fff;
  font-weight: 600;
  font-size: 0.9rem;
}

.btn-accent:hover {
  background: var(--sportpro-accent-dark);
  color: #fff;
}

@media (max-width: 575.98px) {
  .product-body {
    padding: 0.75rem 0.85rem 1rem;
  }

  .product-name {
    font-size: 0.88rem;
  }

  .price-current {
    font-size: 0.95rem;
  }

  .btn-wishlist {
    width: 32px;
    height: 32px;
  }
}
</style>
