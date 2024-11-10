import React, { useEffect, useState } from 'react';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import Picture from 'src/main/components/common/atoms/Picture/Picture';
import Text from 'src/main/components/common/atoms/Text/Text';
import { getImage } from 'src/main/util/getImage';
import { getLanguage } from "src/main/util/getLanguage";
// const AttractionCard: React.FC<any> = ({ card, isWithShadow, isWithBackground }: any) => {


const AttractionCard: React.FC<any> = ({
  pageLink,
  title,
  subtitle,
  image1,
  image2,
  image3,
  s7image1,
  s7image2,
  s7image3,
  bannerImage,
  lat,
  lng,
  location,
  getDirectionsLabel,
  categories,
  agesValue,
  agesLabel,
  accessibilityValue,
  accessibilityLabel,
  isWithShadow,
  isWithBackground,
  className,
}: any) => {

  const [layout2Cols, setIslayout2Cols] = useState(false);
  useEffect(() => {
    const selector = document.querySelector('.AttractionsAndMapComp');
    const layout2Cols = selector?.closest(".layout2Cols");
    if (layout2Cols) setIslayout2Cols(true);
  }, []);

  return (
    <>
      <a href={pageLink?.url} target={pageLink?.targetInNewWindow ? '_blank' : ''}
        className={`flex gap-2 md:gap-4 items-center relative rounded-[20px]
         bg-white md:bg-transparent drop-shadow-lg md:drop-shadow-none
         hover:scale-[0.99] duration-300 h-full p-2.5 md:p-2 ${className}`}>
        <div className={`w-[102px] h-20 ${layout2Cols ? 'md:h-[6.181vw] md:w-[10.973vw]' : 'md:h-40 md:w-[20vw]'}`}>
          {bannerImage ?
            <Picture
              image={getImage(bannerImage)}
              breakpoints={bannerImage.bannerImage}
              containerClassName="flex items-center"
              pictureClassNames='w-full h-full'
              imageClassNames={`w-full  h-full img-blk rounded-2xl object-cover`}
            // alt={card?.image?.alt}
            />
            : <></>}
        </div>

        <div className={`flex-col ltr:text-left rtl:text-right flex align-item-center justify-center w-full flex-1`} >

          <div className="flex items-center mb-1 md:mb-2">
            <div className='flex gap-1 items-center whitespace-nowrap'>
              <Icon name="location-mark" />
              <Text text={location} styles={`${layout2Cols ? 'md:text-xs font-primary-regular' : 'md:text-sm font-primary-bold'} 'text-xs'`} type="span" />
            </div>

            {categories[0] && <div className='line-clamp-1'>
              <span className=' mx-1'>|</span>
              <Text text={categories[0].title} styles={`${layout2Cols ? 'md:text-xs font-primary-regular' : 'md:text-sm font-primary-bold'} 'text-xs mb-2'`} type="span" />
            </div>
            }
          </div>


          <Text text={title} styles={`
                font-primary-bold text-lg line-clamp-2 leading-[22.28px] md:leading-[33px]
                ${layout2Cols ? 'md:text-base md:leading-[19.81px]' : 'md:text-1.5xl'}
          `} />
        </div>
      </a>
    </>

  );
};

export default AttractionCard;
