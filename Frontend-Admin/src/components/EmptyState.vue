<template>
  <div class="empty-state">
    <div class="empty-icon">
      <i :class="icon"></i>
    </div>
    <h6>{{ title }}</h6>
    <p v-if="description">{{ description }}</p>
    <div v-if="$slots.default" class="mt-3">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: 'bi bi-inbox' },
  title: { type: String, default: 'Chưa có dữ liệu' },
  description: { type: String, default: '' }
})
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 2.75rem 1.5rem;
  border: 1.5px dashed #dbe3f0;
  border-radius: 16px;
  background: linear-gradient(180deg, #fbfdff 0%, #f8fafc 100%);
}

.empty-icon {
  width: 58px;
  height: 58px;
  margin: 0 auto 0.9rem;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: rgba(29, 78, 216, 0.08);
  color: var(--sp-blue);
  font-size: 1.45rem;
}

.empty-state h6 {
  margin: 0 0 0.35rem;
  font-weight: 720;
  color: #0f172a;
}

.empty-state p {
  margin: 0;
  color: #64748b;
  font-size: 0.9rem;
  max-width: 360px;
  margin-inline: auto;
}
</style>
