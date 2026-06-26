<template>
  <section class="benefits-bar py-4">
    <div class="container">
      <div class="row g-3">
        <div v-for="item in items" :key="item.title" class="col-12 col-sm-6 col-lg-3">
          <div class="benefit-item d-flex align-items-center gap-3">
            <div class="benefit-icon">
              <i :class="['bi', item.icon]"></i>
            </div>
            <div>
              <h6 class="benefit-title mb-0">{{ item.title }}</h6>
              <small class="text-muted">{{ item.desc }}</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const items = [
  { icon: 'bi-truck', title: 'Giao hàng nhanh', desc: 'Toàn quốc 2-5 ngày' },
  { icon: 'bi-shield-check', title: 'Hàng chính hãng', desc: '100% authentic' },
  { icon: 'bi-arrow-repeat', title: 'Đổi trả 30 ngày', desc: 'Miễn phí đổi size' },
  { icon: 'bi-headset', title: 'Hỗ trợ 24/7', desc: 'Hotline & chat' },
]
</script>

<style scoped>
.benefits-bar {
  background: #fff;
  border-block: 1px solid #e2e8f0;
}

.benefit-icon {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  border-radius: 12px;
  background: rgba(249, 115, 22, 0.12);
  color: var(--sportpro-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}

.benefit-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 0.95rem;
}

@media (max-width: 575.98px) {
  .benefit-item {
    padding: 0.35rem 0;
  }

  .benefit-icon {
    width: 40px;
    height: 40px;
    font-size: 1.1rem;
  }

  .benefit-title {
    font-size: 0.88rem;
  }
}
</style>
