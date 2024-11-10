import React from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { NeighborhoodCardProps } from "./INeighborhoodsProps";
import Text from "../../common/atoms/Text/Text";

const NeighborhoodCard: React.FC<NeighborhoodCardProps> = ({
    title,
    ctaLink,
    image,
}) => {
    return (
        <a href={ctaLink} className=" w-full md:w-96 rounded-2xl bg-white block md:bg-transparent cursor-pointer">
            <Picture
                image={getImage(image)}
                breakpoints={image?.breakpoints}
                imageClassNames="w-full md:rounded-2xl rounded-t-2xl"
                containerClassName="picture-element"
            />
            <Text
                text={title}
                styles="font-primary-bold text-lg md:text-[22px] capitalize p-4 md:px-2 md:pb-0 md:pt-4 truncate"
            />
        </a>
    );
};

export default NeighborhoodCard;

