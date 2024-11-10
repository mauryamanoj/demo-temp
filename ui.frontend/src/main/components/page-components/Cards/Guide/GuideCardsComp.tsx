/* eslint-disable max-len */
import React, { useLayoutEffect, useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import OtherOrMap from "../Others/Card/Card";
import isMobile from "src/main/util/isMobile";
import { useResize } from "src/main/util/hooks/useResize";
import Text from "src/main/components/common/atoms/Text/Text";

const GuideCardsComp: React.FC<any> = (props) => {
    const { cards, title, backgroundImage, type } = props;
    const [showMobile, setShowMobile] = useState(isMobile(1024));
    const { widthSize } = useResize();
    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;

    useLayoutEffect(() => {
        setShowMobile(isMobile(768));
    }, [widthSize]);

    function isAllowTouchMove() {
        if (isMobile(1024)) {
            return true;
        }
        if (!isMobile(1024) && cards && cards.length < 5) {
            return false;
        }
    }
    return (
        <>
            {type == "GuideCardsItem" && !showMobile ? (
                <div
                    className=" mb-20 lg:mx-100 mx-5"
                    id="GuideCards"
                >
                    <div>
                        <Text text={title} isTitle />

                        <div className="grid grid-cols-2 gap-8">
                            {cards.map((card: any, key: any) => (
                                <OtherOrMap
                                    key={key}
                                    card={card}
                                    type={type}
                                    showMobile={showMobile}
                                />
                            ))}
                        </div>
                    </div>
                </div>
            ) : (
                <div className="relative bg-white pt-10" id="GuideCards">
                    <div className="absolute top-0 bottom-0 rtl:right-0 rtl:-scale-x-100 w-full md:w-auto">
                        {backgroundImage && (
                            // please dnt change it to picture component
                            <img src={backgroundImage} className="h-full w-full md:w-auto object-cover" alt={title} />
                        )}
                    </div>
                    <div className=" ltr:md:ml-100 rtl:md:mr-100 relative">
                        {title ? (
                            <h1 className="mx-5 md:mx-0 text-3xl md:text-5xl mb-6 md:mb-10 font-primary-bold leading-[37px]">
                                {title}
                            </h1>
                        ) : (
                            ""
                        )}
                        <Swiper
                            modules={[Navigation, Pagination, Scrollbar]}
                            navigation={{
                                nextEl: `.navigg-categoryCards-rights${arrowsSelectorRandNum}`,
                                prevEl: `.navigg-categoryCards-lefts${arrowsSelectorRandNum}`,
                                disabledClass: "hidden",
                            }}
                            threshold={30}
                            slidesOffsetAfter={20}
                            slidesPerView={1.6}
                            spaceBetween={16}
                            allowTouchMove={isAllowTouchMove()}
                            breakpoints={{
                                768: {
                                    slidesPerView: 4.34,
                                    spaceBetween: 36,
                                    slidesOffsetAfter: 80,
                                },
                            }}
                        >
                            <div>
                                {cards && cards.map((card: any, index: any) => (
                                    <SwiperSlide key={index}>
                                        <OtherOrMap card={card} type={"Map"} />
                                    </SwiperSlide>
                                ))}
                            </div>
                        </Swiper>
                        {cards && cards.length > 4 ? (
                            <div className="hidden md:block">
                                <SliderArrow
                                    icon="arrow-left"
                                    className={`navigg-categoryCards-lefts${arrowsSelectorRandNum} -left-5 rtl:-right-[2%] rtl:left-[auto] hidden top-[calc(50%+24px)]`}
                                />
                                <SliderArrow
                                    icon="arrow-right"
                                    className={`navigg-categoryCards-rights${arrowsSelectorRandNum} right-[5%] rtl:left-[5%] rtl:right-[auto] top-[calc(50%+24px)]`}
                                />
                            </div>
                        ) : null}
                    </div>
                </div>
            )}
        </>
    );
};

export default GuideCardsComp;

