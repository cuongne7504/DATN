import { createRouter, createWebHistory } from 'vue-router'
import CustomerLayout from '../layouts/CustomerLayout.vue'

const routes = [
  {
    path: '/',
    component: CustomerLayout,
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('../views/TrangChu.vue'),
        meta: { title: 'Trang chủ' },
      },
      {
        path: 'product/:id',
        name: 'product-detail',
        component: () => import('../views/ChiTietSanPham.vue'),
        meta: { title: 'Chi tiết sản phẩm' },
      },
      {
        path: 'shop',
        name: 'shop',
        component: () => import('../views/CuaHang.vue'),
        meta: { title: 'Cửa hàng' },
      },
      {
        path: 'san-pham',
        name: 'products',
        redirect: { name: 'shop' },
      },
      {
        path: 'giam-gia',
        name: 'sale',
        component: () => import('../views/CuaHang.vue'),
        meta: { title: 'Giảm giá', sale: true },
      },
      {
        path: 'gioi-thieu',
        name: 'about',
        component: () => import('../views/GioiThieu.vue'),
        meta: { title: 'Giới thiệu' },
      },
      {
        path: 'lien-he',
        name: 'contact',
        component: () => import('../views/LienHe.vue'),
        meta: { title: 'Liên hệ' },
      },
      {
        path: 'dang-nhap',
        name: 'login',
        component: () => import('../views/DangNhap.vue'),
        meta: { title: 'Đăng nhập' },
      },
      {
        path: 'gio-hang',
        name: 'cart',
        component: () => import('../views/GioHang.vue'),
        meta: { title: 'Giỏ hàng' },
      },
      {
        path: 'thanh-toan',
        name: 'checkout',
        component: () => import('../views/DatHang.vue'),
        meta: { title: 'Thông tin giao hàng' },
      },
      {
        path: 'thanh-toan/dang-xu-ly',
        name: 'payment-processing',
        component: () => import('../views/DangXuLyThanhToan.vue'),
        meta: { title: 'Đang xử lý thanh toán' },
      },
      {
        path: 'thanh-toan/thanh-cong',
        name: 'payment-success',
        component: () => import('../views/ThanhToanThanhCong.vue'),
        meta: { title: 'Thanh toán thành công' },
      },
      {
        path: 'thanh-toan/that-bai',
        name: 'payment-failure',
        component: () => import('../views/ThanhToanLoi.vue'),
        meta: { title: 'Thanh toán thất bại' },
      },
      {
        path: 'don-hang',
        name: 'order-history',
        component: () => import('../views/LichSuDonHang.vue'),
        meta: { title: 'Lịch sử đơn hàng' },
      },
      {
        path: 'don-hang/:id',
        name: 'order-detail',
        component: () => import('../views/ChiTietDonHang.vue'),
        meta: { title: 'Chi tiết đơn hàng' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/Loi404.vue'),
    meta: { title: 'Không tìm thấy trang' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

router.afterEach((to) => {
  document.title = to.meta.title
    ? `${to.meta.title} | SportPro`
    : 'SportPro - Cửa hàng đồ thể thao'
})

export default router
