import React, { useMemo, useState } from "react";
import { IFilterSection } from "./IFilterSection";
import { useResize } from "src/main/util/hooks/useResize";
import DesktopFilter from "./DesktopFilter/DesktopFilter";
import MobileFilter from "./MobileFilter/MobileFilter";

export interface IFilteredList {
    [key: string]: Array<string>;
}
export interface ISelectedFilterObj {
    [key: string]: Array<string>;
}

// Todo: calendar  react-calendar
const FilterSection = ({ filters, cta }: IFilterSection) => {
    const { isMobile } = useResize();
    const [selectedFilterButton, setSelectedFilterButton] = useState<number>(0);
    const [filteredSearch, setFilteredSearch] = useState<IFilteredList>({});
    const [selectedFilterObj, setSelectedFilterObj] =
        useState<ISelectedFilterObj>({});

    const getSelectedFilterList = (id: string | number) =>
        filters.find((el) => el.id === id)?.list || [];

    useMemo(() => {
        selectedFilterButton &&
            setFilteredSearch({
                selectedFilterButton:
                    getSelectedFilterList(selectedFilterButton),
            });
    }, [filters, selectedFilterButton]);

    const handleSearch = (idList: string | number, keyword: string) => {
        if (keyword.length) {
            const result = filters
                .find((el) => el.id === idList)
                ?.list?.filter((el) =>
                    el.toLocaleLowerCase().includes(keyword.toLocaleLowerCase())
                );

            setFilteredSearch({ [idList]: result || [] });
        } else {
            setFilteredSearch({ [idList]: getSelectedFilterList(idList) });
        }
    };
    const handleCheckFilter = (idList: string | number, val: string) => {
        if (selectedFilterObj[idList]?.length) {
            setSelectedFilterObj({
                [idList]: [...selectedFilterObj[idList], val],
            });
        } else {
            setSelectedFilterObj((prev: any) => ({ ...prev, [idList]: [val] }));
        }
    };

    const handleSubmit = () => {
        console.log(selectedFilterObj);
        // cta?.targetInNewWindow
        // ? () => {
        //       window.open(cta?.link, "_blank");
        //   }
        // : () => {
        //       window.open(cta?.link, "_self");
        //   }
    };

    return (
        <>
            <section>
                {(!isMobile && (
                    <DesktopFilter
                        filters={filters}
                        filteredSearch={filteredSearch}
                        setSelectedFilterButton={setSelectedFilterButton}
                        selectedFilterButton={selectedFilterButton}
                        handleSearch={handleSearch}
                        handleSubmit={handleSubmit}
                        handleCheckFilter={handleCheckFilter}
                        cta={cta}
                    />
                )) || (
                    <>
                        <MobileFilter
                            filters={filters}
                            filteredSearch={filteredSearch}
                            setSelectedFilterButton={setSelectedFilterButton}
                            selectedFilterButton={selectedFilterButton}
                            handleSearch={handleSearch}
                            handleSubmit={handleSubmit}
                            handleCheckFilter={handleCheckFilter}
                            cta={cta}
                        />
                    </>
                )}
            </section>
        </>
    );
};

export default FilterSection;

