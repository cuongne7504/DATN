/**
 * v-reveal — fade/slide in when element enters viewport
 * Usage: v-reveal | v-reveal="'up'" | v-reveal="{ delay: 120, dir: 'left' }"
 */
export const vReveal = {
  mounted(el, binding) {
    if (binding.value === false) return

    const opts = typeof binding.value === 'string'
      ? { dir: binding.value }
      : (binding.value || {})

    const dir = opts.dir || opts.direction || 'up'
    const delay = opts.delay || 0
    const once = opts.once !== false

    el.classList.add('reveal', `reveal-${dir}`)
    if (delay) el.style.setProperty('--reveal-delay', `${delay}ms`)

    const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    if (reduce) {
      el.classList.add('is-visible')
      return
    }

    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            el.classList.add('is-visible')
            if (once) io.unobserve(el)
          } else if (!once) {
            el.classList.remove('is-visible')
          }
        })
      },
      { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
    )

    io.observe(el)
    el._revealIo = io
  },
  unmounted(el) {
    el._revealIo?.disconnect()
  }
}
