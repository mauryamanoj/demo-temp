import React, { useEffect, useState, useRef } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import Card from "./PromotionalBanner/PromotionalBannerCard";
import Slider from "src/main/components/common/organisms/Slider/Slider";
import "./PromotionalBanner.css";
import { useResize } from "src/main/util/hooks/useResize";
import Icon from "../../common/atoms/Icon/Icon";
import Picture from "../../common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import ShowMore from "../Gallery/components/ShowMore";
import Gallery from "../../common/organisms/Gallery/Gallery";

const host = window.location.hostname == "localhost" ? 'https://qa-revamp.visitsaudi.com' : '';
const ornamentDesktop = `${host}/content/dam/sauditourism/ornaments/ornament-v-m2-24.png`;
const ornamentMobile = `${host}/content/dam/sauditourism/ornaments/ornament-h-m1-24.png`;

// todo: change hideImageBrush to showImageBrush check with AEM team
const PromotionalBannerComp: React.FC<any> = ({
    cards,
    type,
    hideImageBrush = "false",
    enableManualAuthoring = true,
    height,
    showArrows,
    logo,
    thumbs,
    ctaData,
    logoSize
}) => {
    const [openGallery, setOpenGallery] = useState(-1);
    const [mobileView, setMobileView] = useState(window.innerWidth < 768);
    const { isMobile } = useResize(1023);
    const [heightSize, setheight] = useState("");
    const [isLayout2ColsExist, setIsLayout2ColsExist] = useState(false);
    const promotionalBannerRef = useRef<HTMLDivElement | null>(null);


    const brushImage =
        window.location.hostname == "localhost"
            ? // eslint-disable-next-line max-len
            "https://dev-revamp.visitsaudi.com/content/dam/static-images/resources/images/ornaments-03-brushes/horizontal/desktop/10.png"
            : `/content/dam/static-images/resources/images/ornaments-03-brushes/horizontal/desktop/10.png`;

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

    function sendData(heightSize: string) {
        setheight(heightSize);
    }

    function getSize(heightSize: string) {
        if (heightSize == "small") {
            return `small-height`;
        }
    }
    useEffect(() => {
        const checkLayout2Cols = () => {
            const currentElement = promotionalBannerRef.current;
            if (currentElement && currentElement.closest(".layout2Cols")) {
                setIsLayout2ColsExist(true);
            }
        };

        checkLayout2Cols();
    }, [promotionalBannerRef.current]);

    return (
        <div ref={promotionalBannerRef}
            className={`relative ${getSize(heightSize)} ${!!isLayout2ColsExist ? "-mx-5 md:mx-auto" : ""}`}
            id={type == "HomeBanner" ? "homeBanner" : "promotionalBanner"}
        >
            {type == "Story" ? (
                <Slider
                    showArrows={
                        (heightSize == "large" || heightSize == "medium") &&
                            showArrows &&
                            cards.length > 1
                            ? true
                            : false
                    }
                    showMiddelArrow={true}
                    autoplay={cards.length > 1 ? { delay: 6000 } : undefined}
                    loop={false}
                >
                    {cards.map((item: any, index: any) => (
                        <SwiperSlide key={index}>
                            <Card
                                {...item}
                                logo={logo}
                                index={index}
                                showArrows={showArrows}
                                heightSize={heightSize}
                                sendData={() => sendData(height)}
                                mobileView={mobileView}
                                className={cards.length > 1 ? `bottom-[75px] md:bottom-[95px]` : 'bottom-[30px] md:bottom-[64px]'}
                                thumbs={thumbs}
                                setOpenGallery={setOpenGallery}
                                ctaData={ctaData}
                                type={type}
                                logoSize={logoSize}
                            />
                        </SwiperSlide>
                    ))}
                </Slider>
            ) : cards && cards.length > 0 ? (
                <Card
                    {...cards[0]}
                    index={0}
                    type={type}
                    sendData={(e: string) => sendData(e)}
                    mobileView={mobileView}
                    className="pb-13"
                    thumbs={thumbs}
                    setOpenGallery={setOpenGallery}
                    ctaData={ctaData}
                    logoSize={logoSize}
                />
            ) : (
                <></>
            )}

            {thumbs?.gallery && isMobile ? (
                <div className="h-24 w-screen absolute -bottom-8 z-30">
                    <Swiper
                        slidesOffsetAfter={20}
                        slidesOffsetBefore={20}
                        slidesPerView={2.2}
                        spaceBetween={20}
                        threshold={30}
                        slideToClickedSlide
                    >
                        {thumbs.gallery
                            .slice(0, 4)
                            .map((item: any, key: number) => (
                                <SwiperSlide
                                    key={key}
                                    className="cursor-pointer"
                                    onClick={() =>
                                        setOpenGallery(
                                            thumbs.gallery.length > 4 &&
                                                key === 3
                                                ? 9999
                                                : key
                                        )
                                    }
                                >
                                    {item?.type === "video" ? (
                                        <>
                                            {!(
                                                thumbs.gallery.length > 4 &&
                                                key === 3
                                            ) ? (
                                                <div
                                                    className="absolute top-1/2 left-1/2 z-10 cursor-pointer
                                                    -translate-y-1/2 -translate-x-1/2"
                                                >
                                                    <Icon name="play" />
                                                </div>
                                            ) : (
                                                <></>
                                            )}
                                            {item?.thumbnail ? (
                                                <Picture
                                                    imageClassNames="h-24 w-full rounded-2xl"
                                                    image={item.thumbnail}
                                                />
                                            ) : null}
                                        </>
                                    ) : item?.type === "image" && item.image ? (
                                        <Picture
                                            imageClassNames="h-24 w-full rounded-2xl"
                                            image={getImage(item.image, 767)}
                                            breakpoints={
                                                item.image?.breakpoints
                                            }
                                        />
                                    ) : (
                                        <></>
                                    )}
                                    {thumbs.gallery.length > 4 && key === 3 ? (
                                        <ShowMore
                                            gallery={thumbs.gallery}
                                            moreLabel={thumbs.moreLabel}
                                        />
                                    ) : (
                                        <></>
                                    )}
                                </SwiperSlide>
                            ))}
                    </Swiper>
                </div>
            ) : (
                <></>
            )}

            {enableManualAuthoring && hideImageBrush.toString() === "false" ? (
                <img
                    className={`absolute bottom-1 w-full h-3 z-10 ${heightSize == "small" ? "md:rounded-full" : ""}`}
                    src={brushImage}
                />
            ) : null}
            {thumbs?.gallery?.length > 0 ? (
                <Gallery
                    gallery={thumbs.gallery}
                    openGallery={openGallery}
                    closeGallery={() => setOpenGallery(-1)}
                />
            ) : (
                <></>
            )}
            {type === "HomeBanner" ? (
                <div className="absolute bottom-0 top-auto md:top-0 right-0 left-0 md:left-auto z-10 overflow-hidden">
                    <img src={mobileView ? ornamentMobile : ornamentDesktop} className='max-w-none' />
                </div>

                // <div className={`absolute bottom-0 md:top-0 right-0 z-10 ${mobileView ? "w-full" : ""}`} >
                //     <Icon name={mobileView ? "ornament-h-m1-24" : "ornament-v-m2-24"} svgClass="md:h-full md:w-6 w-full h-6" />
                // </div>
            ) : null}
        </div>
    );
};

export default PromotionalBannerComp;

