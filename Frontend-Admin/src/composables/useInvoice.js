import html2pdf from 'html2pdf.js'

export function formatInvoicePrice(price) {
  const value = Number(price) || 0
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

export function formatInvoiceDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

export function invoiceNumber(maDonHang) {
  const id = Number(maDonHang) || 0
  return `HD-${String(id).padStart(6, '0')}`
}

export function resolveInvoicePaperElement(templateComponent) {
  if (!templateComponent) return null
  if (typeof templateComponent.getPaperElement === 'function') {
    return templateComponent.getPaperElement()
  }
  const exposed = templateComponent.paperRef
  if (!exposed) return null
  if (exposed instanceof HTMLElement) return exposed
  return exposed.value || null
}

export function isPosOrder(order) {
  if (!order?.diaChiGiao) return true
  const value = order.diaChiGiao.toLowerCase()
  return value.includes('tại quầy') || value.includes('tai quay') || value.includes('khách mua tại quầy')
}

export function parseCustomerInfo(order) {
  if (!order?.diaChiGiao || isPosOrder(order)) {
    const raw = order?.diaChiGiao || ''
    if (raw.toLowerCase().includes('tại quầy')) {
      return { name: 'Khách mua tại quầy', phone: '', address: 'Bán tại quầy' }
    }
    const parts = raw.split(' - ')
    return {
      name: parts[0] || 'Khách mua tại quầy',
      phone: parts[1] || '',
      address: 'Bán tại quầy'
    }
  }
  const parts = order.diaChiGiao.split(' - ')
  return {
    name: parts[0] || 'Khách hàng',
    phone: parts[1] || '',
    address: order.diaChiGiao
  }
}

export function calcInvoiceSubtotal(order) {
  const items = order?.chiTietList || []
  return items.reduce((sum, item) => sum + (Number(item.soLuong) || 0) * (Number(item.donGia) || 0), 0)
}

export function calcInvoiceDiscount(order) {
  const subtotal = calcInvoiceSubtotal(order)
  const total = Number(order?.tongTien) || 0
  const ship = Number(order?.phiShip) || 0
  const discount = subtotal + ship - total
  return discount > 0 ? discount : 0
}

export function isInvoiceable(order) {
  if (!order) return false
  const status = String(order.trangThai || '').trim().toLowerCase()
  if (status.includes('đã hủy') || status.includes('da huy')) return false
  return true
}

export function getInvoicePrintStyles() {
  return `
    * { box-sizing: border-box; }
    body { margin: 0; padding: 16px; font-family: 'Segoe UI', Arial, sans-serif; color: #0f172a; background: #fff; }
    .invoice-paper { max-width: 720px; margin: 0 auto; }
    .invoice-header { display: flex; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
    .invoice-brand { display: flex; gap: 12px; align-items: center; }
    .invoice-brand img { width: 52px; height: 52px; object-fit: contain; }
    .invoice-brand h1 { margin: 0; font-size: 22px; }
    .invoice-brand p { margin: 4px 0 0; color: #64748b; font-size: 13px; }
    .invoice-meta { text-align: right; font-size: 13px; color: #475569; }
    .invoice-meta strong { display: block; color: #0f172a; font-size: 18px; margin-bottom: 6px; }
    .invoice-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px; }
    .invoice-box { border: 1px solid #e2e8f0; border-radius: 10px; padding: 12px; background: #f8fafc; }
    .invoice-box h3 { margin: 0 0 8px; font-size: 13px; text-transform: uppercase; color: #64748b; letter-spacing: 0.04em; }
    .invoice-box p { margin: 0 0 4px; font-size: 13px; }
    table { width: 100%; border-collapse: collapse; margin: 12px 0; font-size: 13px; }
    th, td { border: 1px solid #e2e8f0; padding: 8px; text-align: left; }
    th { background: #f1f5f9; }
    .text-end { text-align: right; }
    .text-center { text-align: center; }
    .invoice-totals { margin-left: auto; width: min(320px, 100%); }
    .invoice-totals .row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 13px; }
    .invoice-totals .grand { border-top: 2px solid #0f172a; margin-top: 6px; padding-top: 10px; font-size: 16px; font-weight: 700; color: #ea580c; }
    .invoice-footer { margin-top: 18px; padding-top: 12px; border-top: 1px dashed #cbd5e1; text-align: center; color: #64748b; font-size: 12px; }
    .badge-type { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; }
    .badge-pos { background: #fef3c7; color: #92400e; }
    .badge-online { background: #dbeafe; color: #1d4ed8; }
  `
}

export function printInvoiceElement(element) {
  if (!element) return
  const win = window.open('', '_blank', 'width=900,height=700')
  if (!win) return

  win.document.write(`<!DOCTYPE html><html><head><title>Hóa đơn</title><style>${getInvoicePrintStyles()}</style></head><body>`)
  win.document.write(element.outerHTML)
  win.document.write('</body></html>')
  win.document.close()
  win.focus()
  setTimeout(() => {
    win.print()
    win.close()
  }, 300)
}

export async function exportInvoicePdf(element, maDonHang) {
  if (!element) return
  const filename = `${invoiceNumber(maDonHang)}.pdf`
  const opt = {
    margin: [8, 8, 8, 8],
    filename,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2, useCORS: true, logging: false },
    jsPDF: { unit: 'mm', format: 'a5', orientation: 'portrait' }
  }
  await html2pdf().set(opt).from(element).save()
}
