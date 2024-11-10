import React from 'react';
import Icon from '../Icon/Icon';
import Text from '../Text/Text';

export const NoResult: React.FC<any> = ({ title, description }) => {
    return (
        <>
            <div className="text-center">
                <span ></span>
                <Icon name="empty"  svgClass="m-auto w-full w-[324px] h-[162px] lg:mb-[119px] lg:mt-0 mb-[89px] mt-[89px]" />
                <div>
                {title ? <Text text={title}
                    styles="lg:text-5xl text-3xl font-primary-bold lg:mb-6 mb-4" type="div" /> : null}
                {description ? <Text text={description}
                    styles="text-sm lg:text-base font-primary-regular text-gray-350" type="p" /> : null}
                </div>
            </div>
        </>
    );
};
