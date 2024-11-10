/* eslint-disable max-len */
import React, { useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import HeaderLogo from "../HeaderLogo";
import { SSIDContext } from "src/main/util/ssid-provider/ssidContext";
import UserLoginStateIcon from "../UserLoginStateIcon";
import SearchInputBlock from "../../../SearchResult/Components/SearchInputBlock/SearchInputBlock";
import Text from "../../../../common/atoms/Text/Text";
import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from "src/main/util/updateAnalyticsData";

import LanguagesModal from "../LanguagesModal"

const MobileMenu = (props: any) => {
    const {
        visitSaudiLogo,
        data,
        languages,
        evisa,
        scrolled,
        registerLabel,
        handleLogin,
        handleMenu,
        searchBox,
        searchForm,
        searchLabel,
        setShowFilter,
        clearItemHandler,
        showHistory,
        trending,
        recentSearchData,
        clearAllHandler,
        handleSearchClick,
        showSearchBox,
        isMobile,
        userMenu,
    } = props;

    const { signOutLabel, viewProfileLabel, viewProfileCTA } = userMenu;
    const [toggleMenu, setToggleMenu] = useState(false);
    // const [showInfoMenu, setShowInfoMenu] = useState(false);
    const { user } = React.useContext(SSIDContext);

    var profileData: any = sessionStorage.getItem("profileData");
    if (profileData) {
        profileData = JSON.parse(profileData);
    }
    const [showItem, setShowItem] = useState<null | {
        title: "";
        data: [];
        viewAllLink: "";
        viewAllLabel: "";
    }>(null);

    const [showLangCurrency, setShowLangCurrency] = useState(false);

    const toggleLangCurrency = () => {
        setShowItem(null);
        setShowLangCurrency(!showLangCurrency);
    };

    const toggleMenuFunction = () => {
        const htmlElement = document.getElementsByTagName('html')[0];
        const bodyElement = document.body;

        if (toggleMenu) {
            htmlElement.style.overflow = "";
            bodyElement.style.overflow = "";
            htmlElement.style.height = "";
            bodyElement.style.height = "";
        } else {
            htmlElement.style.overflow = "hidden";
            bodyElement.style.overflow = "hidden";
            htmlElement.style.height = "100%";
            bodyElement.style.height = "100%";
        }

        setToggleMenu(!toggleMenu);
    };

    const goBack = () => {
        setShowItem(null);
        setShowLangCurrency(false);
    };

    const closeAll = () => {
        setShowItem(null);
        setShowLangCurrency(false);
        toggleMenuFunction();
    };

    const HTMLItem = (index: any, subItem: any) => (
        <a
            className={`flex font-primary-semibold text-sm gap-4 items-center`}
            key={index}
            href={subItem.ctaLink}
        >
            <div className="min-w-[64px] h-16">
                {subItem.image ? (
                    <Picture
                        image={getImage(subItem.image)}
                        breakpoints={subItem?.image?.breakpoints}
                        imageClassNames={`rounded-lg w-16 h-16 object-cover`}
                    />
                ) : (
                    <></>
                )}
            </div>
            <div className="w-full">{subItem.ctaLabel}</div>
        </a>
    );

    const HTMLanguage = (index: any, subItem: any) => (
        <span
            className={`flex font-primary-bold text-sm gap-4 uppercase${subItem.isSelected ? " border-theme-100 text-theme-100" : ""}`}
            key={index}
            onClick={() => handleLanguageClick(subItem)}
        >
            {subItem.ctaLabel}
        </span>
    );

    const renderFormattedLanguageLabel = (data: any[]) => {
        return (
            data
                .filter((el: any) => {
                    const browserLang =
                        window.location.pathname.split("/")[1]?.slice(0, 2) ??
                        "EN";
                    if (el?.ctaLink?.startsWith("/" + browserLang)) {
                        return el.ctaLabel;
                    }
                })[0]
                ?.ctaLabel?.split("-")[1]?.trim() || "ENGLISH"
        );
    };

    // function handleInfo(){
    //     setShowInfoMenu(!showInfoMenu);
    //  }

    function isSearchResult() {
        return window.location.href.includes("search-result");
    }


    function handleFirstSectionClick(item: any) {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: "header_navigation",
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
        item.data && item.data.length > 0;

        if (item.data && item.data.length > 0) {
            setShowLangCurrency(false);
            setShowItem(item);
        }
    }

    function handleEvisa(item: any) {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: "header_navigation",
            title: item?.title,
            url: item?.link,
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

        setTimeout(() => {
            window.location.href = item?.link;
        }, 400);
    }

    function handleLoginItem() {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: "header_navigation",
            title: registerLabel,
            url: '',
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation"
        });
        trackAnalytics({
            trackEventname: "header_change_language",
            trackName: "dl_push",
            title: registerLabel,
            url: '',
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: registerLabel,
        });
        setTimeout(() => {
            handleLogin();
        }, 400);
    }

    function handleSignOutClick() {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: "header_navigation",
            title: signOutLabel,
            url: "",
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation"
        });
        trackAnalytics({
            trackEventname: "header_navigation",
            trackName: "dl_push",
            title: signOutLabel,
            url: '',
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: signOutLabel,
        });
        props.handleSignOut();
    }

    function handleLanguageClick(item: any) {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();
        trackingEvent({
            event_name: "header_change_language",
            title: item?.ctaLabel,
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation"
        });
        trackAnalytics({
            trackEventname: "header_change_language",
            trackName: "dl_push",
            title: item?.ctaLabel,
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: item?.ctaLabel,
        });
        setTimeout(() => {
            window.location.href = window.location.href.replace(
                window.location.pathname.slice(0, 4),
                item.ctaLink + "/"
            );
        }, 400);
    }


    return (
        <>
            <HeaderLogo visitSaudiLogo={visitSaudiLogo} scrolled={scrolled} />
            {/* start search section */}
            {showSearchBox && !isSearchResult() ? (
                <>
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
                        searchModalTitle={searchBox.searchModalTitle}
                        withFilterIcon={false}
                        hideOverlay={true}
                        isMobile={isMobile}
                    />
                </>
            ) : null}
            {/* end search section */}
            <div className="flex gap-6">
                {!isSearchResult() ? (
                    <div onClick={handleSearchClick}>
                        <Icon
                            name="search"
                            svgClass={`transition-all duration-300 ${scrolled ? "fill-black" : "fill-white"
                                }`}
                        />
                    </div>
                ) : null}

                <div onClick={closeAll}>
                    <Icon
                        name="menu"
                        svgClass={scrolled ? "fill-black" : "fill-white"}
                    />
                </div>
            </div>
            {toggleMenu ? (
                <div className="bg-stone-50 w-full h-[100svh] fixed inset-0 z-50 p-6 text-black overflow-hidden overflow-y-scroll">
                    <div className="flex justify-between">
                        <div>
                            {showItem || showLangCurrency ? (
                                <div onClick={goBack}>
                                    <Icon
                                        name="back"
                                        svgClass="rtl:scale-x-[-1]"
                                    />
                                </div>
                            ) : (
                                <></>
                            )}
                        </div>
                        <div onClick={toggleMenuFunction}>
                            <Icon name="close" />
                        </div>
                    </div>
                    <div className="my-8 grid gap-8">
                        {user && (
                            <div className="relative border-b-[1px] border-gray/50">
                                <div className="h-auto justify-start items-center gap-4 inline-flex mb-8">
                                    <UserLoginStateIcon
                                        scrolled={scrolled}
                                        registerLabel={registerLabel}
                                        handleLogin={handleLogin}
                                        handleMenu={handleMenu}
                                        user={user}
                                    />
                                    <div className="flex flex-col">
                                        {profileData && (
                                            <a href={viewProfileCTA}>
                                                <Text
                                                    styles="text-black text-sm font-primary-bold uppercase leading-snug"
                                                    text={viewProfileLabel}
                                                />
                                            </a>
                                        )}
                                    </div>
                                    {/* <div className="flex flex-col">
                                        {profileData &&
                                        profileData?.firstName ? (
                                            <div className="font-primary-regular text-sm uppercase leading-snug">
                                                {welcomeLabel ? (
                                                    <>{welcomeLabel},</>
                                                ) : null}{" "}
                                                <span className="font-primary-semibold">
                                                    {profileData?.firstName}
                                                </span>
                                            </div>
                                        ) : null}
                                        {profileData && profileData?.email ? (
                                            <div className="text-xs text-[#4B4B4B] font-primary-regular leading-snug">
                                                {profileData?.email}
                                            </div>
                                        ) : null}
                                    </div> */}
                                </div>
                                {/* DO NOT DELETE MAYBE WILL USED IT */}
                                {/* START USER DROPDOWN MENU  */}
                                {/* <div
                                    className="absolute top-1 ltr:right-2 rtl:left-2"
                                    onClick={handleInfo}
                                >
                                    <Icon
                                        name="arrow-bottom"
                                        svgClass={
                                            showInfoMenu ? "rotate-180" : ""
                                        }
                                    />
                                </div> */}
                                {/* <ul className={showInfoMenu ? "" : "hidden"}>
                                    {subMenu.map((item: any, index: any) => (
                                        <li className="flex items-center hover:bg-[#F2F2F2] px-0 py-[13.5px] cursor-pointer border-b-[1px] border-[#F2F2F2]">
                                            <a
                                                href={item?.url}
                                                className="flex items-center"
                                            >
                                                <Icon name={item?.iconName} />
                                                <span
                                                    className={handleIconSpaces(
                                                        subMenu?.length,
                                                        index
                                                    )}
                                                >
                                                    {item?.label}
                                                </span>
                                            </a>
                                        </li>
                                    ))}
                                    {signOutLabel ? (
                                        <li
                                             onClick={()=>{
                                                trackingEvent("header_navigation",signOutLabel,"");
                                                trackAnalytics(
                                                    "header_change_language",
                                                    "dl_push",
                                                    signOutLabel,
                                                    "",
                                                    'navigation',
                                                    'navigation');
                                                props.handleSignOut();
                                            }
                                            className="flex items-center  hover:bg-[#F2F2F2] px-0 py-[13.5px] cursor-pointer"
                                        >
                                            <Icon name="signoutMenu" />
                                            <span className="ltr:ml-[12px] rtl:mr-[12px]">
                                                {signOutLabel}
                                            </span>
                                        </li>
                                    ) : null}
                                </ul> */}
                                {/* END USER DROPDOWN MENU  */}
                            </div>
                        )}
                        {showLangCurrency ? (
                            <LanguagesModal languages={languages} handleItemClick={handleLanguageClick} />
                        ) : showItem ? (
                            <>
                                <div className="flex items-center justify-between text-black text-lg font-primary-bold mb-2">
                                    <div className="leading-7 uppercase">{showItem.title}</div>
                                    <a
                                        href={showItem.viewAllLink}
                                        className="text-theme-100 text-sm leading-[17.33px]                                            "
                                    >
                                        {showItem.viewAllLabel}
                                    </a>
                                </div>
                                {showItem?.data?.map(
                                    (subItem: any, index: any) =>
                                        HTMLItem(index, subItem)
                                )}
                            </>
                        ) : (
                            <div>
                                <div className="border-b-[1px] border-gray/50">
                                    {data && data.length > 0 ? (
                                        data.map((item: any, index: any) => (
                                            <a
                                                className="font-primary-bold text-sm flex mb-8 uppercase"
                                                key={index}
                                                href={
                                                    item.data &&
                                                        item.data.length > 0
                                                        ? undefined
                                                        : item.ctaLink
                                                }
                                                onClick={() =>
                                                    handleFirstSectionClick(
                                                        item
                                                    )
                                                }
                                            >
                                                {item.title}
                                            </a>
                                        ))
                                    ) : (
                                        <></>
                                    )}
                                </div>
                                <div className="mt-8 border-b-[1px] border-gray/50">
                                    {evisa?.link ? (
                                        <span
                                            onClick={() => handleEvisa(evisa)}
                                            className="font-primary-bold text-sm flex mb-8 uppercase text-theme-100"
                                        >
                                            {evisa.title}
                                        </span>
                                    ) : (
                                        <></>
                                    )}
                                    {!user && (
                                        <div
                                            className="font-primary-bold text-sm flex mb-8 uppercase text-theme-100
                                                   cursor-pointer"
                                            onClick={handleLoginItem}
                                        >
                                            {registerLabel}
                                        </div>
                                    )}
                                </div>
                                <div className="mt-8">
                                    <div
                                        className="font-primary-bold text-sm w-full mb-8 uppercase"
                                        onClick={toggleLangCurrency}
                                    >
                                        <div>{languages.label}</div>
                                        <div className="mt-2 font-primary-semibold text-gray capitalize">
                                            {renderFormattedLanguageLabel(
                                                languages.data
                                            )}
                                        </div>
                                    </div>
                                    {/* <div className="font-primary-bold text-sm w-full mb-8 uppercase">
                                        <div>Currency</div>
                                        <div className="mt-2 font-primary-semibold text-gray uppercase">
                                            SAR
                                        </div>
                                    </div> */}
                                    {/* <div className="font-primary-bold text-sm w-full mb-8 uppercase"></div> */}
                                </div>
                                {user && (
                                    <div className="mt-8 border-t-[1px] border-gray/50">
                                        <div
                                            className="font-primary-bold text-sm flex mt-8 uppercase text-theme-100
                                                    cursor-pointer"
                                        >
                                            <div
                                                onClick={handleSignOutClick}
                                            >
                                                {signOutLabel}
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            ) : (
                <></>
            )}
        </>
    );
};

export default MobileMenu;

