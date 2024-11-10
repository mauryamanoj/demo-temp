import React from "react";
import Wrapper from "src/main/components/common/HOC/Wrapper/Wrapper";
const InformationListWidgetComp = React.lazy(
    () => import("./InformationListWidgetComp")
);

const InformationListWidget = (props: any) => {
    const {
        accessibilityLabel,
        accessibilityValue,
        languageLabel,
        languageValue,
        openingHoursLabel,
        openingHoursValue,
        informationLabel,
        locationLabel,
        durationLabel,
        agesLabel,
        agesValueFrom,
        agesValueTo,
        typeLabel,
    } = props;
    const isAccessibilityExist =
        accessibilityLabel && accessibilityValue?.length > 0;
    const isLanguagesExist = languageLabel && languageValue?.length > 0;
    const isOpenHoursExist = openingHoursLabel && openingHoursValue?.length > 0;
    const isInformationListExist =
        informationLabel &&
        (locationLabel ||
            durationLabel ||
            (agesLabel && (agesValueFrom || agesValueTo)) ||
            typeLabel ||
            isAccessibilityExist ||
            isLanguagesExist ||
            isOpenHoursExist);
    return (
        <Wrapper
            className={isInformationListExist ? "min-h-[143px] !mb-4" : "min-h-0 !mb-0"}
        >
            {isInformationListExist && (
                <InformationListWidgetComp
                    { ...props}
                />
            )}
        </Wrapper>
    );
};

export default InformationListWidget;

