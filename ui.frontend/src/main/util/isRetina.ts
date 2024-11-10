const retinaQuery =
  '(-webkit-min-device-pixel-ratio: 2), (min-device-pixel-ratio: 2), (min-resolution: 192dpi)';
export const isRetina = (): boolean => window.matchMedia(retinaQuery).matches;
