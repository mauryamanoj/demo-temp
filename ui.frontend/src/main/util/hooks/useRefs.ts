import { createRef, RefObject, useEffect, useState } from 'react';
import times from 'lodash/times';

export const useRefs = <RefElement extends HTMLElement>(length: number) => {
  const [elementRefs, setElementRefs] = useState<Array<RefObject<RefElement>>>([]);

  useEffect(() => {
    const refs = times(length, (i) => elementRefs[i] || createRef());
    setElementRefs(refs);
  }, [length]);

  return elementRefs;
};
