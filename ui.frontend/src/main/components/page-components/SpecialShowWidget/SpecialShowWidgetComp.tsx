import React from "react";

import Text from "../../common/atoms/Text/Text";
import Icon from "../../common/atoms/Icon/Icon";
import { SpecialShowWidgetProps } from "./ISpecialShowWidget";

const SpecialShowWidgetComp: React.FC<SpecialShowWidgetProps> = ({
    title,
    data,
}) => {
    const hasNonWhiteSpaceContent = (str: string) => {
        // Replace &nbsp; with regular space and then trim
        const cleanedStr = str?.replace(/&nbsp;/g, ' ').replace(/<[^>]*>/g, '').trim();
        return cleanedStr !== "";
      };

    return (
        <>
            { (hasNonWhiteSpaceContent(title) || data?.length > 0 ) &&
                <div className=" rounded-2xl bg-white">
                    <Icon
                        name="ornament-h-g4-dark-48"
                        svgClass="w-full h-12 rounded-t-2xl"
                    />
                    <div className="p-4">
                        {title && (
                            <Text
                                text={title}
                                styles={
                                    "text-xs md:text-sm font-primary-bold pb-4 uppercase"
                                }
                            />
                        )}
                        {data?.length > 0 &&
                            data?.map((day, i) => (
                                <div
                                    key={i}
                                    className={`flex ${
                                        i < data?.length - 1 && "pb-4"
                                    }`}
                                >
                                    <Text
                                        text={day?.key + ":"}
                                        styles={
                                            "w-6/12 text-sm md:text-base font-primary-regular capitalize truncate"
                                        }
                                    />
                                    <Text
                                        text={day?.value}
                                        styles="w-6/12 text-sm md:text-base font-primary-normal
                                         font-semibold truncate text-right"
                                    />
                                </div>
                            ))}
                    </div>
                </div>
            }
        </>
    );
};

export default SpecialShowWidgetComp;

