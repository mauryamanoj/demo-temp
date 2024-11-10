import dayjs from "dayjs";
import React, { useEffect, useLayoutEffect, useRef } from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { getLanguage } from "src/main/util/getLanguage";

const HighlightedCard = ({
  image,
  breakpoints,
  alt,
  title,
  startDate,
  endDate,
  comingSoonLabel = "Coming Soon",
  expiredLabel = "Expired",
  cardCtaLink,
  logo,
}: any) => {
  const expired = dayjs(endDate).isBefore(dayjs());
  const dates = () => {
    const dates = startDate && endDate
      ? startDate === endDate
        ? dayjs(startDate).format("DD MMM YYYY")
        : dayjs(startDate).format("DD MMM YYYY") + " - " + dayjs(endDate).format("DD MMM YYYY")

      : undefined;
    if (dates) {
      if (getLanguage() == 'ar') {
        return dates.replace(/,/g, '<span class="font-sans">،</span>');
      } else {
        return dates;
      }
    }
    return ''
  }
  return (
    <a href={cardCtaLink}
      className="flex w-full h-[386px] md:h-[580px] rounded-3xl relative cursor-pointer">

      {image ? (
        <>
          <Picture
            image={getImage(image)}
            breakpoints={breakpoints}
            alt={alt}
            imageClassNames="w-full h-full rounded-2xl"
          />
        </>
      ) : (
        <></>
      )}
      <div className={`absolute w-full bottom-0 backdrop-blur-[2px] bg-gradient-to-t from-black/40 !rounded-2xl `}>
        <div className="absolute top-0 w-[calc(100%-48px)] h-[1px] bg-[rgb(170,170,170)] mx-6"></div>
        {logo?.desktopImage ? (
          <Picture
            image={getImage(logo)}
            imageClassNames="w-full h-auto"
            containerClassName="pb-[34px] md:pb-10 px-13 md:px-20"
          />
        ) : (
          <></>
        )}
        <div className="mx-6 text-center text-white pb-12 md:pb-20">
          <div className="hc-labelSelector font-primary-semibold text-base md:text-lg pt-[14px]">
            {expired && expiredLabel}
            {startDate && !expired && <div dangerouslySetInnerHTML={{ __html: dates() }} />}

            {!startDate && !expired && comingSoonLabel}
          </div>
          {title ? <div className="hc-titleSelector font-primary-bold text-lg md:text-1.5xl pt-[14px]">{title}</div> : <></>}
        </div>
      </div>
    </a>
  );
};

export default HighlightedCard;
