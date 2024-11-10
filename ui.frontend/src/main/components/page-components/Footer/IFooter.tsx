export interface FooterInterface {
    fragmentNewsletter: FragmentNewsletter;
    fragmentDownloads: FragmentDownloads;
    fragmentGroup: FragmentGroup;
    fragmentBranding: FragmentBranding;
    fragmentInternalLinks: FragmentLinks;
    fragmentExternalLinks: FragmentLinks;
    fragmentContact: FragmentLinks;
    ctaListIcon: CtaListIcon[];
    fragmentContainer: FragmentContainer;
    componentId: string;
}

interface CtaImage {
    ctaImage: string;
    ctaLink: string;
    // isOpenInNewTab: string;
    isOpenInNewTab: boolean;
}

export interface FragmentNewsletter {
    title: string;
    placeholder: string;
    ctaLabel: string;
    invalidEmailMessage: string;
    successMessage: string;
    failureMessage: string;
    apiUrl: string;
}
interface FragmentDownloads {
    title: string;
    ctaImageList: CtaImage[];
    newsletterSubscriptionTitle: string;
    yourEmailAddress: string;
    joinCta: string;
    info: string;
}

interface FragmentGroup {
    orientation: string;
    patternType: string;
}

export interface FragmentBranding {
    visitSaudiLogo: string;
    visitSaudiLogoLink: string;
    poweredBy: string;
    staLogo: string;
    staLogoLink: string;
}

interface CtaList {
    ctaLabel: string;
    ctaLink: string;
    isOpenInNewTab: string;
}

export interface FragmentLinks {
    titleLink: string;
    ctaList: CtaList[];
    sectionType?: string;
}

interface CtaListIcon {
    iconLabel: string;
    iconLink: string;
    isOpenInNewTab: string;
}

interface FragmentContainer {
    copyrights: string;
    upArrowCta: string;
    ctaList: CtaList[];
}

