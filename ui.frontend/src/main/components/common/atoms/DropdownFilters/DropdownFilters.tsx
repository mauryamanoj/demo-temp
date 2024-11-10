/* eslint-disable max-len */
import React, { useEffect, useState, useRef } from 'react';
import { useResize } from 'src/main/util/hooks/useResize';
import { Checkbox } from '../Checkbox/Checkbox';
import Icon from '../Icon/Icon';
import Text from '../Text/Text';
import { getLanguage } from "src/main/util/getLanguage";


export const DropdownFilters: React.FC<any> = (props) => {
    const {
        filters,
        label,
        onSelect,
        isOpened,
        selectedValues,
        secondLabel,
        hideOrnament,
        clearLabel
    } = props;

    const { isMobile } = useResize();

    const [showFilter, setshowFilter] = useState(isOpened)
    // const [locationNumber, setLocationNumber] = useState(0);
    const ref: any = useRef(null);
    const [selectedTypes, setSelectedTypes] = useState(selectedValues);



    useEffect(() => {
        onSelect(selectedTypes);
    }, [selectedTypes])

    useEffect(() => {
        setSelectedTypes(selectedValues);
    }, [selectedValues])

    const handelClearFilter = () => {
        setSelectedTypes([]);
    }

    function handleChange(event: any, id: any) {
        const value = id;
        const isChecked = event.target.checked;

        if (id == undefined) {
            setSelectedTypes([])
            return;
        }
        if (isChecked) {
            // Add the selected type to the array
            setSelectedTypes((prevSelectedTypes: any) => [...prevSelectedTypes, value]);
        } else {
            // Remove the unselected type from the array
            setSelectedTypes((prevSelectedTypes: any) =>
                prevSelectedTypes.filter((type: any) => type !== value)
            );
        }

        return;
    }

    window.onscroll = () => {
        if (window.scrollY > 400) {
            setshowFilter(false);
        }
    };

    useEffect(() => {
        const handleClickOutside = (event: any) => {
            if (ref.current && !ref.current?.contains(event.target)) {
                document.body.style.position = "initial";
                document.body.style.overflow = "unset";
                (setshowFilter(false));
            }
        };
        document.addEventListener('click', handleClickOutside, true);
        return () => {
            document.removeEventListener('click', handleClickOutside, true);
        };
    }, []);

    const isCheckBoxChecked = (item: any) => {
        let ischecked;
        if (selectedTypes.length == filters.length - 1) {
            return true;
        }
        if (selectedTypes.length) {
            ischecked = selectedTypes.includes(item.id)
        } else {
            ischecked = item.id == undefined;
        }
        return ischecked;
    }

    //reutn the label of the selected values
    const getTypeLabels = (selectedTypeId: string[]): string[] => {
        const matchingType = filters.find((filter: any) => filter.id === selectedTypeId);
        return matchingType?.label;
    };
    function getSelectedType() {
        const language = getLanguage();
        const separator = language === 'ar' ? <span className='font-sans'>، </span> : <span>, </span>;

        if (selectedTypes?.length) {
            const typeLabels = selectedTypes.map((selectedType: any) => getTypeLabels(selectedType));
            return (
                <>
                    {typeLabels.map((label: string, index: number) => (
                        <React.Fragment key={index}>
                            {label}
                            {index !== typeLabels.length - 1 && separator}
                        </React.Fragment>
                    ))}
                </>
            );
        } else {
            const objectWithoutId = filters.find((obj: any) => !obj.hasOwnProperty('id'));
            return objectWithoutId.label;
        }
    }
    const selectedTypesString = getSelectedType();

    return (
        <div ref={ref} className="relative">
            <div className={`py-4 mb-4 bg-white  rounded-2xl overflow-hidden shadow-md relative ${isMobile ? 'mx-[20px]' : ''}`}>
                {!hideOrnament ? <Icon name="ornaments-dropdown" svgClass="absolute left-0 rtl:right-0 top-0 h-full" /> : null}

                <div className="flex justify-between px-4">
                    {label ? <>
                        <div className={`${!hideOrnament ? 'ltr:ml-10 rtl:mr-10 cursor-pointer' : 'cursor-pointer'} `}>
                            {label ? <Text text={label}
                                styles="lg:text-sm text-xs uppercase font-primary-bold text-[#000] line-clamp-1" type="h3" /> : null}
                            <div onClick={() => setshowFilter(!showFilter)} className="flex items-center">
                                {true ? <Text text={selectedTypesString}
                                    styles="lg:text-base text-sm font-primary-bold text-theme-100 mt-2 line-clamp-2" type="h3" /> : null}

                                <span className="cursor-pointer">
                                    <Icon name="arrow-down-small"
                                        svgClass={`mx-2 mt-2 rtl:scale-x-[-1] ${showFilter ? 'rotate-180' : 'rotate-0'}`} />
                                </span>
                            </div>
                        </div>
                    </> : null}
                    {clearLabel && selectedTypes.length > 0 && selectedTypes.length < filters.length &&
                        <div className="flex items-center justify-center">
                            <span onClick={handelClearFilter}>
                                <Text text={clearLabel}
                                    styles="text-sm font-primary-semibold text-theme-200 cursor-pointer" type="span" />
                            </span>
                        </div>}
                </div>

            </div>
            <div className={`absolute z-10 w-full shadow-md bg-white rounded-2xl mt-2 ${showFilter ? 'block' : 'hidden'} ${isMobile ? 'mx-[20px] w-[90%]' : ''}`}>

                <div className="flex flex-col">
                    {filters && filters?.map((item: any, i: any) => {
                        return (item.label) && <>
                            {item.label?.trim() || item.id?.trim() ? <div key={i} className="py-4 mx-4 flex items-center">
                                <Checkbox
                                    key={item.id}
                                    checked={isCheckBoxChecked(item)}
                                    labelStyle={'cursor-pointer text-base font-primary-regular line-clamp-1 ltr:pl-[0.15rem] rtl:pr-[0.15rem] hover:cursor-pointer'}
                                    item={item}
                                    defaultChecked={item.checked}
                                    labelText={item.label || item.id}
                                    name={item.label || item.id}

                                    handleChange={(event: any) => handleChange(event, item.id)}
                                    style={`accent-theme-100  relative float-left ltr:mr-[6px]  rtl:ml-[6px] w-[12px] h-[12px] border border-solid  outline-none !rounded-none`}
                                />
                            </div> : null}

                            {filters && (i + 1 != filters.length) ? <hr className='text-[#ddd8d8] m-0 p-0' /> : null}
                        </>
                    }
                    )}
                </div>
            </div>
        </div>
    );
};
