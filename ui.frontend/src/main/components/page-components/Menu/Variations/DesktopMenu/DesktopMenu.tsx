/* eslint-disable max-len */
import React, { useState } from "react";
import Icon from "../../../../common/atoms/Icon/Icon";
import HeaderLogo from "../HeaderLogo";
import ItemDetails from "./ItemDetails";
import UserLoginStateIcon from "../UserLoginStateIcon";
import SearchInputBlock from "../../../SearchResult/Components/SearchInputBlock/SearchInputBlock";
import { SSIDContext } from "src/main/util/ssid-provider/ssidContext";
import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from 'src/main/util/updateAnalyticsData';

const DesktopMenu = ({
    visitSaudiLogo,
    data,
    languages,
    evisa,
    scrolled,
    registerLabel,
    handleLogin,
    handleMenu,
    searchBox,
    handleSearchClick,
    showSearchBox,
    searchForm,
    searchLabel,
    setShowFilter,
    clearItemHandler,
    showHistory,
    trending,
    recentSearchData,
    clearAllHandler,
    onClickOutside,
    isMobile,
}: any) => {
    const [showItem, setShowItem] = useState<null | { data: [] }>(null);
    const [showLangCurrency, setShowLangCurrency] = useState(false);
    const { user } = React.useContext(SSIDContext);
    const toggleLangCurrency = () => {
        setShowItem(null);
        setShowLangCurrency(!showLangCurrency);
    };

    const closeAll = () => {
        setShowItem(null);
        setShowLangCurrency(false);
    };


    const handleItemClick = (item: any) => {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: 'header_navigation',
            title: item?.title,
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation"
        });
        trackAnalytics({
            trackEventname: "header_navigation",
            trackName: "dl_push",
            title: item?.title,
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: item?.title,
        });
    };



    function handleEvisaClick(item: any) {
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: 'apply_evisa_click',
            navigation_name: item.title,
            event_category: "navigation",
            title: item.title,
            url: item?.link,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
        });

        trackAnalytics({
            trackEventname: "apply_evisa_click",
            trackName: "dl_push",
            title: item?.title,
            url: item?.link,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: item?.title,
        });
    }

    const handleMouseLeave = (event: any) => {
        const divRect = event.target.getBoundingClientRect();
        const mouseX = event.clientX;
        const mouseY = event.clientY;
        const isTop = mouseY < divRect.top;
        if (isTop) setShowItem(null)
    }

    return (
        <>
            <HeaderLogo
                visitSaudiLogo={visitSaudiLogo}
                onMouseEnter={closeAll}
                scrolled={scrolled}
            />
            {/* start search section */}
            {showSearchBox ? (
                <div className="absolute top-[140px] left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full max-w-[814px]">
                    <SearchInputBlock
                        isWithCloseIcon={true}
                        searchForm={searchForm}
                        searchPlaceholderLabel={searchLabel}
                        searchPagePath={searchBox?.searchPagePath}
                        searchPlaceholder={searchLabel}
                        setShowFilter={setShowFilter}
                        clearItemHandler={clearItemHandler}
                        trending={trending}
                        clearHistory={searchBox?.clearHistoryLabel}
                        showHistory={showHistory}
                        recentSearchLabel={searchBox?.recentSearchLabel}
                        handleSearchClick={handleSearchClick}
                        recentSearchData={recentSearchData}
                        clearAllHandler={clearAllHandler}
                        hideOverlay={true}
                        onClickOutside={onClickOutside}
                        isMobile={isMobile}
                    />
                </div>
            ) : null}
            {/* end search section */}
            <div className="flex w-6/12 child:h-20 child:flex child:items-center">
                {data && data.length > 0 ? (
                    data.map((item: any, index: any) => (
                        <a href={item.ctaLink}
                            target={item.targetInNewWindow ? "_blank" : '_self'}
                            className="px-5 cursor-pointer font-primary-semibold text-sm line-clamp truncate"
                            key={index}
                            onClick={() => handleItemClick(item)}
                            onMouseEnter={() => {
                                setShowLangCurrency(false);
                                setShowItem(
                                    item.data && item.data.length > 0
                                        ? item
                                        : null
                                );
                            }}
                            onMouseOut={handleMouseLeave}
                        >
                            {item.title}
                        </a>
                    ))
                ) : (
                    <></>
                )}
            </div>
            <div className="flex gap-3 w-5/12 min-w-[445px] justify-end">
                {!window.location.href.includes("search-result") ? (
                    <div
                        onClick={() => { closeAll(); handleSearchClick(); }}
                        className={`${showSearchBox ? "bg-white" : "bg-white/20"} p-3 rounded-full cursor-pointer font-primary-semibold`}
                    >
                        <Icon
                            name="search"
                            svgClass={`transition-all duration-300 ${showSearchBox ? "fill-theme-200" : scrolled ? "fill-black" : "fill-white"
                                }`}
                        />
                    </div>
                ) : null}
                <div
                    className="flex gap-1.5 bg-white/20 pl-2 pr-3 py-3 rounded-full
                               cursor-pointer uppercase items-center text-sm truncate font-primary-semibold w-auto"
                    onClick={toggleLangCurrency}
                >
                    <Icon
                        name="language"
                        svgClass={`transition-all duration-300 ${scrolled ? "fill-black" : "fill-white"
                            }`}
                    />
                    {window.location.pathname.split("/")[1]?.slice(0, 2) ??
                        "EN"}
                    {/* {"-SAR"} */}
                </div>
                {evisa?.link ? (
                    <a
                        href={evisa?.link}
                        onClick={() => handleEvisaClick(evisa)}
                        className="bg-white/20 py-3 px-5 rounded-full cursor-pointer
                                   line-clamp truncate w-32 text-center font-primary-semibold"
                    >
                        {evisa.title}
                    </a>
                ) : (
                    <></>
                )}

                <UserLoginStateIcon
                    scrolled={scrolled}
                    registerLabel={registerLabel}
                    handleLogin={handleLogin}
                    handleMenu={handleMenu}
                    user={user}
                />

                {showItem?.data ? (
                    <ItemDetails showItem={showItem} onClick={closeAll} />
                ) : (
                    <></>
                )}
                {languages?.data &&
                    languages.data.length > 0 &&
                    showLangCurrency ? (
                    <>
                        <ItemDetails languages={languages} onClick={closeAll} />
                    </>
                ) : (
                    <></>
                )}
            </div>
        </>
    );
};

export default DesktopMenu;

