/* eslint-disable max-len */
import React from "react";

import DateCard from "src/main/components/common/atoms/DateCard/DateCard";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import Pill from "src/main/components/common/atoms/Pill/Pill";
// import Stars from "src/main/components/common/atoms/Stars/Stars";
import { getImage } from "src/main/util/getImage";
import FavoriteButton from "src/main/components/common/atoms/FavoriteButton/FavoriteButton";
import { IThingsToDoCardProps } from "./IThingsToDoCard";

const ThingsToDoCard = ({
    title,
    bannerImages,
    startDate,
    endDate,
    hideFavorite,
    ticketPrice,
    destination,
    ticketsCtaLink,
    pageLink,
    categories,
    type,
    cardSize,
    imageClass,
    titleClass,
    updateFavUrl,
    deleteFavUrl,
    buttonStyle,
    ticketPriceSuffix
}: IThingsToDoCardProps) => {
    // const { isMobile } = useResize();
    const titleCategoriesPadding =
        cardSize == "large" || cardSize == "medium" ? "p-4" : "p-4 pt-2 md:p-2";

    const btnWidthClass = !ticketPrice && "w-full md:w-auto";

    return (
        <>
            <a
                href={pageLink?.url}
                target={pageLink?.targetInNewWindow ? '_blank' : '_self'}
                className={`block relative cursor-pointer h-full select-none rounded-[20px] bg-white md:bg-transparent  mb-2 md:mb-0 drop-shadow-lg md:drop-shadow-none ${pageLink?.url ? "cursor-pointer" : ""}`}>
                {startDate || endDate ? (
                    <DateCard
                        from={startDate}
                        to={endDate}
                        className="absolute ltr:left-0 rtl:right-0 top-0 z-10"
                    />
                ) : null}

                <div className="absolute ltr:right-4 rtl:left-4 top-4 z-10 flex gap-2 md:gap-4">
                    {/* {type && (
                        <Pill className="flex items-center bg-theme-100 font-primary-bold">
                            {type}
                        </Pill>
                    )} */}
                    {!hideFavorite && (
                        <FavoriteButton
                            id={title}
                            updateFavoriteUrl={updateFavUrl}
                            deleteFavoriteUrl={deleteFavUrl}
                        />
                    )}
                </div>
                <div>
                    {bannerImages[0] ? (
                        <Picture
                            image={getImage(bannerImages[0])}
                            breakpoints={bannerImages[0].breakpoints}
                            alt={bannerImages[0].alt}
                            imageClassNames={`rounded-t-[20px] md:rounded-[20px] w-full  object-cover ${imageClass}`}
                        />
                    ) : (
                        <img src="" className={`rounded-t-[20px] md:rounded-[20px] w-full ${imageClass}`} />
                    )}
                </div>
                <div className={`TTDtitleElement font-primary-bold flex flex-col md:flex-row justify-between ${titleCategoriesPadding}`}>
                    <div className="md:max-w-[61%] mb-4 md:mb-0">
                        <div className="text-xs md:text-sm line-clamp-1 uppercase flex items-center mb-2 md:mb-4 cursor-default"
                            onClick={(e) => e.stopPropagation()}>
                            {destination?.title && (
                                <div className={`flex items-center gap-1 md:gap-2 whitespace-nowrap`}>
                                    <Icon wrapperClass="w-[15px] h-[15px] flex items-center justify-center" name="location-mark" />
                                    <span>{destination.title}</span>
                                </div>
                            )}

                            {categories && categories[0] && (
                                <div className="line-clamp-1">
                                    {destination?.title && <span className="mx-1 md:mx-2">|</span>}
                                    <span>{categories[0].title}</span>
                                </div>
                            )}
                        </div>

                        {title && (
                            <div className={`line-clamp-2 font-primary-bold text-xl md:text-lg ${titleClass}`}>
                                {title}
                            </div>
                        )}
                    </div>

                    <div className={`flex flex-row-reverse md:flex-col gap-2 md:max-w-[34%] md:items-end justify-between leading-[17.33px] md:leading-[19.81px]`}>
                        {ticketPrice && (
                            <div className="ltr:text-right rtl:text-left flex gap-1 items-center">
                                {/* {oldPrice && <div className="text-gray-50 line-through text-xs font-primary-semibold">{oldPrice ?? ""}</div>} */}
                                {ticketPrice && (
                                    <div className="text-sm md:text-base ">
                                        {ticketPrice}
                                    </div>
                                )}
                                {/* show suffix only for halayalla (type == 'EXPERIENCE') */}
                                {ticketPriceSuffix && (type == 'EXPERIENCE' || type == 'تجربة') && <div>{ticketPriceSuffix}</div>}
                            </div>
                        )}

                        {ticketsCtaLink?.text && pageLink?.url && (
                            <div className="flex flex-1 items-end">
                                <button
                                    className={`bg-theme-100 hover:bg-theme-200 text-white px-4 py-2
                                    font-primary-semibold text-base rounded-lg whitespace-nowrap
                                    ${cardSize != "small" &&
                                        cardSize != "small_2" &&
                                        "md:px-6"
                                        }
                                    ${btnWidthClass} ${buttonStyle}`}
                                >
                                    {ticketsCtaLink.text?.substring(0, 30)}
                                </button>
                            </div>
                        )}

                    </div>
                </div>
            </a>
        </>
    );
};

export default ThingsToDoCard;

