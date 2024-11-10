import React from "react";
import MediaCards from "../Cards/Media/MediaCards";

export interface ReachUs {
    proxy: Proxy
    type: string
    title: string
    quickLinkCard: QuickLinkCard
  }
  
  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface Proxy {}
  
  export interface QuickLinkCard {
    subTitle: string
    link: Link
  }
  
  export interface Link {
    hideMap: boolean
    url: string
    urlWithExtension: string
    urlSlingExporter: string
    targetInNewWindow: boolean
    text: string
    appType: string
  }

  
const ReachUs: React.FC<ReachUs> = (props) => {
    return (
        <MediaCards {...props} />
    );
};

export default ReachUs;

