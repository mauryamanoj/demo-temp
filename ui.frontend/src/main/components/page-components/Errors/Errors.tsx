import React from "react";
import { getImage } from "src/main/util/getImage";
import Picture from "../../common/atoms/Picture/Picture";
import Text from "../../common/atoms/Text/Text";

const Errors: React.FC<any> = (props) => {
    const { image, title, description } = props;
    return (
        <div className="flex justify-center items-center flex-col md:mb-20 mb-16">
            <Picture
                imageClassNames="w-auto h-full"
                image={getImage(image, 767)}
                alt={title}
                containerClassName="flex justify-center md:max-w-[400px]  md:min-h-[400px] max-w-[300px] min-h-[300px]  md:mt-10"
            />
            <div className="text-center mt-6">
                {title ? <Text
                    styles="text-3xl md:text-5xl font-primary-bold leading-[37.14px] md:leading-[59.42px]"
                    text={title}
                /> : null}

                {description ? <Text
                    styles="md:max-w-[446px] md:px-0 px-5 m-auto mt-6 md:text-base text-sm md:text-3.5xl font-primary-regular text-[#4B4B4B] leading-[16.41px] md:leading-[18.75px]"
                    text={description}
                /> : null}
            </div>

        </div>
    );
};

export default Errors;

