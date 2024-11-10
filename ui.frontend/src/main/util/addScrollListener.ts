import throttle from 'lodash/throttle';

export const addScrollListener = (listener: () => void) => {
  const throttledListener = throttle(listener, 100);
  window.addEventListener('scroll', throttledListener);
  window.addEventListener('resize', throttledListener);

  return () => {
    window.removeEventListener('scroll', throttledListener);
    window.removeEventListener('resize', throttledListener);
  };
};
