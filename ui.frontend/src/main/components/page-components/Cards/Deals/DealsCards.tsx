/* eslint-disable max-len */
import React from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination, Scrollbar } from 'swiper';

import Picture from 'src/main/components/common/atoms/Picture/Picture';
import { getImage } from 'src/main/util/getImage';
import Button from "src/main/components/common/atoms/Button/Button";




const DealsCards: React.FC<any> = (props) => {
    const { type, componentTitle, cards } = props;

    const image = {
        "fileReference": "/content/dam/saudi-tourism/media/tour-packages/ahsa/760x570/1MV - FINAL.00_00_16_30720.Still005_1440 x 1080.jpg",
        "mobileImageReference": "/content/dam/saudi-tourism/media/tour-packages/ahsa/760x570/1MV - FINAL.00_00_16_30720.Still005_1440 x 1080.jpg",
        "alt": "One day in Riyadh",
        "s7fileReference": "https://scth.scene7.com/is/image/scth/1MV - FINAL.00_00_16_30720.Still005_1440 x 1080-3",
        "s7mobileImageReference": "https://scth.scene7.com/is/image/scth/1MV - FINAL.00_00_16_30720.Still005_1440 x 1080-3",
        "desktopImage": "https://qa-perf.visitsaudi.com/content/dam/no-dynamic-media-folder/Jeddah_Balad-at-night-6ar.jpg",
        "mobileImage": "https://scth.scene7.com/is/image/scth/1MV - FINAL.00_00_16_30720.Still005_1440 x 1080-3:crop-360x480?defaultImage\u003d1MV - FINAL.00_00_16_30720.Still005_1440 x 1080-3"
    };

    return (
        <>
            <div>
                <div className='ml-5 md:ml-100 rtl:ml-[auto] rtl:mr-5 md:rtl:ml-[auto] md:rtl:mr-100
                                relative flex flex-col gap-2 md:gap-4 w-[260px] md:w-[390px]' id="DealsCards">

                    <div className='text-center'>
                        <h1 className='text-xl md:text-3.5xl'>{componentTitle.text}</h1>
                        <p className='text-sm md:text-base text-gray font-primary-regular'>Check out the amazing car rental deals. A wide range of cars that would suit all needs and budgets. Hire your car now.</p>

                        <Button title={"Button Label"}
                            styles="m-0"
                            buttonType='type3'
                        />
                    </div>
                    {image ?
                    <Picture
                        image={getImage(image)}
                        imageClassNames="w-full h-full rounded-b-2xl"
                        containerClassName="picture-element"
                    />
                    : <></>}
                </div>

            </div>
        </>
    );
};

export default DealsCards;
