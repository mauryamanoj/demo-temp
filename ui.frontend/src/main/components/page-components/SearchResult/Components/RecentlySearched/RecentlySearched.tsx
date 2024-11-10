import React, { useEffect, useState } from 'react';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import Text from 'src/main/components/common/atoms/Text/Text';

const RecentlySearched: React.FC<any> = (props) => {
    const [recentSearchData, setRecentSearchData]: any = useState([]);
    const { isChange, searchPagePath, recentSearchLabel, clearHistory, showHistory, isSearchResult, isMobile } = props;
    const value = props.recentSearchData;
    const rounded = `rounded-[8px] lg:rounded-[16px]`;

    const clearAllHandler = () => {
        window.localStorage.removeItem('searchkeys');
        props.clearAll();
    };

    const removeItem = (event: any, index: number) => {
        event.stopPropagation();

        // remove from UI
        const data = recentSearchData.filter((element: any, i: number) => {
            return i != index;
        });

        window.localStorage.setItem('searchkeys', data.join());
        setRecentSearchData(data);

        if (value.length == 1) {
            props.clearAll();
        }
        // remove from localsorge
        props.clearItemHandler(index);
    };

    const handleLink = (title: string) => {
        window.location.href = searchPagePath + '?search=' + title;
    };

    useEffect(() => {
        setRecentSearchData(value);
    }, [isChange]);

  
    return (
        <>
            {!isSearchResult() && recentSearchData && showHistory && recentSearchData.length !=0 ? 
            <div className={`bg-white absolute top-26 mt-[8px] w-full flex flex-col lg:px-6 px-0 py-4 lg:max-h-[456px] max-h-[856px] overflow-y-scroll 
                ${!isMobile ? 'scrollbar !scrollbar-thumb-theme-200 gap-2':''}  text-black ${rounded}`}>
                {props?.children}
                {recentSearchData && recentSearchData.length != 0 &&
                    <>
                        {recentSearchData && showHistory &&
                            <>
                                <div className="flex items-center justify-between">
                                    {recentSearchLabel ?
                                        <Text text={recentSearchLabel}
                                            // eslint-disable-next-line max-len
                                            styles="text-black lg:text-sm text-xs font-primary-bold line-clamp-1 uppercase lg:py-4 py-[8px]" type="p" /> : null}
                                    {clearHistory && isMobile ? 
                                    <div onClick={() => clearAllHandler()}>
                                        <Text 
                                        text={clearHistory}
                                        styles="text-sm text-theme-100 font-primary-bold line-clamp-1 py-4 cursor-pointer"
                                        type="a" /> 
                                    </div> : 
                                    null}

                                </div>
                                <ul>
                                    {recentSearchData?.map((title: string, index: number) =>
                                        <li className="flex justify-between py-4"
                                         key={index} onClick={() => handleLink(title)}>
                                            <span className="flex gap-2 items-center">
                                                <Icon name="search-iconx10" />
                                                 <span className="cursor-pointer lg:text-base text-sm">{title}</span>
                                            </span>
                                            <span onClick={(event) => removeItem(event, index)}
                                             className="cursor-pointer px-1">
                                                <Icon name="close-iconx10" />
                                            </span>
                                        </li>
                                    )}
                                    {clearHistory && !isMobile ? 
                                    <li onClick={() => clearAllHandler()}>
                                        <Text 
                                        text={clearHistory}
                                        styles="text-sm text-theme-100 font-primary-bold line-clamp-1 py-4 cursor-pointer"
                                        type="a" /> 
                                    </li> : 
                                    null}
                                </ul>
                            </>}

                    </>}
            </div> : null}
        </>
    );
};

export default RecentlySearched;
