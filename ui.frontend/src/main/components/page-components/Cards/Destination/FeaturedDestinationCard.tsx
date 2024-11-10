import React from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";

const FeaturedDestinationCard = ({
    card,
    containerStyles,
    imageStyle,
    link,
}: any) => {
    const { image, alt, breakpoints, title, subTitle, tagline } = card;

    return (
        <a
            href={link?.url || card?.cardCtaLink}
            title={link?.copy}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            // eslint-disable-next-line max-len
            className={`${link?.url ? "cursor-pointer " : ""} block relative rounded-2xl bg-gray-200 w-[260px] h-[260px] lg:w-[283px] lg:h-[283px] ${containerStyles}`}
        >
            <div
                className="absolute ltr:left-4 rtl:right-4 top-4 ltr:lg:left-6
                          rtl:lg:right-6 lg:top-6 font-primary-regular ltr:pr-2 rtl:pl-2  z-[11]"
            >
                {title ? (
                    <div
                        className={`
                        ltr:text-xl ltr:lg:text-3.5xl ltr:line-clamp-1 ltr:leading-8 ltr:lg:leading-10
                        rtl:text-lg rtl:line-clamp-2 rtl:leading-7
                        font-primary-bold`}>{title}</div>
                ) : null}
                {subTitle || tagline ? (
                    <div
                        className={`text-sm lg:text-base line-clamp-2 font-primary-regular leading-5`}
                    >
                        {subTitle || tagline}
                    </div>
                ) : null}
            </div>

            {image && image.s7fileReference && (
                <img className="absolute bottom-0 ltr:right-0 rtl:left-0 rtl:scale-x-[-1] w-auto h-44 rounded-2xl" src={image.s7fileReference + '?fmt=png-alpha'} />

            )}
        </a>
    );
};

export default FeaturedDestinationCard;

