/* eslint-disable max-len */
import React from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import isMobile from "src/main/util/isMobile";
import Text from "src/main/components/common/atoms/Text/Text";
import AppListCard from "./Components/AppListCard";

const AppListCards: React.FC<any> = ({ cards, title, link ,variation}) => {
  const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;

  function isAllowTouchMove() {
    if (isMobile(768)) {
      return true;
    }
    if (!isMobile(768) && cards && cards.length < 4) {
      return false;
    }
  }
  return (
    <section className="relative pb-20">
      <div className="md:flex justify-between items-center md:mb-[40px] mb-0 px-5 md:px-100">
        {title ? (
          <Text
            text={title}
            type="h1"
            styles="text-3xl md:text-5xl
        font-primary-bold"
          />
        ) : null}
        {link && link.url && link.text ? (
          <a
            href={link.url}
            title={link.text}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 hover:text-theme-200 font-primary-bold mb-[24px] md:mb-0 inline-block"
          >
            {link.text.slice(0,30)}
          </a>
        ) : null}
      </div>
      <div className="pl-5 md:pl-100 rtl:pr-5 rtl:pl-0 rtl:md:pr-100 rtl:md:pl-0 relative">
        <Swiper
          modules={[Navigation, Pagination, Scrollbar]}
          navigation={{
            nextEl: `.navigg-categoryCards-rights${arrowsSelectorRandNum}`,
            prevEl: `.navigg-categoryCards-lefts${arrowsSelectorRandNum}`,
            disabledClass: "hidden",
          }}
          threshold={30}
          slidesOffsetAfter={20}
          spaceBetween={16}
          slidesPerView="auto"
          breakpoints={{
            768: {
              spaceBetween: 36,
          }
          }}
          allowTouchMove={isAllowTouchMove()}
        >
          {cards?.map((card: any, index: any) => (
            <SwiperSlide key={index} className={variation === "big" ? "w-[283px]" : "w-[261px]"}>
              <AppListCard {...card} variation={variation}/>
            </SwiperSlide>
          ))}
        </Swiper>
        {cards.length && document.body.clientWidth < (variation === "big" ? 283*(cards.length+1) : 261*(cards.length+1)) ? (
          <div className="hidden md:block">
            <SliderArrow
              icon="arrow-left"
              className={`navigg-categoryCards-lefts${arrowsSelectorRandNum} left-16 rtl:right-16 rtl:left-[auto]
              ${variation === "big" ? "md:top-[117px]" : "md:top-[106.5px]"}`}
            />
            <SliderArrow
              icon="arrow-right"
              className={`navigg-categoryCards-rights${arrowsSelectorRandNum} right-16 rtl:left-16 rtl:right-[auto]
              ${variation === "big" ? "md:top-[117px]" : "md:top-[106.5px]"}`}
            />
          </div>
        ) : null}
      </div>
    </section>
  );
};

export default AppListCards;
