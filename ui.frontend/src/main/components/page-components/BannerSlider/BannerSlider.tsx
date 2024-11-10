import React from "react";
import BannerSliderComp from "./BannerSliderComp";

const BannerSlider: React.FC<any> = (props) => {
  return (
    <section id={props.componentId} className="min-h-[650px] relative mb-16 md:mb-20 -mt-16 lg:-mt-20">
      <BannerSliderComp {...props} />
    </section>
  );
};

export default BannerSlider;
