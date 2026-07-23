import { ref } from 'vue'

const toasts = ref([])
let seed = 0

export function useToast() {
  const show = (message, type = 'success', duration = 2800) => {
    const id = ++seed
    toasts.value.push({ id, message, type })
    window.setTimeout(() => {
      toasts.value = toasts.value.filter((t) => t.id !== id)
    }, duration)
  }

  const success = (message) => show(message, 'success')
  const error = (message) => show(message, 'error')
  const info = (message) => show(message, 'info')

  const remove = (id) => {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  return { toasts, show, success, error, info, remove }
}
