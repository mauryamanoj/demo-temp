/* eslint-disable max-len */
import React from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";

import FooterList from "./FooterList";
import FooterLogo from "./FooterLogo";

import { FooterInterface } from "./IFooter";
import FooterNewsLetter from "./FooterNewsLetter";

import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from 'src/main/util/updateAnalyticsData';
import Text from "src/main/components/common/atoms/Text/Text";


const FooterComp: React.FC<FooterInterface> = (props) => {
    const {
        fragmentNewsletter,
        fragmentDownloads,
        fragmentBranding,
        fragmentInternalLinks,
        fragmentExternalLinks,
        fragmentContact,
        ctaListIcon,
        fragmentContainer,
    } = props;

    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    };

    function handleDownloadAppClick(item: any) {
        const pageInfo = extractPageInfoFromEventProperties();
        trackingEvent({
            event_name: "footer_download_app",
            title: item?.ctaLink.split("https://")[1].split("/")[0],
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            platform_name: item?.ctaLink.split("https://")[1].split("/")[0],
            city: pageInfo?.city
        });

        trackAnalytics({
            trackEventname: "footer_download_app",
            trackName: "dl_push",
            url: item?.ctaLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            platform_name: item?.ctaLink.split("https://")[1].split("/")[0],
        });
    }


    function handleIconClick(item: any) {
        const url = window.location.href;
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();
        trackingEvent({
            event_name: "footer_connect_with_us",
            title: item?.iconLabel,
            url: item?.iconLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            platform_name: item?.iconLabel
        });

        trackAnalytics({
            trackEventname: "footer_connect_with_us",
            trackName: "dl_push",
            title: item?.iconLabel,
            url: item?.iconLink,
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            platform_name: item?.iconLabel,
        });
    }

    return (
        <>
            <div className="flex flex-col md:flex-row justify-between items-start px-5 md:px-100 pb-6 md:pb-0 pt-6 md:pt-4 bg-white gap-0 md:gap-4 ">
                {fragmentDownloads && (
                    <div className="flex w-full md:w-auto mb-7 md:mb-5">
                        <div className="w-full">
                            <Text text={fragmentDownloads.title} styles="select-none leading-[22px] font-primary-bold" />

                            <div className="select-none	flex gap-1.5 md:gap-4 mt-2 md:mb-4 justify-between md:justify-normal">
                                {fragmentDownloads.ctaImageList.map(
                                    (download, index) => (
                                        <a href={download.ctaLink} target={download.isOpenInNewTab ? "_blank" : '_self'}
                                            className=" cursor-pointer"
                                            key={index}
                                            onClick={() => handleDownloadAppClick(download)}
                                        >
                                            <img
                                                src={download.ctaImage}
                                                className="w-full md:w-130 hover:scale-[1.05] hover:shadow-lg duration-300"
                                            />
                                        </a>
                                    )
                                )}
                            </div>
                        </div>
                    </div>
                )}

                {fragmentNewsletter &&
                    fragmentNewsletter.title &&
                    fragmentNewsletter.ctaLabel && (
                        <FooterNewsLetter {...fragmentNewsletter} />
                    )}
            </div>
            <div>
                <Icon name="ornament-h-g4-dark-48" svgClass="w-full h-12 text-gray-100" />
            </div>

            <div className="gap-5 md:gap-9 px-5 md:px-100 pt-10 md:pb-13 pb-6 justify-between grid grid-cols-1 md:grid-cols-4 bg-gray-300">
                {fragmentBranding && (
                    <div className="flex md:flex-col justify-between md:justify-start items-center md:mb-0 mb-6">
                        <FooterLogo {...fragmentBranding} />

                        <div className="flex flex-col items-center max-w-[140px]">
                            <p className="mt-0 md:mt-8 mb-1.5 text-xxs text-gray-350 line-clamp-1">
                                {fragmentBranding.poweredBy}
                            </p>

                            <a href={fragmentBranding.staLogoLink} className="max-w-[140px]">
                                <img src={fragmentBranding.staLogo} />
                            </a>
                        </div>
                    </div>
                )}

                {fragmentInternalLinks && (
                    <div className="flex flex-col gap-4 mb-2 md:mb-0">
                        <FooterList
                            titleLink={fragmentInternalLinks.titleLink}
                            ctaList={fragmentInternalLinks.ctaList}
                        />
                    </div>
                )}
                {fragmentExternalLinks && (
                    <div className="flex flex-col gap-4">
                        <FooterList
                            titleLink={fragmentExternalLinks.titleLink}
                            ctaList={fragmentExternalLinks.ctaList}
                        />
                    </div>
                )}
                <div className="flex flex-col gap-4">
                    {fragmentContact && (
                        <FooterList
                            titleLink={fragmentContact.titleLink}
                            ctaList={fragmentContact.ctaList}
                            sectionType={'contentUs'}
                        />
                    )}

                    {ctaListIcon && (
                        <div className="flex justify-start gap-4 md:mt-4">
                            {ctaListIcon?.map((cta, index) => (
                                <a href={cta.iconLink} target={cta.isOpenInNewTab ? "_blank" : '_self'}
                                    key={index}
                                    onClick={() => handleIconClick(cta)}
                                    title={cta?.iconLabel}
                                    className="hover:-translate-y-1  duration-300 cursor-pointer"
                                >
                                    {cta?.iconLabel && (
                                        <Icon
                                            name={`footer_share_${cta.iconLabel.toLowerCase()}`}
                                        />
                                    )}
                                </a>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {fragmentContainer && (
                <div className="flex flex-col md:flex-row relative justify-between items-center px-5 md:px-100 bg-black text-white py-2.5 text-xs font-primary-semibold text-center">
                    <a
                        onClick={scrollToTop}
                        title={fragmentContainer.upArrowCta}
                        className="absolute -top-[1.5rem] ltr:right-5 ltr:left-auto rtl:left-5 rtl:right-auto border border-white w-8 h-8 bg-black flex items-center justify-center rounded-full hover:-top-3.5 duration-300 cursor-pointer"
                    >
                        <Icon
                            name="arrow-left"
                            svgClass="text-white rotate-90 w-4"
                        />
                    </a>
                    <div>{fragmentContainer.copyrights}</div>
                    {fragmentContainer.ctaList && (
                        <div className="flex gap-2 md:gap-4 items-center">
                            {fragmentContainer.ctaList.map((cta, index) => (
                                <React.Fragment key={index}>
                                    <a
                                        href={cta.ctaLink}
                                        target={
                                            !!cta.isOpenInNewTab
                                                ? "_blank"
                                                : "_self"
                                        }
                                        className="hover:underline line-clamp-1"
                                    >
                                        {cta.ctaLabel}
                                    </a>
                                    {index !==
                                        fragmentContainer.ctaList.length -
                                        1 && (
                                            <div className="min-w-[7px] min-h-[7px] w-1.5 h-1.5 bg-white rounded-full mx-1" />
                                        )}
                                </React.Fragment>
                            ))}
                        </div>
                    )}
                </div>
            )}
        </>
    );
};

export default FooterComp;

