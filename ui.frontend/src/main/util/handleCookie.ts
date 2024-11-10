/* eslint-disable max-len */
export const getCookie = (cookieName: string): string => {
    const name = `${cookieName}=`;
    const decodedCookie = decodeURIComponent(document.cookie);
    const chains = decodedCookie.split(";");

    for (let chain of chains) {
        while (chain.charAt(0) === " ") {
            chain = chain?.substring(1);
        }
        if (chain.indexOf(name) === 0) {
            return chain?.substring(name.length, chain.length);
        }
    }
    return "";
};

export const setCookie = (cookieName: string, value: string): void => {
    document.cookie = `${cookieName}=${value}`;
};

export const removeSSIDcookie = () => {
    document.cookie.split(";").forEach((c) => {
        const cookieName = c.trim().split("=")[0];
        if (
            cookieName.includes("userData") ||
            cookieName.includes("access_token") ||
            cookieName.includes("current_url")
        ) {
            document.cookie =
                c.trim().split("=")[0] +
                "=;expires=Thu, 01 Jan 1970 00:00:01 GMT;";
            document.cookie =
                c.trim().split("=")[0] +
                "=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;";
            document.cookie =
                c.trim().split("=")[0] +
                "=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;domain=.visitsaudi.com;";
            document.cookie =
                c.trim().split("=")[0] +
                "=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;domain=" +
                window.location.hostname;
            document.cookie =
                c.trim().split("=")[0] +
                "=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;domain=www.visitsaudi.com;";
            document.cookie =
                c.trim().split("=")[0] +
                "=; path=/; domain=.visitsaudi.com; expires=" +
                new Date(0).toUTCString();
        }
    });
};

const getConsentCookie = (cname: string) => {
    const name = cname;
    const nameEQ = name + "=";
    const ca = document.cookie.split(";");
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === " ") {
            c = c?.substring(1, c.length);
        }
        if (c.indexOf(nameEQ) === 0) {
            return c?.substring(nameEQ.length, c.length);
        }
    }
    return "";
};
export const setConsentCookie = (cname: string, cvalue: string) => {
    const exdays = 30;
    if (exdays) {
        let domain, domainParts, date, expires;
        const days = exdays;
        const value = cvalue;
        const name = cname;
        if (days) {
            date = new Date();
            date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
            expires = "; expires=" + date.toUTCString();
        } else {
            expires = "";
        }
        const host = window.location.host;

        if (host.split(".").length === 1) {
            document.cookie = name + "=" + value + expires + "; path=/";
        } else {
            domainParts = host.split(".");
            domainParts.shift();
            domain = "." + domainParts.join(".");
            document.cookie =
                name + "=" + value + expires + "; path=/; domain=" + domain;
            if (
                getConsentCookie(name) === "null" ||
                getConsentCookie(name) !== value
            ) {
                domain = "." + host;
                document.cookie =
                    name + "=" + value + expires + "; path=/; domain=" + domain;
            }
        }
    } else {
        document.cookie = cname + "=" + cvalue;
    }
};

export const setSessionStorage = (key: string, value: any) => {
    if (key && value) {
        window.sessionStorage.setItem(key, value);
    }
};
export const getSessionStorage = (key: string) => {
    if (key) {
        return window.sessionStorage.getItem(key);
    }
};
export const removeSessionStorage = (key: string) => {
    if (key) {
        window.sessionStorage.removeItem(key);
    }
};

