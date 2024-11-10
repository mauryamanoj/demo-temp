import React from "react";

import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import Pill from "src/main/components/common/atoms/Pill/Pill";
import { getLanguage } from "src/main/util/getLanguage";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { trackEvents } from "src/main/util/updateAnalyticsData";

const AppListCard: React.FC<any> = ({ variation, image, link, pill, title, subTitle, ctaData }) => {
  const setGtmCustomLink = () => {
    const cardInfo: any = {
      event: ctaData?.ctaEventName,
      section_name: ctaData?.sectionName,
      click_text: ctaData?.cardCtaLink,
      link: ctaData?.cardCtaLink,
      language: getLanguage(),
      page_category: ctaData?.pageCategory,
      page_subcategory: ctaData?.pageSubCategory,
      device_type: window.navigator.userAgent,
    };
    
    if(ctaData?.ctaEventName){
      gtmCustomEventClick(cardInfo);
    }
  

    const dataSet: any = {};
    dataSet.trackEventname = ctaData?.ctaEventName;
    dataSet.trackSection = ctaData?.sectionName;
    dataSet.trackEventname && trackEvents(dataSet);
  };

  const handleClick = () => {
    setGtmCustomLink();
    if (link && link.url) {
      window.open(link.url, link.targetInNewWindow ? "_blank" : "_self");
    }
  };

  return (
    <div className={`relative w-fit mb-2 lg:mb-6 rounded-2xl ${link ? "cursor-pointer" : ""}`} onClick={handleClick}>
      {pill ? <Pill children={pill} className="absolute top-2 right-2 font-primary-bold text-xs" /> : <></>}
      {image ? (
        <Picture
          image={getImage(image)}
          imageClassNames={`${variation === "big" ? "w-[283px] h-[283px]" : "w-[261px] h-[261px]"}
          ${title || subTitle ? "rounded-t-2xl" : "rounded-2xl"}`}
          containerClassName="picture-element"
        />
      ) : (
        <></>
      )}
      {title || subTitle ?
      <div
        className={`py-2 px-4 md:py-0 md:px-0 rounded-b-2xl font-primary-bold
        drop-shadow-md bg-white md:bg-transparent md:drop-shadow-none`}
      >
       {subTitle ? <div className="my-2 text-sm">{subTitle}</div>: <></>}
      {title ?  <div className="text-xl md:text-lg line-clamp-2 h-[60px]">{title}</div> : <></>}
      </div>
      : <></>}
    </div>
  );
};

export default AppListCard;
