<template>
  <img
    :src="currentSrc"
    :alt="alt"
    :class="imgClass"
    :loading="loading"
    decoding="async"
    @error="onError"
  />
</template>

<script setup>
import { ref, watch } from 'vue'
import {
  resolveImageUrl,
  getDemoImageUrl,
  PLACEHOLDER_IMAGE,
} from '../utils/productHelpers'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  imgClass: { type: String, default: '' },
  loading: { type: String, default: 'lazy' },
})

const currentSrc = ref(resolveImageUrl(props.src))
let fallbackStep = 0

watch(
  () => props.src,
  (value) => {
    fallbackStep = 0
    currentSrc.value = resolveImageUrl(value)
  }
)

function onError() {
  if (fallbackStep === 0) {
    const demo = getDemoImageUrl(props.src)
    if (demo && currentSrc.value !== demo) {
      fallbackStep = 1
      currentSrc.value = demo
      return
    }
  }
  if (fallbackStep < 2 && currentSrc.value !== PLACEHOLDER_IMAGE) {
    fallbackStep = 2
    currentSrc.value = PLACEHOLDER_IMAGE
  }
}
</script>
