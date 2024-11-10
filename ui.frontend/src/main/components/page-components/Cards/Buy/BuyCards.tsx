/* eslint-disable max-len */
import React, { useEffect, useState, useRef } from "react";
import "./BuyCards.css";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import isMobile from "src/main/util/isMobile";
import Text from "src/main/components/common/atoms/Text/Text";
import MainCard from "../Main/Card/MainCard";
import getIsRTL from "src/main/util/getIsRTL";
import { BuyCardInetrface } from "./IBuyCards";
import { fetchLatestStoriesApi } from "./Services/service";
import { getLanguage } from "src/main/util/getLanguage";
import filterDataByUrl from "src/main/util/filterDataByUrl";

const BuyCards: React.FC<BuyCardInetrface> = (props) => {
  const { cards, title, link, fetchLatestStories, apiUrl, filter, numberOfStories = 3 } = props;
  const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;
  const [cardsData, setCardsData]: any = useState([]);
  const [isLayout2ColsExist, setIsLayout2ColsExist] = useState(false);

  const buyCardsRef = useRef<HTMLDivElement | null>(null);


  function isAllowTouchMove() {
    if (isMobile(1024)) {
      return true;
    }
    if (!isMobile(1024) && cards && cards.length < 4) {
      return false;
    }
  }

  useEffect(() => {
    if (cards && cards.length != 0) {
      setCardsData(cards);
    }

  }, [cards]);

  const objectToQueryString = (obj: any) => {
    const params = new URLSearchParams();

    for (const key in obj) {
      const value = obj[key];
      if (Array.isArray(value)) {
        value.forEach((item) => params.append(key, item));
      } else {
        params.append(key, value);
      }
    }

    return params.toString();
  };


  useEffect(() => {

    const filtersQueryString = objectToQueryString(filter);

    if (fetchLatestStories && apiUrl) {
      fetchLatestStoriesApi(apiUrl, getLanguage(), numberOfStories, filtersQueryString).then((data: any) => {
        if (data && data.length != 0) {
          setCardsData(filterDataByUrl(window.location.pathname,data.data));
        }
      });
    }
  }, [fetchLatestStories]);

  useEffect(() => {
    const checkLayout2Cols = () => {
      const currentElement = buyCardsRef.current;
      if (currentElement && currentElement.closest(".layout2Cols")) {
        setIsLayout2ColsExist(true);
      }
    };

    checkLayout2Cols();

  }, [buyCardsRef.current]);

  return (
    <section ref={buyCardsRef} className={`buy-blk relative ${!isLayout2ColsExist ? " px-5 md:px-100" : ""} slider-blk lg:pb-20  md:pb-10 pb-20`}>
      <div className="md:flex justify-between items-center mb-6 md:mb-10">
        {title ? (
          <Text
            text={title}
            type="h1"
            styles="text-3xl md:text-5xl font-primary-bold"
          />
        ) : null}

        {link && link.url && link.text ? (
          <a
            href={link.url}
            title={link.text}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 hover:text-theme-200 font-primary-bold mt-2 md:mt-0 inline-block ltr:md:pr-100 rtl:md:pl-100"
          >
            {link.text.slice(0, 30)}
          </a>
        ) : null}
      </div>
      <div className="relative mx-[-20px] md:mx-0 [&>div]:rtl:pr-5 [&>div]:ltr:pl-5 [&>div]:md:px-0">
        <Swiper
          modules={[Navigation, Pagination, Scrollbar]}
          navigation={{
            nextEl: `.navigg-categoryCards-rights${arrowsSelectorRandNum}`,
            prevEl: `.navigg-categoryCards-lefts${arrowsSelectorRandNum}`,
            disabledClass: "hidden",
          }}
          className={'pb-4'}
          threshold={30}
          slidesOffsetAfter={20}
          spaceBetween={16}
          slidesPerView={'auto'}
          allowTouchMove={isAllowTouchMove()}
          breakpoints={{
            768: {
              slidesPerView: 'auto',
              spaceBetween: 41,
              slidesOffsetAfter: 100,
              threshold: 30
            }
          }}
        >
          <div>
            {cardsData?.map((card: any, index: any) => (
              <SwiperSlide key={index}>

                <MainCard {...card} variant={"What-to-buy"} link={card?.cardCtaLink ? link : null} />

              </SwiperSlide>
            ))}
          </div>
        </Swiper>
        {cardsData && cardsData.length > 3 ? (
          <div className="arrows-blk md:block hidden">
            <SliderArrow
              icon="arrow-left"
              className={`navigg-categoryCards-lefts${arrowsSelectorRandNum} arrow-left ltr:left-[4.5%] ltr:xl:left-[4.5%]
                        ltr:2xl:left-[4.4%] rtl:right-[4.5%] rtl:xl:right-[4.5%] rtl:2xl:right-[4.4%] top-[171px]`}
            />
            <SliderArrow
              icon="arrow-right"
              className={`navigg-categoryCards-rights${arrowsSelectorRandNum} arrow-right ltr:right-[4.5%] ltr:xl:right-[4.5%]
                         ltr:2xl:right-[4.4%] rtl:left-[4.5%] rtl:xl:left-[4.5%] rtl:2xl:left-[4.4%] top-[171px]`}
            />
          </div>
        ) : null}
      </div>
    </section>
  );
};

export default BuyCards;
