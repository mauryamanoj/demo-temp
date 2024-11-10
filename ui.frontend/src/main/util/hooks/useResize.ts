import { useLayoutEffect, useMemo, useState } from 'react';

const viewportBreakpoint = 768;

export const useResize = (mobileBreakpoint?: number) => {
  const [widthSize, setWidthSize] = useState(window.innerWidth);
  const [heightSize, setHeightSize] = useState(window.innerHeight);

  const updateSize = () => {
    setWidthSize(window.innerWidth);
    setHeightSize(window.innerHeight);
  };

  const isMobile = useMemo(() => widthSize < (mobileBreakpoint ?? viewportBreakpoint), [widthSize]);

  const isDesktop = useMemo(() => widthSize >= (mobileBreakpoint ?? viewportBreakpoint), [widthSize]);

  useLayoutEffect(() => {
    window.addEventListener('resize', updateSize);
    updateSize();
    return () => window.removeEventListener('resize', updateSize);
  }, []);

  return { isMobile, isDesktop, widthSize, heightSize };
};
