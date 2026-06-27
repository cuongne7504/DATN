<template>
  <footer class="sportpro-footer">
    <div class="footer-top py-5">
      <div class="container">
        <div class="row g-4">
          <div class="col-lg-4">
            <div class="footer-brand mb-3">
              <i class="bi bi-lightning-charge-fill me-2"></i>
              Sport<span class="text-accent">Pro</span>
            </div>
            <p class="footer-desc">
              Cửa hàng đồ thể thao chính hãng — Trang bị toàn diện cho mọi môn thể thao,
              giao hàng nhanh toàn quốc.
            </p>
            <div class="social-links d-flex gap-2">
              <a href="#" aria-label="Facebook"><i class="bi bi-facebook"></i></a>
              <a href="#" aria-label="Instagram"><i class="bi bi-instagram"></i></a>
              <a href="#" aria-label="YouTube"><i class="bi bi-youtube"></i></a>
              <a href="#" aria-label="TikTok"><i class="bi bi-tiktok"></i></a>
            </div>
          </div>

          <div class="col-12 col-sm-6 col-lg-2">
            <h6 class="footer-heading">Danh mục</h6>
            <ul class="footer-links">
              <li v-for="dm in danhMucList" :key="dm.ma_danh_muc">
                <RouterLink :to="{ name: 'shop', query: { ma_danh_muc: dm.ma_danh_muc } }">
                  {{ dm.ten_danh_muc }}
                </RouterLink>
              </li>
              <li><RouterLink to="/shop">Tất cả sản phẩm</RouterLink></li>
              <li><RouterLink to="/giam-gia">Khuyến mãi</RouterLink></li>
            </ul>
          </div>

          <div class="col-12 col-sm-6 col-lg-2">
            <h6 class="footer-heading">Hỗ trợ</h6>
            <ul class="footer-links">
              <li><RouterLink to="/lien-he">Liên hệ</RouterLink></li>
              <li><a href="#">Chính sách đổi trả</a></li>
              <li><a href="#">Hướng dẫn mua hàng</a></li>
              <li><a href="#">Câu hỏi thường gặp</a></li>
            </ul>
          </div>

          <div class="col-lg-4">
            <h6 class="footer-heading">Liên hệ</h6>
            <ul class="footer-contact">
              <li><i class="bi bi-geo-alt me-2"></i>123 Đường Thể Thao, TP.HCM</li>
              <li><i class="bi bi-telephone me-2"></i>1900 6688</li>
              <li><i class="bi bi-envelope me-2"></i>support@sportpro.vn</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-bottom py-3">
      <div class="container text-center">
        <small>&copy; {{ currentYear }} SportPro. All rights reserved.</small>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchDanhMuc } from '../services/catalogService'

const currentYear = new Date().getFullYear()
const danhMucList = ref([])

onMounted(async () => {
  danhMucList.value = await fetchDanhMuc()
})
</script>

<style scoped>
.sportpro-footer {
  margin-top: auto;
  background: #0f172a;
  color: rgba(255, 255, 255, 0.85);
}

.footer-brand {
  font-size: 1.5rem;
  font-weight: 800;
  text-transform: uppercase;
  color: #fff;
}

.text-accent {
  color: var(--sportpro-accent);
}

.footer-desc {
  font-size: 0.95rem;
  line-height: 1.6;
  opacity: 0.8;
  max-width: 320px;
}

.footer-heading {
  font-weight: 700;
  color: #fff;
  margin-bottom: 1rem;
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.footer-links,
.footer-contact {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li,
.footer-contact li {
  margin-bottom: 0.5rem;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: var(--sportpro-accent);
}

.footer-contact li {
  font-size: 0.9rem;
  opacity: 0.8;
}

.social-links a {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  text-decoration: none;
  transition: background 0.2s, color 0.2s;
}

.social-links a:hover {
  background: var(--sportpro-accent);
  color: #fff;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  opacity: 0.7;
}
</style>
