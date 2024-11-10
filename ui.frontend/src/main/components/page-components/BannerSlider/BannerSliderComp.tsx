import React, { useEffect, useRef, useState } from "react";
import { Swiper, SwiperSlide, SwiperRef } from "swiper/react";
import Card from "./BannerSliderCard/MainSliderCard";
import Slider from "src/main/components/common/organisms/Slider/Slider";
import Tabs from "../../common/atoms/Tab/Tab";
import "./styles.css";
import OrnamentsBrushes from "../../common/atoms/Ornaments/OrnamentsBrushes";


const host = window.location.hostname == "localhost" ? 'https://qa-revamp.visitsaudi.com' : '';
const ornamentDesktop = `${host}/content/dam/sauditourism/ornaments/ornament-v-m3-24.png`;
const ornamentMobile = `${host}/content/dam/sauditourism/ornaments/ornament-h-m1-24.png`;


const BannerSliderComp: React.FC<any> = (props) => {
  const swiperRef = useRef<SwiperRef>(null);
  const { cards, hideImageBrush } = props;
  const hideImageBrushKey = eval(hideImageBrush);

  const [mobileView, setMobileView] = useState(window.innerWidth < 768 || window.innerWidth < 1024);
  const [selected, setSelected] = useState(0);
  const [pageLoaded, setPageLoaded] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 768 && window.innerWidth >= 1024) {
        setMobileView(false);
      } else {
        setMobileView(true);
      }
    };

    const handleSlider = () => setPageLoaded(true);

    handleResize();
    window.addEventListener("resize", handleResize);
    window.addEventListener("load", handleSlider);

    return () => {
      clearTimeout(1000);
      window.removeEventListener("resize", handleResize);
      window.removeEventListener("load", handleSlider);
    };
  }, []);

 

  useEffect(() => {
    swiperRef.current?.swiper.slideTo(selected, 1000);
    console.log("test",selected);
  }, [selected]);



  return (
    <>
      <div className="relative">
        {mobileView ? (
          <div className="banner-slider-comp">
            {pageLoaded ? (
              <Slider   autoplay={cards.length > 1 ? { delay: 6000 } : undefined}>
                {cards.map((item: any, index: any) => (
                  <SwiperSlide key={index}>
                    <Card {...item} />
                  </SwiperSlide>
                ))}
              </Slider>
            ) : (
              <Card {...cards[0]} />
            )}
          </div>
        ) : (
          <>
            {pageLoaded ? (
              <Swiper 
              ref={swiperRef}
              onSlideChange={(swiper) => setSelected(swiper.realIndex)}>
                {cards.map((item: any, index: any) => (
                  <SwiperSlide key={index}>
                    <Card {...item} />
                  </SwiperSlide>
                ))}
              </Swiper>
            ) : (
              <Card {...cards[0]} />
            )}
            {cards.length > 1 ? (
              <Tabs
                onClick={setSelected}
                tabTitles={cards.map((item: any) => item.title)}
                buttonTitle={cards.map((item: any) => item.tabTitle)}
                ctaData={cards.map((item: any) => item?.ctaData)}
                className="font-primary-bold bottom-0 pb-8 z-10"
                btnClassName="hover:opacity-100"
                selected={selected}
              />
            ) : (
              <></>
            )}
          </>
        )}
        <div className="absolute bottom-0 top-auto right-0 left-0 md:left-auto z-10 overflow-hidden">
          <img src={mobileView ? ornamentMobile : ornamentDesktop} className='max-w-none' />
          {/* <Icon name={mobileView ? "ornament-h-m3-24" : "ornament-v-m3-24"} svgClass="md:h-full md:w-6 w-full h-6" /> */}
        </div>
      </div>
      {!hideImageBrushKey && <OrnamentsBrushes />}
    </>
  );
};

export default BannerSliderComp;
