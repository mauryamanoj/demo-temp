import { useEffect, useRef } from 'react';

export const useIsMounted = () => {
  const componentIsMounted = useRef(true);

  useEffect(() => {
    return () => {
      componentIsMounted.current = false;
    };
  }, []);

  return componentIsMounted;
};
