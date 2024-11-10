import React from "react";
import { IDestinationGuideCardProps } from "../IDestinationGuideProps";
import Text from "src/main/components/common/atoms/Text/Text";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import { getWeatherIcon } from "src/main/util/getWeatherIcon";

const WeatherContent: React.FC<IDestinationGuideCardProps> = ({
    type,
    weather,
    temp,
    icon,
    overallWeatherLabel,
    items,
}) => {

    return (
        <div className={`flex flex-col gap-8`}>
            <div className={`flex justify-start items-center flex-col`}>
                <div className="flex items-center whitespace-nowrap">
                    {icon && <Icon subfolder="weatherIcons" name={getWeatherIcon(icon)}
                    wrapperClass="fill-themed-200" svgClass="w-[60px] h-[60px]"/>}
                    {temp ? (
                        <Text
                            text={temp + "°C"}
                            styles={"font-primary-bold text-5xl text-black"}
                        />
                    ) : null}
                </div>
                {weather && (
                    <Text
                        text={weather}
                        styles="font-tertiary-regular text-base text-black text-center"
                    />
                )}
            </div>
            <div>
                {overallWeatherLabel && (
                    <Text
                        text={overallWeatherLabel}
                        styles={
                            "font-primary-bold text-xs text-black w-full truncate mb-2 capitalize"
                        }
                    />
                )}
                <div>
                    {items &&
                        items?.length > 0 &&
                        items?.map((item, index) => (
                            <div
                                className="flex justify-between mb-1"
                                key={index}
                            >
                                {item && "title" in item && (
                                    <Text
                                        text={item.title}
                                        styles={
                                            "font-primary-semibold text-base text-black truncate capitalize "
                                        }
                                    />
                                )}
                                <div className="flex items-center gap-4">
                                    {"lowTemp" in item && (
                                        <div className="flex items-center w-16">
                                            {"lowTempIcon" in item &&
                                                item.lowTempIcon && (
                                                    <img
                                                        src={item.lowTempIcon}
                                                        className="w-[15px] h-[15px] ltr:mr-[6px] rtl:ml-[6px]"
                                                    />
                                                )}
                                            <Text
                                                text={item.lowTemp + "°C"}
                                                styles={
                                                    "font-primary-semibold text-base text-black "
                                                }
                                            />
                                        </div>
                                    )}
                                    {"highTemp" in item && (
                                        <div className="flex items-center  w-16">
                                            {"highTempIcon" in item &&
                                                item.highTempIcon && (
                                                    <img
                                                        src={item.highTempIcon}
                                                        className="w-[15px] h-[15px] ltr:mr-[6px] rtl:ml-[6px]"
                                                    />
                                                )}

                                            <Text
                                                text={item.highTemp + "°C"}
                                                styles={
                                                    "font-primary-semibold text-base text-black "
                                                }
                                            />
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                </div>
            </div>
        </div>
    );
};

export default WeatherContent;

