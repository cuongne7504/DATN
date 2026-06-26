<template>
  <div class="not-found-page d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="text-center px-3">
      <h1 class="display-1 fw-bold text-muted">404</h1>
      <h2 class="fw-bold mb-3">Không tìm thấy trang</h2>
      <p class="text-muted mb-4">
        Trang bạn đang tìm kiếm không tồn tại hoặc đã bị di chuyển.
      </p>
      <RouterLink to="/" class="btn btn-accent rounded-pill px-4">
        <i class="bi bi-house me-1"></i> Về trang chủ
      </RouterLink>
    </div>
  </div>
</template>
