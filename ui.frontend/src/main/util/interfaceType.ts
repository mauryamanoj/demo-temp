export interface TrackingDataType {
    trackName: string;
    trackSection?: string;
    trackProduct?: string;
    trackPackagename?: string;
    trackEventname: string;
    trackDestination?: string;
}

export interface Cardstrings {
    readonly from: string;
    readonly currency: string;
    readonly off: string;
    readonly person: string;
    readonly free: string;
    readonly notAvailable: string;
}

export type Link = Record<"copy" | "url", string>;

export type Language = Record<"copy" | "text", string>;

export type CurrentLanguage = Record<"name" | "code", string>;

export interface MapTypes {
    locationType: string;
    regionId: string;
    cityId: string;
    mapLink: string;
    "background-type"?: string;
    hideMap?: boolean;
}

export interface PhoneNumber {
    text: string;
    url: string;
    copy: string;
}

export interface UserApi {
    path: string;
    getProfile: string;
    getTripPlansEndpoint: string;
    updateProfile: string;
    domain?: string;
    clientId?: string;
    ssidUserInfo: string;
    ssidLoginUrl: string;
    ssidLogoutUrl: string;
}
export interface Contact {
    copy: string;
    link?: string;
    icon: string;
    url?: string;
}
export interface LoginDropdownMenu {
    cta: Contact;
}
export type LoginConfig = {
    userApi: UserApi;
    signInCopy: string;
    signOutCopy: string;
    submenu: Array<LoginDropdownMenu>;
    avatar?: {
        mobile?: string;
        desktop?: string;
    };
    apiError?: {
        message?: string;
        ctaText?: string;
    };
};

export type UserType = {
    firstName: string;
    lastName: string;
    email: string;
    sid: string;
    gender: string;
    isEmailVerified: string;
    birthDate: string;
    pictureUrl: string;
};

