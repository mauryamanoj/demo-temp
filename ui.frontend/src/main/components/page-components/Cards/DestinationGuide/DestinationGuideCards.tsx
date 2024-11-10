import React, { useState, useEffect } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";

import { IDestinationGuideProps } from "./IDestinationGuideProps";
import Text from "src/main/components/common/atoms/Text/Text";
import Button from "src/main/components/common/atoms/Button/Button";
import DestinationGuideCard from "./DestinationGuideCard";
import { getImage } from "src/main/util/getImage";
import Picture from "src/main/components/common/atoms/Picture/Picture";

const DestinationGuideCards: React.FC<IDestinationGuideProps> = ({
    cards,
    title,
    link,
    subTitle,
    background,
    haveBackgroundImage
}) => {

    const [mobileView, setMobileView] = useState(window.innerWidth < 768);

    useEffect(() => {
        let resizeTimeout: NodeJS.Timeout;
        const handleResize = () => {
            if (window.innerWidth >= 768) {
                setMobileView(false);
            } else {
                setMobileView(true);
            }
        };

        handleResize();
        window.addEventListener("resize", handleResize);

        return () => {
            clearTimeout(resizeTimeout);
            window.removeEventListener("resize", handleResize);
        };
    }, []);

    const getSize = (index: number) => {
        const isLarge =
            cards?.length <= 2 || (cards?.length === 3 && index === 0);
        return isLarge ? "large" : "small";
    };

    return (
        <div className={`relative px-5 md:px-100 overflow-hidden mb-16 md:mb-20
        ${haveBackgroundImage && background ? "bg-white" : ""}`}>
            {!mobileView ?
                <div className="absolute top-0 bottom-0 ltr:left-0 rtl:right-0 rtl:-scale-x-100 z-0">
                    {haveBackgroundImage && background && (
                        <Picture
                            image={getImage(background)}
                            imageClassNames="h-full"
                            containerClassName="picture-element"
                        />
                    )}
                </div>
                : <></>}
            <div className="z-[1] relative">
                {(title || subTitle || (link?.text && link?.url)) && (
                    <div className="mb-6 md:mb-10">
                        <div className="md:flex ">
                            <Text
                                text={title}
                                styles={`font-primary-bold text-3xl md:text-5xl
                        leading-[37px] md:leading-[60px] mb-2 md:mb-0 md:w-9/12`}
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

                        {subTitle ? <div dangerouslySetInnerHTML={{ __html: subTitle }}
                            className="text-1.5xl font-primary-semibold leading-[21px] md:leading-[27px] mt-2 md:mt-4 w-full richTextContainer" /> : null}
                    </div>
                )}
                {mobileView ? (
                    <div>
                        {cards?.find((card) => card?.type === "text")
                            ? cards
                                ?.filter((card) => card.type === "text")
                                ?.map((textCard: any, index: number) => (
                                    <div key={index}>
                                        {/* Render text cards without a slider */}
                                        <DestinationGuideCard
                                            {...textCard}
                                            styles="ltr:mr-5 rtl:ml-5 mb-4"
                                        />
                                    </div>
                                ))
                            : null}
                        {/* Common Swiper setup for non text cards */}
                        <Swiper
                            modules={[Navigation, Pagination, Scrollbar]}
                            slidesOffsetAfter={20}
                            slidesPerView={1.3}
                            spaceBetween={20}
                            threshold={30}
                            style={{
                                display: "grid",
                            }}
                        >
                            {cards
                                .filter((card) => card.type !== "text")
                                .map((nonTextCard: any, index: number) => (
                                    <SwiperSlide key={index}>
                                        <DestinationGuideCard {...nonTextCard} />
                                    </SwiperSlide>
                                ))}
                        </Swiper>
                    </div>
                ) : (
                    <div className=" pr-5 pl-0 rtl:pr-0 rtl:pl-5 lg:pr-100 lg:pl-0 rtl:lg:pr-0 rtl:lg:pl-100 md:flex gap-9">
                        {cards?.map((card: any, index: any) => (
                            <div
                                key={index}
                                className={`${getSize(index) === "large"
                                        ? "md:w-6/12"
                                        : "md:w-3/12"
                                    }`}
                            >
                                <DestinationGuideCard {...card} />
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default DestinationGuideCards;

