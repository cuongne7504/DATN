import { createRouter, createWebHistory } from 'vue-router'
import ThongKeDoanhThu from '../views/ThongKeDoanhThu.vue'
import QuanLyDanhMuc from '../views/QuanLyDanhMuc.vue'
import QuanLyThuongHieu from '../views/QuanLyThuongHieu.vue'
import QuanLySanPham from '../views/QuanLySanPham.vue'
import QuanLyBienThe from '../views/QuanLyBienThe.vue'
import QuanLyKhuyenMai from '../views/QuanLyKhuyenMai.vue'
import QuanLyDonHang from '../views/QuanLyDonHang.vue'
import BanHangTaiQuay from '../views/BanHangTaiQuay.vue'
import DangNhap from '../views/DangNhap.vue'
import QuanLyKhachHang from '../views/QuanLyKhachHang.vue'
import QuanLyNhanVien from '../views/QuanLyNhanVien.vue'
import QuanLyKho from '../views/QuanLyKho.vue'
import QuanLyHoanHang from '../views/QuanLyHoanHang.vue'

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
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/categories',
    name: 'QuanLyDanhMuc',
    component: QuanLyDanhMuc,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/brands',
    name: 'QuanLyThuongHieu',
    component: QuanLyThuongHieu,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/products',
    name: 'QuanLySanPham',
    component: QuanLySanPham,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/variants',
    name: 'QuanLyBienThe',
    component: QuanLyBienThe,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/vouchers',
    name: 'QuanLyKhuyenMai',
    component: QuanLyKhuyenMai,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/customers',
    name: 'QuanLyKhachHang',
    component: QuanLyKhachHang,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/employees',
    name: 'QuanLyNhanVien',
    component: QuanLyNhanVien,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1] }
  },
  {
    path: '/admin/inventory',
    name: 'QuanLyKho',
    component: QuanLyKho,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1, 2] }
  },
  {
    path: '/admin/orders',
    name: 'QuanLyDonHang',
    component: QuanLyDonHang,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1, 2] }
  },
  {
    path: '/admin/returns',
    name: 'QuanLyHoanHang',
    component: QuanLyHoanHang,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1, 2] }
  },
  {
    path: '/admin/pos',
    name: 'BanHangTaiQuay',
    component: BanHangTaiQuay,
    meta: { requiresAuth: true, requiresAdmin: true, roles: [1, 2] }
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
    } else if (to.meta.requiresAdmin) {
      if (to.meta.roles && !to.meta.roles.includes(user.maQuyen)) {
        if (user.maQuyen === 2) {
          next('/admin/pos')
        } else {
          next('/')
        }
      } else {
        next()
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
