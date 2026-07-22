<template>
  <div class="page-header">
    <div class="page-header-text">
      <h2>{{ title }}</h2>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>
    <div v-if="$slots.actions" class="page-header-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' }
})
</script>

<style scoped>
.page-header {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.35rem;
}

.page-header-text h2 {
  margin: 0 0 0.25rem;
  font-size: 1.45rem;
  font-weight: 780;
  letter-spacing: -0.02em;
  color: #0f172a;
}

.page-header-text p {
  margin: 0;
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 500;
}

.page-header-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.55rem;
}
</style>
