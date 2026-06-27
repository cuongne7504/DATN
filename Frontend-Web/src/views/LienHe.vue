<template>
  <div class="contact-page">
    <div class="page-header">
      <div class="container">
        <h1>Liên hệ</h1>
        <p>Chúng tôi luôn sẵn sàng hỗ trợ bạn</p>
      </div>
    </div>

    <div class="container pb-5">
      <div class="row g-4">
        <div class="col-lg-5">
          <div class="contact-info card border-0 shadow-sm rounded-4 p-4 h-100">
            <h5 class="fw-bold mb-4">Thông tin liên hệ</h5>
            <div class="d-flex gap-3 mb-3">
              <i class="bi bi-geo-alt-fill text-accent fs-5"></i>
              <div>
                <strong>Địa chỉ</strong>
                <p class="text-muted mb-0">123 Đường Thể Thao, Quận 1, TP.HCM</p>
              </div>
            </div>
            <div class="d-flex gap-3 mb-3">
              <i class="bi bi-telephone-fill text-accent fs-5"></i>
              <div>
                <strong>Hotline</strong>
                <p class="text-muted mb-0">1900 6688 (8h - 22h)</p>
              </div>
            </div>
            <div class="d-flex gap-3">
              <i class="bi bi-envelope-fill text-accent fs-5"></i>
              <div>
                <strong>Email</strong>
                <p class="text-muted mb-0">support@sportpro.vn</p>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-7">
          <div class="card border-0 shadow-sm rounded-4 p-4">
            <h5 class="fw-bold mb-4">Gửi tin nhắn</h5>
            <form @submit.prevent>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">Họ tên</label>
                  <input type="text" class="form-control rounded-3" placeholder="Nguyễn Văn A" />
                </div>
                <div class="col-md-6">
                  <label class="form-label">Email</label>
                  <input type="email" class="form-control rounded-3" placeholder="email@example.com" />
                </div>
                <div class="col-12">
                  <label class="form-label">Nội dung</label>
                  <textarea class="form-control rounded-3" rows="4" placeholder="Nội dung tin nhắn..."></textarea>
                </div>
                <div class="col-12">
                  <button type="submit" class="btn btn-accent rounded-pill px-4">
                    Gửi tin nhắn
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-accent {
  color: var(--sportpro-accent);
}
</style>
