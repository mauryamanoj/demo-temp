interface Link {
    hideMap: boolean;
    url: string;
    urlWithExtension: string;
    urlSlingExporter: string;
    targetInNewWindow: boolean;
    text: string;
    appType: string;
}

interface Image {
    fileReference: string;
    alt: string;
    s7fileReference: string;
    breakpoints?: any[];
}
export interface ITextImageSection {
    title: string;
    description: string;
    link: Link;
    image: Image;
    logo: Image & { isTransparent?: boolean };
    position: "left" | "right" | "top";
    skipMargin: boolean;
    whiteBackground?: boolean;
    ctaData?: any;
}

