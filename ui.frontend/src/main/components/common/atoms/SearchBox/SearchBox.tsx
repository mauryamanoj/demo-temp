/* eslint-disable max-len */
import React, { useEffect } from "react";
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import { Checkbox } from "../Checkbox/Checkbox";

const SearchBox = (props: any) => {
    const {
        className = "",
        searchPlaceholderLabel,
        handleChange,
        searchForm,
        onKeyDown,
        onKeyUp,
        setIsInputFocused,
        clearInput,
        value,
        withFilterIcon,
        handleFilter,
        tabIndex,
        showClear,
        checkboxHandleChange,
        sortFilterLabel,
        formKey,
        clearInputStyle,
        inputOnClick,
        showSearchBox,
        showFilter,
        smallIcon,
        isMobile,
        localLocations,
        localContentType
    } = props;

    // remove autoFocus on mobile
    useEffect(() => {
        if (showFilter) {
            document.querySelector("input")?.blur();
        }
    }, [showFilter]);


    return (

        <div className={`relative w-full ${!isMobile ? 'bg-white' : ''}
         rounded-[8px] lg:rounded-[16px]`}>

            {!isMobile && !smallIcon ? <Icon name="search-v1"
                svgClass="absolute z-[2] top-6 ltr:ml-[24px] rtl:mr-[24px] rtl:scale-x-[-1]" />
                : null}
            {isMobile || smallIcon ? <Icon name="search-mobile-v1"
                svgClass="absolute top-[12px] ltr:ml-[16px] rtl:mr-[16px] rtl:scale-x-[-1] " /> : null}

            {showClear ? <span onClick={clearInput}
                className={`block absolute top-[14px] z-8 ltr:right-[16px] rtl:left-[16px] ltr:ml-[24px] rtl:mr-[24px] z-10 ${clearInputStyle}`}>
                <Icon name="close-icon" svgClass={`
              block lg:hidden `} /></span> : null}

            <div className="flex">
                <input
                    autoFocus
                    tabIndex={tabIndex}
                    onChange={() => handleChange}
                    placeholder={searchPlaceholderLabel ? searchPlaceholderLabel : ''}
                    className={className}
                    type="text"
                    name="search"
                    onClick={inputOnClick}
                    onKeyDown={(event) => onKeyDown(event)}
                    onKeyUp={(event) => onKeyUp(event)}
                    onFocus={() => {
                        setIsInputFocused(true);
                    }}
                    value={searchForm?.values?.search || value}
                    maxLength={255}
                />
                {withFilterIcon && isMobile ?
                    <span className="relative z-10" onClick={handleFilter}>
                        <Icon name="filter" svgClass="ltr:ml-2 rtl:mr-2 mt-[4px]" />
                        {(localLocations && localLocations.length != 0) || (localContentType && localContentType.length != 0) ? <span className="w-[11px] h-[11px] absolute top-1 ltr:right-[-2px] rtl:left-[-2px] bg-theme-100 rounded-[50px]"></span> : null}
                    </span> : null}
            </div>
            {props.children}
            {!isMobile && sortFilterLabel ?
                <div className="absolute flex items-center z-50 gap-1.5 ltr:right-4 rtl:left-4 top-8  ltr:flex-row-reverse">
                    <Checkbox
                        key={formKey}
                        labelText={sortFilterLabel}
                        labelStyle={'text-base text-gray-350 font-primary-regular cursor-pointer'}
                        handleChange={checkboxHandleChange}
                        style={`accent-theme-100 relative float-left h-[1rem] w-[1rem]
        border border-solid  outline-none !rounded-none cursor-pointer`}
                    />
                </div> : null}

        </div>
    );
};

export default SearchBox;
