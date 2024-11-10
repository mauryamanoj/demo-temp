/* eslint-disable max-len */
import React from "react";
import { ITextImageSection } from "./ITextImageSection";
import Text from "../../common/atoms/Text/Text";
import Button from "../../common/atoms/Button/Button";
import Picture from "../../common/atoms/Picture/Picture";
import { getImage } from "../../../util/getImage";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";
import { trackEvents } from "src/main/util/updateAnalyticsData";

const TextImageSection = ({
    title,
    description,
    link,
    image,
    position,
    skipMargin,
    logo,
    whiteBackground,
    ctaData
}: ITextImageSection) => {
    var cardInfo: any = {};

    const setGtmCustomLink = (link: any) => {
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
        if (ctaData?.ctaEventName) {
            gtmCustomEventClick(cardInfo);
        }

    };

    const setAdobeCustomLink = (link: any) => {
        const dataSet: any = {};
        dataSet.trackEventname = ctaData?.ctaEventName;
        dataSet.trackSection = ctaData?.sectionName;
        dataSet.trackName = link?.text;
        dataSet.trackEventname && trackEvents(dataSet);

    };

    const handleClick = (link: any) => {
        setGtmCustomLink(link);
        setAdobeCustomLink(link);
    };

    const formatDescription = (htmlContent: any) => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(htmlContent, "text/html");

        const tables = doc.querySelectorAll("table");
        tables.forEach((table) => {
            table.style.borderCollapse = "collapse";
            table.style.width = "100%";
            table.style.backgroundColor = "white";

            const firstRow = table.querySelector("tr");
            if (firstRow) {
                firstRow.classList.add("bg-gray-100");
            }

            table.querySelectorAll("th, td").forEach((cell: any) => {
                cell.style.border = "1px solid #000";
                cell.style.padding = "8px";
            });
        });

        return doc.body.innerHTML;
    };

    const containerClassName = position === "top" ? "flex-col" : position === "left" ? "flex-col-reverse md:flex-row md:gap-9 items-center" : position == "right" ? "flex-col-reverse md:flex-row-reverse md:gap-9 items-center" : null;
    const imageClassName = position === "top" ? "w-full md:h-[32vw] h-[55vw] mb-6 md:mb-4" : position === "left" ? "md:w-6/12 md:h-[339px] h-[199px]" : position == "right" ? "md:w-6/12 md:h-[339px] h-[199px]" : null;

    return (
        <div className={`flex w-full ${containerClassName} ${whiteBackground ? "bg-white py-10" : "bg-transparent"}  ${!skipMargin && 'px-5 md:px-100'}`}>

            {title && position == "top" && (
                <Text styles="capitalize text-xl md:text-3.5xl font-primary-bold mb-4 leading-tight" text={title} />
            )}

            {image && image.fileReference && (
                <div className={`${imageClassName} relative`}>
                    <Picture
                        imageClassNames="rounded-[20px] object-cover h-full w-full md:max-h-full"
                        image={getImage(image)}
                        breakpoints={image?.breakpoints}
                        alt={image?.alt}
                    />
                    {logo && logo.fileReference && (
                        <img
                            src={getImage(logo)}
                            className={`${logo.isTransparent ? "bg-transparent" : ""
                                } absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2
                            w-[56.74px] h-[91.40px] md:w-[96.77px] md:h-[155.87px]`}
                        />
                    )}
                </div>
            )}


            <div className={`${!image || !image.fileReference || position == "top" ? "md:w-full" : "md:w-6/12 md:py-8"}`}>
                {title && position != "top" && (
                    <Text
                        styles="capitalize text-3xl md:text-5xl font-primary-bold mb-4 leading-tight"
                        text={title}
                    />
                )}

                {description && (
                    <div
                        className={`font-primary-regular text-base leading-[19.2px] ${link && link.url && link.text ? "md:mb-10 mb-5" : ""
                            } richTextContainer`}
                        dangerouslySetInnerHTML={{
                            __html: formatDescription(description),
                        }}
                    ></div>
                )}
                {link && link.url && link.text && (
                    <div className="inline-flex justify-items-start md:w-auto w-full mb-8 md:mb-0 ">
                        <a
                            href={link.url}
                            target={link.targetInNewWindow ? "_blank" : "_self"}
                            className="font-primary-semibold justify-center items-center inline-flex capitalize
                            rounded-lg transition-all duration-300 bg-theme-100 hover:bg-theme-200 border border-theme-100 hover:border-theme-200 text-white py-2.5 px-6 text-base md:w-auto w-full"
                            onClick={() => handleClick(link)}>{link.text}</a>
                    </div>
                )}
            </div>

        </div>
    );
};

export default TextImageSection;

