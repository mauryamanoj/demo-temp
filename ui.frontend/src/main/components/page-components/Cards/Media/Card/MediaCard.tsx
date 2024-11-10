import React from 'react';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import Text from 'src/main/components/common/atoms/Text/Text';

const MediaCard: React.FC<any> = ({ tags }) => {

    function handelClasses() {
        if (tags?.icon == 'facebook') {
            return 'relative ltr:left-[-3px] rtl:left-0.5 ltr:mr-[-3px] rtl:ml-[-3px]';
        }
        if (tags?.icon == 'twitter') {
            return 'relative ltr:left-[-3px] rtl:left-[3px] ltr:mr-[-3px] rtl:ml-[-3px]';
        }
        if (tags?.icon == 'instagram') {
            return 'relative ltr:left-[-3px] rtl:left-0.5 ltr:mr-[-5px] rtl:ml-[-5px]';
        }
    }

  
    return (
        <li>
            {tags?.link?.url && tags?.link?.text ? 
            <a className="flex"
                href={tags?.link?.url}
                target={tags?.link?.targetInNewWindow ? "_blank" : "_self"}
                title={tags?.link?.text} >
                <Icon name={`footer_share_${tags?.icon}`} svgClass={handelClasses()} />
                <Text
                    text={tags?.link?.text}
                    styles={'hover:bg-white focus:bg-white text-base lg:text-lg font-primary-regular ltr:pl-[8px] rtl:pr-[8px]'}
                />
            </a> : null}

        </li>
    );
};

export default MediaCard;
