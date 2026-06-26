<template>
  <aside class="account-sidebar rounded-4">
    <div class="account-profile">
      <div class="avatar">{{ initials }}</div>
      <div>
        <p class="profile-name mb-0">{{ user.ho_ten }}</p>
        <small class="profile-email">{{ user.email }}</small>
      </div>
    </div>

    <nav class="account-nav">
      <RouterLink
        :to="{ name: 'order-history' }"
        class="account-nav-link"
        :class="{ active: isActive('order-history') || isActive('order-detail') }"
      >
        <i class="bi bi-bag-check"></i>
        Lịch sử đơn hàng
      </RouterLink>
      <RouterLink
        to="/gio-hang"
        class="account-nav-link"
        :class="{ active: isActive('cart') }"
      >
        <i class="bi bi-cart3"></i>
        Giỏ hàng
      </RouterLink>
      <RouterLink
        to="/dang-nhap"
        class="account-nav-link"
        :class="{ active: isActive('login') }"
      >
        <i class="bi bi-person-gear"></i>
        Tài khoản
      </RouterLink>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { getCurrentUser } from '../utils/auth'

const route = useRoute()
const user = getCurrentUser()

const initials = computed(() =>
  user.ho_ten
    .split(' ')
    .map((w) => w[0])
    .slice(-2)
    .join('')
    .toUpperCase()
)

function isActive(name) {
  return route.name === name
}
</script>

<style scoped>
.account-sidebar {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 1.25rem;
  height: fit-content;
}

.account-profile {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--sportpro-accent), #ea580c);
  color: #fff;
  font-weight: 800;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.profile-name {
  font-weight: 700;
  color: #0f172a;
  font-size: 0.95rem;
}

.profile-email {
  color: #94a3b8;
  font-size: 0.78rem;
}

.account-nav {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.account-nav-link {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0.65rem 0.85rem;
  border-radius: 10px;
  color: #475569;
  text-decoration: none;
  font-weight: 600;
  font-size: 0.9rem;
  transition: background 0.2s, color 0.2s;
}

.account-nav-link i {
  font-size: 1.1rem;
  width: 1.25rem;
}

.account-nav-link:hover {
  background: #f8fafc;
  color: var(--sportpro-accent);
}

.account-nav-link.active {
  background: rgba(249, 115, 22, 0.1);
  color: var(--sportpro-accent);
}

@media (max-width: 991.98px) {
  .account-sidebar {
    margin-bottom: 1rem;
  }

  .account-nav {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .account-nav-link {
    flex: 1 1 auto;
    justify-content: center;
    font-size: 0.82rem;
    padding: 0.5rem 0.65rem;
  }
}
</style>
