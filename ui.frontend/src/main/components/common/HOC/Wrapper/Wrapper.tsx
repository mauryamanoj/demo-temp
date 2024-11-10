import React, { Suspense, useRef } from "react";
import PulseAnimation from "../LoadingAnimation/PulseAnimation";
import useIsVisible from "../useIsVisible";
import { IWrapperProps } from "./IWrapper";

const Wrapper = ({ children, className, componentId }: IWrapperProps) => {

  const sectionRef = useRef<HTMLDivElement>(null);
  const isVisible = useIsVisible(
    sectionRef as { current: HTMLDivElement }
);
  return (
    <section ref={sectionRef} id={componentId} className={`relative mb-16 md:mb-20 ${className && className}`}>
      <Suspense fallback={<PulseAnimation/>}>
        {isVisible ? children : <></>}
        </Suspense>
    </section>
  );
};

export default Wrapper;
