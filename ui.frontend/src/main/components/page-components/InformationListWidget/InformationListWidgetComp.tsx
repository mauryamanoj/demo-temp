/* eslint-disable max-len */
import React from "react";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";

import Text from "../../common/atoms/Text/Text";
import Icon from "../../common/atoms/Icon/Icon";
import Collapsible from "../../common/atoms/Collapsible/Collapsible";
import { InformationListWidgetProps } from "./IInformationListWidget";
import {
    FormatOpeningHours,
    isEventInProgress,
} from "./Components/FormattedOpeningHours";
import InformationField from "./Components/InformationField";
import CategoriesTagsInline from "src/main/components/common/organisms/CategoriesTagsInline/CategoriesTagsInline";

dayjs.extend(utc);
dayjs.extend(timezone);

const InformationListWidget: React.FC<InformationListWidgetProps> = ({
    informationLabel,
    locationLabel,
    durationLabel,
    agesLabel,
    typeLabel,
    accessibilityLabel,
    languageLabel,
    locationValue,
    durationValue,
    agesValue,
    typeValue,
    accessibilityValue,
    languageValue,
    openingHoursLabel,
    openingHoursValue,
    openNowLabel,
    closedNowLabel,
    toLabel,
    idealForLabel,
    categoriesTags,
    sameTimeAcrossWeek,
    startDate,
    endDate,
}) => {
    const isOpenHoursExist = openingHoursLabel && openingHoursValue?.length > 0;
    const currentDate = dayjs();
    const isExpired = currentDate.isAfter(dayjs(endDate), "day");
    const hasDates = !!(startDate && endDate);

    const displayOpeningHoursStatus = () => {
        if (isOpenHoursExist && openNowLabel && closedNowLabel) {
            if (!!hasDates) if (isExpired) return false;
            return true;
        }
    };
    const formattedOpeningHours = FormatOpeningHours(
        openingHoursValue,
        toLabel || " - ",
        sameTimeAcrossWeek
    );

    const eventInProgress = isEventInProgress(
        openingHoursValue,
        sameTimeAcrossWeek,
        isExpired
    );

    return (
        <>
            <div className="rounded-2xl bg-white md:mt-8">
                <Icon
                    name="ornament-h-g4-dark-48"
                    svgClass="w-full h-12 rounded-t-2xl"
                />
                <div className="p-4">
                    <div>
                        <Text
                            text={informationLabel}
                            styles="font-primary-bold text-xs md:text-sm uppercase pb-2"
                        />

                        <InformationField
                            label={locationLabel}
                            value={locationValue}
                            icon={"location"}
                        />
                        {durationValue && (
                            <InformationField
                                label={durationLabel}
                                value={`${durationValue}`}
                                icon={"duration"}
                            />
                        )}
                        {agesValue && (
                            <InformationField
                                label={agesLabel}
                                value={agesValue}
                                icon={"ages"}
                            />
                        )}

                        <InformationField
                            label={typeLabel}
                            value={typeValue}
                            icon={"group"}
                        />
                        <InformationField
                            label={accessibilityLabel}
                            value={accessibilityValue}
                            icon={"accessibility"}
                            suffix="&"
                            isMulti={true}
                        />
                        <InformationField
                            label={languageLabel}
                            value={languageValue}
                            icon={"languages"}
                            suffix="&"
                            isMulti={true}
                        />
                    </div>

                    {idealForLabel &&
                        categoriesTags?.length > 0 && (
                            <div className="mt-6">
                                <Text
                                    text={idealForLabel}
                                    styles="font-primary-bold text-xs md:text-sm uppercase pb-4"
                                />
                                <CategoriesTagsInline max={3} tags={categoriesTags} />
                            </div>
                        )}

                    {isOpenHoursExist && (
                        <div className="mt-6">
                            <Collapsible
                                title={
                                    <div className="flex pb-2">
                                        <Text text={openingHoursLabel} />
                                        {displayOpeningHoursStatus() && (
                                            <Text
                                                text={eventInProgress ? openNowLabel : closedNowLabel}
                                                styles={eventInProgress ? "text-success ltr:pl-1 rtl:pr-1" : "text-danger ltr:pl-1 rtl:pr-1"}
                                            />
                                        )}
                                    </div>
                                }
                                className="font-primary-bold text-xs md:text-sm uppercase"
                                children={formattedOpeningHours}
                            />
                        </div>
                    )}
                </div>
            </div>
        </>
    );
};

export default InformationListWidget;

