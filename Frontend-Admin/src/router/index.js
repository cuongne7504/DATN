import { createRouter, createWebHistory } from 'vue-router'
import ThongKeDoanhThu from '../views/ThongKeDoanhThu.vue'
import QuanLySanPham from '../views/QuanLySanPham.vue'
import QuanLyBienThe from '../views/QuanLyBienThe.vue'
import QuanLyDonHang from '../views/QuanLyDonHang.vue'
import BanHangTaiQuay from '../views/BanHangTaiQuay.vue'
import DangNhap from '../views/DangNhap.vue'

const routes = [
  {
    path: '/login',
    name: 'DangNhap',
    component: DangNhap
  },
  {
    path: '/',
    redirect: '/admin/dashboard'
  },
  {
    path: '/admin/dashboard',
    name: 'ThongKeDoanhThu',
    component: ThongKeDoanhThu,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/products',
    name: 'QuanLySanPham',
    component: QuanLySanPham,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/variants',
    name: 'QuanLyBienThe',
    component: QuanLyBienThe,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/orders',
    name: 'QuanLyDonHang',
    component: QuanLyDonHang,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/pos',
    name: 'BanHangTaiQuay',
    component: BanHangTaiQuay,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  
  if (to.meta.requiresAuth) {
    if (!user) {
      next('/login')
    } else if (to.meta.requiresAdmin && (user.maQuyen !== 1 && user.maQuyen !== 2)) {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
