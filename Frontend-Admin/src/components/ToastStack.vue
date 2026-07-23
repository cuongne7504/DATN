<template>
  <div class="toast-stack" aria-live="polite">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast-item"
        :class="`toast-${toast.type}`"
        @click="remove(toast.id)"
      >
        <i :class="iconFor(toast.type)"></i>
        <span>{{ toast.message }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { useToast } from '@/composables/useToast.js'

const { toasts, remove } = useToast()

const iconFor = (type) => {
  if (type === 'error') return 'bi bi-x-circle-fill'
  if (type === 'info') return 'bi bi-info-circle-fill'
  return 'bi bi-check-circle-fill'
}
</script>

<style scoped>
.toast-stack {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
  width: min(360px, calc(100vw - 2rem));
  pointer-events: none;
}

.toast-item {
  pointer-events: auto;
  display: flex;
  align-items: flex-start;
  gap: 0.65rem;
  padding: 0.85rem 1rem;
  border-radius: 14px;
  background: #fff;
  border: 1px solid var(--sp-border);
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.14);
  font-weight: 600;
  color: #0f172a;
  cursor: pointer;
}

.toast-item i {
  font-size: 1.1rem;
  margin-top: 0.1rem;
}

.toast-success i { color: #059669; }
.toast-error i { color: #dc2626; }
.toast-info i { color: var(--sp-blue); }

.toast-enter-active,
.toast-leave-active {
  transition: all 0.28s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(18px);
}
</style>
