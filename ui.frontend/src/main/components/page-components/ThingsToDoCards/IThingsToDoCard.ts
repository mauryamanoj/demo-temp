import { ImageType } from "../../common/CommonTypes";
export interface ThingsToDoCardsProps {
    title: string;
    filter: {
        period: 'this-week' | 'this-month';
        destination: string;
        season: string;
        startDate: string;
        endDate: string;
        type: string[];
        categories: string[];
        destinations: string[];
        poiTypes: string[];
        freeOnly?: boolean;

    };
    sort: {
        sortBy: string[];
    };
    handpick: {
        cfPaths: string[];
    };
    loadMore: {
        loadMoreLabel: string;
    };
    display: {
        cardSize: 'small' | 'small_2' | 'medium' | 'large';
        displayType: 'scrollable' | 'list_view';
        numberOfCards: number;
    };
    apiUrl: string;
    link: {
        hideMap: boolean;
        url: string;
        urlWithExtension: string;
        urlSlingExporter: string;
        text: string;
        targetInNewWindow: boolean;
    };
    tabs?: IThingsTodoTabProps[];
}

export interface IThingsTodoTabProps {
    id: string | number;
    title: string;
    icon: string;
    category: string;
}


export interface IThingsToDoCardProps {
    authorImage: ImageType;
    bannerImages: Array<ImageType>;
    categories?: {
        title: string;
        icon: string;
    }[];
    destination?: {
        title: string;
        path: string;
    };
    endDate?: string;
    hideFavorite?: boolean;
    hideShareIcon?: boolean;
    lat?: string;
    lng?: string;
    startDate: string;
    ticketDetails?: string;
    ticketPrice?: string;
    ticketPriceSuffix?: string;
    ticketsCtaLink: {
        url: string;
        text: string;
        targetInNewWindow?: boolean;
    };
    pageLink: {
      url: string;
      text: string;
      targetInNewWindow?: boolean;
  };
    ticketType: string;
    title: string;
    type: string;

    cardSize?: "large" | "medium" | "small" | "small_2";
    imageClass?: string;
    titleClass?: string;

    updateFavUrl: string;
    deleteFavUrl: string;
    getFavUrl: string;
    favId: string;
    buttonStyle?: string
}

