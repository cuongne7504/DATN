<template>
  <section class="hero-banner">
    <div id="heroCarousel" class="carousel slide carousel-fade" data-bs-ride="carousel">
      <div class="carousel-indicators">
        <button
          v-for="(_, index) in slides"
          :key="index"
          type="button"
          data-bs-target="#heroCarousel"
          :data-bs-slide-to="index"
          :class="{ active: index === 0 }"
          :aria-current="index === 0 ? 'true' : undefined"
          :aria-label="`Slide ${index + 1}`"
        />
      </div>

      <div class="carousel-inner">
        <div
          v-for="(slide, index) in slides"
          :key="slide.title"
          class="carousel-item"
          :class="{ active: index === 0 }"
        >
          <div
            class="hero-slide"
            :style="{ backgroundImage: `url(${slide.image})` }"
          >
            <div class="hero-overlay">
              <div class="container">
                <div class="hero-content">
                  <span class="hero-badge">{{ slide.badge }}</span>
                  <h1 class="hero-title">{{ slide.title }}</h1>
                  <p class="hero-subtitle">{{ slide.subtitle }}</p>
                  <div class="d-flex flex-wrap gap-2 gap-md-3 hero-actions">
                    <RouterLink :to="slide.primaryLink" class="btn btn-accent btn-hero rounded-pill">
                      {{ slide.primaryCta }}
                    </RouterLink>
                    <RouterLink :to="slide.secondaryLink" class="btn btn-outline-light btn-hero rounded-pill">
                      {{ slide.secondaryCta }}
                    </RouterLink>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Trước</span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Sau</span>
      </button>
    </div>
  </section>
</template>

<script setup>
const slides = [
  {
    badge: 'Bộ sưu tập mới',
    title: 'World Cup Collection 2026',
    subtitle: 'Áo đấu chính hãng — Thiết kế đậm chất thể thao, chất liệu thoáng mát cho mọi trận đấu.',
    image: 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=1600&q=80',
    primaryCta: 'Mua ngay',
    primaryLink: '/shop',
    secondaryCta: 'Xem bộ sưu tập',
    secondaryLink: '/giam-gia',
  },
  {
    badge: 'Giày thể thao',
    title: 'Chạy Nhanh Hơn Mỗi Ngày',
    subtitle: 'Giày chạy bộ & training cao cấp từ Nike, Adidas, Puma — Giảm đến 30%.',
    image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1600&q=80',
    primaryCta: 'Khám phá giày',
    primaryLink: '/shop',
    secondaryCta: 'Ưu đãi hot',
    secondaryLink: '/giam-gia',
  },
  {
    badge: 'Phụ kiện thể thao',
    title: 'Trang Bị Toàn Diện Cho Vận Động Viên',
    subtitle: 'Bóng, túi gym, găng tay, bình nước — Mọi thứ bạn cần cho buổi tập.',
    image: 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=1600&q=80',
    primaryCta: 'Mua phụ kiện',
    primaryLink: '/shop',
    secondaryCta: 'Liên hệ tư vấn',
    secondaryLink: '/lien-he',
  },
]
</script>

<style scoped>
.hero-banner {
  margin-bottom: 0;
}

.hero-slide {
  height: 560px;
  background-size: cover;
  background-position: center;
}

.hero-overlay {
  height: 100%;
  display: flex;
  align-items: center;
  background: linear-gradient(
    105deg,
    rgba(15, 23, 42, 0.88) 0%,
    rgba(15, 23, 42, 0.55) 55%,
    rgba(15, 23, 42, 0.25) 100%
  );
}

.hero-content {
  max-width: 620px;
  color: #fff;
}

.hero-badge {
  display: inline-block;
  padding: 0.35rem 1rem;
  margin-bottom: 1rem;
  border-radius: 50rem;
  background: var(--sportpro-accent);
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.hero-title {
  font-size: clamp(2rem, 5vw, 3.25rem);
  font-weight: 800;
  line-height: 1.15;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: clamp(0.9rem, 2.5vw, 1.1rem);
  opacity: 0.92;
  margin-bottom: clamp(1rem, 3vw, 1.75rem);
  line-height: 1.6;
}

.btn-hero {
  padding: 0.6rem 1.15rem;
  font-size: 0.9rem;
  font-weight: 700;
}

.btn-accent {
  background: var(--sportpro-accent);
  border: none;
  color: #fff;
}

.btn-accent:hover {
  background: var(--sportpro-accent-dark);
  color: #fff;
}

@media (min-width: 768px) {
  .hero-slide {
    height: 520px;
  }

  .btn-hero {
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
  }
}

@media (min-width: 992px) {
  .hero-slide {
    height: 560px;
  }
}

@media (max-width: 767.98px) {
  .hero-slide {
    height: min(72vh, 420px);
  }

  .hero-badge {
    font-size: 0.75rem;
    padding: 0.3rem 0.75rem;
    margin-bottom: 0.75rem;
  }

  .carousel-control-prev,
  .carousel-control-next {
    width: 12%;
  }
}
</style>
