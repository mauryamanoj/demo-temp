/* eslint-disable max-len */
import React, { useMemo, useState } from "react";
import InputText from "../../../common/atoms/InputText/InputText";
import Icon from "../../../common/atoms/Icon/Icon";
import Calendar from "react-calendar";
import { getLanguage } from "src/main/util/getLanguage";

export type IFilterDropdownButton = {
    id: number;
    title: string;
    placeholder: string;
    search?: {
        enabled: boolean;
        searchPlaceholder: string;
        searchLabel: string;
    };
    type?: "list" | "calendar";
    list?: Array<string>;
    containerStyle?: string;
    selected?: boolean;
    setSelectedFilterButton?: (val: number) => void;
    handleSearch: (id: number | string, keyword: string) => void;
    handleCheckFilter: (id: number | string, val: string) => void;
};

const FilterDropdownButton = ({
    id,
    title,
    placeholder,
    type = "list",
    list = [],
    search,
    selected = false,
    containerStyle,
    setSelectedFilterButton = () => null,
    handleSearch = () => null,
    handleCheckFilter = () => null,
}: IFilterDropdownButton) => {
    const [isDropdownOpen, setDropdownOpen] = useState(false);
    const [keyword, setKeyword] = useState<string>("");
    const [selectedDates, setSelectedDates] = useState<any[]>([]);

    useMemo(() => {
        id && setDropdownOpen(true);
        () => setDropdownOpen(false);
    }, [id, setDropdownOpen]);

    useMemo(() => {
        handleSearch(id, keyword);
    }, [keyword]);

    const isDateBetween = (checkDate: Date) => {
        return checkDate > selectedDates[0] && checkDate < selectedDates[1];
    };

    const handleDateChange = (date: any) => {
        setSelectedDates(date);
    };

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const titleClassName = ({ _, date, view }: any) => {
        let className = "";
        if (view === "month") {
            isDateBetween(date)
                ? (className += " bg-gray-200")
                : (className += "");
        }
        return className;
    };

    return (
        <>
            <div
                className={`relative ${containerStyle}`}
                onClick={() => setSelectedFilterButton(id)}
            >
                <div className="flex flex-col h-full justify-items-center justify-center">
                    <div className="text-black text-sm font-primary-bold uppercase leading-snug">
                        {title}
                    </div>
                    <div className="self-stretch justify-start items-center gap-1 inline-flex">
                        <div className="flex gap-2 justify-center items-center text-neutral-500 text-base font-primary-regular capitalize">
                            {placeholder}
                            <Icon
                                name="chevron"
                                svgClass={`${
                                    isDropdownOpen && selected
                                        ? "-rotate-90"
                                        : "rotate-90"
                                } duration-300 w-[16px] h-[16px] select-ele`}
                            />
                        </div>
                    </div>
                </div>
                {selected && isDropdownOpen && (
                    <div
                        className="absolute min-w-full max-h-[397px] overflow-y-scroll left-0 top-[86px] flex flex-col
                                   z-40 bg-white p-2 rounded-2xl shadow px-2"
                    >
                        {search && search.enabled && (
                            <InputText
                                type="text"
                                style="
                                    rounded-[8px] w-full border px-[16px]
                                    text-[#4B4B4B] pl-8 pr-4 py-2
                                    border-theme-200 focus:outline-none focus:border-theme-200 focus:border-1"
                                placeHolder={search.searchLabel}
                                value={keyword}
                                onChange={(event: any) =>
                                    setKeyword(event?.target?.value)
                                }
                                id={id.toString()}
                                htmlFor=""
                                prefixIcon={
                                    <Icon
                                        name="search-icon"
                                        wrapperClass="absolute inset-y-0 left-0 flex items-center pl-2"
                                    />
                                }
                            />
                        )}

                        {(type === "list" &&
                            list.length &&
                            list.map((item, index) => (
                                <InputText
                                    key={index}
                                    type={"checkbox"}
                                    containerStyle="flex gap-2 border-b border-neutral-400 p-4"
                                    labelStyle="grow shrink basis-0 text-base font-primary-regular order-2 capitalize"
                                    style="accent-theme-200 border order-1"
                                    label={item}
                                    onChange={(e: any) =>
                                        handleCheckFilter(id, e.target.value)
                                    }
                                    value={item}
                                    id={"search"}
                                    htmlFor=""
                                />
                            ))) || (
                            <>
                                <Calendar
                                    className="p-4 border-none"
                                    showDoubleView
                                    selectRange={true}
                                    tileClassName={titleClassName}
                                    value={selectedDates as any}
                                    nextLabel={null}
                                    prevLabel={null}
                                    locale={getLanguage()}
                                    next2Label={
                                        <Icon
                                            name="chevron"
                                            wrapperClass="flex items-center justify-center h-full hover:bg-white"
                                            svgClass="w-[16px] h-[16px]"
                                        />
                                    }
                                    prev2Label={
                                        <Icon
                                            name="chevron"
                                            wrapperClass="flex items-center justify-center h-full hover:bg-white"
                                            svgClass="-rotate-180 w-[16px] h-[16px]"
                                        />
                                    }
                                    onChange={handleDateChange}
                                />
                            </>
                        )}
                    </div>
                )}
            </div>
        </>
    );
};

export default FilterDropdownButton;

