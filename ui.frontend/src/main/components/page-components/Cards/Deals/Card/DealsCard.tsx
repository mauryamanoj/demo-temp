import React from 'react';
import Picture from 'src/main/components/common/atoms/Picture/Picture';
import { getImage } from 'src/main/util/getImage';


const DealsCard: React.FC<any> = ({ card }) => {
    return (
        <div className='h-[41.7vw] lg:h-[15vw] relative flex justify-center items-center'>
            <div className='absolute top-0 left-0 w-full h-full bg-black opacity-30 z-10 rounded-2xl'></div>
            <h1 className="absolute z-10 text-white text-xl md:text-3.5xl">{card.title}</h1>
            {card?.image ?
            <Picture
                image={getImage(card?.image)}
                breakpoints={card?.image?.breakpoints}
                imageClassNames="w-full rounded-2xl h-full"
                containerClassName="picture-element"
            />
:<></>}
        </div>
    );
};

export default DealsCard;
