import React from "react";
import { IHeaderSection } from "./IHeaderSection";
import Text from "../../common/atoms/Text/Text";
import Picture from "../../common/atoms/Picture/Picture";
import { getImage } from "../../../util/getImage";

// todo: change titleImage to image
const HeaderSection = ({
    title,
    subtitle,
    byLabel,
    author,
    titleImage,
}: IHeaderSection) => {
    return (
        <>
            <section className="pt-2 gap-6 !mb-6 md:!mb-10">
                {titleImage && titleImage?.s7fileReference ? (
                    <Picture
                        imageClassNames="w-full h-full"
                        image={getImage(titleImage)}
                        alt={titleImage.alt}
                    />
                ) : (
                    title && (
                        <>
                            <Text
                                styles="text-5.5xl md:text-8xl font-secondary-bold leading-[55.2px] md:leading-[96px]"
                                text={title}
                            />
                            {subtitle && (
                                <Text
                                    styles="text-lg md:text-1.5xl mt-6 font-primary-semibold"
                                    text={subtitle}
                                />
                            )}
                        </>
                    )
                )}

                {author?.image && author.authorCtaLink && author.authorText && (
                    <div className="justify-start flex w-full gap-2 items-center mt-6">
                        {author.image && (
                            <div className="w-auto">
                                <Picture
                                    imageClassNames="w-[30px] h-[30px] rounded-full"
                                    image={getImage(author.image)}
                                    alt={author.image.alt}
                                />
                            </div>
                        )}
                        {byLabel && (
                            <Text
                                styles="text-lg md:text-1.5xl font-primary-semibold"
                                text={byLabel}
                            />
                        )}
                        <a href={author.authorCtaLink} target={"_blank"}>
                            <Text
                                styles="text-lg font-primary-semibold text-theme-100 hover:text-theme-200"
                                text={author.authorText}
                            />
                        </a>
                    </div>
                )}
            </section>
        </>
    );
};

export default HeaderSection;

