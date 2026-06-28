import { createRouter, createWebHistory } from 'vue-router'
import TrangChu from '../views/TrangChu.vue'
import ChiTietSanPham from '../views/ChiTietSanPham.vue'
import GioHang from '../views/GioHang.vue'
import ThanhToan from '../views/ThanhToan.vue'
import LichSuDonHang from '../views/LichSuDonHang.vue'
import DangNhap from '../views/DangNhap.vue'

const routes = [
  {
    path: '/',
    name: 'TrangChu',
    component: TrangChu
  },
  {
    path: '/product/:id',
    name: 'ChiTietSanPham',
    component: ChiTietSanPham
  },
  {
    path: '/cart',
    name: 'GioHang',
    component: GioHang,
    meta: { requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'ThanhToan',
    component: ThanhToan,
    meta: { requiresAuth: true }
  },
  {
    path: '/history',
    name: 'LichSuDonHang',
    component: LichSuDonHang,
    meta: { requiresAuth: true }
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
    component: DangNhap
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
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
