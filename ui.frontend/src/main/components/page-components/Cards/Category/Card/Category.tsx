/* eslint-disable max-len */
import React from 'react';
import Picture from 'src/main/components/common/atoms/Picture/Picture';
import { getImage } from 'src/main/util/getImage';
import { CategoryCardProps } from '../iCategoryCards';

const CategoryCard = (card: CategoryCardProps) => {
    return (
        <div className={`h-[41.7vw] md:h-[15vw] relative flex justify-center items-center overflow-hidden hover:scale-[0.99] duration-300
        ${card?.link?.url ? "cursor-pointer": ""}`}
        onClick={card?.link?.url ? ()=>  window.open(card.link.url, card.link.targetInNewWindow ? "_blank" : "_self"): undefined}>
            <div className='absolute top-0 left-0 w-full h-full bg-black opacity-30 z-10 rounded-2xl'></div>
            <a href={card.link.url} className='absolute w-full h-full z-10 '>
                <h1 className="flex items-center justify-center w-full h-full z-10 text-white text-xl md:text-2.5xl lg:text-3.5xl text-center p-4 font-primary-bold">{card.title}</h1>
            </a>
            {card?.image ?
            <Picture
                image={getImage(card.image)}
                imageClassNames="w-full rounded-2xl h-full object-cover"
                containerClassName="picture-element"
            />
              : <></>}
        </div>
    );
};

export default CategoryCard;
