type Image = {
    alt: string;
    s7fileReference: string;
    desktopImage: string;
    mobileImage: string;
};
type Author = {
    authorText: string;
    image?: Image;
    authorCtaLink: string;
};

export interface IHeaderSection {
    enableManualAuthoring?: boolean;
    isCFragment?: boolean;
    title: string;
    subtitle: string;
    byLabel: string;
    author?: Author;
    titleImage?: Image;
}

