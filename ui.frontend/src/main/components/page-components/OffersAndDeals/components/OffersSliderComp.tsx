import React, { useEffect } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import SwiperCore, { Navigation, Pagination, Scrollbar } from "swiper";

import Text from "src/main/components/common/atoms/Text/Text";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import Button from "src/main/components/common/atoms/Button/Button";

import { OffersAndDealsProps } from "../IOffersAndDeals";
import OffersAndDealsCard from "./OffersAndDealsCards";
import OffersCard from "./OffersCard";
import { Type } from "../IOffersAndDeals";
import "../styles.css";

SwiperCore.use([Navigation, Pagination, Scrollbar]);

const OffersSliderComp: React.FC<OffersAndDealsProps> = ({
    type,
    title,
    cards,
    link,
}) => {
    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;

    useEffect(() => {
        const offerCardsInDom = document.querySelectorAll<HTMLElement>('.offersCardlowerWhiteBgSelector');

        // Find the maximum height of the offer cards
        let maxHeight = 0;
        offerCardsInDom.forEach(card => {
            if (card.offsetHeight > maxHeight) {
                maxHeight = card.offsetHeight;
            }
        });

        // Set the height of each offer card to the maximum height
        offerCardsInDom.forEach(card => {
            card.style.height = `${maxHeight}px`;
        });

    }, [])
    return (
        <>
            <div className=" bg-transparent pl-5 pr-0 rtl:pl-0 rtl:pr-5 lg:pl-[100px] lg:pr-0 rtl:lg:pl-0 rtl:lg:pr-[100px] overflow-hidden">
                {(title || (link?.text && link?.url)) && (
                    <div className=" md:flex mb-6 md:mb-10 rtl:pl-5 ltr:pr-5 ltr:lg:pr-[100px] rtl:lg:pl-[100px]">
                        <Text
                            text={title}
                            styles={`font-primary-bold text-3xl md:text-5xl text-left rtl:text-right capitalize text-left text-black break-word mb-2 md:mb-0 md:w-9/12 ${title ? "block" : "invisible"
                                }`}
                        />
                        {link?.text && link?.url && (
                            <Button
                                title={link.text}
                                styles={`!px-0 !py-0 mx-0 border-none md:w-3/12 md:flex md:justify-end`}
                                spanStyle="font-primary-bold text-sm themed !px-0 truncate w-full md:w-auto"
                                transparent
                                arrows={false}
                                onclick={() => {
                                    window.open(
                                        link?.url,
                                        !!link?.targetInNewWindow
                                            ? "_blank"
                                            : "_self"
                                    );
                                }}
                            />
                        )}
                    </div>
                )}

                {cards?.length > 0 && (
                    <div>
                        {
                            <div className="offersAndDealsSlider swiper-container-2 relative">
                                <Swiper
                                    modules={[
                                        Navigation,
                                        Pagination,
                                        Scrollbar,
                                    ]}
                                    navigation={{
                                        nextEl: `.navigg-categoryCards-right${arrowsSelectorRandNum}`,
                                        prevEl: `.navigg-categoryCards-left${arrowsSelectorRandNum}`,
                                        disabledClass: "hidden",
                                    }}
                                    slidesOffsetAfter={20}
                                    slidesPerView={1.3}
                                    spaceBetween={20}
                                    threshold={30}
                                    breakpoints={{
                                        768: {
                                            slidesPerView: "auto",
                                            spaceBetween: 36,
                                            slidesOffsetAfter: 100,
                                            slidesPerGroup: 1,
                                            slidesPerGroupSkip: 1,
                                        },
                                    }}
                                >
                                    <div>
                                        {cards?.map((card: any, index: any) => (
                                            <SwiperSlide key={index}>
                                                {type == Type.Deal ? <OffersAndDealsCard {...card} /> : <OffersCard {...card} />}
                                            </SwiperSlide>
                                        ))}
                                    </div>
                                </Swiper>
                                {cards.length > 3 && (
                                    <div className="hidden lg:block">
                                        <SliderArrow
                                            icon="arrow-left"
                                            className={`navigg-categoryCards-left${arrowsSelectorRandNum} left-0 rtl:-right-[22px] rtl:left-[auto] lg:top-[50%]`}
                                        />
                                        <SliderArrow
                                            icon="arrow-right"
                                            className={`navigg-categoryCards-right${arrowsSelectorRandNum} right-[68px] rtl:left-[78px] rtl:right-[auto] lg:top-[50%]`}
                                        />
                                    </div>
                                )}
                            </div>
                        }
                    </div>
                )}
            </div>
        </>
    );
};

export default OffersSliderComp;

