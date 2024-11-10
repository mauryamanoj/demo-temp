import React from 'react';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import Text from 'src/main/components/common/atoms/Text/Text';

const QuickCard: React.FC<any> = (porps) => {
    const { item, title } = porps;
    return (

    <div className='flex gap-4 items-center w-full'
    >
        <div className='flex flex-col'>
            {title ? <Text
                text={title}
                styles={'text-xl lg:text-1.5xl font-primary-bold pb-[10px]'}
            /> : null}
            {item.subTitle ? <Text
                text={item.subTitle}
                styles={'text-sm lg:text-base font-primary-regular'}
            /> : null}

        </div>
        {/* link.targetInNewWindow */}
        { item.link.url  &&
            <a href={item?.link?.url }  target={item?.link?.targetInNewWindow
                ? '_blank' : ''} className='inline-block ltr:ml-auto rtl:mr-auto cursor-pointer'>
                <Icon name={`externalLink`} />
            </a>
        }
    </div>
    );
};

export default QuickCard;
