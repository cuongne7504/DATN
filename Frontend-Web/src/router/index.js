import { createRouter, createWebHistory } from 'vue-router'
import TrangChu from '../views/TrangChu.vue'
import ChiTietSanPham from '../views/ChiTietSanPham.vue'
import GioHang from '../views/GioHang.vue'
import ThanhToan from '../views/ThanhToan.vue'
import LichSuDonHang from '../views/LichSuDonHang.vue'
import DangNhap from '../views/DangNhap.vue'
import DangKy from '../views/DangKy.vue'

const routes = [
  {
    path: '/login',
    name: 'DangNhap',
    component: DangNhap
  },
  {
    path: '/register',
    name: 'DangKy',
    component: DangKy
  },
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
    component: GioHang
  },
  {
    path: '/checkout',
    name: 'ThanhToan',
    component: ThanhToan
  },
  {
    path: '/orders',
    name: 'LichSuDonHang',
    component: LichSuDonHang
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
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
