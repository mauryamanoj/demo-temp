import { ImageType } from "../../common/CommonTypes";

export interface Link {
  text: string;
  url: string;
  icon: string;
  targetInNewWindow: boolean;
}

export interface Store {
  text: string;
  icon: string;
  url: string;
}


export interface SingleAppPromotionalBannerProps {
  title: string;
  description: string;
  link: Link;
  stores: Store[];
  image: ImageType;
  ctaData?:any;
  componentId: string;
}
