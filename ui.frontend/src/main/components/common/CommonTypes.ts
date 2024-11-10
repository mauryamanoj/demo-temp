export type VideoType = {
  videoFileReference: string;
  s7videoFileReference?: string;
  autorerun: boolean;
  autoplay: boolean;
  poster?: string;
};

export interface ImageType {
  alt?: string;
  fileReference: string;
  mobileImageReference: string;
  s7fileReference: string;
  s7mobileImageReference: string;
  desktopImage?:string;
  mobileImage?:string;
  breakpoints?: any;
  defaultImage?: string;
}

export type linkType = {
  title: string;
  copy:string;
  url: string;
  targetInNewWindow: boolean;
  appEventData: {
    link: string;
  };
};
