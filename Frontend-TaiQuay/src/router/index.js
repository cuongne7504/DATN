import { createRouter, createWebHistory } from 'vue-router'
import TrangChuAdmin from '../views/TrangChuAdmin.vue'
import QuanLySanPham from '../views/QuanLySanPham.vue'
import BanHangTaiQuay from '../views/BanHangTaiQuay.vue'
import QuanLyDonHang from '../views/QuanLyDonHang.vue'
import ThongKeDoanhThu from '../views/ThongKeDoanhThu.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'TrangChu', component: TrangChuAdmin },
    { path: '/san-pham', name: 'SanPham', component: QuanLySanPham },
    { path: '/pos', name: 'POS', component: BanHangTaiQuay },
    { path: '/don-hang', name: 'DonHang', component: QuanLyDonHang },
    { path: '/thong-ke', name: 'ThongKe', component: ThongKeDoanhThu },
  ]
})

export default router
