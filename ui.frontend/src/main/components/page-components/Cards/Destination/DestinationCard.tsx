/* eslint-disable max-len */
import React from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";

import { getWeatherIcon } from "src/main/util/getWeatherIcon";

import { getImage } from "src/main/util/getImage";
import Icon from "src/main/components/common/atoms/Icon/Icon";

import { DestinationCardProps } from "./IDestinationCard";

import { getLanguage } from "src/main/util/getLanguage";
import PulseAnimation from "src/main/components/common/HOC/LoadingAnimation/PulseAnimation";

const DestinationCard = ({
    title,
    subTitle,
    bannerImage,
    popUpImage,
    latitude,
    longitude,
    id,
    categories,
    weather,
    className,
    pagePath,
}: DestinationCardProps) => {
    const comma = getLanguage() == "ar" ? "، " : ", ";
    return (
        <a
            href={pagePath?.url}
            target={pagePath?.targetInNewWindow ? "_blank" : ""}
            className={`relative block rounded-[20px] bg-white md:bg-transparent drop-shadow-lg md:drop-shadow-none  hover:scale-[0.99] duration-300 ${className}`}
        >
            {bannerImage ? <Picture
                image={getImage(bannerImage)}
                breakpoints={bannerImage.breakpoints}
                imageClassNames={`rounded-t-[20px] lg:rounded-[20px] w-full object-cover h-36 md:h-auto`}
            /> :
                <div className="relative h-36">
                    <PulseAnimation className={'rounded-t-[20px] lg:rounded-[20px] w-full bg-slate-400'} />
                </div>
            }
            <div className={`font-primary-bold rounded px-4 pt-4 gap-8`}>
                <div className="flex justify-between items-center ">
                    <div className={`w-9/12 text-xs lg:text-sm`}>
                        <div className="line-clamp-1 uppercase ltr:lg:pr-0 rtl:lg:pl-0 ltr:pr-[15px] rtl:pl-[15px]">
                            {categories &&
                                categories.map((category, index) => (
                                    <>
                                        <span>{category.title}</span>
                                        {index < categories.length - 1 && comma}
                                    </>
                                ))}
                        </div>
                    </div>
                    <div className={`flex justify-end items-start`}>
                        <div className="flex items-center gap-2 whitespace-nowrap">
                            {weather?.icon && (
                                <Icon
                                    svgClass="w-5"
                                    subfolder="weatherIcons"
                                    name={getWeatherIcon(weather.icon)}
                                />
                            )}
                            {weather?.temp ? (
                                <span className="text-sm md:text-base font-primary-regular">
                                    {weather.temp}°C
                                </span>
                            ) : null}
                        </div>
                    </div>
                </div>
                {title ? (
                    <div className={`mt-2 lg:mt-1 line-clamp-2 text-lg md:text-1.5xl lg:min-h-full min-h-[60px]`}>
                        {title}
                    </div>
                ) : null}
            </div>
        </a>
    );
};

export default DestinationCard;

