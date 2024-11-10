import { useEffect } from 'react';

export const useOnMount = (onMount: () => void) => {
  useEffect(() => {
    onMount();
    // TODO: seems like react-hooks/exhaustive-deps is not enabled
    // // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};
