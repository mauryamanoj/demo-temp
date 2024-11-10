import { VideoType } from "src/main/components/common/CommonTypes";
import { ImageType }  from "src/main/components/common/CommonTypes";

export interface ICardProps {
  title?: string;
  pageBannerTitle?:string;
  description?: string;
  subTitle?:string;
  logo?:any;
  replaceTitleWithLogo?:boolean;
  image?: ImageType;
  height?:string;
  heightSize?:string;
  showArrows?:boolean;
  item?:any;
  index?:any;
  ctaData?:any;
  video?: VideoType;
  mobileView?:any;
  sendData?:any
  type?:string;
  link?: {
    url:string;
    copy: string;
    targetInNewWindow: boolean;
    appEventData: {
      link: string;
    };
  };
  className?: string;
  imageClassNames?: string;
  logoSize?:"small" | "big";
  thumbs?: {gallery: any[], moreLabel: string};
  setOpenGallery:React.Dispatch<React.SetStateAction<number>>
}

