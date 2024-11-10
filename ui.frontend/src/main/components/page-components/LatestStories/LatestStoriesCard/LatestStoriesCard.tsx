import React, { useState } from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { ICardProps } from "./ICardProps";
import Overlay from "src/main/components/common/atoms/Overlay/Overlay";

const Card: React.FC<ICardProps> = ({
    title,
    image,
    className = "",
    imageClassNames = "",
    link,
    tags
}) => {
    return (
        <a href={link?.url} target={link?.targetInNewWindow ? '_blank' : '_self'}
            className={`relative !overflow-hidden group ${imageClassNames}`}>

            <Overlay
                classNames={`absolute hidden group-hover:block top-0 bottom-0 right-0 left-0 z-10 !h-full`}
                contentStyles={imageClassNames}
            />

            {image &&
                <Picture
                    imageClassNames={`w-full transform transition-transform duration-700 ease-in-out group-hover:scale-150 object-cover h-[699px] md:h-[51vw] md:max-h-[730px]`}
                    containerClassName={`h-auto !overflow-hidden ${imageClassNames}`}
                    image={getImage(image)}
                    breakpoints={image?.breakpoints}
                    hasRoundedCorners={false}
                />
            }
            <div className={`absolute bottom-0 text-white w-full
                backdrop-blur-[3px] bg-gradient-to-t from-black/40 px-6 z-20 ${className}`}>
                <div className={`group-hover:-translate-y-6 duration-300`}>
                    <h4
                        className="border-solid border-2 uppercase pt-10 pb-4
                        border-[#AAAAAA] border-t-0 border-l-0 border-r-0 font-primary-bold"
                    >
                        {tags &&
                            tags?.length != 0 &&
                            tags.map((tag) => tag).join(", ")}
                    </h4>
                    <a
                        href={link?.url}
                        target={link?.targetInNewWindow ? "_blank" : "_self"}
                        title={link?.title}
                        className="block pt-4 text-lg lg:text-1.5xl font-primary-bold pb-12"
                    >
                        <span className="line-clamp-2 h-[66px]">{title}</span>
                    </a>
                </div>
            </div>
        </a>
    );
};

export default Card;

