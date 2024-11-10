import { useEffect, useRef } from 'react';
import { disableBodyScroll, enableBodyScroll } from 'body-scroll-lock';

export const useScrollLock = <T extends Element>() => {
  const elementRef = useRef<T>(null);

  useEffect(() => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    disableBodyScroll(elementRef.current!);

    return () => {
      enableBodyScroll(elementRef.current || document.body);
    };
  }, []);

  return elementRef;
};
