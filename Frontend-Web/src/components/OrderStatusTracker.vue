<template>
  <div class="status-tracker" :class="{ cancelled: isCancelled }">
    <div v-if="isCancelled" class="cancelled-banner">
      <i class="bi bi-x-circle me-2"></i>
      Đơn hàng đã bị hủy
    </div>

    <div v-else class="tracker-steps">
      <div
        v-for="(step, index) in TRACKING_STEPS"
        :key="step.key"
        class="tracker-step"
        :class="{
          done: index < activeIndex,
          active: index === activeIndex,
          pending: index > activeIndex,
        }"
      >
        <div class="step-marker">
          <i v-if="index < activeIndex" class="bi bi-check-lg"></i>
          <i v-else :class="step.icon"></i>
        </div>
        <span class="step-label">{{ step.label }}</span>
        <div v-if="index < TRACKING_STEPS.length - 1" class="step-line"></div>
      </div>
    </div>

    <div v-if="timeline.length" class="timeline mt-4">
      <h6 class="timeline-title">Chi tiết theo dõi</h6>
      <ul class="timeline-list">
        <li
          v-for="(event, idx) in timelineSorted"
          :key="idx"
          class="timeline-item"
          :class="{ latest: idx === 0 }"
        >
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-status">{{ getStatusConfig(event.trang_thai).label }}</span>
            <p class="timeline-desc mb-0">{{ event.mo_ta }}</p>
            <time class="timeline-time">{{ formatOrderDate(event.thoi_gian) }}</time>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  TRACKING_STEPS,
  getStatusConfig,
  getActiveStepIndex,
  formatOrderDate,
} from '../utils/orderHelpers'

const props = defineProps({
  trang_thai: { type: String, required: true },
  lich_su_trang_thai: { type: Array, default: () => [] },
})

const isCancelled = computed(() => props.trang_thai === 'DaHuy')
const activeIndex = computed(() => getActiveStepIndex(props.trang_thai))
const timeline = computed(() => props.lich_su_trang_thai ?? [])

const timelineSorted = computed(() =>
  [...timeline.value].sort(
    (a, b) => new Date(b.thoi_gian) - new Date(a.thoi_gian)
  )
)
</script>

<style scoped>
.tracker-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
  gap: 0.25rem;
}

.tracker-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  text-align: center;
}

.step-marker {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  background: #f1f5f9;
  color: #94a3b8;
  border: 2px solid #e2e8f0;
  position: relative;
  z-index: 1;
  transition: all 0.25s;
}

.tracker-step.done .step-marker {
  background: #16a34a;
  border-color: #16a34a;
  color: #fff;
}

.tracker-step.active .step-marker {
  background: var(--sportpro-accent);
  border-color: var(--sportpro-accent);
  color: #fff;
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.25);
}

.step-label {
  margin-top: 0.5rem;
  font-size: 0.72rem;
  font-weight: 600;
  color: #94a3b8;
}

.tracker-step.done .step-label,
.tracker-step.active .step-label {
  color: #334155;
}

.step-line {
  position: absolute;
  top: 20px;
  left: calc(50% + 20px);
  width: calc(100% - 40px);
  height: 2px;
  background: #e2e8f0;
  z-index: 0;
}

.tracker-step.done .step-line {
  background: #16a34a;
}

.cancelled-banner {
  padding: 0.85rem 1rem;
  border-radius: 10px;
  background: #fef2f2;
  color: #dc2626;
  font-weight: 600;
  font-size: 0.9rem;
}

.timeline-title {
  font-weight: 700;
  font-size: 0.85rem;
  color: #334155;
  margin-bottom: 0.85rem;
}

.timeline-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.timeline-item {
  display: flex;
  gap: 0.85rem;
  padding-bottom: 1rem;
  position: relative;
}

.timeline-item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 18px;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
}

.timeline-item.latest .timeline-dot {
  background: var(--sportpro-accent);
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.2);
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #cbd5e1;
  flex-shrink: 0;
  margin-top: 3px;
}

.timeline-status {
  font-weight: 700;
  font-size: 0.85rem;
  color: #0f172a;
}

.timeline-desc {
  font-size: 0.82rem;
  color: #64748b;
  margin-top: 0.15rem;
}

.timeline-time {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 0.2rem;
  display: block;
}

@media (max-width: 575.98px) {
  .step-label {
    font-size: 0.62rem;
  }

  .step-marker {
    width: 34px;
    height: 34px;
    font-size: 0.85rem;
  }

  .step-line {
    top: 17px;
    left: calc(50% + 17px);
    width: calc(100% - 34px);
  }
}
</style>
