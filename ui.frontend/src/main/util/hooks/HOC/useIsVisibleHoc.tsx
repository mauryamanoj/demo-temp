import React from "react";
import { useState, useEffect, useRef } from "react";

const OPTIONS = {
  root: null,
  rootMargin: "0px 0px 0px 0px",
  threshold: 0,
};

const useIsVisibleHoc = (Component: any) => (props: any) => {
  const [isVisible, setIsVisible] = useState(false);
  const elementRef = useRef<HTMLElement>(null);

  useEffect(() => {
    if (elementRef.current) {
      const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setIsVisible(true);
            observer.unobserve(elementRef.current as HTMLElement);
          }
        });
      }, OPTIONS);
      observer.observe(elementRef.current);
    }
  }, [elementRef]);

  return (
    elementRef && <div ref={elementRef as {current: HTMLDivElement}}>
      {isVisible && <Component {...props}/>}
    </div>
  );
};

export default useIsVisibleHoc;
