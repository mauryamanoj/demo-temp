import React from "react";
import Text from "src/main/components/common/atoms/Text/Text";
import { IDestinationGuideCardProps } from "../IDestinationGuideProps";

const TextContent: React.FC<IDestinationGuideCardProps> = ({
    type,
    description,
}) => {
    return (
        <div
            className="richTextContainer ulPadding font-primary-regular text-black text-lg leading-[19.2px] md:leading-[21.6px]"
            dangerouslySetInnerHTML={{
                __html: description || "",
            }}
        />
    );
};

export default TextContent;

