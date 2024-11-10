import React, { useEffect, useState } from "react";
import { SwiperSlide } from "swiper/react";
import Card from "./LatestStoriesCard/LatestStoriesCard"; // todo: use story card component
import Slider from "src/main/components/common/organisms/Slider/Slider";
import Text from "../../common/atoms/Text/Text";

const LatestStoriesComp: React.FC<any> = ({ cards, link, title }) => {
  const [mobileView, setMobileView] = useState(window.innerWidth < 768);

  useEffect(() => {
    let resizeTimeout: NodeJS.Timeout;

    const handleResize = () => {
      if (window.innerWidth >= 768) {
        setMobileView(false);
      } else {
        setMobileView(true);
      }
    };

    handleResize();
    window.addEventListener("resize", handleResize);

    return () => {
      clearTimeout(resizeTimeout);
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return (
    <>
      <div className="md:flex justify-between items-center lg:mb-10 ltr:pl-5 rtl:pr-5 lg:ltr:pl-0 lg:rtl:pr-0 mb-[24px] md:mb-0">
        {title ? (
          <Text
            text={title}
            type="h1"
            styles="text-3xl md:text-5xl
             font-primary-bold leading-[49px]"
          />
        ) : null}
        {link && link.url && link.copy ? (
          <a
            href={link.url}
            title={link.title}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 font-primary-bold inline-block"
          >
            {link.copy.slice(0, 30)}
          </a>
        ) : null}
      </div>
      {mobileView ? (
        <Slider
          autoplay={{ delay: 6000 }}
          paginationClass='!bottom-[-7px]'
          loop={cards.length > 1}
        >
          {cards.map((item: any, index: any) => (
            <SwiperSlide key={index}>
              <Card {...item} className="pb-10" />
            </SwiperSlide>
          ))}
        </Slider>
      ) : (
        <div className="flex child:w-1/3">
          {cards.map((item: any, index: any) => {
            const style = index === 0 ? "!rounded-s-3xl" : index === cards.length - 1 ? "!rounded-e-3xl" : "";

            return <Card {...item} cardIndex={index} key={index} imageClassNames={style} className={style} />;
          })}
        </div>
      )}
    </>
  );
};

export default LatestStoriesComp;
