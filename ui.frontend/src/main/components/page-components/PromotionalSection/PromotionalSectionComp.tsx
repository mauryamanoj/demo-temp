import React from "react";
import Button from "../../common/atoms/Button/Button";
import Picture from "../../common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";
import { trackEvents } from "src/main/util/updateAnalyticsData";
import { useResize } from "src/main/util/hooks/useResize";

const PromotionalSectionComp = ({ title, subTitle, ctaLabel, ctaLink, image, ctaData }: any) => {
  const { isMobile } = useResize();

  var cardInfo: any = {};

  const setGtmCustomLink = (link: any, ctaLabel: string) => {
    cardInfo = {
      event: ctaData?.ctaEventName,
      section_name: ctaData?.sectionName,
      click_text: ctaLabel,
      link: link,
      language: getLanguage(),
      page_category: ctaData?.pageCategory,
      page_subcategory: ctaData?.pageSubCategory,
      device_type: window.navigator.userAgent,
    };
    if (ctaData?.ctaEventName) {
      gtmCustomEventClick(cardInfo);
    }

  };

  const setAdobeCustomLink = (ctaLabel?: string) => {
    const dataSet: any = {};
    dataSet.trackEventname = ctaData?.ctaEventName;
    dataSet.trackSection = ctaData?.sectionName;
    dataSet.trackName = ctaLabel;
    dataSet.trackEventname && trackEvents(dataSet);
  };


  function handleClick(link: any, ctaLabel: string) {
    setGtmCustomLink(link, ctaLabel);
    setAdobeCustomLink(ctaLabel);

  }

  return (
    //Note: we need the margin-top here since we have an overlay image
    <section className={`md:flex md:justify-between md:gap-4
                       bg-white mt-5 px-5 md:px-100 py-10 relative`}>
      <div className={`w-full md:w-[40%] lg:w-[60%]`}>
        <div className="font-primary-bold text-3xl md:text-5xl mb-4 leading-[37px] md:leading-[60px]">{title}</div>
        <div className="font-primary-semibold text-lg md:text-1.5xl leading-[22.05px]
         md:leading-[26.95px]">{subTitle}</div>
        {ctaLabel && ctaLink ? (
          <div className="pt-8 inline-block">
            <a href={ctaLink}
              onClick={() => handleClick(ctaLink, ctaLabel)}
              className="rounded-lg flex items-center justify-center transition-all duration-300 bg-theme-100 hover:bg-theme-200 border border-theme-100 hover:border-theme-200 text-white py-1.5 px-7 text-sm md:text-base font-primary-semibold capitalize"
            >{ctaLabel}</a>
          </div>
        ) : (
          <></>
        )}
      </div>
      <div className={`ltr:right-100 rtl:left-100 ${isMobile ? "mt-6" : "-mb-10"}`}>
        {image &&
          <Picture
            image={getImage(image)}
            breakpoints={!!image?.isTransparent ? '' : image?.breakpoints}
            imageClassNames={`w-[355px] h-[355px] md:mt-[-140px] mt-auto`}
            containerClassName={"flex justify-center"}
          />}
      </div>
    </section>
  );
};

export default PromotionalSectionComp;
