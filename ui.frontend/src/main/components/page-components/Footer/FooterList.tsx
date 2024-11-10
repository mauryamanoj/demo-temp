/* eslint-disable max-len */
import React from "react";
import { FragmentLinks } from "./IFooter";
import { useResize } from "src/main/util/hooks/useResize";
import Collapsible from "src/main/components/common/atoms/Collapsible/Collapsible";
import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from 'src/main/util/updateAnalyticsData';
import Text from "src/main/components/common/atoms/Text/Text";

const FooterList: React.FC<FragmentLinks> = (props) => {
    const { titleLink, ctaList, sectionType } = props;
    const { isMobile } = useResize();

    function handleLinkClick(item: any) {
        const isWhatsapp = item.ctaLink.includes('api.whatsapp.com');
        const isHelpCenter = item.ctaLink.includes('help-center');
        const isContactUs = item.ctaLink.includes('contact-us');
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: sectionType == 'contentUs' && !isHelpCenter && !isContactUs ? (isWhatsapp ? "contact_us_whatsapp" : "footer_connect_with_us") : 'footer_navigation',
            title: item?.ctaLabel,
            url: item.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            platform_name: item?.ctaLabel
        });

        trackAnalytics({
            trackEventname: sectionType == 'contentUs' && !isHelpCenter && !isContactUs ? (isWhatsapp ? "contact_us_whatsapp" : "footer_connect_with_us") : 'footer_navigation',
            trackName: "dl_push",
            title: item?.ctaLabel,
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            platform_name: item?.ctaLabel
        });
    }

    return (
        <>
            {isMobile ? (
                <Collapsible
                    isFooter
                    collapsed
                    title={titleLink}
                    className="text-sm font-semibold"
                    children={
                        <>
                            {ctaList.map((link, index) => (
                                <a href={link.ctaLink} target={link.isOpenInNewTab ? "_blank" : '_self'}
                                    key={index}
                                    onClick={() => handleLinkClick(link)}>
                                    {link.ctaLabel}
                                </a>
                            ))}
                        </>
                    }
                    childrenClass="flex flex-col gap-4 text-gray pt-4"
                />
            ) : (
                <>
                    <Text text={titleLink} styles="font-primary-bold line-clamp truncate text-sm" />
                    {ctaList.map((link, index) => (
                        <a href={link.ctaLink} target={link.isOpenInNewTab ? "_blank" : '_self'}
                            key={index}
                            onClick={() => handleLinkClick(link)}
                            className="cursor-pointer hover:translate-x-1 duration-300 font-primary-regular text-gray-350 text-base"
                        >
                            {link.ctaLabel}
                        </a>
                    ))}
                </>
            )}
        </>
    );
};
export default FooterList;

