<template>
  <Teleport to="body">
    <Transition name="confirm-fade">
      <div
        v-if="open"
        class="confirm-overlay"
        @click.self="onCancel"
      >
        <div class="confirm-dialog" role="dialog" aria-modal="true" :aria-labelledby="titleId">
          <div class="confirm-icon" :class="`tone-${tone}`">
            <i :class="iconClass"></i>
          </div>
          <h3 :id="titleId" class="confirm-title">{{ title }}</h3>
          <p class="confirm-message">{{ message }}</p>
          <p v-if="detail" class="confirm-detail">{{ detail }}</p>
          <div class="confirm-actions">
            <button type="button" class="btn btn-ghost confirm-cancel" @click="onCancel">
              {{ cancelText }}
            </button>
            <button
              type="button"
              class="btn confirm-ok"
              :class="tone === 'danger' ? 'btn-danger' : 'btn-primary'"
              @click="onConfirm"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Xác nhận' },
  message: { type: String, default: '' },
  detail: { type: String, default: '' },
  confirmText: { type: String, default: 'Xác nhận' },
  cancelText: { type: String, default: 'Hủy' },
  tone: { type: String, default: 'danger' } // danger | primary
})

const emit = defineEmits(['confirm', 'cancel', 'update:open'])

const titleId = `confirm-title-${Math.random().toString(36).slice(2, 8)}`

const iconClass = computed(() =>
  props.tone === 'danger' ? 'bi bi-trash3' : 'bi bi-question-lg'
)

const onCancel = () => {
  emit('update:open', false)
  emit('cancel')
}

const onConfirm = () => {
  emit('update:open', false)
  emit('confirm')
}
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  z-index: 2300;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(15, 23, 42, 0.48);
  backdrop-filter: blur(6px);
}

.confirm-dialog {
  width: min(420px, 100%);
  background: #fff;
  border: 1px solid #e8ecf3;
  border-radius: 20px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.22);
  padding: 1.6rem 1.5rem 1.35rem;
  text-align: center;
  animation: dialogIn 0.32s cubic-bezier(0.22, 1, 0.36, 1);
}

.confirm-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 1rem;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 1.35rem;
  color: #fff;
}

.tone-danger {
  background: linear-gradient(135deg, #dc2626, #ef4444);
  box-shadow: 0 12px 24px rgba(220, 38, 38, 0.28);
}

.tone-primary {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  box-shadow: 0 12px 24px rgba(29, 78, 216, 0.28);
}

.confirm-title {
  margin: 0 0 0.45rem;
  font-size: 1.2rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: #0b1220;
}

.confirm-message {
  margin: 0;
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.55;
}

.confirm-detail {
  margin: 0.65rem 0 0;
  padding: 0.55rem 0.75rem;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e8ecf3;
  color: #334155;
  font-size: 0.88rem;
  font-weight: 650;
}

.confirm-actions {
  display: flex;
  gap: 0.65rem;
  margin-top: 1.35rem;
}

.confirm-actions .btn {
  flex: 1;
  min-height: 44px;
  border-radius: 12px !important;
  font-weight: 700;
}

.confirm-cancel {
  background: #f8fafc !important;
  border: 1px solid #e2e8f0 !important;
  color: #475569 !important;
}

.confirm-cancel:hover {
  background: #f1f5f9 !important;
  color: #0f172a !important;
  transform: none;
}

.confirm-fade-enter-active,
.confirm-fade-leave-active {
  transition: opacity 0.22s ease;
}

.confirm-fade-enter-from,
.confirm-fade-leave-to {
  opacity: 0;
}

@keyframes dialogIn {
  from {
    opacity: 0;
    transform: translateY(12px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
