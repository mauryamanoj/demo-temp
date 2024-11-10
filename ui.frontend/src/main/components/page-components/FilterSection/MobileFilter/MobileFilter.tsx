/* eslint-disable max-len */
import React, { useMemo, useState } from "react";
import { IFilteredList } from "../FilterSection";
import Button from "../../../common/atoms/Button/Button";
import { Cta } from "../IFilterSection";
import Icon from "../../../common/atoms/Icon/Icon";
import FilterItem from "./FilterItem";
import InputText from "src/main/components/common/atoms/InputText/InputText";
import Calendar from "react-calendar";
import { getLanguage } from "src/main/util/getLanguage";

type Props = {
    cta: Cta;
    filters: Array<any>;
    selectedFilterButton: string | number;
    filteredSearch: IFilteredList;
    setSelectedFilterButton: (val: number) => void;
    handleSearch: (idList: string | number, keyword: string) => void;
    handleCheckFilter: (idList: string | number, val: string) => void;
    handleSubmit: () => void;
};

const MobileFilter = ({
    cta,
    filters,
    selectedFilterButton,
    filteredSearch,
    setSelectedFilterButton,
    handleSearch,
    handleCheckFilter,
    handleSubmit,
}: Props) => {
    const [showItem, setShowItem] = useState<boolean>(false);
    const [selectedFilterInfo, setSelectedFilterInfo] =
        useState<any>(undefined);

    const [keyword, setKeyword] = useState<string>("");
    const [selectedDates, setSelectedDates] = useState<any[]>([]);

    useMemo(() => {
        if (selectedFilterInfo?.id) {
            handleSearch(selectedFilterInfo.id, keyword);
        }
    }, [keyword]);

    useMemo(() => {
        if (filteredSearch[selectedFilterInfo?.id]?.length) {
            setSelectedFilterInfo({
                ...selectedFilterInfo,
                list: filteredSearch[selectedFilterInfo.id],
            });
        }
    }, [filteredSearch]);

    const goBack = () => {
        if (!selectedFilterInfo) {
            setShowItem(false);
            setSelectedFilterButton(0);
        } else {
            setShowItem(true);
            setSelectedFilterInfo(undefined);
            setSelectedFilterButton(0);
        }
    };

    const handleFilterMenuClick = (filter: any) => {
        setSelectedFilterButton(filter.id);
        setSelectedFilterInfo(filter);
    };

    return (
        <>
            <div className="mt-5 w-full rounded-2xl shadow-sm justify-start items-start inline-flex">
                {(showItem && (
                    <>
                        <div
                            className="bg-stone-50 w-full h-full fixed top-0 left-0 z-50
                                        p-4 text-black"
                        >
                            <div className="flex justify-between items-center px-1">
                                <div className="text-3xl font-primary-bold capitalize">
                                    {selectedFilterInfo?.title || "Filters"}
                                </div>
                                <div onClick={goBack}>
                                    <Icon
                                        name="close-filter"
                                        svgClass="ml-auto rtl:mr-auto rtl:ml-0 cursor-pointer"
                                    />
                                </div>
                            </div>
                            <div className="flex-col w-full h-full gap-0 pt-6">
                                <div className="h-5/6 overflow-y-scroll pb-4">
                                    {(!selectedFilterButton &&
                                        filters.map(
                                            ({
                                                title,
                                                placeholder,
                                                id,
                                                type,
                                                list,
                                                search,
                                            }) => (
                                                <>
                                                    <FilterItem
                                                        title={title}
                                                        placeholder={
                                                            placeholder
                                                        }
                                                        type={type}
                                                        list={list}
                                                        search={search}
                                                        id={id}
                                                        onClick={
                                                            handleFilterMenuClick
                                                        }
                                                    />
                                                </>
                                            )
                                        )) ||
                                        (selectedFilterButton && (
                                            <>
                                                {/* rendering items filter checkbox or calendar or simple list */}
                                                {selectedFilterInfo?.search &&
                                                    selectedFilterInfo?.search
                                                        .enabled && (
                                                        <InputText
                                                            type="text"
                                                            style="rounded-[8px] w-full border px-[16px] text-[#4B4B4B] 
                                                                pl-8 pr-4 py-2 border-theme-200 focus:outline-none 
                                                                focus:border-theme-200 focus:border-1"
                                                            placeHolder={
                                                                selectedFilterInfo
                                                                    .search
                                                                    .searchLabel
                                                            }
                                                            value={
                                                                selectedFilterInfo.keyword
                                                            }
                                                            onChange={(
                                                                event: any
                                                            ) =>
                                                                setKeyword(
                                                                    event
                                                                        ?.target
                                                                        ?.value
                                                                )
                                                            }
                                                            id={selectedFilterInfo.id.toString()}
                                                            htmlFor=""
                                                            prefixIcon={
                                                                <Icon
                                                                    name="search-icon"
                                                                    wrapperClass="absolute inset-y-0 left-0 flex 
                                                                                 items-center pl-2"
                                                                />
                                                            }
                                                        />
                                                    )}

                                                {(selectedFilterInfo?.type ===
                                                    "list" &&
                                                    selectedFilterInfo?.list
                                                        .length &&
                                                    selectedFilterInfo?.list.map(
                                                        (
                                                            item: any,
                                                            index: number
                                                        ) => (
                                                            <InputText
                                                                key={index}
                                                                type={
                                                                    "checkbox"
                                                                }
                                                                containerStyle="flex gap-2 border-b border-neutral-400 p-4"
                                                                labelStyle="grow shrink basis-0 text-base font-primary-regular order-2 capitalize"
                                                                style="accent-theme-200 border order-1"
                                                                label={item}
                                                                onChange={(
                                                                    e: any
                                                                ) =>
                                                                    handleCheckFilter(
                                                                        selectedFilterInfo.id,
                                                                        e.target
                                                                            .value
                                                                    )
                                                                }
                                                                value={item}
                                                                id={`checkbox-${index}`}
                                                                htmlFor=""
                                                            />
                                                        )
                                                    )) || (
                                                    <>
                                                        <Calendar
                                                            className="border-none w-full bg-stone-50"
                                                            selectRange={true}
                                                            value={
                                                                selectedDates as any
                                                            }
                                                            nextLabel={null}
                                                            prevLabel={null}
                                                            locale={getLanguage()}
                                                            tileClassName="h-[36px]"
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
                                                            onChange={(
                                                                date: any
                                                            ) =>
                                                                setSelectedDates(
                                                                    date
                                                                )
                                                            }
                                                        />
                                                    </>
                                                )}
                                            </>
                                        ))}
                                </div>

                                <Button
                                    // title={cta?.label}
                                    title="Apply"
                                    arrows={false}
                                    size="big"
                                    styles="font-primary-semibold justify-center 
                                                inline-flex capitalize w-full"
                                />
                            </div>
                        </div>
                    </>
                )) || (
                    <div
                        className="grow shrink basis-0 px-6 py-4 h-full 
                                  flex-col justify-center items-center gap-2"
                    >
                        <div className="font-primary-bold text-sm uppercase leading-snug mb-1">
                            What are you looking for?
                        </div>

                        <Button
                            // title={cta?.label}
                            title={"All saudi, anytime, All categories"}
                            arrows={false}
                            buttonType="type3"
                            size="big"
                            styles="font-primary-semibold
                            justify-center items-center inline-flex capitalize
                            border border-theme-100 hover:border-theme-200 w-full"
                            // onclick={handleSubmit}
                            onclick={() => setShowItem(true)}
                        >
                            <Icon
                                name="filter"
                                wrapperClass="flex items-center justify-center h-full hover:bg-white"
                                svgClass="w-[16px] h-[16px]"
                            />
                        </Button>
                        {/*  */}
                    </div>
                )}
            </div>
        </>
    );
};

export default MobileFilter;

