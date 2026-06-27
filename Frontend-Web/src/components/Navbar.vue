<template>
  <nav class="navbar navbar-expand-lg navbar-dark sportpro-navbar sticky-top">
    <div class="container">
      <RouterLink class="navbar-brand d-flex align-items-center gap-2" to="/" title="Về trang chủ">
        <span class="brand-icon">
          <i class="bi bi-lightning-charge-fill"></i>
        </span>
        <span class="brand-text">
          Sport<span class="text-accent">Pro</span>
        </span>
      </RouterLink>

      <button
        class="navbar-toggler border-0"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSportPro"
        aria-controls="navbarSportPro"
        aria-expanded="false"
        aria-label="Mở menu"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSportPro">
        <ul class="navbar-nav navbar-main-nav mb-3 mb-lg-0">
          <!-- Danh mục dropdown -->
          <li class="nav-item dropdown">
            <a
              class="nav-link dropdown-toggle px-lg-2"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
              :class="{ active: isAnyCategoryActive }"
              @click.prevent
            >
              Danh mục
            </a>
            <ul class="dropdown-menu dropdown-menu-dark sportpro-dropdown">
              <li v-for="dm in danhMucList" :key="dm.ma_danh_muc">
                <RouterLink
                  class="dropdown-item"
                  :class="{ active: isCategoryActive(dm.ma_danh_muc) }"
                  :to="{ name: 'shop', query: { ma_danh_muc: dm.ma_danh_muc } }"
                >
                  <i class="bi bi-chevron-right item-arrow"></i>
                  {{ dm.ten_danh_muc }}
                </RouterLink>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <RouterLink class="dropdown-item" :to="{ name: 'shop' }">
                  <i class="bi bi-grid me-1"></i> Xem tất cả sản phẩm
                </RouterLink>
              </li>
            </ul>
          </li>

          <li class="nav-item">
            <RouterLink custom v-slot="{ href, navigate }" :to="{ name: 'shop' }">
              <a
                :href="href"
                class="nav-link px-lg-2"
                :class="{ active: isShopActive }"
                @click="navigateAndBlur(navigate, $event)"
              >
                Cửa hàng
              </a>
            </RouterLink>
          </li>

          <li class="nav-item">
            <RouterLink custom v-slot="{ href, navigate }" :to="{ name: 'sale' }">
              <a
                :href="href"
                class="nav-link nav-link-sale px-lg-2"
                :class="{ active: isSaleActive }"
                @click="navigateAndBlur(navigate, $event)"
              >
                <i class="bi bi-percent sale-icon"></i>
                Giảm giá
              </a>
            </RouterLink>
          </li>
        </ul>

        <div class="navbar-end ms-lg-auto">
          <form class="navbar-search" role="search" @submit.prevent="handleSearch">
            <div class="search-input-wrap">
              <i class="bi bi-search search-icon" aria-hidden="true"></i>
              <input
                v-model="searchQuery"
                class="form-control search-input"
                type="search"
                placeholder="Tìm áo, giày..."
                aria-label="Tìm kiếm sản phẩm"
              />
            </div>
          </form>

          <div class="navbar-actions">
            <RouterLink
              :to="{ name: 'order-history' }"
              class="btn btn-outline-light btn-sm rounded-pill px-3 d-flex align-items-center gap-1"
              title="Lịch sử đơn hàng"
            >
              <i class="bi bi-bag-check"></i>
              <span class="d-none d-xl-inline">Đơn hàng</span>
            </RouterLink>

            <RouterLink
              to="/dang-nhap"
              class="btn btn-outline-light btn-sm rounded-pill px-3 d-flex align-items-center gap-1"
            >
              <i class="bi bi-person"></i>
              <span class="d-none d-xl-inline">Tài khoản</span>
            </RouterLink>

            <RouterLink
              to="/gio-hang"
              class="btn btn-accent btn-sm rounded-pill px-3 position-relative d-flex align-items-center gap-1"
            >
              <i class="bi bi-cart3"></i>
              <span class="d-none d-xl-inline">Giỏ hàng</span>
              <span
                v-if="cartCount > 0"
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
              >
                {{ cartCount }}
              </span>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchDanhMuc } from '../services/catalogService'
import { fetchCartItemCount } from '../services/cartService'

const router = useRouter()
const route = useRoute()
const searchQuery = ref('')
const cartCount = ref(0)
const danhMucList = ref([])

const isShopActive = computed(
  () => route.name === 'shop' && !route.query.ma_danh_muc && !route.meta?.sale
)

const isSaleActive = computed(() => route.name === 'sale')

const isAnyCategoryActive = computed(
  () => route.name === 'shop' && Boolean(route.query.ma_danh_muc)
)

function isCategoryActive(maDanhMuc) {
  return (
    route.name === 'shop' &&
    Number(route.query.ma_danh_muc) === Number(maDanhMuc)
  )
}

onMounted(async () => {
  danhMucList.value = await fetchDanhMuc()
  await refreshCartCount()
})

watch(
  () => route.path,
  () => {
    refreshCartCount()
  }
)

async function refreshCartCount() {
  cartCount.value = await fetchCartItemCount()
}

function handleSearch() {
  const query = searchQuery.value.trim()
  if (!query) return
  router.push({ name: 'shop', query: { q: query } })
}

function navigateAndBlur(navigate, event) {
  navigate(event)
  event.currentTarget?.blur()
}
</script>

<style scoped>
.sportpro-navbar {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  padding-block: 0.75rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: 800;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  text-decoration: none;
  flex-shrink: 0;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--sportpro-accent);
  color: #fff;
  font-size: 1.1rem;
}

.brand-text {
  color: #fff;
}

.text-accent {
  color: var(--sportpro-accent) !important;
}

.nav-link {
  font-weight: 600;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.85) !important;
  text-decoration: none;
  transition: color 0.2s ease, background 0.2s ease;
  white-space: nowrap;
}

.nav-link.dropdown-toggle::after {
  margin-left: 0.35em;
  vertical-align: 0.15em;
}

.nav-link-sale {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  margin-left: 0.15rem;
  padding: 0.35rem 0.75rem !important;
  border-radius: 50rem;
  background: rgba(249, 115, 22, 0.15);
  border: 1px solid rgba(249, 115, 22, 0.35);
  color: #fdba74 !important;
  transition: background 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

/* Chỉ đổi nền cam khi hover — không phải trang đang xem */
.nav-link-sale:hover:not(.active) {
  background: var(--sportpro-accent) !important;
  border-color: var(--sportpro-accent) !important;
  color: #fff !important;
}

/* Đang ở trang giảm giá: giữ style pill mặc định, không giống hover */
.nav-link-sale.active {
  background: rgba(249, 115, 22, 0.15) !important;
  border-color: rgba(249, 115, 22, 0.5) !important;
  color: #fdba74 !important;
  box-shadow: inset 0 0 0 1px rgba(249, 115, 22, 0.25);
}

.nav-link-sale.active:hover,
.nav-link-sale.active:focus {
  background: rgba(249, 115, 22, 0.15) !important;
  border-color: rgba(249, 115, 22, 0.5) !important;
  color: #fdba74 !important;
}

.nav-link-sale.active::after {
  display: none;
}

.nav-link-sale:focus:not(:focus-visible) {
  background: rgba(249, 115, 22, 0.15) !important;
  border-color: rgba(249, 115, 22, 0.35) !important;
  color: #fdba74 !important;
  outline: none;
  box-shadow: none;
}

.sale-icon {
  font-size: 0.85rem;
}

.sportpro-dropdown {
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 0.5rem;
  margin-top: 0.5rem;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
  min-width: 220px;
}

.sportpro-dropdown .dropdown-item {
  border-radius: 8px;
  padding: 0.5rem 0.85rem;
  font-size: 0.88rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.sportpro-dropdown .dropdown-item:hover,
.sportpro-dropdown .dropdown-item.active {
  background: rgba(249, 115, 22, 0.2);
  color: #fff;
}

.sportpro-dropdown .item-arrow {
  font-size: 0.65rem;
  opacity: 0.5;
}

.sportpro-dropdown .dropdown-divider {
  border-color: rgba(255, 255, 255, 0.1);
  margin: 0.35rem 0;
}

.nav-link:not(.nav-link-sale):hover {
  color: var(--sportpro-accent) !important;
}

.nav-link:not(.nav-link-sale).active {
  color: var(--sportpro-accent) !important;
  position: relative;
}

.nav-link:not(.nav-link-sale).active::after {
  content: '';
  position: absolute;
  left: 0.75rem;
  right: 0.75rem;
  bottom: 0.15rem;
  height: 2px;
  background: var(--sportpro-accent);
  border-radius: 2px;
}

.nav-link:focus {
  outline: none;
  box-shadow: none;
}

.nav-link:focus-visible {
  outline: 2px solid var(--sportpro-accent);
  outline-offset: 3px;
  border-radius: 4px;
}

.navbar-end {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 0.75rem;
  width: 100%;
}

.navbar-search {
  width: 100%;
  min-width: 0;
}

.search-input-wrap {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.search-icon {
  position: absolute;
  left: 0.9rem;
  color: #94a3b8;
  font-size: 0.95rem;
  pointer-events: none;
  z-index: 1;
}

.search-input {
  width: 100%;
  min-width: 0;
  padding: 0.45rem 0.9rem 0.45rem 2.4rem;
  border: none;
  border-radius: 50rem;
  font-size: 0.9rem;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-input:focus {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.14);
  border-color: transparent;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.btn-accent {
  background: var(--sportpro-accent);
  border: none;
  color: #fff;
  font-weight: 600;
}

.btn-accent:hover {
  background: var(--sportpro-accent-dark);
  color: #fff;
}

@media (min-width: 992px) {
  .navbar-collapse {
    display: flex !important;
    align-items: center;
    flex-wrap: nowrap;
    gap: 0.75rem;
  }

  .navbar-main-nav {
    flex: 0 0 auto;
    flex-wrap: nowrap;
  }

  .navbar-end {
    flex-direction: row;
    align-items: center;
    flex: 1 1 auto;
    justify-content: flex-end;
    min-width: 0;
    width: auto;
    gap: 0.65rem;
  }

  .navbar-search {
    flex: 1 1 auto;
    min-width: 100px;
    max-width: 220px;
  }

  .navbar-actions {
    flex: 0 0 auto;
    flex-shrink: 0;
  }

  .navbar-actions .btn {
    padding-left: 0.65rem !important;
    padding-right: 0.65rem !important;
  }
}

@media (min-width: 1200px) {
  .navbar-search {
    max-width: 260px;
  }

  .nav-link {
    font-size: 0.95rem;
  }

  .navbar-actions .btn {
    padding-left: 0.85rem !important;
    padding-right: 0.85rem !important;
  }
}

@media (min-width: 1400px) {
  .navbar-search {
    max-width: 300px;
  }
}

@media (max-width: 991.98px) {
  .navbar-brand {
    font-size: 1.25rem;
  }

  .brand-icon {
    width: 32px;
    height: 32px;
    font-size: 1rem;
  }

  .navbar-actions .btn {
    flex: 1;
    justify-content: center;
  }

  .nav-link-sale {
    margin-left: 0;
    width: fit-content;
  }

  .sportpro-dropdown {
    border: none;
    background: rgba(255, 255, 255, 0.05);
    box-shadow: none;
    margin-left: 0.5rem;
  }
}
</style>
