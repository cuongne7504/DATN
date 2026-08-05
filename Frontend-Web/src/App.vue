<template>
  <div class="web-shell">
    <Navbar v-if="!isAuthRoute" />
    <main class="web-main" :class="{ 'auth-main': isAuthRoute }">
      <router-view v-slot="{ Component, route: r }">
        <Transition name="page-fade" mode="out-in">
          <component :is="Component" :key="r.path" />
        </Transition>
      </router-view>
    </main>
    <Footer v-if="!isAuthRoute" />
    <ScrollToTop v-if="!isAuthRoute" />
    <ToastStack />
  </div>
</template>

<script setup>
import { computed, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import ScrollToTop from './components/ScrollToTop.vue'
import ToastStack from './components/ToastStack.vue'

const route = useRoute()
const isAuthRoute = computed(() => route.path === '/login' || route.path === '/register')

watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    window.scrollTo(0, 0)
  }
)
</script>

<style scoped>
.web-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.web-main {
  flex: 1;
}

.auth-main {
  min-height: 100vh;
}
</style>
