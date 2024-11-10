import { clearAllBodyScrollLocks, disableBodyScroll, enableBodyScroll } from 'body-scroll-lock';
import { useState, useRef, useEffect } from 'react';

export const useModal = <E extends HTMLElement>(initialState = false) => {
  const modalRef = useRef<E | null>(null);
  const [isOpen, setIsOpen] = useState(initialState);

  useEffect(() => {
    const target = modalRef.current || document.body;
    const toggleBodyScroll:any = isOpen ? disableBodyScroll : enableBodyScroll;

    toggleBodyScroll(target, {
      allowTouchMove: (element:any) => {
        while (element && element !== document.body) {
          if (element.getAttribute('body-scroll-lock-ignore') !== null) {
            return true;
          }

          element = element.parentElement as HTMLElement;
        }
      },
    });

    return () => {
      modalRef.current ? enableBodyScroll(modalRef.current) : clearAllBodyScrollLocks();
    };
  }, [isOpen]);

  const close = () => {
    setIsOpen(false);
    const genElm = document.getElementsByClassName('view-all-generic');
    if (genElm && genElm.length > 0) {
      genElm[0]?.classList?.remove('isOpenModal');
    }
  };

  const open = () => {
    setIsOpen(true);
    const genElm = document.getElementsByClassName('view-all-generic');
    if (genElm && genElm.length > 0) {
      genElm[0]?.classList?.add('isOpenModal');
    }
  };
  return { isOpen, close, open, setIsOpen, modalRef };
};
