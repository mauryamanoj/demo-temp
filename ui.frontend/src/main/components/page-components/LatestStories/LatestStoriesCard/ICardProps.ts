import { ImageType } from "src/main/components/common/CommonTypes";

export interface ICardProps {
    title?: string;
    description?: string;
    image: ImageType;
    tags?: string[];
    link?: {
        url: string;
        copy: string;
        title: string;
        targetInNewWindow: boolean;
        appEventData: {
            link: string;
        };
    };
    className?: string;
    imageClassNames?: string;
    cardIndex: number;
}

