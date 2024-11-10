import React from "react";

import { OffersAndDealsCardProps } from "../IOffersAndDeals";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import Text from "src/main/components/common/atoms/Text/Text";
import { getImage } from "src/main/util/getImage";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";

const OffersCard: React.FC<OffersAndDealsCardProps> = ({
    title,
    description,
    image,
    link,
    cta,
    cardCtaData,
    title2,
    description2,
}) => {
    var cardInfo: any = {};

    const setGtmCustomLink = (link: any) => {
        cardInfo = {
            event: cardCtaData?.ctaEventName,
            section_name: cardCtaData?.sectionName,
            click_text: title,
            link: link?.url,
            language: getLanguage(),
            page_category: cardCtaData?.pageCategory,
            page_subcategory: cardCtaData?.pageSubCategory,
            device_type: window.navigator.userAgent,
        };
        if (cardCtaData?.ctaEventName) {
            gtmCustomEventClick(cardInfo);
        }
    };


    function cardWithDetails() {
        return title?.length || description?.length || cta?.text?.length || cta?.url?.length;
    }
    return (
        <a href={link?.url ? link?.url : cta?.url} target={link?.targetInNewWindow ? '_blank' : '_self'} className="md:w-[389px] cursor-pointer select-none block"
            onClick={(event) => {
                const target = event.target as HTMLElement;
                if (!target.closest('button')) {
                    setGtmCustomLink(link);
                }
            }}>
            <div className="bg-white rounded-[20px] select-none">
                {cardWithDetails() && (
                    <div className="px-6 pt-6 flex flex-col justify-center items-center">
                        {title && (
                            <Text
                                text={title}
                                styles="font-primary-bold text-lg md:text-1.5xl text-center w-full leading-[27px] md:leading-[32px] rtl:leading-[24px] rtl:md:leading-[40px] overflow-hidden line-clamp-2 h-14 md:h-16 rtl:h-12 rtl:md:h-20"
                            />
                        )}
                        {description && (
                            <Text
                                text={description}
                                styles="font-primary-regular text-sm md:text-base my-2 md:my-4 text-center text-gray-350
                                        overflow-hidden line-clamp-3 ltr:h-[63px] ltr:md:h-[72px] rtl:h-[60px] rtl:md:h-[70px] leading-5 md:leading-6"
                            />
                        )}
                        {cta && cta.url && cta.text &&
                            <a href={cta?.url} onClick={(e) => e.stopPropagation()} target={cta?.targetInNewWindow ? '_blank' : '_self'} className="font-primary-bold mb-3 text-sm themed hover:text-theme-200">{cta?.text}</a>
                        }
                    </div>

                )}
                {image && Object.entries(image).length !== 0 && (
                    <Picture image={getImage(image, 767)}
                        imageClassNames={`w-full h-[260px] md:h-[365px] object-cover rounded-[20px]
                                        ${cardWithDetails() && '!rounded-t-none'}
                                        ${(title2 || description2) && '!rounded-b-none md:!rounded-b-[20px]'}`}
                    />
                )}

            </div>

            {(title2 || description2) &&
                <div className="p-4 md:p-2 bg-white md:bg-transparent rounded-b-2xl font-primary-bold offersCardlowerWhiteBgSelector">
                    <div className="leading-[22px] line-clamp-1">{title2}</div>
                    <div className="mt-2 text-xl md:text-lg leading-[29.7px] md:leading-7 line-clamp-2">{description2}</div>
                </div>
            }

        </a>
    );
};

export default OffersCard;

