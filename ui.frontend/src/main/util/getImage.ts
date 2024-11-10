import isMobile from "./isMobile";
export interface ImageTypes {
    pullDesktopImageFromDAM?: string;
    pullMobileImageFromDAM?: string;
    fileReference?: string;
    mobileImageReference?: string;
    s7fileReference?: string;
    s7mobileImageReference?: string;
    desktopImage?: string;
    mobileImage?: string;
    defaultImage?: string;
    alt?: string;
    breakpoints?: {
        srcset: string;
        media: string;
    }[]
}
export const getImage = (imageCard: ImageTypes, breakpoint?: number) => {
    const { mobileImageReference, s7mobileImageReference, s7fileReference,
        fileReference, desktopImage, mobileImage, defaultImage } = imageCard;
    let imgSrc = defaultImage;
    if (isDam(imageCard)) {
        return getDamImage(imageCard);
    }
    else {
        if (isMobile(breakpoint ? breakpoint : 1024)) {
            if (mobileImage) {
                imgSrc = mobileImage;
            }
            else if (s7mobileImageReference) {
                imgSrc = s7mobileImageReference;
            }
            else if (mobileImageReference) {
                imgSrc = mobileImageReference;
            }
            else if (s7fileReference) {
                imgSrc = s7fileReference;
            }
            else if (fileReference) {
                imgSrc = fileReference;
            }
            else if (desktopImage) {
                imgSrc = desktopImage;
            }
        }
        else {
            if (desktopImage) {
                imgSrc = desktopImage;
            }
            else if (s7fileReference) {
                imgSrc = s7fileReference;
            }
            else if (fileReference) {
                imgSrc = fileReference;
            }
            else if (s7mobileImageReference) {
                imgSrc = s7mobileImageReference;
            }
            else if (mobileImageReference) {
                imgSrc = mobileImageReference;
            }
            else if (mobileImage) {
                imgSrc = mobileImage;
            }
        }
        return imgSrc;
    }
};

export const getDamImage = (imageCard: ImageTypes) => {
    const { mobileImageReference, s7mobileImageReference, s7fileReference,
        fileReference, desktopImage, mobileImage } = imageCard;
    if (isMobile(1024)) {
        if (mobileImageReference) {
            return mobileImageReference;
        }
        else if (mobileImage) {
            return mobileImage;
        }
        return s7mobileImageReference;
    }
    else {
        if (fileReference) {
            return fileReference;
        }
        else if (desktopImage) {
            return desktopImage;
        }
        return s7fileReference;
    }
};

export const isDam = (imageCard: ImageTypes) => {
    const { pullDesktopImageFromDAM, pullMobileImageFromDAM } = imageCard;
    if (isMobile(1024)) {
        return pullMobileImageFromDAM === 'true' ? true : false;
    }
    else {
        return pullDesktopImageFromDAM === 'true' ? true : false;
    }
};

