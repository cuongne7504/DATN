<template>
  <ToastStack />
  <div v-if="isLoginRoute">
    <router-view />
  </div>

  <div v-else class="admin-shell">
    <aside class="admin-sidebar" :class="{ open: sidebarOpen }">
      <div class="sidebar-brand">
        <img src="/logo.png" alt="SportPro" class="brand-logo" />
        <div class="brand-text">
          <strong>SportPro</strong>
          <span>Admin Panel</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <p class="nav-section">Tổng quan</p>
        <router-link
          v-if="user?.maQuyen === 1"
          class="sidebar-link"
          active-class="active"
          to="/admin/dashboard"
          @click="closeSidebar"
        >
          <i class="bi bi-speedometer2"></i>
          <span>Thống kê</span>
        </router-link>

        <p class="nav-section">Kinh doanh</p>
        <router-link class="sidebar-link" active-class="active" to="/admin/pos" @click="closeSidebar">
          <i class="bi bi-bag-check"></i>
          <span>Bán tại quầy</span>
        </router-link>
        <router-link class="sidebar-link" active-class="active" to="/admin/orders" @click="closeSidebar">
          <i class="bi bi-bag-check"></i>
          <span>Đơn hàng</span>
        </router-link>
        <router-link class="sidebar-link" active-class="active" to="/admin/invoices" @click="closeSidebar">
          <i class="bi bi-receipt-cutoff"></i>
          <span>Hóa đơn</span>
        </router-link>
        <router-link class="sidebar-link" active-class="active" to="/admin/returns" @click="closeSidebar">
          <i class="bi bi-arrow-return-left"></i>
          <span>Hoàn hàng</span>
        </router-link>
        <router-link
          v-if="user?.maQuyen === 1 || user?.maQuyen === 2"
          class="sidebar-link"
          active-class="active"
          to="/admin/inventory"
          @click="closeSidebar"
        >
          <i class="bi bi-archive"></i>
          <span>Kho hàng</span>
        </router-link>

        <template v-if="user?.maQuyen === 1">
          <p class="nav-section">Sản phẩm</p>
          <router-link class="sidebar-link" active-class="active" to="/admin/products" @click="closeSidebar">
            <i class="bi bi-box-seam"></i>
            <span>Sản phẩm</span>
          </router-link>
          <router-link class="sidebar-link" active-class="active" to="/admin/variants" @click="closeSidebar">
            <i class="bi bi-grid-3x3-gap"></i>
            <span>Biến thể</span>
          </router-link>
          <router-link class="sidebar-link" active-class="active" to="/admin/categories" @click="closeSidebar">
            <i class="bi bi-tags"></i>
            <span>Danh mục</span>
          </router-link>
          <router-link class="sidebar-link" active-class="active" to="/admin/brands" @click="closeSidebar">
            <i class="bi bi-award"></i>
            <span>Thương hiệu</span>
          </router-link>
          <router-link class="sidebar-link" active-class="active" to="/admin/vouchers" @click="closeSidebar">
            <i class="bi bi-ticket-perforated"></i>
            <span>Khuyến mãi</span>
          </router-link>

          <p class="nav-section">Tài khoản</p>
          <router-link class="sidebar-link" active-class="active" to="/admin/employees" @click="closeSidebar">
            <i class="bi bi-person-badge"></i>
            <span>Nhân viên</span>
          </router-link>
          <router-link class="sidebar-link" active-class="active" to="/admin/customers" @click="closeSidebar">
            <i class="bi bi-people"></i>
            <span>Khách hàng</span>
          </router-link>
        </template>
      </nav>
    </aside>

    <div class="sidebar-backdrop" v-if="sidebarOpen" @click="closeSidebar"></div>

    <div class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <button class="icon-btn d-lg-none" type="button" @click="sidebarOpen = !sidebarOpen" aria-label="Menu">
            <i class="bi bi-list"></i>
          </button>
          <div>
            <div class="page-kicker">SportPro Management</div>
            <h1 class="page-title">{{ pageTitle }}</h1>
          </div>
        </div>

        <div class="header-right" v-if="user">
          <div class="user-chip">
            <div class="avatar">{{ userInitial }}</div>
            <div class="user-meta">
              <strong>{{ user.hoTen || user.email }}</strong>
              <span>{{ roleLabel }}</span>
            </div>
          </div>
          <button class="logout-btn" type="button" @click="logout" title="Đăng xuất">
            <i class="bi bi-box-arrow-right"></i>
            <span class="d-none d-md-inline">Thoát</span>
          </button>
        </div>
      </header>

      <main class="admin-content">
        <router-view v-slot="{ Component }">
          <Transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </Transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ToastStack from '@/components/ToastStack.vue'

const route = useRoute()
const router = useRouter()
const user = ref(null)
const sidebarOpen = ref(false)

const isLoginRoute = computed(() => route.path === '/login')

const titleMap = {
  '/admin/dashboard': 'Thống kê',
  '/admin/categories': 'Danh mục',
  '/admin/brands': 'Thương hiệu',
  '/admin/products': 'Sản phẩm',
  '/admin/variants': 'Biến thể sản phẩm',
  '/admin/orders': 'Đơn hàng',
  '/admin/invoices': 'Hóa đơn',
  '/admin/returns': 'Hoàn hàng',
  '/admin/inventory': 'Kho hàng',
  '/admin/vouchers': 'Khuyến mãi',
  '/admin/employees': 'Nhân viên',
  '/admin/customers': 'Khách hàng',
  '/admin/pos': 'Bán tại quầy'
}

const pageTitle = computed(() => titleMap[route.path] || 'Quản trị')

const userInitial = computed(() => {
  const name = user.value?.hoTen || user.value?.email || 'A'
  return name.charAt(0).toUpperCase()
})

const roleLabel = computed(() => {
  if (user.value?.maQuyen === 1) return 'Quản trị viên'
  if (user.value?.maQuyen === 2) return 'Nhân viên'
  return 'Người dùng'
})

const checkUser = () => {
  const userData = localStorage.getItem('user')
  user.value = userData ? JSON.parse(userData) : null
}

const closeSidebar = () => {
  sidebarOpen.value = false
}

watch(() => route.path, closeSidebar)

onMounted(() => {
  checkUser()
  window.addEventListener('storage', checkUser)
})

onUnmounted(() => {
  window.removeEventListener('storage', checkUser)
})

const logout = () => {
  localStorage.removeItem('user')
  user.value = null
  router.push('/login')
}
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(249, 115, 22, 0.08), transparent 28%),
    radial-gradient(circle at top left, rgba(29, 78, 216, 0.08), transparent 30%),
    var(--sp-bg);
}

.admin-sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  width: var(--sp-sidebar-w);
  background: #ffffff;
  border-right: 1px solid var(--sp-border);
  display: flex;
  flex-direction: column;
  z-index: 1040;
  box-shadow: 4px 0 24px rgba(15, 23, 42, 0.03);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  padding: 1.15rem 1.2rem;
  border-bottom: 1px solid var(--sp-border);
  min-height: var(--sp-header-h);
}

.brand-logo {
  width: 42px;
  height: 42px;
  object-fit: contain;
  border-radius: 12px;
  background: #fff7ed;
  padding: 4px;
  box-shadow: inset 0 0 0 1px rgba(249, 115, 22, 0.12);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand-text strong {
  font-size: 1.05rem;
  letter-spacing: -0.02em;
  color: #0f172a;
}

.brand-text span {
  font-size: 0.75rem;
  color: var(--sp-muted);
  font-weight: 600;
}

.sidebar-nav {
  padding: 0.9rem 0.85rem 1.25rem;
  overflow-y: auto;
  flex: 1;
}

.nav-section {
  margin: 0.95rem 0.65rem 0.4rem;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #94a3b8;
}

.nav-section:first-child {
  margin-top: 0.25rem;
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.72rem 0.85rem;
  margin-bottom: 0.2rem;
  border-radius: 12px;
  color: #475569;
  font-weight: 600;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.sidebar-link i {
  font-size: 1.05rem;
  width: 1.2rem;
  text-align: center;
}

.sidebar-link:hover {
  background: #f8fafc;
  color: var(--sp-blue);
  transform: translateX(2px);
}

.sidebar-link.active {
  background: rgba(29, 78, 216, 0.1);
  color: var(--sp-blue);
  box-shadow: inset 0 0 0 1px rgba(29, 78, 216, 0.08);
}

.admin-main {
  margin-left: var(--sp-sidebar-w);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 1020;
  min-height: var(--sp-header-h);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.85rem 1.5rem;
  background: rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(232, 236, 243, 0.9);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 0.85rem;
}

.page-kicker {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--sp-orange);
}

.page-title {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 750;
  letter-spacing: -0.02em;
  color: #0f172a;
}

.icon-btn,
.logout-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  border: 1px solid var(--sp-border);
  background: #fff;
  color: #334155;
  border-radius: 12px;
  min-height: 40px;
  padding: 0 0.9rem;
  font-weight: 650;
  transition: all 0.2s ease;
}

.icon-btn {
  width: 40px;
  padding: 0;
  font-size: 1.25rem;
}

.logout-btn:hover,
.icon-btn:hover {
  border-color: rgba(29, 78, 216, 0.25);
  color: var(--sp-blue);
  background: #f8fbff;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.35rem 0.65rem 0.35rem 0.35rem;
  border-radius: 999px;
  background: #fff;
  border: 1px solid var(--sp-border);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--sp-blue), #60a5fa);
  color: #fff;
  font-weight: 750;
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
  padding-right: 0.35rem;
}

.user-meta strong {
  font-size: 0.9rem;
  color: #0f172a;
}

.user-meta span {
  font-size: 0.72rem;
  color: var(--sp-muted);
  font-weight: 600;
}

.admin-content {
  padding: 1.35rem 1.5rem 2rem;
  flex: 1;
}

.sidebar-backdrop {
  display: none;
}

@media (max-width: 991.98px) {
  .admin-sidebar {
    transform: translateX(-105%);
    transition: transform 0.28s ease;
    width: 280px;
  }

  .admin-sidebar.open {
    transform: translateX(0);
  }

  .admin-main {
    margin-left: 0;
  }

  .sidebar-backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.35);
    z-index: 1035;
  }

  .admin-content {
    padding: 1rem;
  }

  .user-meta {
    display: none;
  }
}
</style>
