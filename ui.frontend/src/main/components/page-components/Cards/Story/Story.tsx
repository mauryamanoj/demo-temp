/* eslint-disable max-len */
import React, { useEffect, useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import Card from "./Card/Card";
import { Navigation, Pagination, Scrollbar, A11y } from "swiper";

const StoryCard: React.FC<any> = (props) => {
    const { cards } = props.props;
    const [shouldRenderSwiper, setShouldRenderSwiper] = useState(true);
  
    useEffect(() => {
        const handleResize = () => {
            const isDesktop = window.innerWidth >= 768; // Adjust breakpoint as needed
            setShouldRenderSwiper(!isDesktop);
        };

        handleResize();
        window.addEventListener("resize", handleResize);
    }, [shouldRenderSwiper]);

    return (
        <div>
            {shouldRenderSwiper ? (
                <Swiper
                    onPaginationRender={(swiper, el) => {
                        el.classList.add("!bottom-[-6px]");
                    }}
                    modules={[Navigation, Pagination, Scrollbar, A11y]}
                    spaceBetween={20}
                    slidesPerView={1}
                    threshold={30}
                    navigation
                    pagination={{ clickable: true }}
                    scrollbar={{ draggable: true }}
                    onSwiper={(swiper) => console.log(swiper)}
                    onSlideChange={() => console.log("slide change")}
                >
                    <ul>
                        {cards.map((item: any, index: any) => (
                            <SwiperSlide key={index}>
                                <Card item={item} />
                            </SwiperSlide>
                        ))}
                        <div className="swiper-pagination"></div>
                    </ul>
                </Swiper>
            ) : (
                <>
                    {cards.map((item: any, index: any) => (
                        <Card item={item} key={index} />
                    ))}
                </>
            )}
        </div>
    );
};

export default StoryCard;

