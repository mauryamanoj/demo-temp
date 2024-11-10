/* eslint-disable max-len */
import React,{ useEffect} from 'react';
import Text from 'src/main/components/common/atoms/Text/Text';
import MediaCard from './Card/MediaCard';
import PhoneCard from './Card/PhoneCard';
import QuickCard from './Card/QuickCard';

const MediaCards: React.FC<any> = (props) => {
    const wrapper:any = document.querySelector('.wrapper');
    const { quickLinkCard, phoneCard, type, title, socialMediaCards, variation } = props;
    function renderMediaCard(socialMediaCards: any) {
        return (
            <div className='inline-block w-full p-6 bg-white rounded-[16px]' >
                {title ? <Text text={title} styles={'text-xl lg:text-1.5xl font-primary-bold pb-6'} /> : null}
                <ul className="grid grid-cols-2 gap-x-20 gap-y-4">
                    {socialMediaCards && socialMediaCards?.map((item: any, index: any) => (
                        item ? <MediaCard tags={item} key={index} /> : null
                    ))}
                </ul>
            </div>

        );
    }

  

    useEffect(()=>{
        if(!wrapper){
            const cards = document.querySelectorAll('.bg-white.inline-block');
            if(cards && cards.length !=0){
                for (let index = 0; index < cards.length; index++) {
                    const element = cards[index];
                    element?.classList?.add('lg:mb-6');
                    element?.classList?.add('mb-[20px]');
                }
            }
        }
    },[wrapper]);
    return (
        <div className="lg:flex content-center m-auto w-full lg:mb-0 mb-[20px] h-full">

            {/* start phone section */}
            {type == "PHONE_CARD" && phoneCard  &&
                <div className="inline-block p-6 bg-white rounded-[16px] w-full h-full" >
                    <PhoneCard PhoneCard={phoneCard} title={title} variation={variation} />
                </div>}
            {/* end phone section */}

            {/* start media section */}
            {type == "SOCIAL_MEDIA_CARD" &&
                <div className="w-full mb-[20px]">
                    {renderMediaCard(socialMediaCards)}
                </div>}
            {/* end media section */}

            {/* start chat with us section */}
            {type == "QUICK_LINK_CARD" && quickLinkCard ?
                <div className="lg:flex w-full h-full gap-8">
                    <div className="inline-block w-full  p-6 bg-white rounded-[16px]" >
                        <QuickCard item={quickLinkCard} title={title} />
                    </div>
                </div>
                : null}
            {/* end chat with us section */}

        </div>
    );
};

export default MediaCards;
