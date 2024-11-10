import { ImageType } from "../../common/CommonTypes";

export interface OffersAndDealsProps {
  title: string;
  type: Type;
  link: Link;
  image: ImageType
  cards: OffersAndDealsCardProps[];
  ctaData?: any;
  isWithOrnament?: boolean
}

export interface OffersAndDealsCardProps {
  title: string;
  description: string;
  link: Link;
  cta: Link;
  image: ImageType;
  cardCtaData?: any;
  title2?: string;
  description2?: string;
}

export interface Link {
  url: string;
  text: string;
  targetInNewWindow: boolean;
}

export enum Type {
  Deal = "deal",
  Offer = "offer"
}
