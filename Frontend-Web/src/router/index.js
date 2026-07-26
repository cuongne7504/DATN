import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'TrangChu',
    component: () => import('../views/TrangChu.vue')
  },
  {
    path: '/product/:id',
    name: 'ChiTietSanPham',
    component: () => import('../views/ChiTietSanPham.vue')
  },
  {
    path: '/cart',
    name: 'GioHang',
    component: () => import('../views/GioHang.vue')
  },
  {
    path: '/checkout',
    name: 'ThanhToan',
    component: () => import('../views/ThanhToan.vue')
  },
  {
    path: '/history',
    name: 'LichSuDonHang',
    component: () => import('../views/LichSuDonHang.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/lookup',
    name: 'TraCuuDonHang',
    component: () => import('../views/TraCuuDonHang.vue')
  },
  {
    path: '/account',
    name: 'TaiKhoan',
    component: () => import('../views/TaiKhoan.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'DangNhap',
    component: () => import('../views/DangNhap.vue')
  },
  {
    path: '/register',
    name: 'DangKy',
    component: () => import('../views/DangKy.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))

  if (to.meta.requiresAuth && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
