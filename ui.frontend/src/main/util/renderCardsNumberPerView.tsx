const calculateSlidesPerView = (selector: string, defaultVal: number) => {
  const container = document.querySelector( "."+selector
  ) as HTMLElement | null;
  const cardWidth = 384;
  if (container) {
      return Math.floor(container.offsetWidth / cardWidth);
  }

  return defaultVal;
};

export default calculateSlidesPerView;