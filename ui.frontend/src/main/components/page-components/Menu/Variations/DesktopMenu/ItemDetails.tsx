import React, { useEffect, useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { getLanguage } from "src/main/util/getLanguage";
import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from "src/main/util/updateAnalyticsData";
import { ImageType } from "src/main/components/common/CommonTypes";
import LanguagesModal from "../LanguagesModal";


const ItemDetails = ({ showItem, languages, onClick }: any) => {
  const [image, setImage] = useState<null | ImageType>(showItem?.image || null);

  useEffect(() => {
    setImage(showItem?.image || null);
  }, [showItem?.data]);

  const handleItemClick = (language: any, subItem: any) => {
    const url = window.location.href;
    //const pageInfo = extractPageInfo(url);
    const pageInfo = extractPageInfoFromEventProperties();

    trackingEvent({
      event_name: language ? "header_change_language" : "header_navigation",
      title: subItem?.ctaLabel,
      url: subItem?.ctaLink,
      page_category: pageInfo?.pageCategory,
      page_subcategory: pageInfo?.pageSubcategory,
      city: pageInfo?.city,
      event_category: "navigation",
      navigation_name: !language && subItem?.ctaLabel ? subItem?.ctaLabel : '',
      language: language ? language.ctaLink : getLanguage()
    });

    trackAnalytics({
      trackEventname: language ? "header_change_language" : "header_navigation",
      trackName: "dl_push",
      title: subItem?.ctaLabel,
      url: subItem?.ctaLink,
      page_category: pageInfo?.pageCategory,
      page_subcategory: pageInfo?.pageSubcategory,
      city: pageInfo?.city,
      event_category: "navigation",
      navigation_name: !language && subItem?.ctaLabel ? subItem?.ctaLabel : '',
      language: language ? language.ctaLink : getLanguage()
    });
  };

  const HTMLItem = (index: any, subItem: any, language?: boolean) => (
    <a href={subItem.ctaLink}
      className={`border-l-[1px] rtl:border-l-0 rtl:border-r-[1px] border-gray/50 py-1 px-2
                       mb-6 cursor-pointer block truncate line-clamp-1 max-w-[200px] text-ellipsis
                       font-primary-semibold text-base hover:border-theme-100 hover:text-theme-100
                       ${subItem.isSelected ? "border-theme-100 text-theme-100" : ""}`}
      key={index}
      onClick={() => handleItemClick(language, subItem)}
      onMouseEnter={() => {
        setImage(null);
        setTimeout(() => setImage(subItem.image), 10);
      }}
    >
      {subItem.ctaLabel}
    </a>
  );

  return (
    <div className="fixed w-screen h-screen left-0 top-20 flex justify-center z-40">
      <div className="pt-4 h-fit" onMouseLeave={onClick}>
        <div
          className={`bg-gray-300 w-[80vw] pl-32 rtl:pl-0 rtl:pr-32 pt-10 h-64
                                rounded-2xl shadow-lg text-black relative
                                transition-all duration-300`}
        >
          {languages ? (
            <LanguagesModal languages={languages} setImage={setImage} />

          ) : (
            <div
              className="flex gap-5 w-3/5 overflow-y-auto max-h-40"
              onMouseLeave={() => {
                setImage(null);
                setImage(showItem?.image || null);
              }}
            >
              <div>{showItem.data.slice(0, 3).map((subItem: any, index: any) => HTMLItem(index, subItem))}</div>
              <div>{showItem.data.slice(3, 6).map((subItem: any, index: any) => HTMLItem(index, subItem))}</div>
              <div className={`flex ${showItem.data.length > 1 ? "items-end" : "items-start"}`}>
                <div className="py-1 px-2 mb-4 h-[32px]">
                  <a onClick={() => handleItemClick(null, { ctaLabel: showItem.viewAllLabel, ctaLink: showItem.viewAllLink })} href={showItem.viewAllLink} className="cursor-pointer text-theme-100 font-primary-bold">
                    {showItem.viewAllLabel}
                  </a>
                </div>
              </div>
            </div>
          )}
          {!languages && image ? (
            <div className="absolute right-0 rtl:left-0 rtl:right-auto bottom-0 z-10">
              <Picture
                image={getImage(image)}
                breakpoints={image?.breakpoints}
                imageClassNames={`rounded-e-2xl transition-all duration-400 h-64 w-auto`}
              />
            </div>
          ) : (
            <></>
          )}
          <div className="absolute bottom-0 left-0 h-12 w-full">
            <Icon name="ornament-h-g2-light-48" svgClass="w-full h-12 rounded-b-2xl" />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ItemDetails;
