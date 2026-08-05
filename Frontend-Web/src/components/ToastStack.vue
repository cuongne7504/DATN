<template>
  <div class="toast-stack" aria-live="polite" aria-atomic="true">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast-item"
        :class="`toast-${toast.type}`"
      >
        <div class="toast-icon" :class="`icon-${toast.type}`">
          <i :class="iconFor(toast.type)"></i>
        </div>
        <div class="toast-body">
          <strong v-if="toast.title">{{ toast.title }}</strong>
          <p>{{ toast.message }}</p>
          <button
            v-if="toast.actionLabel"
            type="button"
            class="toast-action"
            @click.stop="runAction(toast)"
          >
            {{ toast.actionLabel }}
          </button>
        </div>
        <button type="button" class="toast-close" aria-label="Đóng" @click="remove(toast.id)">
          <i class="bi bi-x"></i>
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { useToast } from '@/composables/useToast.js'

const { toasts, remove } = useToast()

const iconFor = (type) => {
  if (type === 'error') return 'bi bi-x-lg'
  if (type === 'info') return 'bi bi-info-lg'
  if (type === 'warning') return 'bi bi-exclamation-lg'
  return 'bi bi-check-lg'
}

const runAction = (toast) => {
  toast.onAction?.()
  remove(toast.id)
}
</script>

<style scoped>
.toast-stack {
  position: fixed;
  top: 1.1rem;
  right: 1.1rem;
  z-index: 2200;
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
  width: min(380px, calc(100vw - 1.5rem));
  pointer-events: none;
}

.toast-item {
  pointer-events: auto;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 0.75rem;
  align-items: start;
  padding: 0.95rem 1rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid #e8ecf3;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(10px);
}

.toast-icon {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 0.95rem;
  flex-shrink: 0;
}

.icon-success { background: linear-gradient(135deg, #059669, #10b981); }
.icon-error { background: linear-gradient(135deg, #dc2626, #ef4444); }
.icon-info { background: linear-gradient(135deg, #1d4ed8, #2563eb); }
.icon-warning { background: linear-gradient(135deg, #ea580c, #f97316); }

.toast-body {
  min-width: 0;
  padding-top: 0.1rem;
}

.toast-body strong {
  display: block;
  font-size: 0.95rem;
  font-weight: 750;
  color: #0b1220;
  letter-spacing: -0.01em;
  margin-bottom: 0.15rem;
}

.toast-body p {
  margin: 0;
  font-size: 0.88rem;
  color: #64748b;
  line-height: 1.45;
  font-weight: 550;
}

.toast-action {
  margin-top: 0.55rem;
  border: none;
  background: rgba(29, 78, 216, 0.08);
  color: #1d4ed8;
  font-weight: 700;
  font-size: 0.82rem;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}

.toast-action:hover {
  background: rgba(29, 78, 216, 0.14);
  transform: translateY(-1px);
}

.toast-close {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #94a3b8;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.toast-close:hover {
  background: #f1f5f9;
  color: #475569;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.32s cubic-bezier(0.22, 1, 0.36, 1);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(22px) scale(0.96);
}

.toast-move {
  transition: transform 0.28s ease;
}

@media (max-width: 576px) {
  .toast-stack {
    top: auto;
    bottom: 1rem;
    right: 0.75rem;
    left: 0.75rem;
    width: auto;
  }

  .toast-enter-from,
  .toast-leave-to {
    transform: translateY(16px) scale(0.98);
  }
}
</style>
