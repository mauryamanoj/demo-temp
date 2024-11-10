import React from "react";

import Text from "../../../common/atoms/Text/Text";
import Icon from "../../../common/atoms/Icon/Icon";
import { FieldProps } from "../IInformationListWidget";

const InformationField = ({
    label,
    value,
    icon,
    isMulti,
    suffix,
}: FieldProps) => {
    if (!label || !value) return null;

    let renderedValue: string;

    if (isMulti) {
        if (Array.isArray(value)) {
            renderedValue = value.join(" & ");
        } else {
            renderedValue = String(value);
        }
    } else {
        renderedValue = `${value}${suffix || ""}`;
    }

    return (
        <div className="py-2 flex">
            <Icon name={icon} svgClass="px-0 ltr:mr-1 rtl:ml-1" />

            <Text
                text={
                    <>
                        <span className="font-primary-regular text-sm md:text-base capitalize">{`${label}:`}</span>
                        <span className="text-sm md:text-base font-primary-semibold capitalize">
                            {renderedValue}
                        </span>
                    </>
                }
                styles={"flex justify-between items-center w-full overflow-hidden whitespace-nowrap text-ellipsis"}
            />
        </div>
    );
};

export default InformationField;
