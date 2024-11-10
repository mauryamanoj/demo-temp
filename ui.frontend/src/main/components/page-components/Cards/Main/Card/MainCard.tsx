import React from "react";
import { useResize } from "src/main/util/hooks/useResize";
import Button from "../../../../common/atoms/Button/Button";
import DateCard from "../../../../common/atoms/DateCard/DateCard";
import Icon from "../../../../common/atoms/Icon/Icon";
import Picture from "../../../../common/atoms/Picture/Picture";
import Pill from "../../../../common/atoms/Pill/Pill";
import Stars from "../../../../common/atoms/Stars/Stars";
import { getImage } from "src/main/util/getImage";
import FavoriteButton from "src/main/components/common/atoms/FavoriteButton/FavoriteButton";
import { MainCardProps } from "./IMainCard";

const MainCard = ({
    image,
    imageClass,
    alt,
    fromDate,
    toDate,
    city,
    tag,
    oldPrice,
    price,
    title,
    cta,
    pill,
    hideFavorite,
    variant = "all-things-to-do",
    stars,
    share,
    cardWrapperStyle,
    cardCtaLink,
    link,
}: MainCardProps) => {
    const { isMobile } = useResize();

    let imageCls = imageClass || "h-[60vw] lg:h-[25vw]";
    let titleSecWidth = "lg:w-9/12";
    let titleSecStyle = "";
    let buttonSecWidth = "w-3/12";
    let titleStyle = "text-xl lg:text-3xl lg:leading-8";
    let buttonSize: "small" | "medium" = isMobile ? "small" : "medium";
    let buttonSpace = "mt-4";
    let textContainer = "p-4 gap-8";
    let containerStyle = "drop-shadow-lg lg:drop-shadow-none";

    if (variant === "events-card") {
        titleSecWidth = "lg:w-7/12";
        buttonSecWidth = "w-5/12";
        imageCls = imageClass || "h-[80vw] lg:h-[32vw]";
    } else if (variant === "all-things-to-do-small") {
        titleSecWidth = "lg:w-7/12";
        buttonSecWidth = "w-5/12";
        imageCls = imageClass || "h-[80vw] lg:h-[20vw]";
        titleStyle = "lg:text-xl";
        buttonSize = "small";
        textContainer = "p-2 gap-3";
        buttonSpace = "mt-2";
    } else if (variant === "hotels-and-accomodations") {
        titleSecWidth = "lg:w-9/12";
        buttonSecWidth = "w-3/12";
        imageCls = imageClass || "h-[80vw] lg:h-[20vw]";
        titleStyle = "lg:text-xl";
        textContainer = "flex p-2 gap-3";
    } else if (variant === "What-to-buy") {
        titleSecWidth = "w-full";
        buttonSecWidth = "w-5/12";
        imageCls = imageClass || "h-[261px] md:h-[390px] md:w-[390px]";
        titleStyle =
            "text-xl lg:text-3.5xl font-primary-semibold min-h-[50px] leading-6 lg:leading-10 w-full";
        buttonSize = "small";
        textContainer = "p-4 py-2 lg:px-2 gap-3";
        buttonSpace = "mt-2";
        containerStyle = "drop-shadow-md lg:drop-shadow-none";
        titleSecStyle = "!mb-0";
    }

    const fullButton = isMobile && !price && !oldPrice;

    const ButtonRender = () => {
        return cta && cta.label ? (
            <div
                className={`pb-2 lg:pb-0 lg:${buttonSpace}${fullButton ? " w-full" : ""}`}>
                <Button
                    title={cta.label}
                    onclick={
                        cta.link
                            ? () =>
                                window.open(
                                    cta.link,
                                    cta.openInNewTab ? "__blank" : undefined
                                )
                            : undefined
                    }
                    arrows={false}
                    size={buttonSize}
                    styles={`m-0 float-right${fullButton ? " w-full justify-center" : ""
                        }`}
                />
            </div>
        ) : (
            <></>
        );
    };

    return (
        <div
            className={`relative rounded-[20px] !rounded-b-0 bg-white md:bg-transparent
                    mb-2 lg:mb-6 ${containerStyle} ${cardWrapperStyle}`}
        >
            {fromDate ||
                (toDate && (
                    <DateCard
                        from={fromDate}
                        to={toDate}
                        className="absolute left-0 top-0 z-10"
                    />
                ))}
            <div className="absolute right-4 top-4 z-10 flex gap-2 lg:gap-4">
                {pill && <Pill>{pill}</Pill>}
                {!hideFavorite && <FavoriteButton id={title} />}
            </div>

            <a
                target={link?.targetInNewWindow ? "_blank" : "_self"}
                href={cardCtaLink}
                className="block"
            >
                {image ? (
                    <Picture
                        image={getImage(image)}
                        breakpoints={image.breakpoints}
                        alt={alt}
                        imageClassNames={`rounded-t-[20px] lg:rounded-[20px] object-cover w-full ${imageCls}`}
                    />
                ) : (
                    <></>
                )}
                <div
                    className={`font-primary-regular lg:flex lg:justify-between ${textContainer}`}
                >
                    <div
                        className={`${titleSecWidth} text-xs lg:text-sm mb-4 lg:mb-0 ${titleSecStyle}`}
                    >
                        <div className="flex line-clamp-1 uppercase leading-[22px]">
                            {city ? (
                                <span className="flex items-center gap-2">
                                    <Icon
                                        name="location-mark"
                                        svgClass=""
                                    />
                                    <span className="font-primary-bold">
                                        {city}
                                    </span>
                                </span>
                            ) : <div className="h-[22px]"></div>}
                            {tag && tag.length > 0 && (
                                <span className="font-primary-bold flex">
                                    {city && <div className="flex w-0.5 h-[70%] my-[3px] mx-2 bg-black"></div>}
                                    {tag}
                                </span>
                            )}
                        </div>
                        {title && (
                            <div
                                className={`mt-2 lg:mt-4 line-clamp-2 ${titleStyle}`}
                            >
                                {title}
                            </div>
                        )}
                    </div>
                    <div
                        className={`lg:${buttonSecWidth} uppercase flex justify-between
                                    lg:grid lg:justify-end text-right`}
                    >
                        {variant === "hotels-and-accomodations" ? (
                            <div>
                                {stars && stars > 0 && <Stars count={stars} />}
                                {share && (
                                    <div className="mt-8 float-right">
                                        <Icon name="share" />
                                    </div>
                                )}
                            </div>
                        ) : (
                            <>
                                {isMobile ? ButtonRender() : <></>}
                                {isMobile && !price && !oldPrice ? (
                                    <></>
                                ) : (
                                    <div>
                                        <div className="text-gray-100 line-through text-xs font-primary-semibold">
                                            {oldPrice ?? ""}
                                        </div>
                                        <div className="text-sm lg:text-base lg:font-bold">
                                            {price ?? ""}
                                        </div>
                                    </div>
                                )}
                                {!isMobile ? ButtonRender() : <></>}
                            </>
                        )}
                    </div>
                </div>
            </a>
        </div>
    );
};

export default MainCard;

