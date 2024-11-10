export interface BuyCardInetrface {
    title: string;
    type: string;
    link: Link;
    cards: Card[];
    fetchLatestStories: boolean
    apiUrl: string
    numberOfStories: number,
    filter: {
        categories: string[],
        destinations: string[]
    }
}


export interface Link {
    hideMap: boolean;
    url: string;
    urlWithExtension: string;
    urlSlingExporter: string;
    targetInNewWindow: boolean;
    text: string;
    appType: string;
}

export interface Card {
    title: string;
    city: string;
    hideFavorite: boolean;
    image: Image;
    tags: string[];
    cardCtaLink: string;
}

export interface Image {
    fileReference: string;
    mobileImageReference: string;
    s7fileReference: string;
    s7mobileImageReference: string;
    desktopImage: string;
    mobileImage: string;
}

