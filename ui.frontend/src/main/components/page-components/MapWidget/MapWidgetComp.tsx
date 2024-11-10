import React from "react";
import Text from "../../common/atoms/Text/Text";
import Icon from "../../common/atoms/Icon/Icon";
import Button from "../../common/atoms/Button/Button";
import { DestinationCardProps } from "../Cards/Destination/IDestinationCard";
import Map from "../../common/atoms/Map/Map";

interface MapWidgetProps {
    title: string;
    locationLabel: string;
    locationValue: string;
    latitude: string;
    longitude: string;
    ctaLabel: string;
    mapApiKey: string;
    destinations: DestinationCardProps[];
}

const MapWidget: React.FC<MapWidgetProps> = ({
    title,
    locationLabel,
    locationValue,
    latitude,
    longitude,
    mapApiKey,
    ctaLabel,
}) => {
    return (
        <div className={`bg-white rounded-2xl ${!(title || locationValue) ? "pt-0 pb-4" : "py-4"}`}>
            {title && (
                <Text text={title} styles={"font-primary-bold text-xs md:text-sm px-4 pb-4 uppercase"} />
            )}
            {locationValue && locationLabel && (
                <div className="flex px-4 ">
                    {/* <Icon
                        name={"emptyBlackLocation"}
                        svgClass={"!w-4 !h-4 lg:!w-5 lg:!h-5"}
                    /> */}
                    <Text
                        text={
                            <>
                                <span>{locationLabel}</span>
                                <span className="text-[#4B4B4B] ltr:pl-1 rtl:pr-1">
                                    {locationValue}
                                </span>
                            </>
                        }
                        styles={"font-primary-regular text-sm md:text-base capitalize break-all"}
                    />
                </div>
            )}

            <Map
                draggable={false}
                mapApiKey={mapApiKey}
                styles={`!h-[124px] ${(title || locationValue) && "mt-2"}`}
                radius={
                    !(title || locationValue)
                        ? "!rounded-t-2xl"
                        : "!rounded-none"
                }
                latitude={latitude}
                longitude={longitude}
            />
            <div className="m-4 mb-0">
                {ctaLabel && (
                    <Button
                        title={ctaLabel}
                        onclick={
                            true
                                ? async () =>
                                    window.open(
                                        `https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`,
                                        true ? "__blank" : undefined
                                    )
                                : undefined
                        }
                        arrows={false}
                        spanStyle={
                            "!px-0 relative top-[1px] overflow-hidden whitespace-nowrap text-ellipsis"
                        }
                        children={<Icon name="emptyBlackDirections" svgClass={`ltr:mr-2 rtl:ml-2`} />}
                        styles={`w-full justify-center text-sm py-2 px-4 lg:py-3 lg:px-6 lg:text-base font-primary-semibold justify-items-center rounded-lg capitalize`}
                    />
                )}
            </div>
        </div>
    );
};

export default MapWidget;

