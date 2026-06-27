# SportPro — Frontend khách hàng

Giao diện cửa hàng thể thao SportPro (Vue 3 + Vite + Bootstrap 5). Dữ liệu lấy từ **Backend API** — không còn dữ liệu mock trong repo.

## Yêu cầu

- Node.js 18+
- Backend chạy tại URL cấu hình trong `.env` (mặc định `http://localhost:5000/api`)

## Cài đặt

```bash
npm install
cp .env.example .env
# Chỉnh VITE_API_URL nếu backend chạy port khác
npm run dev
```

## Build production

```bash
npm run build
npm run preview
```

## API endpoints cần có (Backend)

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/danh-muc` | Danh mục sản phẩm |
| GET | `/thuong-hieu` | Thương hiệu |
| GET | `/san-pham` | Danh sách sản phẩm |
| GET | `/san-pham/:id` | Chi tiết sản phẩm |
| GET | `/gio-hang/:ma_nguoi_dung` | Giỏ hàng |
| GET | `/don-hang/khach-hang/:ma_nguoi_dung` | Lịch sử đơn |
| GET | `/don-hang/:id` | Chi tiết đơn |
| POST | `/don-hang` | Tạo đơn hàng |
| POST | `/thanh-toan` | Thanh toán online |

## Cấu trúc chính

- `src/services/api.js` — Axios client
- `src/services/catalogService.js` — Sản phẩm, danh mục
- `src/services/cartService.js` — Giỏ hàng
- `src/services/orderService.js` — Đơn hàng
- `src/services/paymentService.js` — Thanh toán

## Biến môi trường

| Biến | Mô tả |
|------|--------|
| `VITE_API_URL` | Base URL API backend |
| `VITE_DEFAULT_USER_ID` | Mã người dùng tạm khi chưa login |
