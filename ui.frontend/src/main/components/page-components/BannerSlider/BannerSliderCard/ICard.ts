import { VideoType } from "src/main/components/common/CommonTypes";
import { ImageType }  from "src/main/components/common/CommonTypes";
export interface ICardProps {
  title?: string;
  description?: string;
  image: ImageType;
  video: VideoType;
  ctaData?:any;
  link?: {
    copy: string;
    url: string;
    targetInNewWindow: boolean;
    appEventData: {
      link: string;
    };
  };
}
