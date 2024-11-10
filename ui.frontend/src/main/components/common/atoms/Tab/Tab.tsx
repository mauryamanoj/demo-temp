// src/components/Tabs.tsx
import React, { useEffect, useState } from "react";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";

interface TabProps {
  tabTitles: string[];
  tabContents?: React.ReactNode[];
  onClick?: (index: number) => void;
  className?: string;
  btnClassName?:string;
  buttonTitle?:string
  ctaData?:any;
  selected?:number;
}

const Tabs: React.FC<TabProps> = ({ tabTitles, tabContents, onClick,
  className, btnClassName, buttonTitle, ctaData, selected }) => {
  const [activeTab, setActiveTab] = useState(0);
  const [isNext, setIsNext] = useState(true);
  var info:any={};

  const setGtmCustomLink = (title:any,ctaData:any) => {
    info = {
     event: ctaData?.ctaEventName,
     section_name: ctaData?.sectionName,
     click_text: title,
     language: getLanguage(),
     page_category: ctaData?.pageCategory,
     page_subcategory: ctaData?.pageSubCategory,
     device_type: window.navigator.userAgent,
   };
   gtmCustomEventClick(info);
 };
  const handleClick = (index: number, title:string, ctaData:any) => {
    setIsNext(activeTab < index)
    setActiveTab(index);
    if(ctaData?.ctaEventName){
      setGtmCustomLink(title,ctaData);
    }
    if (onClick) {
      onClick(index);
    }
  };

  useEffect(()=>{
if(selected !== undefined && selected !== activeTab) {setActiveTab(selected);}
  },[selected]);

  return (
    <div>
      <div
        className={`absolute bottom-3  w-full px-100
        flex justify-evenly text-white text-base ${className ?? ""}`}
      >
        {tabTitles.map((title, index) => (
          <button
            title={buttonTitle && buttonTitle[0] ? buttonTitle[index]:""}
            key={index}
            className={`w-full p-4 border-t-[1px] border-[rgba(208,208,208,0.5)] relative ${btnClassName ?? ""}
            ${activeTab === index ? "font-primary-bold opacity-100" :
            "font-primary-semibold opacity-80"} leading-5`}
            onClick={() => handleClick(index,title,ctaData && ctaData[0] ? ctaData[index]:"")}
          >
            <div className={`border-white border-t-[1px] absolute -top-[1px]
            transition-all duration-1000
            ${isNext ? "ltr:left-0 rtl:right-0" : "ltr:right-0 rtl:left-0"}
            ${activeTab === index ?"w-full": "w-0"}`}></div>
            {title?.substring(0,38)}
          </button>
        ))}
      </div>
      {tabContents ? <div className="p-20">{tabContents[activeTab]}</div> : <></>}
    </div>
  );
};

export default Tabs;
