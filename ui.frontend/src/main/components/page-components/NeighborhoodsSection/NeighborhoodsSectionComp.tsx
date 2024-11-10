import React from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import SwiperCore, { Navigation, Pagination, Scrollbar } from "swiper";

import SliderArrow from "../../common/atoms/SliderArrow/SliderArrow";
import Text from "../../common/atoms/Text/Text";
import Button from "../../common/atoms/Button/Button";
import { NeighborhoodsProps } from "./INeighborhoodsProps";
import NeighborhoodCard from "./NeighborhoodCard";
import '../OffersAndDeals/styles.css'
import calculateSlidesPerView from "src/main/util/renderCardsNumberPerView";
const NeighborhoodsSectionComp: React.FC<NeighborhoodsProps> = ({
    title,
    link,
    cards,
}) => {
    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;

    return (
        <div className="md:px-[100px] bg-transparent">
            <div className=" md:flex px-5 md:px-0 md:mb-10 mb-6 ">
                {title && (
                    <Text
                        text={title}
                        styles="font-primary-bold text-3xl md:text-5xl capitalize md:mb-0 mb-2 truncate md:w-6/12"
                    />
                )}
                {link?.copy && link?.url && (
                    <Button
                        title={link.copy}
                        styles={`!px-0 !py-0 mx-0 border-none  md:w-6/12 md:flex md:justify-end`}
                        spanStyle="font-primary-bold text-sm themed !px-0 truncate w-full md:w-auto"
                        transparent
                        arrows={false}
                        onclick={ () => {
                                      window.open(link?.url, !!link?.targetInNewWindow ? "_blank" : "_self");
                                  }
                        }
                    />
                )}
            </div>
            {cards?.length > 0 && (
                <div className="ltr:pl-5 rtl:pr-5 md:pl-0 neighborhood-swiper-container">
                    {
                        <div className="offersAndDealsSlider swiper-container-2 relative">
                            <Swiper
                                modules={[Navigation, Pagination, Scrollbar]}
                                navigation={{
                                    nextEl: `.navigg-categoryCards-right${arrowsSelectorRandNum}`,
                                    prevEl: `.navigg-categoryCards-left${arrowsSelectorRandNum}`,
                                    disabledClass: "hidden",
                                }}
                                slidesOffsetAfter={20}
                                slidesPerView={1.3}
                                spaceBetween={20}
                                threshold={30}
                                slidesPerGroup={1}
                                breakpoints={{
                                    768: {
                                        slidesPerView:'auto',
                                        spaceBetween: 36,
                                        slidesOffsetAfter: 80,
                                    },
                                }}
                            >
                                <div>
                                    {cards?.map((card: any, index: any) => (
                                        <SwiperSlide key={index}>
                                           <NeighborhoodCard {...card}/>
                                        </SwiperSlide>
                                    ))}
                                </div>
                            </Swiper>
                            {cards.length > calculateSlidesPerView("neighborhood-swiper-container", 3) && (
                                <div className="hidden md:block">
                                    <SliderArrow
                                        icon="arrow-left"
                                        className={`navigg-categoryCards-left${arrowsSelectorRandNum} -left-[22px] rtl:-right-[22px] rtl:left-[auto] lg:top-[50%]`}
                                    />
                                    <SliderArrow
                                        icon="arrow-right"
                                        className={`navigg-categoryCards-right${arrowsSelectorRandNum} -right-[22px] rtl:-left-[22px] rtl:right-[auto] lg:top-[50%]`}
                                    />
                                </div>
                            )}
                        </div>
                    }
                </div>
            )}
        </div>
    );
};

export default NeighborhoodsSectionComp;

