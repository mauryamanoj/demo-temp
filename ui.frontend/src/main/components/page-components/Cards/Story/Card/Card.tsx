import React, { useState } from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { ImageType } from "src/main/components/common/CommonTypes";
import Overlay from "src/main/components/common/atoms/Overlay/Overlay";

interface Item {
    title?: string;
    description?: string;
    image: ImageType;
}

interface Props {
    item: Item;
}

const Card: React.FC<Props> = ({ item }) => {
    const [isHovered, setIsHovered] = useState(false);
    return (
        <li
            className="relative max-w-sm rounded overflow-hidden shadow-lg inline-block mx-2 "
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            {isHovered && (
                <Overlay classNames="absolute top-0 bottom-0 right-0 left-0 z-10 !h-full" />
            )}
            {item?.image ? (
                <Picture
                    imageClassNames={"w-full"}
                    image={getImage(item?.image)}
                    breakpoints={item?.image?.breakpoints}
                />
            ) : (
                <></>
            )}
            <div className="px-4 py-10 lg:py-12 absolute bottom-0 text-blk text-white w-full z-20">
                <h4
                    className="border-solid border-2 py-4
         lg:py-2 border-[#AAAAAA] border-t-0 border-l-0 border-r-0 lg:mb-2 font-primary-semibold"
                >
                    {item?.title}
                </h4>
                <p className="text-lg lg:text-1.5xl lg:mb-2 py-4 lg:py-0 font-primary-semibold">
                    {item?.description}
                </p>
            </div>
        </li>
    );
};

export default Card;

