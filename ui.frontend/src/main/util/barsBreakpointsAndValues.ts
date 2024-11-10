import { useEffect, useState } from 'react';
import { useResize } from './hooks/useResize';

export type Breakpoint = '375' | '768' | '1280' | '1920';

interface BreakpointValue {
  scale: number;
  thickness: number;
}

export const valuesBasedOnWidth: Record<Breakpoint, BreakpointValue> = {
  375: { scale: 0.6, thickness: 4.8 },
  768: { scale: 0.5, thickness: 4 },
  1280: { scale: 0.7, thickness: 5.6 },
  1920: { scale: 1, thickness: 8 },
};

export const getCurrentBreakpoint = (): Breakpoint => {
  const breakpoints = Object.keys(valuesBasedOnWidth) as Array<Breakpoint>;
  const currentBreakpoint = breakpoints.find((breakpoint, index) => {
    return (
      window.innerWidth < parseInt(breakpoint) ||
      (window.innerWidth >= parseInt(breakpoint) &&
        window.innerWidth < parseInt(breakpoints[index + 1]))
    );
  });
  return currentBreakpoint || breakpoints[breakpoints.length - 1];
};

export const useCurrentBreakpoint = () => {
  const [currentBreakpoint, setCurrentBreakpoint] = useState<Breakpoint>(getCurrentBreakpoint());
  const { widthSize } = useResize();

  useEffect(() => {
    setCurrentBreakpoint(getCurrentBreakpoint());
  }, [widthSize]);

  return currentBreakpoint;
};
