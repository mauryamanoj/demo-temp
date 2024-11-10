import { CurrentLanguage, UserApi } from "src/main/util/interfaceType";

export interface IMainMenuProps {
    searchBox: any; // todo: i will change any ASAP
    logo: {
        desktopImage: string;
        mobileImage: string;
    };
    data: MenuData[];
    languages: ILanguages;
    evisa: {
        title: string;
        link: string;
    };
    // registerLabel: string;

    signInCopy: string;
    signOutCopy: string;
    // userApi: UserApi;
    userMenu: UserMenuData; // todo: will add the type
    signOutLabel: string;
    domain?: string;
    clientId?: string;
    currentLanguage: CurrentLanguage;

    // ctaLoginText: string;
    // favoriteApiPath: string;
    apiError?: {
        message?: string;
        ctaText?: string;
    };
}

export type UserMenuData = {
    registerLabel: string;
    signOutLabel: string;
    welcomeLabel: string;
    userApi: {
        ssidLoginUrl: string;
        domain: string;
        clientId: string;
        ssidUserInfo: string;
        ssidLogoutUrl: string;
        getProfile: string;
        getTripPlansEndpoint: string;
        updateProfile: string;
        getFavorites: string;
    };
    subMenu: {
        iconName: string;
        url: string;
        label: string;
    }[];
};

export type ILanguages = {
    label: string;
    data: LanguagesData[];
};
type LanguagesData = {
    unAvailableLanMessage: string;
    ctaLabel: string;
    ctaLink: string;
    isSelected?: boolean;
};

type MenuData = {
    title: string;
    ctaLink: string;
    viewAllLabel: string;
    viewAllLink: string;
    data: SubMenuData[];
};

type SubMenuData = {
    ctaLabel: string;
    ctaLink: string;
    image: object;
};

