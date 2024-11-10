/* eslint-disable max-len */
import React, { StrictMode, useLayoutEffect, useState, useEffect } from "react";
import { useResize } from "src/main/util/hooks/useResize";
import { IMainMenuProps } from "./IMainMenu";
import DesktopMenu from "./Variations/DesktopMenu/DesktopMenu";
import MobileMenu from "./Variations/MobileMenu/MobileMenu";
import {
    getSSID,
    loginWithRedirect,
    updateCookies,
} from "src/main/util/ssid-provider/ssidAction";
import { removeSSIDcookie } from "src/main/util/handleCookie";
import { trackEvents } from "src/main/util/updateAnalyticsData";
import { SSIDProvider } from "src/main/util/ssid-provider/ssidContext";
import { useFormik } from "formik";
import { stripHTML } from "../SearchResult/Helpers/index";
import UserMenu from "./Variations/UserMenu";
import dayjs from "dayjs";
import "dayjs/locale/ar";
import { getLanguage } from "src/main/util/getLanguage";

dayjs.locale(getLanguage() === "ar" ? "ar" : "en");

function isScrolledIntoView(el: any) {
    if (el) {
        var isVisible = el.getBoundingClientRect().top <= 500 && el.getBoundingClientRect().top >= -50;
        return isVisible;
    } else {
        return false
    }
}

const MainMenu = (props: IMainMenuProps) => {
    const { logo, data, languages, evisa, userMenu, searchBox, signOutLabel } =
        props;

    const [scrolled, setScrolled] = useState(false);
    const { isMobile } = useResize(1123);
    const [user, setUser] = useState<any>();

    // ============= start search code ==============
    const [value]: any = useState(window.localStorage.getItem("searchkeys"));
    const [isChange, setIsChange] = useState<boolean>(false);
    const [showHistory, setShowHistory] = useState<boolean>(true);
    const [showSearchBox, setShowSearchBox] = useState(false);
    const [recentSearchData, setRecentSearchData]: any = useState([]);
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const scrollOffset = 70;

    function setSugesstionsData(searchTerm: string) {
        const arr: any = recentSearchData;
        if (searchTerm) {
            arr.push(searchTerm);
            setRecentSearchData(arr);
            window.localStorage.setItem("searchkeys", recentSearchData.join());
        }
        if (arr.length > 5) {
            recentSearchData.shift();
            recentSearchData.reverse();
            setRecentSearchData(recentSearchData);
            window.localStorage.setItem("searchkeys", recentSearchData.join());
        }
    }

    function clearInput() {
        searchForm.resetForm();
    }

    function onClickOutside() {
        setShowSearchBox(false);
    }

    function handleSearchClick() {
        setShowSearchBox(!showSearchBox);
        setIsOpen(false);
        clearInput();
        document.querySelector(".header-ele")?.classList.remove("min-h-full");
        document.querySelector(".header-ele")?.classList.remove("h-full");
        document.body.style.position = "initial";
    }

    const clearItemHandler = (index: number) => {
        const data = recentSearchData.filter(
            (element: any, i: number) => i != index
        );
        setRecentSearchData(data);
        return index;
    };

    const searchForm = useFormik({
        initialValues: {
            search: "",
        },
        onSubmit: (values) => {
            document.body.style.overflow = "unset";
            setSugesstionsData(stripHTML(values.search));
            setIsChange(!isChange);
            setTimeout(() => {
                window.location.href =
                    searchBox.searchPagePath +
                    "?search=" +
                    values.search.replace("<", "").replace(">", "");
            }, 400);
        },
    });

    const scrollHandler = () => {
        if (!isMobile) {
            const hasScrolled = window.pageYOffset > scrollOffset;
            if (hasScrolled) {
                setShowSearchBox(false);
                setIsOpen(false);
            }
        }
    };

    const clearAllHandler = () => {
        setRecentSearchData([]);
        setShowHistory(false);
    };

    useEffect(() => {
        const recentSearchArr: any = recentSearchData;

        if (value) {
            const existData = value.split(",");
            for (let index = 0; index < existData.length; index++) {
                const element = existData[index];
                recentSearchArr.push(element);
                setRecentSearchData(recentSearchArr?.reverse());
            }
        }

        window.addEventListener("scroll", scrollHandler);
        //START scroll to section
        const getAfterHash = window.location.href.split("#")[1];
        if (getAfterHash) {
            const checkQuery = getAfterHash.split("?")
            const sectionId = checkQuery ? checkQuery[0] : getAfterHash
            if (sectionId) {
                setTimeout(() => {
                    handleClickScroll(sectionId)
                }, 500);
            }
        }

        return () => {
            window.removeEventListener("scroll", scrollHandler);
        };
    }, []);

    const handleClickScroll = (id: string) => {
        const element = document.getElementById(id);
        if (element) {
            const y = element.getBoundingClientRect().top + window.pageYOffset - 150;
            window.scrollTo({ top: y, behavior: 'smooth' });
            setTimeout(() => {
                if (!isScrolledIntoView(element)) handleClickScroll(id)
            }, 500)
        }
    };
    //END scroll to section
    // ============= end search code ==============

    const trackAnalytics = (uData: any) => {
        const dataSet: any = {};
        const usr = uData && JSON.parse(uData);
        dataSet.trackUserId = usr?.ssid ? usr?.ssid : usr?.sid;
        dataSet.trackName = "sign in sucess";
        dataSet.trackEventname = "signin_success";
        const id = setInterval(() => {
            const elementExists = document.getElementById(
                "AdobeAnalyticsScript"
            );
            if (elementExists) {
                trackEvents(dataSet);
                clearInterval(id);
            }
        }, 500);
    };

    window.onscroll = () => {
        if (window.scrollY > 200 && !scrolled) {
            setScrolled(true);
        } else if (window.scrollY <= 200 && scrolled) {
            setScrolled(false);
        }
    };

    useLayoutEffect(() => {
        fetchUser(); //fetch user data
    }, []);

    async function fetchUser() {
        const user = await getSSID(userMenu.userApi?.ssidUserInfo);
        if (user && !sessionStorage.getItem("userData")) {
            trackAnalytics(user);
        }
        if (user) {
            sessionStorage.setItem("userData", user);
        } else {
            sessionStorage.removeItem("userData");
            localStorage.removeItem("int");
            localStorage.removeItem("favoriteItems");
        }
        user && setUser(JSON.parse(user));
    }

    const handleLogin = () => {
        removeSSIDcookie();
        updateCookies();
        loginWithRedirect(userMenu.userApi.ssidLoginUrl);
    };

    const handleMenu = () => {
        setIsOpen(!isOpen);
    };

    const handleLogout = () => {
        updateCookies();
        // remove userProfile from cookie
        localStorage.removeItem("int");
        localStorage.removeItem("favoriteItems");
        sessionStorage.removeItem("profileData");
        window.location.href = `${window.location.origin}${userMenu?.userApi.ssidLogoutUrl}`;
    };

    return (
        <div
            className={`
            header-ele fixed top-0 left-0 z-50 backdrop-blur-[20px]
            flex justify-between items-center px-6 lg:px-8 xl:px-16 2xl:px-[100px] py-4 w-full h-16 lg:h-20
            transition-all duration-300
            ${scrolled ? "bg-white text-black" : "bg-transparent/80 text-white"}
`}
        >
            <StrictMode>
                <SSIDProvider user={user} loginConfig={userMenu}>
                    {isMobile ? (
                        <MobileMenu
                            visitSaudiLogo={logo?.mobileImage}
                            data={data}
                            scrolled={scrolled}
                            languages={languages}
                            evisa={evisa}
                            registerLabel={userMenu?.registerLabel}
                            handleLogin={handleLogin}
                            handleMenu={handleMenu}
                            handleSignOut={handleLogout}
                            userMenu={userMenu}
                            searchBox={searchBox} // start search code
                            handleSearchClick={handleSearchClick}
                            showSearchBox={showSearchBox}
                            searchForm={searchForm}
                            clearItemHandler={clearItemHandler}
                            searchLabel={searchBox.searchLabel}
                            trending={searchBox.trending}
                            clearAllHandler={clearAllHandler}
                            recentSearchData={recentSearchData}
                            isMobile={isMobile}
                            showHistory={showHistory} // end search code
                        />
                    ) : (
                        <>
                            <DesktopMenu
                                visitSaudiLogo={logo?.desktopImage}
                                data={data}
                                scrolled={scrolled}
                                languages={languages}
                                evisa={evisa}
                                registerLabel={userMenu.registerLabel}
                                handleLogin={handleLogin}
                                handleMenu={handleMenu}
                                searchBox={searchBox} // start search code
                                handleSearchClick={handleSearchClick}
                                showSearchBox={showSearchBox}
                                searchForm={searchForm}
                                clearItemHandler={clearItemHandler}
                                searchLabel={searchBox.searchLabel}
                                trending={searchBox.trending}
                                clearAllHandler={clearAllHandler}
                                recentSearchData={recentSearchData}
                                onClickOutside={onClickOutside}
                                isMobile={isMobile}
                                showHistory={showHistory} // end search code
                            />
                            <div>
                                {isOpen ? (
                                    <UserMenu
                                        userMenu={userMenu}
                                        user={user}
                                        signOutLabel={signOutLabel}
                                        handleLogout={handleLogout}
                                        handleClose={handleMenu}
                                        isOpen={() => {
                                            setIsOpen(false);
                                        }}
                                    />
                                ) : null}
                            </div>
                        </>
                    )}
                </SSIDProvider>
            </StrictMode>
        </div>
    );
};

export default MainMenu;

