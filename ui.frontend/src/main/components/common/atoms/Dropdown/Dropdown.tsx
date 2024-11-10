/* eslint-disable max-len */
import React, { useState, ChangeEvent, useRef, useEffect, useLayoutEffect } from 'react';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
export interface optionProps {
    id: any,
    title: string,
    src?: string,
    icon?: string,
}
type DropdownProps = {
    options: optionProps[];
    className?: string;
    wrapperClassName?: string;
    onChange?: (value: number) => void;
    handleChange?: (value: optionProps) => void;
    selectedOption?: any;
    isWithIconImg?: boolean;
    isWithIconSvg?: boolean;
    labelText?: string
    labelStyle?: string
    placeHolder?: string
    imgClassName?: string;
    inputClassName?: string;
};

const Dropdown: React.FC<DropdownProps> = ({
    options,
    className,
    wrapperClassName,
    onChange,
    handleChange,
    selectedOption,
    isWithIconImg,
    isWithIconSvg,
    labelText,
    labelStyle,
    placeHolder,
    imgClassName,
    inputClassName
}) => {
    const [isOpened, setIsOpened] = useState(false);
    const [inputVal, setInputVal] = useState('');

    const dropdownRef = useRef<HTMLDivElement>(null);
    const [isTypingAccured, setIsTypingAccured] = useState(false);


    const [selectedObjectId, setSelectedObjectId] = useState(selectedOption);
    const [selectedObject, setSelectedObject] = useState<optionProps | undefined>(undefined);

    useEffect(() => {
        setSelectedObjectId(selectedOption);
        setSelectedObject(options.filter(option => option.id === selectedOption)[0]);
    }, [selectedOption]);

    useEffect(() => {
        setTheSelectedOption();
    }, [selectedObjectId]);

    useEffect(() => {
        selectedObject ? setSelectedObjectId(selectedObject.id) : null;
        setTheSelectedOption();
        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };

    }, []);



    const handleOpen = (e: any) => {
        if (e.target.closest === "INPUT") {
            setIsOpened(true);
            dropdownRef.current?.querySelector('input')?.focus();
        } else {
            setIsOpened(!isOpened);
        }
    };

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
        setInputVal(e.target.value);
        setIsTypingAccured(true);
    };

    const handleSelection = (option: optionProps) => {
        setInputVal(option.title);
        setSelectedObjectId(option.id);
        setIsOpened(false);
        if (onChange) { onChange(option.id); }
        if (handleChange) { handleChange(option); }
    };

    const handleClickOutside = (e: MouseEvent) => {
        if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
            setIsOpened(false);
        }
    };





    useEffect(() => {
        setTheSelectedOption(); // reset the selected if no option was chosen
        setIsTypingAccured(false);
    }, [isOpened]);


    const setTheSelectedOption = () => {
        if (selectedObjectId) {
            const selected = options.filter((option) => option.id === selectedObjectId)[0];
            if (selected && selected.title) {
                setInputVal(selected.title);
            }
            //if (selected && onChange) { onChange(selected.id); }

        } else {
            setInputVal('');
        }
    };


    // Sort Countries alphabetically
    function sortBycontactLabel(arr: any) {
        if (arr && arr.length != 0) {
            return arr?.sort((a: any, b: any) => {
                const nameA = a?.title?.toUpperCase(); // Ignore case
                const nameB = b?.title?.toUpperCase();

                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }

                // Names are equal, compare by other criteria if needed
                return 0;
            });
        }

    }

    // Filter the options based on the input value
    const filteredOptions = options.sort().filter((option) => {
        if (isTypingAccured) {
            return option.title?.toLowerCase().includes(inputVal.toLowerCase()) || null;
        } else {
            return option.title;
        }
    }
    );
    const selectedImageSrc = (selectedObject?.src || selectedObject?.src || filteredOptions.filter((opt: any) => opt.id === inputVal)[0]?.src);
    return (
        <>
            {labelText && <label className={labelStyle}>{labelText}</label>}

            <div className={`relative flex-1 ${className ? className : ''}`} ref={dropdownRef}>
                <div className={`relative h-10 px-4 py-2 bg-white rounded-lg border border-gray-50 cursor-pointer flex items-center gap-2
            ${isWithIconImg && selectedImageSrc ? 'ltr:ml-5 rtl:mr-5 ltr:border-l-0 rtl:border-r-0 ltr:rounded-l-none rtl:rounded-r-none' : ''}
            ${wrapperClassName}`} onClick={handleOpen}>

                    {isWithIconSvg && selectedObject?.icon && <div>
                        <Icon name={selectedObject.icon} isFetch wrapperClass={`w-4 svg-max-w-full block fill-themed-200`} />
                    </div>}

                    {isWithIconImg && (selectedObject?.src || selectedObject?.src || selectedImageSrc) ?
                        <div className={`rounded-[8px] ltr:rounded-r-none rtl:rounded-l-none ltr:border-r-0 rtl:border-l-0 border border-[#AAAAAA] !height-[40px]
                 absolute top-[-1px] ltr:left-[-20px] rtl:right-[-20px] p-[12.4px] ${imgClassName}`}>

                            <img width="20" height="13" className={`max-w-[24px] max-h-[13px]`} src={selectedImageSrc} />

                        </div> : null}

                    <input
                        type="text"
                        className={` rtl:font-secondary-bold w-full h-full outline-none cursor-pointer ${inputClassName ? inputClassName : ""} ${isWithIconImg && (selectedImageSrc) ? 'ltr:pl-[4px] rtl:pr-[4px]' : ''}`}
                        value={inputVal}
                        onChange={handleInputChange}
                        placeholder={placeHolder}
                    />
                    <Icon name="chevron" svgClass={`${isOpened ? '-rotate-90' : 'rotate-90'} duration-300 w-3 h-3 absolute ltr:right-4 rtl:left-4 top-1/3 z-0 select-ele`} wrapperClass='w-2.5' />
                </div>

                <ul className={`bg-white absolute z-20 w-full top-[100%] rounded-2xl overflow-x-hidden transition-all duration-300 ${!isOpened && 'h-0'} max-h-80 overflow-y-scroll`}>
                    {filteredOptions?.map((option: any, index: any) => (
                        <li
                            key={option?.id}
                            onClick={() => handleSelection(option)}
                            className={`select-none p-4 ${index < filteredOptions?.length - 1 ? 'border-b' : ''
                                } border-gray-100 cursor-pointer hover:bg-slate-50 ${selectedObjectId == option.id && 'text-theme-200'}`}>
                            <div className={(option?.src || isWithIconSvg) ? 'flex gap-2 content-center items-center' : ''}>
                                {option?.src ? <img src={option?.src} width="45" height="30" className="max-w-[24px] max-h-[13px]" /> : null}
                                {isWithIconSvg && <Icon name={option.icon} isFetch wrapperClass={`[&_svg]:w-full [&_svg]:h-full w-4 block ${selectedObjectId == option.id ? 'fill-themed-200' : 'fill-gray-350'}`} />}
                                <span className='flex-1'>{option?.title}</span></div>
                        </li>
                    ))}
                </ul>
            </div>
        </>
    );
};

export default Dropdown;
