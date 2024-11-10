export interface CategoryCardProps {
    title: string;
    image: ImageProps
    link: {
        url: string;
        copy: string;
        targetInNewWindow: boolean;
    }
}
export interface ImageProps {
    alt: string;
    desktopImage: string;
    mobileImage: string;
    s7fileReference: string;
    fileReference:string;
}
export interface CategoryCardsProps {
    title: string;
    view?: 'slider' | 'grid';
    cards: CategoryCardProps[];
    backgroundImage: ImageProps;
}
