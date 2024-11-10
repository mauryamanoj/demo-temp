/* eslint-disable max-len */
import React, { useEffect, useRef, useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import isMobile from "src/main/util/isMobile";
import Text from "src/main/components/common/atoms/Text/Text";
import HighlightedCard from "./Components/HighlightedCard";

const HighlightedCardsComp: React.FC<any> = ({ cards, title, subTitle, link }) => {
  const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;
  const [margins, setMargins] = useState(true);
  const currentElement = useRef<HTMLDivElement | null>(null);

  function isAllowTouchMove() {
    if (isMobile(1024)) {
      return true;
    }
    if (!isMobile(1024) && cards && cards.length < 4) {
      return false;
    }
  }

  useEffect(() => {
    if (currentElement.current?.closest(".layout2Cols")) { setMargins(false); }
  }, []);

  const fixTitlesHeight = (swiper: any) => {
    // make sure all titles have the same height, since some of them might be 1 line and others 2 lines
    if (swiper && swiper.$el) {
      const hcLabelSelector = swiper?.$el[0]?.querySelectorAll(".hc-labelSelector");
      const hcTitleSelector = swiper?.$el[0]?.querySelectorAll(".hc-titleSelector");
      let maxLabelHeight = 0;
      let maxTitleHeight = 0;
      hcLabelSelector.forEach((element: HTMLElement) => {
        element.style.height = "auto";
        const elementHeight = element.offsetHeight;
        if (elementHeight >= maxLabelHeight) {
          maxLabelHeight = elementHeight;
        }
      });
      hcTitleSelector.forEach((element: HTMLElement) => {
        element.style.height = "auto";
        const elementHeight = element.offsetHeight;
        if (elementHeight >= maxTitleHeight) {
          maxTitleHeight = elementHeight;
        }
      });
      hcLabelSelector?.forEach((element: HTMLElement) => {
        element.style.height = `${maxLabelHeight}px`;
      });
      hcTitleSelector?.forEach((element: HTMLElement) => {
        element.style.height = `${maxTitleHeight}px`;
      });
    }
  };

  return (
    <div ref={currentElement}>
      <div className={`lg:flex justify-between items-center pb-6 md:pb-10 ${margins ? " mx-5 md:mx-100" : ""}`}>
        <div>
          {title ? <Text text={title} type="h1" styles="text-3xl md:text-5xl font-primary-bold leading-[37.14px] md:leading-[60px]" /> : null}
          {subTitle ? <Text text={subTitle} type="div" styles="text-lg md:text-1.5xl font-primary-semibold leading-[26.95px] mt-2 md:mt-4" /> : null}
        </div>
        {link && link.url && link.copy ? (
          <a
            href={link.url}
            title={link.title}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 hover:text-theme-200 font-primary-bold mt-2 md:mt-4 mb-[24px] lg:mb-0 inline-block"
          >
            {link.copy.slice(0, 30)}
          </a>
        ) : null}
      </div>
      <div className={`relative${margins ? " ltr:md:ml-100 rtl:md:mr-100" : ""}`}>
        <Swiper
          modules={[Navigation, Pagination, Scrollbar]}
          navigation={{
            nextEl: `.navigg-categoryCards-rights${arrowsSelectorRandNum}`,
            prevEl: `.navigg-categoryCards-lefts${arrowsSelectorRandNum}`,
            disabledClass: "hidden",
          }}
          threshold={30}
          slidesOffsetBefore={20}
          slidesOffsetAfter={20}
          spaceBetween={16}
          slidesPerView={1.4}
          allowTouchMove={isAllowTouchMove()}
          breakpoints={{
            768: {
              slidesPerView: "auto",
              spaceBetween: 36,
              slidesOffsetAfter: 80,
              threshold: 30,
              slidesOffsetBefore:0
            },
          }}
          onResize={(swiper) => {
            fixTitlesHeight(swiper);
          }}
          onSwiper={(swiper) => {
            fixTitlesHeight(swiper);
          }}
        >
          {cards?.map((card: any, index: any) => (
            <SwiperSlide key={index} className="w-full md:w-[393px]">
              <HighlightedCard {...card} />
            </SwiperSlide>
          ))}
        </Swiper>
        {cards && cards.length > 3 ? (
          <div className="hidden md:block">
            <SliderArrow
              icon="arrow-left"
              className={`navigg-categoryCards-lefts${arrowsSelectorRandNum} ltr:left-[66px] rtl:right-[66px] top-1/2 -translate-y-1/2`}
            />
            <SliderArrow
              icon="arrow-right"
              className={`navigg-categoryCards-rights${arrowsSelectorRandNum} ltr:right-[66px] rtl:left-[66px] top-1/2 -translate-y-1/2`}
            />
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default HighlightedCardsComp;
