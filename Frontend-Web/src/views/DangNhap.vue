<template>
  <div class="login-page">
    <div class="page-header">
      <div class="container">
        <h1>Đăng nhập</h1>
        <p>Chào mừng bạn quay lại SportPro</p>
      </div>
    </div>

    <div class="container pb-5">
      <div class="row justify-content-center">
        <div class="col-md-5">
          <div class="card border-0 shadow-sm rounded-4 p-4">
            <form @submit.prevent>
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control rounded-3" placeholder="email@example.com" required />
              </div>
              <div class="mb-3">
                <label class="form-label">Mật khẩu</label>
                <input type="password" class="form-control rounded-3" placeholder="••••••••" required />
              </div>
              <div class="d-flex justify-content-between align-items-center mb-4">
                <div class="form-check">
                  <input id="remember" type="checkbox" class="form-check-input" />
                  <label for="remember" class="form-check-label">Ghi nhớ đăng nhập</label>
                </div>
                <a href="#" class="small text-accent">Quên mật khẩu?</a>
              </div>
              <button type="submit" class="btn btn-accent w-100 rounded-pill py-2 fw-bold">
                Đăng nhập
              </button>
            </form>
            <p class="text-center text-muted mt-4 mb-0 small">
              Chưa có tài khoản?
              <a href="#" class="text-accent fw-semibold">Đăng ký ngay</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-accent {
  color: var(--sportpro-accent);
  text-decoration: none;
}
</style>
