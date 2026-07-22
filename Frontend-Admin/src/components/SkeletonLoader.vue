<template>
  <div class="skeleton-wrap" :class="`variant-${variant}`">
    <div v-if="variant === 'kpi'" class="row g-3">
      <div v-for="n in rows" :key="n" class="col-md-3 col-sm-6">
        <div class="sk-card">
          <div class="sk-line w-40"></div>
          <div class="sk-line w-70 mt-3 h-lg"></div>
        </div>
      </div>
    </div>

    <div v-else-if="variant === 'table'" class="sk-table">
      <div class="sk-line w-100 h-md mb-3"></div>
      <div v-for="n in rows" :key="n" class="sk-row">
        <div class="sk-line w-15"></div>
        <div class="sk-line w-35"></div>
        <div class="sk-line w-20"></div>
        <div class="sk-line w-15"></div>
        <div class="sk-line w-10"></div>
      </div>
    </div>

    <div v-else-if="variant === 'chart'" class="sk-chart">
      <div class="sk-line w-40 mb-4"></div>
      <div class="sk-bars">
        <div v-for="n in 8" :key="n" class="sk-bar" :style="{ height: `${35 + (n % 5) * 12}%` }"></div>
      </div>
    </div>

    <div v-else class="sk-block">
      <div v-for="n in rows" :key="n" class="sk-line" :class="n % 2 === 0 ? 'w-90' : 'w-70'"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'block' }, // block | kpi | table | chart
  rows: { type: Number, default: 4 }
})
</script>

<style scoped>
.sk-card,
.sk-table,
.sk-chart,
.sk-block {
  background: #fff;
  border: 1px solid var(--sp-border);
  border-radius: var(--sp-radius);
  padding: 1.15rem;
  box-shadow: var(--sp-shadow-sm);
}

.sk-line,
.sk-bar {
  background: linear-gradient(90deg, #eef2f7 25%, #f8fafc 37%, #eef2f7 63%);
  background-size: 400% 100%;
  animation: shimmer 1.2s ease-in-out infinite;
  border-radius: 8px;
  height: 12px;
}

.sk-line.h-md { height: 16px; }
.sk-line.h-lg { height: 28px; }
.sk-line.mt-3 { margin-top: 0.85rem; }
.sk-line.mb-3 { margin-bottom: 0.85rem; }
.sk-line.mb-4 { margin-bottom: 1.1rem; }

.w-10 { width: 10%; }
.w-15 { width: 15%; }
.w-20 { width: 20%; }
.w-35 { width: 35%; }
.w-40 { width: 40%; }
.w-70 { width: 70%; }
.w-90 { width: 90%; }
.w-100 { width: 100%; }

.sk-row {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 0.85rem;
}

.sk-block .sk-line {
  margin-bottom: 0.75rem;
}

.sk-bars {
  display: flex;
  align-items: flex-end;
  gap: 0.65rem;
  height: 220px;
}

.sk-bar {
  flex: 1;
  border-radius: 10px 10px 4px 4px;
}

@keyframes shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: 0 0; }
}
</style>
