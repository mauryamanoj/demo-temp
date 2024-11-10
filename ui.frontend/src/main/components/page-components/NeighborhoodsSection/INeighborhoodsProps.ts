import { ImageType } from "../../common/CommonTypes";

export interface NeighborhoodsProps {
  title: string;
  link: Link;
  cards: NeighborhoodCardProps[];
  componentId: string;
}

export interface NeighborhoodCardProps {
  title: string;
  ctaLink: string;
  image:ImageType
}

export interface Link {
  url: string;
  copy: string;
  targetInNewWindow: boolean;
}
