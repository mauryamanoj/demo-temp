import React from "react";
import FilterDropdownButton from "./FilterDropdownButton";
import { IFilteredList } from "../FilterSection";
import Button from "../../../common/atoms/Button/Button";
import { Cta } from "../IFilterSection";

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

const DesktopFilter = ({
    cta,
    filters,
    selectedFilterButton,
    filteredSearch,
    setSelectedFilterButton,
    handleSearch,
    handleCheckFilter,
    handleSubmit,
}: Props) => {
    return (
        <>
            <div className="mt-5 w-full h-[81px] rounded-2xl shadow-sm justify-start items-start inline-flex">
                {filters.map(
                    ({ title, placeholder, id, type, search, list }) => (
                        <>
                            <FilterDropdownButton
                                key={id}
                                id={id}
                                title={title}
                                containerStyle={`
                                    grow shrink basis-0 px-6 py-4 h-full rounded-2xl
                                     flex-col justify-start items-start gap-2 inline-flex ${
                                         selectedFilterButton === id
                                             ? "bg-white shadow-sm"
                                             : "shadow-none"
                                     } cursor-pointer`}
                                placeholder={placeholder}
                                search={search}
                                list={filteredSearch[id] || list}
                                type={type}
                                setSelectedFilterButton={
                                    setSelectedFilterButton
                                }
                                selected={id === selectedFilterButton}
                                handleSearch={handleSearch}
                                handleCheckFilter={handleCheckFilter}
                            />
                        </>
                    )
                )}

                <div
                    className="grow shrink basis-0 px-6 py-4 h-full 
                                   flex-col justify-center items-center gap-2 inline-flex"
                >
                    <Button
                        title={cta?.label}
                        arrows={false}
                        size="big"
                        styles="font-primary-semibold justify-center items-center inline-flex capitalize 
                                     w-full"
                        onclick={handleSubmit}
                    />
                </div>
            </div>
        </>
    );
};

export default DesktopFilter;

