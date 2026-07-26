const sharp = require('sharp')
const path = require('path')
const fs = require('fs')

const pub = path.join(__dirname, '..', 'public')

async function run() {
  await sharp(path.join(pub, 'banner.png'))
    .resize({ width: 1920, withoutEnlargement: true })
    .webp({ quality: 72 })
    .toFile(path.join(pub, 'banner.webp'))

  await sharp(path.join(pub, 'banner.png'))
    .resize({ width: 1600, withoutEnlargement: true })
    .jpeg({ quality: 78, mozjpeg: true })
    .toFile(path.join(pub, 'banner.jpg'))

  await sharp(path.join(pub, 'logo.png'))
    .resize(128, 128)
    .png({ compressionLevel: 9 })
    .toFile(path.join(pub, 'logo-sm.png'))

  await sharp(path.join(pub, 'logo.png'))
    .resize(64, 64)
    .png({ compressionLevel: 9 })
    .toFile(path.join(pub, 'favicon.png'))

  for (const f of ['banner.webp', 'banner.jpg', 'logo-sm.png', 'favicon.png', 'banner.png', 'logo.png']) {
    const s = fs.statSync(path.join(pub, f)).size
    console.log(f, Math.round(s / 1024) + 'KB')
  }
}

run().catch((e) => {
  console.error(e)
  process.exit(1)
})
