import { createApp } from 'vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import './style.css'
import App from './App.vue'
import router from './router'
import { vReveal } from './composables/useReveal'

const app = createApp(App)
app.directive('reveal', vReveal)
app.use(router)
app.mount('#app')

// Bootstrap JS chỉ cần cho collapse/navbar — tải sau để không chặn first paint
import('bootstrap/dist/js/bootstrap.bundle.min.js')
