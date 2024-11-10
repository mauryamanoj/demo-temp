import React from "react";
import { IDestinationGuideCardProps } from "../IDestinationGuideProps";
import Text from "src/main/components/common/atoms/Text/Text";
import Button from "src/main/components/common/atoms/Button/Button";

const ItemContent: React.FC<IDestinationGuideCardProps> = ({ type, items }) => {

    return (
        <div className={`flex flex-col gap-6`}>
            {items?.map((item, index) => (
                <div className="flex items-start gap-6" key={index}>
                    {"logo" in item && item?.logo?.desktopImage && (
                        <img
                            alt={item.title}
                            src={item?.logo?.desktopImage}
                            className=" w-8 h-8 mr-2"
                        />
                    )}
                    <div>
                        {item?.title && (
                            <Text
                                text={item?.title}
                                styles="font-primary-semibold text-base text-black w-full truncate capitalize"
                            />
                        )}
                        {"subTitle" in item && (
                            <Text
                                text={item?.subTitle}
                                styles="font-primary-semibold text-[#4B4B4B] text-xs leading-[14.7px]"
                            />
                        )}
                        {"link" in item && item?.link.text && item?.link?.url && (
                            <Button
                                title={item?.link.text}
                                styles={`!px-0 !py-0 mx-0 my-0 border-none`}
                                spanStyle="font-primary-bold text-sm themed !px-0 truncate w-full"
                                transparent
                                arrows={false}
                                onclick={() => {
                                    window.open(
                                        item?.link?.url,
                                        !!item?.link?.targetInNewWindow
                                            ? "_blank"
                                            : "_self"
                                    );
                                }}
                            />
                        )}
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ItemContent;

