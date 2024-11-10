import { useState, useEffect } from "react";

const OPTIONS = {
  root: null,
  rootMargin: "0px 0px 0px 0px",
  threshold: 0,
};

const useIsVisible = (elementRef: { current: HTMLElement }) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (elementRef.current) {

      if(sessionStorage.getItem(elementRef.current.id)){
        setIsVisible(true);
        return;
      }

      const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setIsVisible(true);
            if(elementRef.current.id) sessionStorage.setItem(elementRef.current.id, 'true');
            observer?.unobserve(elementRef.current);
          }
        });
      }, OPTIONS);
      observer.observe(elementRef.current);


    }
  }, [elementRef]);

  return isVisible;
};

export default useIsVisible;
