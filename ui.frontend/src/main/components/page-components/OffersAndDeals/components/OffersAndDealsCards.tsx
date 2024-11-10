import React from "react";

import { OffersAndDealsCardProps } from "../IOffersAndDeals";
import Picture from "../../../common/atoms/Picture/Picture";
import Text from "../../../common/atoms/Text/Text";
import Button from "../../../common/atoms/Button/Button";
import { getImage } from "src/main/util/getImage";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";

const OffersAndDealsCard: React.FC<OffersAndDealsCardProps> = ({
    title,
    description,
    image,
    link,
    cta,
    cardCtaData
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

    function handleButtonClick(link: any) {
        setGtmCustomLink(link);
        setTimeout(() => {
            window.open(
                link?.url,
                !!link?.targetInNewWindow ? "_blank" : "_self"
            );
        }, 400);
    }


    function cardWithDetails() {
        return title?.length || description?.length || cta?.text?.length || cta?.url?.length;
    }
    return (
        <section>

            <a href={link?.url ? link?.url : cta?.url} target={link?.targetInNewWindow ? "_blank" : "_self"}
                className="bg-white rounded-[20px] md:w-[389px] cursor-pointer block select-none"
                onClick={(event) => {
                    const target = event.target as HTMLElement;

                    if (!target.closest('button')) {
                        handleButtonClick(link);
                    }
                }}
            >
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
                    <Picture
                        imageClassNames={`w-full h-[260px] md:h-[365px] object-cover
                                         ${cardWithDetails() ? "rounded-b-[20px]" : "rounded-[20px]"}`}
                        image={getImage(image, 767)}
                    />
                )}
            </a>
        </section>
    );
};

export default OffersAndDealsCard;

