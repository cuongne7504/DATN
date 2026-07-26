<template>
  <div class="sp-select" :class="{ open, disabled }" ref="root">
    <button
      type="button"
      class="sp-select-trigger"
      :disabled="disabled"
      @click="toggle"
    >
      <span class="sp-select-value" :class="{ placeholder: !selectedLabel }">
        {{ selectedLabel || placeholder }}
      </span>
      <i class="bi bi-chevron-down sp-select-caret"></i>
    </button>

    <Transition name="sp-select-menu">
      <ul v-if="open" class="sp-select-menu" role="listbox">
        <li
          v-for="opt in normalizedOptions"
          :key="String(opt.value)"
          class="sp-select-option"
          :class="{ active: String(opt.value) === String(modelValue) }"
          role="option"
          @click="choose(opt.value)"
        >
          {{ opt.label }}
          <i v-if="String(opt.value) === String(modelValue)" class="bi bi-check2"></i>
        </li>
      </ul>
    </Transition>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  options: { type: Array, default: () => [] },
  placeholder: { type: String, default: 'Chọn...' },
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'change'])

const open = ref(false)
const root = ref(null)

const normalizedOptions = computed(() =>
  props.options.map((opt) => {
    if (opt && typeof opt === 'object') {
      return { value: opt.value, label: opt.label }
    }
    return { value: opt, label: String(opt) }
  })
)

const selectedLabel = computed(() => {
  const found = normalizedOptions.value.find(
    (o) => String(o.value) === String(props.modelValue)
  )
  return found?.label || ''
})

const toggle = () => {
  if (props.disabled) return
  open.value = !open.value
}

const choose = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
  open.value = false
}

const onDocClick = (e) => {
  if (!root.value?.contains(e.target)) open.value = false
}

onMounted(() => document.addEventListener('click', onDocClick))
onUnmounted(() => document.removeEventListener('click', onDocClick))
</script>

<style scoped>
.sp-select {
  position: relative;
  width: 100%;
  z-index: 1;
}

.sp-select.open {
  z-index: 50;
}

.sp-select-trigger {
  width: 100%;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.7rem 1rem;
  border-radius: 12px;
  border: 1px solid #d5deea;
  background: #fff;
  color: #1e293b;
  font-family: inherit;
  font-size: 0.95rem;
  font-weight: 600;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.sp-select-trigger:hover {
  border-color: rgba(29, 78, 216, 0.35);
}

.sp-select.open .sp-select-trigger,
.sp-select-trigger:focus {
  outline: none;
  border-color: rgba(29, 78, 216, 0.55);
  box-shadow: 0 0 0 0.22rem rgba(29, 78, 216, 0.14);
}

.sp-select-value.placeholder {
  color: #94a3b8;
  font-weight: 550;
}

.sp-select-caret {
  color: #64748b;
  font-size: 0.85rem;
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}

.sp-select.open .sp-select-caret {
  transform: rotate(180deg);
  color: #1d4ed8;
}

.sp-select-menu {
  position: absolute;
  z-index: 60;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  margin: 0;
  padding: 0.4rem;
  list-style: none;
  background: #fff;
  border: 1px solid #e8ecf3;
  border-radius: 14px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.12);
  max-height: 260px;
  overflow-y: auto;
}

.sp-select-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  padding: 0.65rem 0.8rem;
  border-radius: 10px;
  color: #334155;
  font-weight: 600;
  font-size: 0.92rem;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.sp-select-option:hover {
  background: rgba(29, 78, 216, 0.07);
  color: #1d4ed8;
}

.sp-select-option.active {
  background: rgba(29, 78, 216, 0.1);
  color: #1d4ed8;
}

.sp-select-option i {
  font-size: 1rem;
}

.sp-select-menu-enter-active,
.sp-select-menu-leave-active {
  transition: opacity 0.18s ease, transform 0.18s cubic-bezier(0.22, 1, 0.36, 1);
}

.sp-select-menu-enter-from,
.sp-select-menu-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.sp-select.disabled .sp-select-trigger {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
