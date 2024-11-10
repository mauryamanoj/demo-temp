import { ImageTypes } from "src/main/util/getImage";

export interface MainCardProps {
    imageClass?: string;
    image?: ImageTypes;
    alt?: string;
    fromDate: string;
    toDate?: string;
    city?: string;
    tag?: string;
    oldPrice?: string;
    price?: string;
    title: string;
    cta?: {
        label: string;
        link?: string;
        openInNewTab?: boolean;
    };
    pill?: string;
    hideFavorite?: boolean;
    variant?:
    | "all-things-to-do"
    | "events-card"
    | "all-things-to-do-small"
    | "hotels-and-accomodations"
    | "What-to-buy";
    stars?: 1 | 2 | 3 | 4 | 5;
    share?: boolean;
    cardWrapperStyle?: string;
    cardCtaLink: string;
    link?: {
        targetInNewWindow?: boolean;
    };
}

