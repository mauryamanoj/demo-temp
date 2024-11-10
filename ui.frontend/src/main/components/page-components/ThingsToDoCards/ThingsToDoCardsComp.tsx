/* eslint-disable max-len */
import React, { useEffect, useState, useRef } from "react";
import Text from "src/main/components/common/atoms/Text/Text";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import SliderArrow from "src/main/components/common/atoms/SliderArrow/SliderArrow";
import ThingsToDoCard from "./ThingsToDoCard";
import Button from "src/main/components/common/atoms/Button/Button";
import api from "src/main/util/api";
import { getLanguage } from "src/main/util/getLanguage";
import dayjs from "dayjs";
import { ThingsToDoCardsProps } from "./IThingsToDoCard";
import TabsSection from "./TabsSection";
import filterDataByUrl, { Data } from "src/main/util/filterDataByUrl";
import getIsRTL from "src/main/util/getIsRTL";
import { useResize } from "src/main/util/hooks/useResize";

const ThingsToDoCards: React.FC<ThingsToDoCardsProps> = (props) => {
    const {
        title,
        filter,
        sort,
        handpick,
        display,
        apiUrl,
        link,
        loadMore,
        tabs,
    } = props;

    const [events, setEvents] = useState<Data>([]);
    const [offset, setOffset] = useState(0);
    const [totalEvents, setTotalEvents] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [isLayout2ColsExist, setIsLayout2ColsExist] = useState(false);
    const thingsToDoCardsRef = useRef<HTMLDivElement | null>(null);

    const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;
    // slider configuration
    const componentMargins = !isLayout2ColsExist ? "mx-5 md:mx-100" : "";
    const cardSize = !isLayout2ColsExist ? display.cardSize : "small"; // only small size on 2 cols layout

    const { isMobile } = useResize();


    let imageWidth: string;
    let imageHeight: string;

    if (cardSize == "large") {
        imageWidth = "!w-[318px] md:!w-[900px]";
        imageHeight = "!h-[180px] md:!h-[490px]";
    }
    if (cardSize == "medium") {
        imageWidth = "!w-[260px] md:!w-[600px]";
        imageHeight = "!h-[260px] md:!h-[600px]";
    }
    if (cardSize == "small" || cardSize == "small_2") {
        imageWidth = "!w-[260px] md:!w-[390px]";
        imageHeight = "!h-[260px] md:!h-[390px]";
    }

    const fixTitlesHeight = (swiper?: any) => {
        // make sure all titles have the same height, since some of them might be 1 line and others 2 lines
        let TTDtitleElement = null;
        if (swiper && swiper.$el) {
            TTDtitleElement = swiper?.$el[0]?.querySelectorAll(".TTDtitleElement");
        } else {
            TTDtitleElement = document.querySelector('#gridViewDiv')?.querySelectorAll(".TTDtitleElement");

        }
        let maxHeight = 0;
        TTDtitleElement?.forEach((element: HTMLElement) => {
            // Reset the height to auto to remove any previous height set
            element.style.height = "auto";

            const elementHeight = element.offsetHeight;
            if (elementHeight >= maxHeight) {
                maxHeight = elementHeight;
            }
        });
        TTDtitleElement?.forEach((element: HTMLElement) => {
            element.style.height = `${maxHeight}px`;
        });
    };

    const getStartEndMonth = (startEnd: string, period: any) => {
        if (startEnd === "start") {
            return dayjs().startOf(period).format("YYYY-MM-DD");
        }
        if (startEnd === "end") {
            return dayjs().endOf(period).format("YYYY-MM-DD");
        }
        return "";
    };

    const handleLoadMore = () => {
        setOffset((prevOffset) => prevOffset + 9);
        getThingsToDoCards(offset + 9);
    };

    const buildQueryString = (newOffset?: any, selectedCategory?: any) => {
        const limit = display.numberOfCards ? display.numberOfCards : 100;
        let queryString = `locale=${getLanguage()}&limit=${limit}&offset=${newOffset || offset
            }`;

        if (filter) {
            if (filter.season) {
                queryString += `&season=${filter.season}`;
            }
            if (filter.destination) {
                queryString += `&destination=${filter.destination}`;
            }


            if (filter.period) {
                const period = filter.period.split("-")[1]; // period = this-week | this-month
                queryString += `&startDate=${getStartEndMonth(
                    "start",
                    period
                )}`;
                queryString += `&endDate=${getStartEndMonth(
                    "end",
                    period
                )}`;
            } else {
                if (filter.startDate) {
                    const startDate = filter.startDate
                        ? filter.startDate.split("T")[0]
                        : "";
                    const endDate = filter.endDate
                        ? filter.endDate.split("T")[0]
                        : "";
                    queryString += `&startDate=${startDate}`;
                    queryString += `&endDate=${endDate}`;
                }
            }


            if (filter.type) {
                const typePaths = filter.type
                    .map((path) => `type=${encodeURIComponent(path)}`)
                    .join("&");
                queryString += `&${typePaths}`;
            }

            if (filter.poiTypes) {
                const poiTypesPaths = filter.poiTypes
                    .map((path) => `poiTypes=${encodeURIComponent(path)}`)
                    .join("&");
                queryString += `&${poiTypesPaths}`;
            }

            if (filter.categories) {
                const categoriesPaths = filter.categories
                    .map((path) => `categories=${encodeURIComponent(path)}`)
                    .join("&");
                queryString += `&${categoriesPaths}`;
            }

            if (filter.destinations) {
                const destinationsPaths = filter.destinations
                    .map((path) => `destinations=${encodeURIComponent(path)}`)
                    .join("&");
                queryString += `&${destinationsPaths}`;
            }

            if (filter.freeOnly) {
                queryString += `&freeOnly=${filter.freeOnly}`;
            }
        }

        if (sort?.sortBy && sort?.sortBy.length > 0) {
            const sortByQueryString = sort.sortBy
                .map((item: any) => `sortBy=${item}`)
                .join("&");
            queryString += `&${sortByQueryString}`;
        }

        if (selectedCategory && selectedCategory.category) {
            queryString += `&categories=${selectedCategory.category}`;
        } else if (handpick && handpick.cfPaths) {
            const handPickPaths = handpick.cfPaths
                .map((path) => `paths=${encodeURIComponent(path)}`)
                .join("&");
            queryString += `&${handPickPaths}`;
        }

        queryString += `&imagesSize=${cardSize}`;

        return queryString;
    };

    //colappse the container if error
    const hideSectionDueToError = () => {
        thingsToDoCardsRef.current?.parentElement?.classList.add("hidden");
    };
    //get API data
    const getThingsToDoCards = (newOffset?: number, selectedCategory?: any) => {
        const queryString = buildQueryString(newOffset, selectedCategory);
        const url = `${apiUrl}?${queryString}`;

        const cachedEventsString = sessionStorage.getItem(url);
        if (cachedEventsString) {
            try {
                const cachedEvents = JSON.parse(cachedEventsString);
                setEvents(filterDataByUrl(window.location.pathname, cachedEvents));
                fixTitlesHeight();
                return;
            } catch (error) {
                console.error('Error parsing cached events:', error);
            }
        }

        setIsLoading(true);
        api.get({ url }).then(
            (res: any) => {
                setEvents([]);
                setIsLoading(false);
                if (res.data && res.data.code == 200) {
                    if (res.data.response.data.length) {
                        setTotalEvents(res.data.response.pagination.total);

                        if (newOffset) {
                            const tempEvents: any = [
                                ...events,
                                ...res.data.response.data,
                            ];
                            setEvents(filterDataByUrl(window.location.pathname, tempEvents));
                        } else {
                            setEvents(filterDataByUrl(window.location.pathname, res.data.response.data));
                            sessionStorage.setItem(url, JSON.stringify(filterDataByUrl(window.location.pathname, res.data.response.data)))
                        }
                    } else {
                        setEvents([]);
                        if (!tabs?.length) {
                            hideSectionDueToError();
                        }
                    }
                } else {
                    hideSectionDueToError();
                    console.log("couldnt get events");
                }
            },
            (err) => {
                setIsLoading(false);
                hideSectionDueToError();
                console.log("couldnt get events", err);
            }
        ).finally(() => {
            //fix in list_view
            fixTitlesHeight();
        });
    };

    useEffect(() => {
        const checkLayout2Cols = () => {
            const currentElement = thingsToDoCardsRef.current;
            if (currentElement && currentElement.closest(".layout2Cols")) {
                setIsLayout2ColsExist(true);
            }
        };

        checkLayout2Cols();
        getThingsToDoCards();

    }, [thingsToDoCardsRef.current]);

    const handleTabClick = (category: any) => {
        getThingsToDoCards(undefined, category);
    };

    const linkUrl = () => {
        //don't include the queryparams when in halayalla filter.type['experience]
        let url = link.url + '?' + buildQueryString();
        if (filter.hasOwnProperty("type")) {
            if (filter.type.includes("experience")) {
                url = link.url;
            }
        }
        return url;
    }

    return (
        <section
            ref={thingsToDoCardsRef}
            className={`${isLayout2ColsExist && "-mx-5 md:mx-0"}`}
        >
            <div className="relative">
                {isLoading && (
                    <div className="absolute h-full top-0 left-0 right-0 bg-white/30 z-[11] flex items-center justify-center">
                        <span className="relative flex h-10 w-10 opacity-75">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-theme-100 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-10 w-10 bg-theme-200"></span>
                        </span>
                    </div>
                )}

                <div className={`md:flex mb-4 md:mb-10 justify-between items-center ${componentMargins} ${isLayout2ColsExist && "mx-5 md:mx-0"}`}>
                    <Text
                        styles={`font-primary-bold text-3xl md:text-5xl text-left rtl:text-right capitalize text-left text-black break-word !mb-0 md:mb-0 md:w-9/12 leading-[37px] md:leading-[60px] ${title ? "block" : "invisible"
                            }`}
                        text={title}
                        isTitle
                    />
                    {link?.text && link?.url && (
                        <a href={linkUrl()} target={link?.targetInNewWindow ? "_blank" : ''}
                            className="border-none font-primary-bold text-sm themed mt-2 md:mt-0 block">
                            {link.text}
                        </a>
                    )}
                </div>

                {tabs && (
                    <div className={`${componentMargins} mb-4`}>
                        <TabsSection tabs={tabs} onclick={handleTabClick} />
                    </div>
                )}


                {display.displayType == "scrollable" && events.length > 0 &&
                    <div className={`relative TTDSwiper${events.length === 1 && display.cardSize === "large" ? " mx-5 md:mx-100" : ""}`}>
                        <div className={`relative transition-all duration-300 overflow-hidden ${cardSize != "small_2" && "w-full"}`}
                            dir={isMobile && cardSize == "large" && events.length > 2 && getIsRTL() ? 'ltr' : ''}
                        >
                            <Swiper
                                cssMode={true}
                                touchMoveStopPropagation={true}
                                touchStartForcePreventDefault={true}
                                threshold={50}
                                modules={[
                                    Navigation,
                                    Pagination
                                ]}
                                navigation={{
                                    nextEl: `.navigg-categoryCards-right${arrowsSelectorRandNum}`,
                                    prevEl: `.navigg-categoryCards-left${arrowsSelectorRandNum}`,
                                    disabledClass: "hidden",
                                }}
                                loop={cardSize == "large" && events.length > 2}
                                centeredSlides={cardSize == "large" && events.length > 2}
                                slidesPerView={"auto"}
                                spaceBetween={20}
                                slidesOffsetBefore={cardSize == "large" && events.length > 2 ? 0 : 20}
                                slidesOffsetAfter={cardSize == "large" && events.length > 2 ? 0 : 20}
                                breakpoints={{
                                    768: {
                                        cssMode: false,
                                        slidesPerView: 'auto',
                                        spaceBetween: !isLayout2ColsExist ? 35 : 28,
                                        slidesOffsetBefore: (!isLayout2ColsExist && cardSize != "large") ? 100 : 0,
                                        slidesOffsetAfter: (!isLayout2ColsExist && cardSize != "large") ? 100 : 0,
                                    },
                                }}
                                onResize={(swiper) => {
                                    fixTitlesHeight(swiper);
                                }}
                                onSwiper={(swiper) => {
                                    fixTitlesHeight(swiper);
                                }}


                            >
                                <div>
                                    {events.map((card: any, index: any) => (
                                        <SwiperSlide
                                            key={index}
                                            className={imageWidth}
                                        >
                                            <ThingsToDoCard
                                                {...card}
                                                filter={filter}
                                                imageClass={`${imageWidth} ${imageHeight}`}
                                                cardSize={cardSize}
                                                titleClass={`${cardSize == "large" ||
                                                    cardSize == "medium"
                                                    ? "text-xl md:text-3.5xl"
                                                    : "text-lg"
                                                    }`}
                                            />
                                        </SwiperSlide>
                                    ))}
                                </div>
                            </Swiper>
                        </div>
                        {events.length > 2 &&
                            !isLoading && (
                                <div
                                    className={`hidden md:block ${componentMargins} absolute ${!!isLayout2ColsExist
                                        ? "top-[calc(50%-88px)]"
                                        : "top-[36%]"
                                        } flex justify-between right-0 left-0`}
                                >
                                    <SliderArrow
                                        icon="arrow-left"
                                        className={`navigg-categoryCards-left${arrowsSelectorRandNum} ltr:-left-6 rtl:-right-6 `}
                                    />
                                    <SliderArrow
                                        icon="arrow-right"
                                        className={`navigg-categoryCards-right${arrowsSelectorRandNum} ltr:-right-6 rtl:-left-6`}
                                    />
                                </div>
                            )}
                    </div>
                }

                {display.displayType != "scrollable" &&
                    <div id="gridViewDiv">
                        <div className={`grid grid-cols-1 gap-8 md:grid-cols-2 md:gap-10 lg:grid-cols-3 lg:gap-10  ${componentMargins}`}>
                            {events
                                .slice(0, offset + 9)
                                .map((card: any, index: any) => (
                                    <ThingsToDoCard
                                        key={index}
                                        {...card}
                                        titleClass={"text-lg md:text-xl"}
                                    />
                                ))}
                        </div>

                        {loadMore &&
                            totalEvents > offset + 9 &&
                            totalEvents > events.length ? (
                            <Button
                                onclick={() => handleLoadMore()}
                                styles="mt-20"
                                size="big"
                                title={loadMore.loadMoreLabel}
                                arrows={false}
                            />
                        ) : (
                            <></>
                        )}
                    </div>
                }

                {!events.length && !isLoading && <div className="text-center">
                    {getLanguage() == "ar"
                        ? "لا توجد عناصر في هذا الفئة"
                        : "No Items in this Category"}
                </div>

                }


            </div>
        </section>
    );
};

export default ThingsToDoCards;

