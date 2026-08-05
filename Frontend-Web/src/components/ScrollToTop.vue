<template>
  <Transition name="float-fade">
    <button
      v-if="visible"
      type="button"
      class="scroll-top-btn"
      aria-label="Lên đầu trang"
      @click="scrollTop"
    >
      <i class="bi bi-arrow-up"></i>
    </button>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const visible = ref(false)

const onScroll = () => {
  visible.value = window.scrollY > 420
}

const scrollTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  onScroll()
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
.scroll-top-btn {
  position: fixed;
  right: 1.25rem;
  bottom: 1.4rem;
  z-index: 1040;
  width: 46px;
  height: 46px;
  border: none;
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 1.15rem;
  cursor: pointer;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  box-shadow: 0 12px 28px rgba(29, 78, 216, 0.35);
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1), box-shadow 0.25s ease;
}

.scroll-top-btn:hover {
  transform: translateY(-3px) scale(1.04);
  box-shadow: 0 16px 34px rgba(29, 78, 216, 0.42);
}

.float-fade-enter-active,
.float-fade-leave-active {
  transition: opacity 0.28s ease, transform 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.float-fade-enter-from,
.float-fade-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.92);
}
</style>
