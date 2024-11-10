import React from "react";
import dayjs from "dayjs";
import "dayjs/locale/ar";

import { getLanguage } from "../../../util/getLanguage";

import Icon from "../../common/atoms/Icon/Icon";
import Text from "../../common/atoms/Text/Text";
import { DateTextProps, DateWidgetProps } from "./IDateWidget";


const lang = getLanguage();

const formatDate = (date: string) => {
    const formattedDate = dayjs(date).locale(lang);
    return {
        day: formattedDate.format("D"),
        month: formattedDate.format("MMM"),
        year: formattedDate.format("YYYY"),
    };
};

const DateText = ({ text, styles }: DateTextProps) => (
    <Text text={text} styles={` ${styles}`} />
);

const hasString = (text: string) => text !== undefined && text.trim() !== "";

const DateWidgetComp: React.FC<DateWidgetProps> = ({
    title,
    startDate,
    endDate,
    comingSoonLabel,
    expiredLabel,
}) => {
    const currentDate = dayjs();
    const isExpired = currentDate.isAfter(dayjs(endDate), "day");
    const isComingSoon = !startDate || !endDate;

    const isSingleDate = dayjs(startDate).isSame(endDate, "day");
    const formattedStartDate = formatDate(startDate);
    const formattedEndDate = formatDate(endDate);

    const getLabel = () => {
        if (startDate && endDate) {
            if (isExpired) {
                return hasString(expiredLabel)
                    ? expiredLabel
                    : hasString(title)
                    ? title
                    : "";
            } else {return hasString(title) ? title : "";}
        } else {
            return hasString(comingSoonLabel)
                ? comingSoonLabel
                : hasString(title)
                ? title
                : "";
        }
    };


    return (
        <div
            className={`flex items-center rounded-2xl px-6 ${
                isExpired
                    ? "bg-gray-100 text-black "
                    : "bg-theme-100 text-white "
            }${isComingSoon ? "justify-center py-8" : isSingleDate ? "justify-between py-4" : "justify-between py-2"}`}
        >
            <DateText
                text={getLabel()}
                styles={`capitalize ${
                    !isComingSoon
                        ? "w-min font-primary-semibold text-sm md:text-base"
                        : "text-xl md:text-3.5xl font-primary-bold md:leading-none"
                }
                    `}
            />
            {!isComingSoon && (
                <div
                    className={`uppercase ${
                        isSingleDate
                            ? "flex"
                            : "flex justify-between items-center"
                    }`}
                >
                    {!isSingleDate ? (
                        <>
                            <div className="text-center">
                                <DateText
                                    text={formattedStartDate?.month}
                                    styles="text-sm md:text-base font-primary-semibold"
                                />

                                <DateText
                                    text={formattedStartDate?.day}
                                    styles=" font-primary-bold text-xl md:text-3xl md:leading-none"
                                />

                                <DateText
                                    text={formattedStartDate?.year}
                                    styles="text-sm md:text-base font-primary-semibold"
                                />
                            </div>
                            <Icon
                                name={
                                    isExpired
                                        ? "blackRightIcon"
                                        : "whiteRightArrow"
                                }
                                svgClass="mx-6 rtl:-scale-x-100"
                            />
                            <div className="text-center">
                                <DateText
                                    text={formattedEndDate?.month}
                                    styles="text-sm md:text-base font-primary-semibold"
                                />

                                <DateText
                                    text={formattedEndDate?.day}
                                    styles=" font-primary-bold text-xl md:text-3xl md:leading-none"
                                />

                                <DateText
                                    text={formattedEndDate?.year}
                                    styles="text-sm md:text-base font-primary-semibold"
                                />
                            </div>
                        </>
                    ) : (
                        <div className="flex items-center">
                            <DateText
                                text={formattedStartDate?.day}
                                styles="text-3xl lg:text-5xl font-primary-bold pr-2 rtl:pl-2 md:leading-snug"
                            />
                            <div>
                                <DateText
                                    text={formattedStartDate?.month}
                                    styles="text-sm md:text-base font-primary-semibold"
                                />
                                <DateText
                                    text={formattedStartDate?.year}
                                    styles="text-sm md:text-base font-primary-semibold"
                                />
                            </div>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default DateWidgetComp;

