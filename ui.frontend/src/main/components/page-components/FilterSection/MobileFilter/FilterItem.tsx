import React from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";

type Props = {
    title: string;
    id: number;
    placeholder: string;
    list: string[];
    type: string;
    search?: {
        enabled: boolean;
        searchPlaceholder: string;
        searchLabel: string;
    };
    // setSelectedFilterButton: (val: number) => void;

    onClick: (val: object) => void;
};

const FilterItem = ({ onClick, ...props }: Props) => {
    const { id, title, placeholder } = props;
    return (
        <>
            <div
                className="w-full h-[78px] px-0 py-4 border-b
                         border-neutral-400 justify-start
                          items-start gap-2 inline-flex"
                onClick={() => onClick(props)}
                key={id}
            >
                <div className="grow shrink basis-0 flex-col justify-center items-start inline-flex">
                    <div className="self-stretch text-black text-base font-normal font-primary-regular capitalize">
                        {title}
                    </div>
                    <div className="self-stretch text-neutral-600 text-base font-normal font-primary-regular capitalize">
                        {/* {(list && list[0]) ||
                            (type === "calendar" ? "placeholder" : null)} */}
                        {placeholder}
                    </div>
                </div>
                <Icon
                    name="small-arrow-right"
                    svgClass="child:fill-black w-7 h-7"
                />
            </div>
        </>
    );
};

export default FilterItem;

