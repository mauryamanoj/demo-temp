import React, { createContext, useContext } from "react";
import { getCookie } from "src/main/util/handleCookie";
import { LoginConfig, UserType } from "../interfaceType";

export const SSIDContext = createContext<{ user: UserType | null, loginConfig: LoginConfig, token: string | undefined }>({
    user: null,
    loginConfig: {
        userApi: {
            path: "",
            getProfile: "",
            getTripPlansEndpoint: "",
            updateProfile: "",
            ssidLoginUrl: "",
            ssidLogoutUrl: "",
            ssidUserInfo: "",
            domain: "",
            clientId: "",
        },
        signInCopy: "",
        signOutCopy: "",
        submenu: [],
    },
    token: undefined,
});

export const SSIDProvider = ({ user, loginConfig, children }: any) => {
    const token = getCookie("access_token");

    return (
        <SSIDContext.Provider  value={{ user: user, loginConfig: loginConfig, token: token }} >
            {children}
        </SSIDContext.Provider>
    );
};
export const useSSIDvalue = () => useContext(SSIDContext);

