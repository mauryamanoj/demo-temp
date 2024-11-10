/* eslint-disable max-len */
import React, { useEffect, useRef, useState } from 'react';
import Picture from '../Picture/Picture';
import Text from '../Text/Text';

export interface Root {
    searchData: any
    showSearchBox: boolean
    isMobile: boolean
    searchPagePath: string
    param: string
    ref?: any //todo: change this
    viewAllResultsLabel: string
    showCategory?: boolean
    searchPage?: any
    isScrollActive?: boolean
    setShowSearchBox?: any,
    trending?: any // not ready from the backend side
}

const rounded = `rounded-[8px] lg:rounded-[16px]`;
var imagePath = "";
const Autocomplete: React.FC<Root> = (props) => {
    const { 
        searchData,
        searchPagePath,
        param,
        showCategory = true,
        isScrollActive,
        setShowSearchBox 
    } = props;
    const wrapperRef = useRef(null);
    useOutsideAlerter(wrapperRef);
    const [searchkeysArr, setsearchkeysArr]: any = useState([]);

    useEffect(() => {
        if (setShowSearchBox) {
            props.setShowSearchBox(true);
        }

    }, [isScrollActive]);

    function resultClickHandler(item: any) {
        setData(item.title);
        window.location.href = searchPagePath + '?search=' + item;
    }

     
    function setData(searchTerm: string) {
        const searchkeys: any = searchkeysArr;
            if (searchTerm) {
                searchkeys.push(searchTerm);
                setsearchkeysArr(searchkeys);
                window.localStorage.setItem("searchkeys", searchkeysArr.join());
            }
            if (searchkeys.length > 5) {
                searchkeysArr.shift();
                searchkeysArr.reverse();
                setsearchkeysArr(searchkeysArr);
                window.localStorage.setItem("searchkeys", searchkeysArr.join());
            }
    }


    function listClickHandler(item: any) {
        setData(item.title);
        window.location.href = item.url;
    }


    function getHighlightedText(text: string, highlight: string) {
        // Split on highlight term and include term into parts, ignore case
        if (text) {
            const parts = text.split(new RegExp(`(${highlight?.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")})`, 'gi'));
            return <span className="lg:text-lg text-sm text-[#787878] font-primary-bold line-clamp-1 pb-1"> {parts?.map((part, i) =>
                <span key={i} style={part.toLowerCase() === highlight.toLowerCase() ? { fontWeight: 'bold', color: '#000' } : {}}>
                    {part}
                </span>)
            } </span>;
        } else {
            return text;
        }

    }

    function useOutsideAlerter(ref: any) {
        useEffect(() => {
            /**
             * Alert if clicked on outside of element
             */
            function handleClickOutside(event: any) {

                if (ref.current && !ref.current.contains(event.target)) {
                    if (setShowSearchBox) {
                        props.setShowSearchBox(true);

                    }
                }

            }
            // Bind the event listener
            document.addEventListener("mousedown", handleClickOutside);
            return () => {
                // Unbind the event listener on clean up
                document.removeEventListener("mousedown", handleClickOutside);
            };
        }, [ref]);
    }

    function desktopList(item: any, key: number) {
        return (
            <>

                {typeof item != 'object' ? <div
                >
                    <div className="cursor-pointer flex items-top" key={key}
                        onClick={() => resultClickHandler(item)}>
                        <div className='px-[24px]'>
                            <Text text={getHighlightedText(item, param)}
                                styles="lg:text-lg text-lg text-[#787878] font-primary-semibold line-clamp-1" type="p" />
                        </div>

                    </div>
                </div> :
                    <div>
                        <div className="cursor-pointer flex items-top"
                            key={key}
                            onClick={() => listClickHandler(item)}>
                            {item && item.featureImage ? <Picture
                                image={imagePath + item.featureImage}
                                pictureClassNames="max-w-[90px] max-h-[90px] lg:h-[90px] h-[70px] lg:w-[90px] w-[70px]"
                                containerClassName="max-w-[90px] max-h-[90px] lg:h-[90px] h-[70px] lg:w-[90px] w-[70px]"
                                imageClassNames={`max-w-[90px] max-h-[90px] lg:h-[90px] h-[70px] lg:w-[90px] w-[70px] rounded-2xl object-cover`}
                                alt={item.title ? item.title : ''}
                            /> : null}
                            <div className='px-[24px]'>
                                {showCategory ? <Text text={item.categories[0]}
                                    styles="text-sm uppercase font-primary-semibold text-[#000] line-clamp-1" type="span" /> : null}
                                <Text text={getHighlightedText(item.title, param)}
                                    styles="text-lg text-[#787878] font-primary-semibold line-clamp-1" type="p" />
                                <Text text={getHighlightedText(item.description, param)}
                                    styles="text-base text-[#787878] font-primary-regular line-clamp-1" type="p" />
                            </div>

                        </div>
                    </div>}
            </>

        );
    }

    useEffect(() => {
        if (process.env.NODE_ENV === 'development') {
            imagePath = 'https://www.visitsaudi.com';
        } else {
            imagePath = '';
        }
    }, []);



    return (
        <div ref={wrapperRef} >
            {searchData && searchData.length > 0 ?
                <div className={`bg-white absolute top-26 mt-[8px]
           w-full flex flex-col gap-6 lg:px-6 px-0 py-4 lg:max-h-[456px] max-h-[856px] scrollbar !scrollbar-thumb-theme-200 overflow-y-auto ${rounded}`}>
                    {props?.children}
                    {searchData.map((item: any, key: number) => desktopList(item, key))}
                </div>

                :
                null}
        </div>

    );
};

export default Autocomplete;
