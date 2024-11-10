/* eslint-disable max-len */
import React from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination, Scrollbar } from 'swiper';

import Picture from 'src/main/components/common/atoms/Picture/Picture';
import { getImage } from 'src/main/util/getImage';
import Pill from 'src/main/components/common/atoms/Pill/Pill';


const OffersCards: React.FC<any> = (props) => {
    const { type, componentTitle, cards } = props;

    return (
        <>
            <div>
                <div className='ml-5 md:ml-100 rtl:ml-[auto] rtl:mr-5 md:rtl:ml-[auto] md:rtl:mr-100
                                relative w-[260px] md:w-[283px]'
                    id="DealsCards">
                    <Pill children="new" className='absolute top-2 right-2' />
                    {cards[0]?.image ?
                    <Picture
                        image={getImage(cards[0].image)}
                        imageClassNames="w-full h-full rounded-t-2xl md:rounded-2xl"
                        containerClassName="picture-element"
                    />
                    : <></>}
                    <div className='py-2 px-4 md:py-0 md:px-0 shadow-[0_15px_30px_0px_rgba(0,0,0,0.08)] md:shadow-none rounded-b-2xl'>
                        <h2 className='my-2'>Starting 1-Jun to 30-Aug</h2>
                        <h1 className='text-xl md:text-lg'>{componentTitle.text}</h1>
                    </div>
                </div>

            </div>
        </>
    );
};

export default OffersCards;
