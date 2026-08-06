// Bộ sinh mã vạch SVG Code128 chuẩn cho biến thể và sản phẩm
export function generateBarcodeSVG(code, options = {}) {
  const { width = 2, height = 60, fontSize = 14 } = options;
  const PATTERNS = [
    "212222", "222122", "222221", "121223", "121322", "131222", "122213", "122312", "132212", "221213",
    "221312", "231212", "112232", "122132", "122231", "113222", "123122", "123221", "223211", "221132",
    "221231", "213212", "223112", "312131", "311222", "321122", "321221", "312212", "322112", "322211",
    "212123", "212321", "232121", "111323", "131123", "131321", "112313", "132113", "132311", "211313",
    "231113", "231311", "112133", "112331", "132131", "113123", "113321", "133121", "313121", "211331",
    "231131", "213113", "213311", "213131", "311123", "311321", "331121", "312113", "312311", "332111",
    "314111", "221411", "431111", "111224", "111422", "121124", "121421", "141122", "141221", "112214",
    "112412", "122114", "122411", "142112", "142211", "241211", "221114", "413111", "241112", "134111",
    "111242", "121142", "121241", "114212", "124112", "124211", "411212", "421112", "421211", "212141",
    "214121", "412121", "111143", "111341", "131141", "114113", "114311", "411113", "411311", "113141",
    "114131", "311141", "411131", "211412", "211214", "211232", "2331112"
  ];

  const START_B = 104;
  const STOP = 106;

  let str = String(code || '000000').trim();
  if (!str) str = '000000';

  let checksum = START_B;
  const encodedPatterns = [PATTERNS[START_B]];

  for (let i = 0; i < str.length; i++) {
    const codePoint = str.charCodeAt(i);
    let val = codePoint - 32;
    if (val < 0 || val > 95) val = 31; // fallback '?'
    checksum += val * (i + 1);
    encodedPatterns.push(PATTERNS[val]);
  }

  checksum %= 103;
  encodedPatterns.push(PATTERNS[checksum]);
  encodedPatterns.push(PATTERNS[STOP]);

  const patternStr = encodedPatterns.join('');
  let totalWidth = 0;
  for (let c of patternStr) totalWidth += parseInt(c, 10);

  const svgWidth = totalWidth * width + 40;
  const svgHeight = height + fontSize + 25;

  let x = 20;
  let rects = '';
  let isBar = true;

  for (let char of patternStr) {
    const barW = parseInt(char, 10) * width;
    if (isBar) {
      rects += `<rect x="${x}" y="10" width="${barW}" height="${height}" fill="#000000"/>`;
    }
    x += barW;
    isBar = !isBar;
  }

  const textX = svgWidth / 2;
  const textY = height + fontSize + 15;

  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${svgWidth} ${svgHeight}" width="${svgWidth}" height="${svgHeight}">
    <rect width="100%" height="100%" fill="#ffffff"/>
    ${rects}
    <text x="${textX}" y="${textY}" font-family="monospace" font-size="${fontSize}" font-weight="bold" text-anchor="middle" fill="#000000">${str}</text>
  </svg>`;
}
