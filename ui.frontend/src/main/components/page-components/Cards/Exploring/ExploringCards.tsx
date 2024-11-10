/* eslint-disable max-len */
import React from "react";
import "./Exploring.css";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import isMobile from "src/main/util/isMobile";
import Text from "src/main/components/common/atoms/Text/Text";
import FeaturedDestinationCard from "../Destination/FeaturedDestinationCard";
import { ExploringInterface } from "./IExploring";
import Button from "src/main/components/common/atoms/Button/Button";

const ExploringCards: React.FC<ExploringInterface> = (props) => {
    const { cards, title, link } = props;
    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;

    function isAllowTouchMove() {
        if (isMobile(1024)) {
            return true;
        }
        if (!isMobile(1024) && cards && cards.length < 4) {
            return false;
        }
    }
    return (
        <section className="exploring relative exploring-blk slider-blk py-16 lg:py-20 bg-white ">
            <div className="md:flex mb-4 md:mb-10 justify-between items-center mx-5 md:mx-100 false">
                {title ? (
                    <Text
                        text={title}
                        type="h1"
                        styles="font-primary-bold text-3xl md:text-5xl capitalize md:mb-0 mb-2 truncate md:w-6/12 pb-2"
                    />
                ) : null}

                {link?.copy && link?.url && (
                    <a
                        href={link?.url}
                        target={link?.targetInNewWindow ? '_blank' : '_self'}
                        className="border-none font-primary-bold text-sm themed mt-2 md:mt-0 block">{link.copy}</a>
                )}
            </div>
            <div>
                <Swiper
                    modules={[Navigation, Pagination, Scrollbar]}
                    navigation={{
                        nextEl: `.navigg-categoryCards-rights${arrowsSelectorRandNum}`,
                        prevEl: `.navigg-categoryCards-lefts${arrowsSelectorRandNum}`,
                        disabledClass: "hidden",
                    }}
                    threshold={30}
                    spaceBetween={20}
                    slidesPerView={"auto"}
                    slidesOffsetBefore={16}
                    slidesOffsetAfter={16}
                    allowTouchMove={isAllowTouchMove()}
                    breakpoints={{
                        1023: {
                            slidesPerView: "auto",
                            spaceBetween: 36,
                            slidesOffsetBefore: 100,
                            slidesOffsetAfter: 100,
                            threshold: 30,
                        },
                    }}
                >
                    <div>
                        {cards.map((card: any, index: any) => (
                            <SwiperSlide key={index} className="w-[260px] h-[260px] lg:w-[283px] lg:h-[283px]">
                                <div className="rounded-3xl">
                                    <FeaturedDestinationCard card={card}
                                    />
                                </div>
                            </SwiperSlide>
                        ))}
                    </div>
                </Swiper>
                {cards && cards.length > 3 ? (
                    <div className="arrows-blk xs:block hidden">
                        <SliderArrow
                            icon="arrow-left"
                            className={`navigg-categoryCards-lefts${arrowsSelectorRandNum} arrow-left ltr:left-[5.8%] ltr:xl:left-[5.8%] ltr:2xl:left-[4.4%] rtl:right-[5.8%] rtl:xl:right-[5.8%] rtl:2xl:right-[4.4%] mt-[40px]`}
                        />
                        <SliderArrow
                            icon="arrow-right"
                            className={`navigg-categoryCards-rights${arrowsSelectorRandNum} arrow-right ltr:right-[5.8%] ltr:xl:right-[5.8%] ltr:2xl:right-[4.4%] rtl:left-[5.8%] rtl:xl:left-[5.8%] rtl:2xl:left-[4.4%] mt-[40px]`}
                        />
                    </div>
                ) : null}
            </div>
        </section>
    );
};

export default ExploringCards;

