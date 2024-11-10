import React from "react";
import { getImage } from "../../../util/getImage";
import Picture from "../../common/atoms/Picture/Picture";
import Text from "../../common/atoms/Text/Text";
import { SingleAppPromotionalBannerProps } from "./ISingleAppPromotionalBanner";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";
import { trackEvents } from "src/main/util/updateAnalyticsData";

const SingleAppPromotionalBannerComp: React.FC<SingleAppPromotionalBannerProps>
= ({ title, description, link, stores, image, ctaData }) => {
    var cardInfo:any ={};
    const setGtmCustomLink = (link:any) => {
        cardInfo = {
         event: ctaData?.ctaEventName,
         section_name: ctaData?.sectionName,
         click_text: link?.text,
         link: link?.url,
         language: getLanguage(),
         page_category: ctaData?.pageCategory,
         page_subcategory: ctaData?.pageSubCategory,
         device_type: window.navigator.userAgent,
       };
       if(ctaData?.ctaEventName){
        gtmCustomEventClick(cardInfo);
       }
     };

     const setAdobeCustomLink = (link:any) => {
        const dataSet: any = {};
        dataSet.trackEventname = ctaData?.ctaEventName;
        dataSet.trackSection = ctaData?.sectionName;
        dataSet.trackName = link?.text;
        dataSet.trackEventname && trackEvents(dataSet);
      };


     function handleClick(link:any){
       setGtmCustomLink(link);
       setAdobeCustomLink(link);

       setTimeout(() => {
        window.open(link?.url, link?.targetInNewWindow ? "_blank" : "_self");
       }, 400);
     }
    return (
        <>
            <section className="md:flex bg-white md:py-10 pb-10 pt-100 px-5 lg:px-[100px] relative min-h-[280px]">
                <div
                    className="md:hidden flex visible absolute left-1/2 transform -translate-x-1/2 "
                    style={{ bottom: "calc(100% - 76px)" }}
                >
                    {image ? (
                        <Picture
                            image={getImage(image)}
                            imageClassNames="w-[250px] h-full object-cover"
                            containerClassName=""
                        />
                    ) : (
                        <></>
                    )}
                </div>
                <div className="md:flex md:items-center mr-2 md:w-8/12">
                    {link && link?.icon && (
                            <img
                                src={link?.icon}
                                className="w-[150px] h-[150px] object-contain hidden lg:block mr-8"
                                alt={link?.text}
                            />
                    )}
                    <div>
                        {title && (
                            <Text
                                styles={
                                    "font-primary-bold text-3xl md:text-5xl pb-4 leading-[60px]"
                                }
                                text={title}
                            />
                        )}
                        {description && (
                            <Text
                                styles={
                                    "font-primary-semibold text-lg md:text-1.5xl pb-4 leading-[27px]"
                                }
                                text={description}
                            />
                        )}
                        {link && link?.url && (
                            <span
                                className="font-primary-semibold text-lg md:text-1.5xl text-theme-100 cursor-pointer"
                                onClick={()=>handleClick(link)}
                            >
                                {link?.text || link?.url}
                            </span>
                        )}

                        <div className="flex items-center justify-between pt-5 md:hidden">
                            {stores?.map(
                                (store: any, i: number) =>
                                    store?.icon && (
                                        <img
                                            alt={store?.text}
                                            src={store?.icon}
                                            key={i}
                                            className={`h-8`}
                                            onClick={()=>{
                                                if(store?.url){
                                                    window.open(store?.url, "_blank");
                                                }
                                            }}
                                        />
                                    )
                            )}
                        </div>
                    </div>
                </div>
                <div className="md:w-4/12 hidden md:flex md:visible md:relative md:justify-end">
                    <div className="w-[400px] ">
                        {image ? (
                            <Picture
                                image={getImage(image)}
                                imageClassNames="md:w-full h-auto lg:h-[400px] absolute -bottom-[40px] object-cover"
                                containerClassName="w-full"
                            />
                        ) : (
                            <></>
                        )}
                    </div>
                </div>
            </section>
        </>
    );
};

export default SingleAppPromotionalBannerComp;

