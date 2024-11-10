import React, { useRef } from "react";
import Button from "src/main/components/common/atoms/Button/Button";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { Video } from "src/main/components/common/atoms/Video/Video";
import { getImage } from "src/main/util/getImage";
import { ICardProps } from "./ICard";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { getLanguage } from "src/main/util/getLanguage";
import { trackEvents } from "src/main/util/updateAnalyticsData";
import { isAuthorView } from "src/main/util/type/helpers";

const textShadow = { textShadow: '0px 0px 16px rgba(0, 0, 0, 0.50)' }

const Card: React.FC<ICardProps> = ({ title, description, image, video, link, ctaData }) => {
  // const videoRef = useRef<HTMLVideoElement>(null);
  var cardInfo: any = {};

  const setGtmCustomLink = (link: any) => {
    cardInfo = {
      event: ctaData?.ctaEventName,
      section_name: ctaData?.sectionName,
      click_text: link?.copy,
      link: link?.url,
      language: getLanguage(),
      page_category: ctaData?.pageCategory,
      page_subcategory: ctaData?.pageSubCategory,
      device_type: window.navigator.userAgent,
    };
    if (ctaData?.ctaEventName) {
      gtmCustomEventClick(cardInfo);
    }

  };

  const setAdobeCustomLink = (link: any) => {
    const dataSet: any = {};
    dataSet.trackEventname = ctaData?.ctaEventName;
    dataSet.trackSection = ctaData?.sectionName;
    dataSet.trackName = link?.text;
    dataSet.trackEventname && trackEvents(dataSet);
  };

  function handleButtonClick(link: any) {
    setGtmCustomLink(link);
    setAdobeCustomLink(link);
  }
  return (
    <li className="relative list-none h-full">
      {video?.videoFileReference ? (
        <div
          className={`relative flex items-center justify-center overflow-hidden
        medium-cover ${!!isAuthorView() ? 'h-[650px]' : 'h-[650px]  md:h-screen'} `}
        >
          <Video
            // videoRef={videoRef}
            videoPath={video.s7videoFileReference || video.videoFileReference}
            autoPlay={video.autoplay}
            muted={true}
            poster={video.poster}
            loop={video.autorerun}
            horizontalPosition={"center center"}
            classNames="absolute z-0 w-auto min-w-full min-h-full max-w-none"
          />
        </div>
      ) : image ? (
        <Picture
          imageClassNames={`w-full object-cover ${isAuthorView() ? 'h-[650px]' : 'h-[650px] md:h-screen'}`}
          image={getImage(image, 767)}
          breakpoints={image?.breakpoints}
        />
      ) : (
        <></>
      )}
      <div
        className="px-5 lg:px-100 absolute bottom-0 text-blk text-white w-full text-center
        backdrop-blur-[2px] bg-gradient-to-t from-black/40 lg:pb-[178px] pb-20"
      >
        {title ? (
          <h1
            className="lg:text-8xl lg:leading-[96px] text-5.5xl leading-[67.2px] font-secondary-bold"
            style={textShadow}
          >{title}</h1>
        ) : null}
        {description ? (
          <p
            className={`text-xl lg:text-1.5xl font-primary-semibold mt-6 lg:mt-8 leading-6 lg:leading-[27px]
        ${link && link.copy && link?.url ? "" : "lg:mb-[100px]"}`}
            style={textShadow}
          >
            {description}
          </p>
        ) : (
          <></>
        )}
        {link && link.copy && link?.url ? (
          <div className="mt-7 lg:mt-8">
            <a
              href={link?.url}
              target={link.targetInNewWindow ? '_blank' : '_self'}
              onClick={() => handleButtonClick(link)}
              className="cursor-pointer inline-block select-none py-3 px-[36px] font-primary-semibold text-base border  border-white hover:border-gray-300 transition-all duration-300 hover:bg-white/10 rounded-lg">
              {link.copy}
            </a>

          </div>
        ) : null}
      </div>
    </li>
  );
};

export default Card;
