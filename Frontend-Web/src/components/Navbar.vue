<template>
  <nav class="web-navbar sticky-top" :class="{ scrolled: isScrolled }">
    <div class="container">
      <div class="nav-inner">
        <router-link class="brand" to="/">
          <img src="/logo-sm.png" alt="SportPro" class="brand-logo" width="40" height="40" />
          <div class="brand-text">
            <strong>SportPro</strong>
            <span>Thể thao chính hãng</span>
          </div>
        </router-link>

        <div class="nav-desktop d-none d-md-flex">
          <ul class="nav-links">
            <li>
              <router-link to="/" active-class="active">Trang chủ</router-link>
            </li>
            <li>
              <router-link to="/lookup" active-class="active">Tra cứu đơn</router-link>
            </li>
            <li>
              <router-link to="/history" active-class="active">Lịch sử đơn</router-link>
            </li>
          </ul>
        </div>

        <div class="nav-actions">
          <router-link to="/cart" class="icon-btn" title="Giỏ hàng">
            <i class="bi bi-bag"></i>
          </router-link>

          <div v-if="user" class="dropdown d-none d-sm-block">
            <button class="user-chip dropdown-toggle" type="button" data-bs-toggle="dropdown">
              <span class="avatar">{{ userInitial }}</span>
              <span class="user-name">{{ user.hoTen || user.email }}</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
              <li>
                <router-link class="dropdown-item" to="/account">
                  <i class="bi bi-person me-2"></i> Tài khoản
                </router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/history">
                  <i class="bi bi-bag-check me-2"></i> Đơn mua
                </router-link>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <a class="dropdown-item text-danger fw-semibold" href="#" @click.prevent="logout">
                  <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
                </a>
              </li>
            </ul>
          </div>

          <router-link v-else to="/login" class="btn btn-primary btn-sm px-3 login-btn">
            Đăng nhập
          </router-link>

          <button
            class="nav-toggler d-md-none"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarMobile"
            aria-label="Menu"
          >
            <i class="bi bi-list"></i>
          </button>
        </div>
      </div>

      <div class="collapse d-md-none" id="navbarMobile">
        <ul class="nav-links mobile-links">
          <li>
            <router-link to="/" active-class="active">Trang chủ</router-link>
          </li>
          <li>
            <router-link to="/lookup" active-class="active">Tra cứu đơn</router-link>
          </li>
          <li>
            <router-link to="/history" active-class="active">Lịch sử đơn</router-link>
          </li>
          <li v-if="user">
            <router-link to="/account" active-class="active">Tài khoản</router-link>
          </li>
          <li v-if="user">
            <a href="#" class="text-danger" @click.prevent="logout">Đăng xuất</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStoredUser } from '../utils/auth'

const router = useRouter()
const user = ref(null)
const isScrolled = ref(false)

const userInitial = computed(() => {
  const name = user.value?.hoTen || user.value?.email || 'U'
  return name.charAt(0).toUpperCase()
})

const checkUser = () => {
  user.value = getStoredUser()
}

const onScroll = () => {
  isScrolled.value = window.scrollY > 8
}

onMounted(() => {
  checkUser()
  onScroll()
  window.addEventListener('storage', checkUser)
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('storage', checkUser)
  window.removeEventListener('scroll', onScroll)
})

const logout = () => {
  localStorage.removeItem('user')
  user.value = null
  router.push('/login')
}
</script>

<style scoped>
.web-navbar {
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid transparent;
  z-index: 1030;
  transition: box-shadow 0.25s ease, border-color 0.25s ease, background 0.25s ease;
}

.web-navbar.scrolled {
  background: rgba(255, 255, 255, 0.94);
  border-bottom-color: var(--sp-border, #e8ecf3);
  box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06);
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  min-height: 72px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  color: inherit;
  text-decoration: none;
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.brand:hover {
  transform: translateY(-1px);
}

.brand-logo {
  width: 44px;
  height: 44px;
  object-fit: contain;
  background: linear-gradient(145deg, #fff7ed, #fff);
  border: 1.5px solid #ffedd5;
  border-radius: 13px;
  padding: 4px;
  box-shadow: 0 6px 16px rgba(249, 115, 22, 0.12);
  transition: transform 0.35s cubic-bezier(0.34, 1.4, 0.64, 1), box-shadow 0.3s ease;
}

.brand:hover .brand-logo {
  transform: rotate(-6deg) scale(1.05);
  box-shadow: 0 10px 22px rgba(249, 115, 22, 0.2);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand-text strong {
  font-size: 1.08rem;
  font-weight: 800;
  color: #0b1220;
  letter-spacing: -0.02em;
}

.brand-text span {
  font-size: 0.72rem;
  color: #64748b;
  font-weight: 600;
}

.nav-desktop {
  flex: 1;
  justify-content: center;
}

.nav-links {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-links a {
  display: inline-flex;
  align-items: center;
  padding: 0.52rem 0.95rem;
  border-radius: 999px;
  color: #64748b;
  font-weight: 650;
  font-size: 0.92rem;
  text-decoration: none;
  transition: color 0.25s ease, background 0.25s ease, transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  position: relative;
}

.nav-links a:hover {
  color: var(--sp-blue, #1d4ed8);
  background: rgba(29, 78, 216, 0.06);
  transform: translateY(-1px);
}

.nav-links a.active {
  color: var(--sp-blue, #1d4ed8);
  background: rgba(29, 78, 216, 0.1);
}

.mobile-links {
  flex-direction: column;
  padding: 0.25rem 0 1rem;
  gap: 0.2rem;
}

.mobile-links a {
  width: 100%;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 0.55rem;
}

.nav-toggler {
  width: 40px;
  height: 40px;
  border: 1px solid var(--sp-border, #e8ecf3);
  border-radius: 12px;
  background: #fff;
  color: #334155;
  display: grid;
  place-items: center;
  font-size: 1.25rem;
}

.icon-btn {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  border: 1px solid var(--sp-border, #e8ecf3);
  background: #fff;
  color: #475569;
  display: grid;
  place-items: center;
  font-size: 1.12rem;
  transition: all 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.icon-btn:hover {
  color: var(--sp-blue, #1d4ed8);
  border-color: rgba(29, 78, 216, 0.3);
  background: rgba(29, 78, 216, 0.06);
  transform: translateY(-2px);
}

.icon-btn:hover i {
  animation: bagBounce 0.45s cubic-bezier(0.34, 1.4, 0.64, 1);
}

@keyframes bagBounce {
  0% { transform: translateY(0); }
  40% { transform: translateY(-3px); }
  100% { transform: translateY(0); }
}

.login-btn {
  min-height: 40px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.3rem 0.7rem 0.3rem 0.3rem;
  border-radius: 999px;
  border: 1px solid var(--sp-border, #e8ecf3);
  background: #fff;
  font-weight: 650;
  color: #0f172a;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  color: #fff;
  font-size: 0.78rem;
  font-weight: 750;
}

.user-name {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.88rem;
}

.dropdown-menu {
  border-radius: 14px !important;
  border: 1px solid var(--sp-border, #e8ecf3) !important;
  padding: 0.4rem;
}

.dropdown-item {
  border-radius: 10px;
  font-weight: 600;
  padding: 0.55rem 0.75rem;
}
</style>
