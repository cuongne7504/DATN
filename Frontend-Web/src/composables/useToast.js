import { ref } from 'vue'

const toasts = ref([])
let seed = 0

export function useToast() {
  const show = (message, type = 'success', options = {}) => {
    const {
      title,
      duration = 3200,
      actionLabel,
      onAction
    } = typeof options === 'number' ? { duration: options } : options

    const id = ++seed
    toasts.value.push({
      id,
      message,
      title,
      type,
      actionLabel,
      onAction
    })

    window.setTimeout(() => {
      toasts.value = toasts.value.filter((t) => t.id !== id)
    }, duration)
  }

  const success = (message, options) => show(message, 'success', options)
  const error = (message, options) => show(message, 'error', options)
  const info = (message, options) => show(message, 'info', options)
  const warning = (message, options) => show(message, 'warning', options)

  const remove = (id) => {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  return { toasts, show, success, error, info, warning, remove }
}
