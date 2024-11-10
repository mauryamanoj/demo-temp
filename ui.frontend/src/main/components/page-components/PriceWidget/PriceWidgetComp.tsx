import React from "react";

import Icon from "../../common/atoms/Icon/Icon";
import Text from "../../common/atoms/Text/Text";
import Button from "../../common/atoms/Button/Button";
import { PriceWidgetProps, TicketType } from "./IPriceWidget";

const PriceWidget: React.FC<PriceWidgetProps> = ({
    ticketType,
    price,
    priceSuffix,
    text,
    buttonLabel,
    url,
}) => {
    const openUrl = () => {
        if (url) {
            window.open(url, "_blank");
        }
    };

    const hasNonWhiteSpaceContent = (str: string) => str && str.trim() !== "";

    const isBookTicket =
        ticketType === TicketType.BookTicket &&
        hasNonWhiteSpaceContent(price) &&
        hasNonWhiteSpaceContent(buttonLabel) &&
        hasNonWhiteSpaceContent(url);
    const isGetTicket =
        ticketType === TicketType.GetTicket &&
        hasNonWhiteSpaceContent(buttonLabel) &&
        hasNonWhiteSpaceContent(url);
    const isNoTicket =
        ticketType === TicketType.NoTicket && hasNonWhiteSpaceContent(text);

    return (
        <div className="bg-white rounded-2xl">
            <Icon
                name="ornament-h-m5-48"
                svgClass="w-full h-12 rounded-t-2xl"
            />

            {isBookTicket && (
                <div className="pt-3.5 md:pt-4 px-4 pb-4 flex justify-center items-center flex-col ">
                    <div className=" pb-2 md:pb-4 gap-1 flex justify-center items-center">
                        <Text
                            text={price}
                            styles="font-primary-bold md:text-3.5xl text-xl leading-[39.62px]"
                        />
                        {hasNonWhiteSpaceContent(priceSuffix) && (
                            <Text
                                text={`${priceSuffix}`}
                                styles="font-primary-regular text-sm md:text-base"
                            />
                        )}
                    </div>
                    {hasNonWhiteSpaceContent(text) && (
                        <div
                            className="font-primary-regular text-sm md:text-base text-center text-[#4B4B4B] mb-4"
                            dangerouslySetInnerHTML={{ __html: text }}
                        ></div>
                    )}
                    <Button
                        title={buttonLabel}
                        size="big"
                        arrows={false}
                        onclick={url ? openUrl : undefined}
                        spanStyle="!px-0 relative top-[1px]"
                        styles="py-2 px-4 lg:py-3 lg:px-6 w-full flex justify-center text-sm
                                            lg:text-base font-primary-semibold justify-items-center rounded-lg"
                    >
                        <Icon
                            name="ticket"
                            wrapperClass=""
                            svgClass="ltr:mr-2 rtl:ml-2"
                        />
                    </Button>
                </div>
            )}
            {isGetTicket && (
                <div className="p-4 flex justify-center items-center flex-col">
                    {hasNonWhiteSpaceContent(text) && (
                        <div
                            className="font-primary-regular text-sm md:text-base text-center mb-4"
                            dangerouslySetInnerHTML={{ __html: text }}
                        ></div>
                    )}
                    <Button
                        title={buttonLabel}
                        size="big"
                        arrows={false}
                        onclick={url ? openUrl : undefined}
                        spanStyle="!px-0 relative top-[1px]"
                        styles="py-2 px-4 lg:py-3 lg:px-6 w-full flex justify-center text-sm
                                            lg:text-base font-primary-semibold justify-items-center rounded-lg"
                    >
                        <Icon
                            name="ticket"
                            wrapperClass=""
                            svgClass="ltr:mr-2 rtl:ml-2"
                        />
                    </Button>
                </div>
            )}
            {isNoTicket && (
                <div className="px-4 pb-4 pt-3.5 flex justify-center items-center w-full">
                    <Icon
                        name="ticket"
                        svgClass="mr-4"
                        wrapperClass="fill-themed"
                    />
                    <div
                        className="font-primary-bold text-base md:text-lg"
                        dangerouslySetInnerHTML={{ __html: text }}
                    ></div>
                </div>
            )}
        </div>
    );
};

export default PriceWidget;

