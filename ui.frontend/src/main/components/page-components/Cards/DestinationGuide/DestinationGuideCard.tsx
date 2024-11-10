import React from "react";

import Text from "src/main/components/common/atoms/Text/Text";
import Button from "src/main/components/common/atoms/Button/Button";

import TextContent from "./Components/TextContent";
import ItemContent from "./Components/ItemContent";
import WeatherContent from "./Components/WeatherContent";

import { IDestinationGuideCardProps } from "./IDestinationGuideProps";

const DestinationGuideCard: React.FC<IDestinationGuideCardProps> = (props) => {
    const {
        styles,
        title,
        type,
        logo,
        link,
        description,
        items,
        weather,
        temp,
        icon,
        overallWeatherLabel
    } = props;
    const weatherData = {
        weather,
        temp,
        icon,
        overallWeatherLabel,
        items
    }
    return (
        <div
            className={`rounded-2xl bg-gray-200 p-6 h-full flex flex-col justify-between ${styles}`}
        >
            <div>
                {title && (
                    <Text
                        text={title}
                        styles={
                            "font-primary-bold text-base uppercase text-black pb-6 "
                        }
                    />
                )}
                {logo && logo?.fileReference && (
                    <img
                        src={logo?.desktopImage}
                        alt={logo?.fileReference}
                        className=" w-14 h-14 mb-4"
                    />
                )}
                {type &&
                    (type === "text" ? (
                        <TextContent type={type} description={description} />
                    ) : type === "item" ? (
                        <ItemContent type={type} items={items} />
                    ) : (
                        <WeatherContent type={type} {...weatherData} />
                    ))}
            </div>
            {link?.text && link?.url && (
                <>
                    <a href={link?.url} target={link.targetInNewWindow ? "_blank" : '_self'}
                        className="mt-6 font-primary-bold text-sm themed truncate w-auto mr-1"
                    >{link.text}
                        {
                            link?.icon ? <img src={link?.icon} className="w-4 h-4" /> : <></>
                        }
                    </a>

                </>
            )}
        </div>
    );
};

export default DestinationGuideCard;

