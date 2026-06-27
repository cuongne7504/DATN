<template>
  <div class="order-history-page">
    <div class="page-header">
      <div class="container">
        <h1>Lịch sử đơn hàng</h1>
        <p>Theo dõi và quản lý đơn hàng của bạn tại SportPro</p>
      </div>
    </div>

    <div class="container pb-5">
      <div class="row g-4">
        <div class="col-lg-3">
          <AccountSidebar />
        </div>

        <div class="col-lg-9">
          <div v-if="loading" class="text-center py-5 text-muted">
            <div class="spinner-border text-secondary mb-3" role="status"></div>
            <p class="mb-0">Đang tải đơn hàng...</p>
          </div>

          <template v-else>
            <!-- Bộ lọc trạng thái -->
            <div class="filter-tabs mb-4">
              <button
                v-for="tab in filterTabs"
                :key="tab.value"
                type="button"
                class="filter-tab"
                :class="{ active: activeFilter === tab.value }"
                @click="activeFilter = tab.value"
              >
                {{ tab.label }}
                <span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
              </button>
            </div>

            <div v-if="filteredOrders.length" class="order-list">
              <article
                v-for="order in filteredOrders"
                :key="order.ma_don_hang"
                class="order-card rounded-4"
              >
                <div class="order-card-header">
                  <div>
                    <span class="order-id">{{ formatOrderId(order.ma_don_hang) }}</span>
                    <time class="order-date">{{ formatOrderDate(order.ngay_dat) }}</time>
                  </div>
                  <span
                    class="status-badge"
                    :style="{
                      color: getStatusConfig(order.trang_thai).color,
                      background: getStatusConfig(order.trang_thai).bg,
                    }"
                  >
                    <i :class="getStatusConfig(order.trang_thai).icon"></i>
                    {{ getStatusConfig(order.trang_thai).label }}
                  </span>
                </div>

                <div class="order-items-preview">
                  <div
                    v-for="item in order.chi_tiet"
                    :key="item.ma_ct_don_hang"
                    class="preview-item"
                  >
                    <AppImage
                      :src="item.duong_dan_anh"
                      :alt="item.ten_san_pham"
                      img-class="preview-thumb"
                    />
                    <div class="preview-info">
                      <p class="preview-name mb-0">{{ item.ten_san_pham }}</p>
                      <small class="text-muted">
                        {{ item.mau_sac }} / {{ item.kich_co }} × {{ item.so_luong }}
                      </small>
                    </div>
                  </div>
                </div>

                <div class="order-card-footer">
                  <div class="order-total">
                    <span class="text-muted small">Tổng thanh toán</span>
                    <strong class="total-amount">
                      {{ formatPrice(order.tong_tien + order.phi_ship) }}
                    </strong>
                  </div>
                  <div class="order-actions">
                    <RouterLink
                      v-if="order.trang_thai === 'DangGiao'"
                      :to="{ name: 'order-detail', params: { id: order.ma_don_hang } }"
                      class="btn btn-accent btn-sm rounded-pill px-3"
                    >
                      <i class="bi bi-geo-alt me-1"></i> Theo dõi
                    </RouterLink>
                    <RouterLink
                      :to="{ name: 'order-detail', params: { id: order.ma_don_hang } }"
                      class="btn btn-outline-dark btn-sm rounded-pill px-3"
                    >
                      Chi tiết
                    </RouterLink>
                  </div>
                </div>
              </article>
            </div>

            <div v-else class="empty-state text-center py-5 rounded-4">
              <i class="bi bi-inbox display-4 text-muted"></i>
              <h5 class="mt-3">Không có đơn hàng</h5>
              <p class="text-muted mb-0">
                {{ activeFilter === 'all' ? 'Bạn chưa có đơn hàng nào.' : 'Không có đơn ở trạng thái này.' }}
              </p>
              <RouterLink to="/shop" class="btn btn-accent rounded-pill px-4 mt-3">
                Mua sắm ngay
              </RouterLink>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { fetchDonHangList } from '../services/orderService'
import { formatPrice } from '../utils/productHelpers'
import {
  getStatusConfig,
  formatOrderDate,
  formatOrderId,
} from '../utils/orderHelpers'
import AccountSidebar from '../components/AccountSidebar.vue'
import AppImage from '../components/AppImage.vue'

const loading = ref(true)
const orders = ref([])
const activeFilter = ref('all')

const FILTER_OPTIONS = [
  { value: 'all', label: 'Tất cả', statuses: null },
  { value: 'processing', label: 'Đang xử lý', statuses: ['ChoXuLy', 'DangChuanBi'] },
  { value: 'shipping', label: 'Đang giao', statuses: ['DangGiao'] },
  { value: 'done', label: 'Hoàn thành', statuses: ['HoanThanh'] },
  { value: 'cancelled', label: 'Đã hủy', statuses: ['DaHuy'] },
]

const filterTabs = computed(() =>
  FILTER_OPTIONS.map((opt) => ({
    ...opt,
    count:
      opt.value === 'all'
        ? orders.value.length
        : orders.value.filter((o) => opt.statuses.includes(o.trang_thai)).length,
  }))
)

const filteredOrders = computed(() => {
  const opt = FILTER_OPTIONS.find((f) => f.value === activeFilter.value)
  if (!opt?.statuses) return orders.value
  return orders.value.filter((o) => opt.statuses.includes(o.trang_thai))
})

onMounted(async () => {
  orders.value = await fetchDonHangList()
  loading.value = false
})
</script>

<style scoped>
.filter-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.5rem;
}

.filter-tab {
  border: none;
  background: transparent;
  padding: 0.45rem 0.85rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
}

.filter-tab:hover {
  background: #f8fafc;
  color: #334155;
}

.filter-tab.active {
  background: var(--sportpro-accent);
  color: #fff;
}

.tab-count {
  font-size: 0.72rem;
  padding: 0.1rem 0.4rem;
  border-radius: 50rem;
  background: rgba(0, 0, 0, 0.12);
}

.filter-tab.active .tab-count {
  background: rgba(255, 255, 255, 0.25);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.order-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem 1.25rem;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  flex-wrap: wrap;
}

.order-id {
  font-weight: 800;
  color: #0f172a;
  display: block;
  font-size: 0.95rem;
}

.order-date {
  font-size: 0.8rem;
  color: #94a3b8;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.3rem 0.75rem;
  border-radius: 50rem;
  font-size: 0.78rem;
  font-weight: 700;
  white-space: nowrap;
}

.order-items-preview {
  padding: 1rem 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.preview-item {
  display: flex;
  align-items: center;
  gap: 0.85rem;
}

.preview-thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.preview-name {
  font-weight: 600;
  font-size: 0.88rem;
  color: #334155;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.order-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.85rem 1.25rem;
  border-top: 1px solid #f1f5f9;
  flex-wrap: wrap;
}

.total-amount {
  display: block;
  font-size: 1.1rem;
  color: #dc2626;
}

.order-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
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

.empty-state {
  background: #fff;
  border: 1px solid #e2e8f0;
}

@media (max-width: 575.98px) {
  .filter-tabs {
    flex-wrap: nowrap;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
  }

  .filter-tabs::-webkit-scrollbar {
    display: none;
  }

  .filter-tab {
    flex-shrink: 0;
    font-size: 0.8rem;
    padding: 0.4rem 0.7rem;
  }

  .order-card-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .order-actions {
    width: 100%;
  }

  .order-actions .btn {
    flex: 1;
  }
}
</style>
