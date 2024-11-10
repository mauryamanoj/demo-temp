/* eslint-disable max-len */
import React, { useEffect, useState } from 'react';
import Button from '../Button/Button';
import { Checkbox } from '../Checkbox/Checkbox';
import Icon from '../Icon/Icon';
import Text from '../Text/Text';

export const Filters: React.FC<any> = (props) => {
    const {
        filters,
        clearLabel,
        label,
        handelClearAllFilter,
        isMobile,
        handleApply,
        searchLocationVal,
        applyLabel,
        isExpanded,
        type,
        onSelect,
        sortFilterLabel,
        checkboxHandleChange,
        clearAllButton,
        selectedValues
    } = props;


    const [showFilter, setshowFilter] = useState(isExpanded)
    const [localFilter, setLocalFilter] = useState(filters);
    const [selectedCounter, setSelectedCounter] = useState(0);

    const [values, setValues] = useState<any>(selectedValues);

    useEffect(() => {
        setValues(selectedValues);
    }, [selectedValues])


    useEffect(() => {
        if (type == 'location') {
            const searchTerm = typeof searchLocationVal === 'string' ? searchLocationVal.toLowerCase() : '';
            const filteredCities = filters?.filter((city: any) => {
                const cityCode = city?.value?.toLowerCase();
                return typeof cityCode === 'string' && cityCode.includes(searchTerm);
            });

            if (filteredCities) {
                setLocalFilter(filteredCities);
            }
        }
    }, [searchLocationVal]);




    useEffect(() => {
        onSelect(values);
        handleFilterCounter();
    }, [values]);

    const handelClearFilter = () => {
        setValues([]);
    }
    const handleFilterCounter = () => {
        if (values) {
            setSelectedCounter(values.length);

        }
    }

    const handleChange = (e: React.MouseEvent<HTMLInputElement> | undefined, code: string) => {
        if (code) {
            if (!values.includes(code)) {
                setValues((prevValues: string[]) => [...prevValues, code]);
            } else {
                setValues((prevValues: string[]) => prevValues.filter(value => value !== code));
            }
        }else{
            setValues([])
        }
    };

    const isCheckBoxChecked = (item: { code: string, value: string }) => {
        let ischecked;
        // if (values.length == localFilter.length - 1) {
        //     return true;
        // }
        if (values.length) {
            ischecked = values.includes(item.code)
        } else {
            ischecked = item.code == undefined;
        }
        return ischecked;
    }

    return (
        <div>
            {sortFilterLabel && isMobile &&
                <div className={`py-6 mb-6 bg-white  rounded-[16px] ${isMobile ? 'mx-[20px]' : ''}`}>
                    <div className="flex justify-between flex-row-reverse px-4">
                        <Checkbox
                            labelText={" "}
                            labelStyle={'line-clamp-1 cursor-pointer text-base font-primary-regular ltr:pl-[0.15rem] rtl:pr-[0.15rem] hover:cursor-pointer'}
                            handleChange={checkboxHandleChange}
                            style={`accent-theme-100 relative float-left mt-[0.15rem] w-[12px] h-[12px]
        border border-solid  outline-none !rounded-none ltr:mr-1 rtl:ml-1`}
                        />
                        <label htmlFor={sortFilterLabel} className="lg:text-base text-xs font-primary-semibold">
                            {sortFilterLabel}
                        </label>
                    </div>
                </div>}

            <div className={`py-6 mb-4 bg-white  rounded-[16px] ${isMobile ? 'mx-[20px]' : ''}`}>
                <div className="flex justify-between px-4">
                    {label ? <>
                        <div className="flex">
                            <Text text={label}
                                styles="lg:text-sm text-xs uppercase font-primary-bold text-[#000]" type="h3" />
                            {selectedCounter != 0 ? <span className="font-primary-bold text-sm h-[22px] w-[22px] rounded-full bg-theme-100 text-white flex items-center justify-center ltr:ml-[8px] rtl:mr-[8px] pt-[1px] mt-[-2px]">  {selectedCounter}</span> : null}

                        </div>
                    </> : null}
                    {clearLabel ?
                        <div className="flex items-center justify-center">
                            {(selectedCounter != 0) ?
                                <span onClick={handelClearFilter}>
                                    <Text text={clearLabel}
                                        styles="text-sm font-primary-bold text-theme-100 cursor-pointer" type="span" />
                                </span> : null}
                            {type != 'content' && !isMobile ? <span onClick={() => setshowFilter(!showFilter)} className="cursor-pointer">
                                <Icon name="arrow-down"
                                    svgClass={`ltr:ml-[24px] rtl:mr-[24px] rtl:scale-x-[-1] ${showFilter ? 'rotate-180' : ''}`} />
                            </span> : null}
                            {type != 'content' && isMobile ? <span onClick={() => setshowFilter(!showFilter)} className="cursor-pointer">
                                <Icon name="arrow-right-16x16"
                                    svgClass={`ltr:ml-[24px] rtl:mr-[24px]  ${showFilter ? '-rotate-90' : 'rotate-90'}`} />
                            </span> : null}
                        </div> : null}
                </div>
                <div className={showFilter ? 'block lg:mt-4 mt-0' : 'hidden'}>
                    <div className="pt-4 mx-4">
                        {props.children}
                    </div>
                    <div className={`flex flex-col gap-4 pt-4 mx-4 ${localFilter && localFilter.length > 8 ? 'h-[456px] overflow-y-auto' : ''} `}>
                        {localFilter && localFilter?.map((item: any, i: any) =>
                            <div key={i}>
                                <div className="pb-4 mx-2 px-[10px] flex items-center">
                                    <Checkbox
                                        key={i}
                                        labelStyle={'line-clamp-1 cursor-pointer text-base font-primary-regular ltr:pl-[0.15rem] rtl:pr-[0.15rem] hover:cursor-pointer'}
                                        item={item}
                                        name={item?.code}
                                        checked={isCheckBoxChecked(item)}
                                        handleChange={handleChange}
                                        style={`accent-theme-100 relative float-left ltr:mr-[6px]  rtl:ml-[6px]
                            border border-solid  outline-none !rounded-none w-[12px] h-[12px]`}
                                    />
                                </div>
                                {localFilter && (i + 1 != localFilter.length) ? <hr className='text-[#ddd8d8] m-0 p-0' /> : null}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            {(clearAllButton || applyLabel) && isMobile &&
                <div className={`bg-white flex space-x-4 pt-4 px-4 gap-4 justify-between lg:mb-0 mb-0 
                 fixed bottom-0 w-full text-center z-10 py-[20px]`}>

                    <>
                        {clearAllButton ? <Button
                            onclick={handelClearAllFilter}
                            title={clearAllButton}
                            arrows={false}
                            spanStyle="!px-0 relative top-[1px]"
                            styles="bg-transparent !text-theme-100 w-full h-[44px] ltr:ml-0 rtl:mr-0 w-full flex justify-center p-2 !text-base font-primary-semibold rounded-lg"
                        >
                        </Button> : null}
                        {applyLabel ? <Button
                            onclick={handleApply}
                            title={applyLabel}
                            arrows={false}
                            spanStyle="!px-0 relative top-[1px]"
                            styles="w-full h-[44px] ltr:ml-0 rtl:mr-0 w-full flex justify-center p-2 !text-base font-primary-semibold rounded-lg !mx-0"
                        >
                        </Button> : null}
                    </>
                </div>}

        </div>
    );
};
