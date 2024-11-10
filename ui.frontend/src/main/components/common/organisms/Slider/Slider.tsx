import React,{ useRef } from "react";
import { Swiper } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";

import "./SliderIndicator.css";

import { useState } from "react";
import SwiperCore, { Autoplay } from "swiper";
import { AutoplayOptions, PaginationOptions, Swiper as SwiperType, SwiperOptions } from "swiper/types";
import SliderArrow from "../../atoms/SliderArrow/SliderArrow";

SwiperCore.use([Autoplay]);

interface SliderProps {
  showArrows?: boolean;
  showMiddelArrow?: any;
  children: any[];
  slidesPerView?: number;
  spaceBetween?: number;
  pagination?: boolean | PaginationOptions;
  loop?:boolean;
  autoplay?: boolean | AutoplayOptions;
  breakpoints?: {
    [width: number]: SwiperOptions;
    [ratio: string]: SwiperOptions;
  };
  slidesOffsetAfter?: number;
  paginationClass?:string
}

const Slider = ({
  showArrows,
  children,
  slidesPerView = 1,
  spaceBetween,
  pagination = { clickable: true ,
  renderBullet(index, className) {
    return `<span class="${className}">
    ${autoplay ?`<span class="innerBullet"
    style="transition:
    width
    ${typeof autoplay === "object" && autoplay.delay ? autoplay.delay/1000+0.3 : 4}s
    ease-in-out;"
    ></span>`: ""}
    </span>`;
  }},
  loop = true,
  autoplay,
  breakpoints,
  slidesOffsetAfter,
  showMiddelArrow,
  paginationClass
}: SliderProps) => {
  const [swiper, setSwiper]:any = useState<SwiperType>();
  const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;
  const [activeIndex, setActiveIndex]:any = useState<SwiperType>();
  const [sliderLength, setSliderLength]:any = useState<SwiperType>();

  const swiperRef:any = useRef(null);


  return children && children.length > 0 ? (
    <div className="relative">
      <Swiper
        ref={swiperRef}
        onPaginationRender={(swiper, el) => {
          paginationClass?  el.classList.add(paginationClass):null;
        }}
        modules={[Navigation, Pagination, Scrollbar]}
        navigation={{
          nextEl: `.navigg-categoryCards-rights${arrowsSelectorRandNum}`,
          prevEl: `.navigg-categoryCards-lefts${arrowsSelectorRandNum}`,
          disabledClass: "hidden",
        }}
        pagination={pagination}
        onSlideChange={(index)=>{
          if(swiperRef && swiperRef.current && swiperRef.current.swiper){
            setSliderLength(swiperRef.current.swiper.slides.length);
          }
            setActiveIndex(index.activeIndex);
        }}
        spaceBetween={spaceBetween}
        slidesPerView={slidesPerView}
        loop={loop}
        threshold={30}
        autoplay={autoplay}
        onSwiper={(swiper) => {
          setSwiper(swiper);
        }}
        breakpoints={breakpoints}
        slidesOffsetAfter={slidesOffsetAfter}
        className={autoplay ? "autoplay" : undefined}
      >
        {children}
      </Swiper>
      {showArrows ? (
        <>
          <div className={`${showMiddelArrow ? 'relative lg:bottom-[86px] bottom-[80px] mx-[6.3rem] lg:block hidden':''}`}>
          <SliderArrow
              icon="arrow-left"
              className={`navigg-categoryCards-lefts${arrowsSelectorRandNum} ltr:left-0 rtl:right-0 ${activeIndex==1 || activeIndex == sliderLength-1?'':'hidden'}`}
            />
            <SliderArrow
              icon="arrow-right"
              className={`navigg-categoryCards-rights${arrowsSelectorRandNum} ltr:right-0 rtl:left-0 ${activeIndex == sliderLength-1?'hidden':''}`}
            />
          </div>
        </>
      ) : (
        <></>
      )}
    </div>
  ) : (
    <></>
  );
};

export default Slider;
