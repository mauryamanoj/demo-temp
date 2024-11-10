/* eslint-disable max-len */
import React from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import Text from "src/main/components/common/atoms/Text/Text";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";

import CategoryCard from "./Card/Category";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import { useResize } from "src/main/util/hooks/useResize";

import { CategoryCardsProps } from "./iCategoryCards";

const CategoryCards: React.FC<CategoryCardsProps> = (props) => {
    const { title, cards, backgroundImage, view } = props;

    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;
    const { isMobile } = useResize();

    const showBackgroundImage =
        backgroundImage?.fileReference || backgroundImage?.s7fileReference;

    return (
        <section className={`${showBackgroundImage ? "bg-white" : ""} relative mb-20 md:mb-20`}>
            <div className="absolute top-0 bottom-0 rtl:right-0 rtl:-scale-x-100">
                {showBackgroundImage && backgroundImage && (
                    <Picture
                        image={getImage(backgroundImage)}
                        imageClassNames="h-full"
                        containerClassName="picture-element"
                    />
                )}
            </div>
            <div
                className={` !pt-4 !pb-12 lg:pb-24 lg:pt-10 md:pt-0 md:pb-0 relative`}
            >
                <div className='mx-5 md:mx-100'>
                    <Text text={title} isTitle />
                </div>
                {view == "slider" || isMobile ? (
                    <div className="relative">
                        <Swiper
                            modules={[Navigation, Pagination, Scrollbar]}
                            navigation={{
                                nextEl: `.navigg-categoryCards-right${arrowsSelectorRandNum}`,
                                prevEl: `.navigg-categoryCards-left${arrowsSelectorRandNum}`,
                                disabledClass: "hidden",
                            }}
                            slidesOffsetAfter={20}
                            slidesOffsetBefore={20}
                            slidesPerView={1.3}
                            spaceBetween={20}
                            threshold={30}
                            breakpoints={{
                                768: {
                                    slidesPerView: 3.24,
                                    spaceBetween: 36,
                                    slidesOffsetBefore: 100,
                                    slidesOffsetAfter: 100
                                },
                            }}
                        >
                            <div>
                                {cards.map((card: any, index: any) => (
                                    <SwiperSlide key={index}>
                                        <CategoryCard {...card} />
                                    </SwiperSlide>
                                ))}
                            </div>
                        </Swiper>
                        {cards.length > 3 && (
                            <div className="hidden md:block">
                                <SliderArrow
                                    icon="arrow-left"
                                    className={`navigg-categoryCards-left${arrowsSelectorRandNum} left-20 rtl:right-24 rtl:left-[auto] top-1/2 -translate-y-1/2`}
                                />
                                <SliderArrow
                                    icon="arrow-right"
                                    className={`navigg-categoryCards-right${arrowsSelectorRandNum} right-20 rtl:left-24 rtl:right-[auto] top-1/2 -translate-y-1/2`}
                                />
                            </div>
                        )}
                    </div>
                ) : (
                    <div className="mx-5 md:mx-100">
                        <div className="grid grid-cols-3 gap-9">
                            {cards.map((card: any, index: any) => (
                                <CategoryCard key={index} {...card} />
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </section>
    );
};

export default CategoryCards;

