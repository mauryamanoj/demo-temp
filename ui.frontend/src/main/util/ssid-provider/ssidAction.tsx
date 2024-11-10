// import {FETCH_SSID} from './ssidConstant';
import { getLanguage, removeURLParameter } from "src/main/util/getLanguage";
import { getCookie, setConsentCookie } from "src/main/util/handleCookie";
import { saveReferralQueryParamsIntoCookies, getReferralQueryParamsFromCookies } from 'src/main/util/type/helpers'
saveReferralQueryParamsIntoCookies();

export const loginWithRedirect = (ssidLoginUrl: string) => {
    if (ssidLoginUrl) {
        window.location.href = `${window.location.origin}${ssidLoginUrl}?lang=${getLanguage()}${getReferralQueryParamsFromCookies()}`;
        // window.location.href = `https://dev.visitsaudi.com/${ssidLoginUrl}?lang=${getLanguage()}`;
        // window.location.href = `https://qa-revamp.visitsaudi.com/bin/api/v2/user/callback`;
    }
};

export const getSSID = async (userInfoUrl: string) => {
    const token = getCookie("access_token");
    if (!token) {
        return undefined;
    }
    try {
        const userData = await getUserDetails(userInfoUrl, token);
        await saveUserData(userData);
        saveFavoriteItems(token);
        return userData;
    } catch {
        return undefined;
    }
};
const saveUserData = async (userData: any) => {
    const userDataObj = JSON.parse(userData);
    const interestsArr = userDataObj.interests?.split(",");
    const profileData = { ...userDataObj, interests: interestsArr };
    localStorage.setItem("int", interestsArr);
    sessionStorage.setItem("profileData", JSON.stringify(profileData));
};

const getUserDetails = async (userInfoUrl: string, token: string) => {
    const userDetails = await fetch(userInfoUrl, {
        method: "POST",
        headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
            token: token,
        },
    });
    const response = await userDetails.json();
    if (response.statusCode === 200 || response.statusCode === 201) {
        return response.message;
    } else {
        return undefined;
    }
};

export const updateCookies = () => {
    let url = removeURLParameter(window.location.href, "status");
    const length = url.length;
    if (url.charAt(length - 1) === "?") {
        url = url.slice(0, length - 1);
    }
    setConsentCookie("current_url", url);
    // setConsentCookie("current_url", "localhost");
};

const saveFavoriteItems = async (token: any) => {
    // get ans save favorite items
    if (window.localStorage.getItem("favoriteItems")) {
        return;
    }
    const baseURL = window.location.origin;
    const favoritesUrl = baseURL + "/bin/api/v2/user/get.favorites?locale=en";
    fetch(favoritesUrl, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            token: token,
        },
    })
        .then((response) => response.json())
        .then((data) => {
            if (data && data.items.length > 0) {
                const favoriteItems = data?.items;
                localStorage.setItem(
                    "favoriteItems",
                    JSON.stringify(favoriteItems)
                );
            }
        });
};

export const updateFavoriteItems = async (id: string, url?: string) => {
    const token = getCookie("access_token");
    if (!token) {
        return undefined;
    }
    const baseURL = window.location.origin;
    const favoritesUrl =
        baseURL + url ||
        `/bin/api/v2/user/update.favorites?lang=${getLanguage()}`;
    // const favoritesUrl =
    //     "https://qa.visitsaudi.com/bin/api/v2/user/update.favorites";

    fetch(favoritesUrl, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            token: token,
        },
        body: id,
    })
        .then((response) => response.json())
        .then((data) => {
            if (data && data.items.length > 0) {
                const favoriteItems = data?.items;
                localStorage.setItem(
                    "favoriteItems",
                    JSON.stringify(favoriteItems)
                );
            }
        });
};

export const deleteFavorite = (id: string, url?: string) => {
    const token = getCookie("access_token");
    if (!token) {
        return undefined;
    }
    const baseURL = window.location.origin;

    const favoritesUrl =
        baseURL + url ||
        `/bin/api/v2/user/delete.favorites?lang=${getLanguage()}`;
    // const favoritesUrl =
    //     "https://qa.visitsaudi.com/bin/api/v2/user/delete.favorites";

    fetch(favoritesUrl, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            token: token,
        },
        body: id,
    })
        .then((response) => response.json())
        .then((data) => {
            if (data && data.items.length > 0) {
                const favoriteItems = data?.items;
                localStorage.setItem(
                    "favoriteItems",
                    JSON.stringify(favoriteItems)
                );
            }
        });
};

export const isFavorite = (id: string | number | undefined): boolean => {
    const favoriteItemsList =
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        JSON.parse(window.localStorage.getItem("favoriteItems")!) || [];

    if (favoriteItemsList.length && favoriteItemsList.includes(id)) {
        return true;
    }

    return false;
};
export const isUserLogged = (): boolean => {
    const token = getCookie("access_token");
    const user = window.sessionStorage.getItem("userData");
    const isLogged = token && user ? true : false;
    return isLogged;
};

