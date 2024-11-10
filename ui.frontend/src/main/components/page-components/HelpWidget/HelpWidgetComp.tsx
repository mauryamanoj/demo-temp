import React from "react";

import Button from "../../common/atoms/Button/Button";
import Icon from "../../common/atoms/Icon/Icon";
import Text from "../../common/atoms/Text/Text";

import { IHelpWidgetProps } from "./HelpWidgetCard/IHelpWidgetProps";

const HelpWidgetComp: React.FC<IHelpWidgetProps> = ({
    title,
    description,
    cta,
}) => {
    const buttonSize = "big";
    const hasNonWhiteSpaceContent = (str: string) => str && str.trim() !== "";

    return (
        <div className="bg-white border border-gray-200 rounded-2xl dark:bg-gray-800 dark:border-gray-700">
            <Icon
                name="ornament-h-g4-dark-48"
                svgClass="w-full h-12 rounded-t-2xl"
            />
            <div className="p-4">
                {hasNonWhiteSpaceContent(title) && (
                    <Text
                        text={title}
                        styles="font-primary-bold text-xs md:text-sm uppercase mb-4"
                    />
                )}
                {hasNonWhiteSpaceContent(description) && (
                    <div
                        className="mb-8 text-sm md:text-base font-primary-regular"
                        dangerouslySetInnerHTML={{ __html: description }}
                    ></div>
                )}
                {hasNonWhiteSpaceContent(cta?.text) && hasNonWhiteSpaceContent(cta?.url) && (
                    <Button
                        title={cta?.text}
                        onclick={
                            true
                                ? () =>
                                      window.open(
                                          cta?.url,
                                          true ? "__blank" : undefined
                                      )
                                : undefined
                        }
                        arrows={false}
                        spanStyle={"!px-0 relative top-[1px]"}
                        size={buttonSize}
                        children={
                            <Icon
                                name="question"
                                svgClass={` rtl:rotate-0 ltr:mr-2 rtl:ml-2`}
                            />
                        }
                        styles={`w-full justify-center text-sm p-2 lg:text-base font-primary-semibold justify-items-center`}
                    />
                )}
            </div>
        </div>
    );
};

export default HelpWidgetComp;

