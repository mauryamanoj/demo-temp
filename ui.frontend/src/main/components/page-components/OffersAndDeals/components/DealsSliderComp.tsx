import React from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import SwiperCore, { Navigation, Pagination, Scrollbar } from "swiper";

import { useResize } from "src/main/util/hooks/useResize";

import Text from "src/main/components/common/atoms/Text/Text";
import OffersAndDealsCard from "./OffersAndDealsCards";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import "../styles.css";
import Button from "src/main/components/common/atoms/Button/Button";
import { OffersAndDealsProps } from "../IOffersAndDeals";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import { trackEvents } from "src/main/util/updateAnalyticsData";

SwiperCore.use([Navigation, Pagination, Scrollbar]);

const DealsSliderComp: React.FC<OffersAndDealsProps> = ({
    title,
    link,
    cards,
    ctaData,
    isWithOrnament
}) => {
    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;
    const { isMobile } = useResize();
    var cardInfo: any = {};

    const setGtmCustomLink = (link: any) => {
        cardInfo = {
            event: ctaData?.ctaEventName,
            section_name: ctaData?.sectionName,
            click_text: link?.text,
            link: link?.url,
            language: getLanguage(),
            page_category: ctaData?.pageCategory,
            page_subcategory: ctaData?.pageSubCategory,
            device_type: window.navigator.userAgent,
        };
        if (ctaData?.ctaEventName) {
            gtmCustomEventClick(cardInfo);
        }
    };

    const setAdobeCustomLink = (link: any) => {
        const dataSet: any = {};
        dataSet.trackEventname = ctaData?.ctaEventName;
        dataSet.trackSection = ctaData?.sectionName;
        dataSet.trackName = link?.text;
        dataSet.trackEventname && trackEvents(dataSet);
    };

    function handleButtonClick(link: any) {
        setGtmCustomLink(link);
        setAdobeCustomLink(link);

        setTimeout(() => {
            window.open(
                link?.url,
                !!link?.targetInNewWindow ? "_blank" : "_self"
            );
        }, 400);
    }

    return (
        <>
            <div className="lg:flex px-5 lg:px-[100px] bg-theme-100 pt-14 pb-[68px] lg:py-20 relative">
                {!!isWithOrnament && <div className="absolute ltr:left-0 rtl:right-0 bottom-0 w-full lg:w-[55%] ">
                    <Icon
                        name={!!isMobile ? "ornament-mixt-expressive-125-resp" : "ornament-mixt-expressive-300"}
                        svgClass="w-full md:w-auto h-[125px] md:h-auto rtl:transform rtl:scale-x-[-1]"
                    />
                </div>}
                <div className="lg:w-4/12 ltr:md:mr-9 rtl:md:ml-9 mb-4 lg:mb-0 leading-[60px]">
                    {title && (
                        <Text
                            text={title}
                            styles={`font-primary-bold text-3xl md:text-5xl capitalize text-left rtl:text-right
                            text-white mb-4 md:mb-8 break-word w-full ${isMobile ? "truncate" : ""
                                }`}
                        />
                    )}
                    {link?.text && link?.url && (
                        <a className="rounded-lg px-4 md:px-6 py-3 md:py-4 font-primary-semibold text-xs md:text-base capitalize
                            text-white leading-normal border" href={link?.url} target={link.targetInNewWindow ? '_blank' : '_self'}>{link.text}</a>
                    )}
                </div>
                <div className="lg:w-8/12 relative -mx-5 md:mx-0">
                    {cards?.length > 0 && (
                        <div className="offersAndDealsSlider swiper-container-1">
                            <Swiper
                                modules={[Navigation, Pagination, Scrollbar]}
                                navigation={{
                                    nextEl: `.navigg-categoryCards-right${arrowsSelectorRandNum}`,
                                    prevEl: `.navigg-categoryCards-left${arrowsSelectorRandNum}`,
                                    disabledClass: "hidden",
                                }}
                                slidesPerView={1.3}
                                spaceBetween={20}
                                threshold={30}
                                slidesOffsetAfter={20}
                                slidesOffsetBefore={20}
                                breakpoints={{
                                    768: {
                                        slidesPerView: "auto",
                                        spaceBetween: 36,
                                        slidesOffsetAfter: 0,
                                        slidesOffsetBefore: 0,
                                    },
                                }}
                            >
                                <>
                                    {cards?.map((card: any, index: any) => (
                                        <SwiperSlide key={index}>
                                            <OffersAndDealsCard {...card} />
                                        </SwiperSlide>
                                    ))}
                                </>
                            </Swiper>
                            {cards.length > 2 && (
                                <div className="hidden lg:block">
                                    <SliderArrow
                                        icon="arrow-left"
                                        className={`navigg-categoryCards-left${arrowsSelectorRandNum} -left-[22px]
                                        rtl:-right-[22px] rtl:left-[auto] lg:top-[50%]`}
                                    />
                                    <SliderArrow
                                        icon="arrow-right"
                                        className={`navigg-categoryCards-right${arrowsSelectorRandNum} -right-[15px]
                                        rtl:-left-[22px] rtl:right-[auto] lg:top-[50%]`}
                                    />
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </>
    );
};

export default DealsSliderComp;

