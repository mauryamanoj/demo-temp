// TODO default value should be inverted
import { useEffect, useState } from 'react';
export const useIsMobile = (
  breakpoint = 768,
  type: 'max-width' | 'min-width' = 'max-width',
) => {
  const mediaQuery = window.matchMedia(`(${type}: ${breakpoint}px)`);
  const [isMobile, setIsMobile] = useState(mediaQuery.matches);
  useEffect(() => {
    mediaQuery.onchange = (event) => {
      setIsMobile(event.matches);
    };
  }, [isMobile, setIsMobile]);
  return isMobile;
};
