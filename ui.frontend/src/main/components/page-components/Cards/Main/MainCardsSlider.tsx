import React from "react";
import MainCard from "src/main/components/page-components/Cards/Main/Card/MainCard";
import Slider from "src/main/components/common/organisms/Slider/Slider";
import { SwiperSlide } from "swiper/react";
import { useResize } from "src/main/util/hooks/useResize";

const MainCardsSlider = ({ variant, data }: any) => {
    const { isMobile } = useResize();

    let slidesPerView = 2;
    const spaceBetween = 36;
    let slidesPerViewMobile = 1.2;
    const spaceBetweenMobile = 16;
    let containerClassName = "";

    if (variant === "events-card") {
        slidesPerView = 3;
        slidesPerViewMobile = 1.3;
    } else if (variant === "all-things-to-do-small") {
        slidesPerView = 5;
        slidesPerViewMobile = 1.4;
    } else if (variant === "hotels-and-accomodations") {
        slidesPerView = 5;
        slidesPerViewMobile = 1.4;
    } else if (variant === "What-to-buy") {
        slidesPerView = 3;
        slidesPerViewMobile = 1.4;
        containerClassName = "max-w-[1440px] m-auto";
    }

    return (
        <>
            <div className={containerClassName}>
                <Slider
                    slidesPerView={slidesPerViewMobile}
                    pagination={false}
                    spaceBetween={spaceBetweenMobile}
                    breakpoints={{
                        1024: {
                            slidesPerView: slidesPerView,
                            spaceBetween: isMobile ? 16 : spaceBetween,
                        },
                    }}
                >
                    {data.map((card: any, key: number) => (
                        <SwiperSlide key={key} className="pl-1 my-4">
                            <MainCard {...card} variant={variant} />
                        </SwiperSlide>
                    ))}
                </Slider>
            </div>
        </>
    );
};

export default MainCardsSlider;

