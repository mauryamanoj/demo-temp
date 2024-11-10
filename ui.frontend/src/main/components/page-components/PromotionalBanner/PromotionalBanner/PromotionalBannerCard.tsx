import React, { useEffect } from "react";
import Button from "src/main/components/common/atoms/Button/Button";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import { getLanguage } from "src/main/util/getLanguage";

import Picture from "src/main/components/common/atoms/Picture/Picture";
import { Video } from "src/main/components/common/atoms/Video/Video";
import { getImage } from "src/main/util/getImage";
import { useResize } from "src/main/util/hooks/useResize";
import ShowMore from "../../Gallery/components/ShowMore";
import { ICardProps } from "./ICardProps";
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { trackEvents } from "src/main/util/updateAnalyticsData";

function getSize(height: string | any, mobileView: any) {
  if (!mobileView) {
    switch (height) {
      case "small":
        return "small-cover h-[407px] sm:h-[407px] md:h-[459px] lg:h-[459px] xl:h-[459px]";
      case "medium":
        return "medium-cover h-[407px] sm:h-[407px] md:h-[550px] lg:h-[550px] xl:h-[550px]";
      case "large":
        return "h-[407px] sm:h-[407px] md:h-[650px] lg:h-[810px] xl:h-[810px]";
    }
  }
  return "h-[580px]";
}

const Card: React.FC<ICardProps> = ({
  title,
  pageBannerTitle,
  description,
  image,
  className = "",
  imageClassNames = "",
  index,
  mobileView,
  sendData,
  type,
  heightSize,
  subTitle,
  video,
  link,
  showArrows,
  logo,
  thumbs,
  ctaData,
  logoSize,
  replaceTitleWithLogo,
  setOpenGallery,
}) => {
  const { isMobile } = useResize(1023);
  var cardInfo: any = {};
  const sendDataToParent = (heightSize: any) => {
    sendData(heightSize);
  };

  const textShadow = { textShadow: '0px 0px 16px rgba(0, 0, 0, 0.50)' };

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
    dataSet.trackName = link?.copy;
    dataSet.trackEventname && trackEvents(dataSet);
  };

  function handleButtonClick(link: any) {
    setGtmCustomLink(link);
    setAdobeCustomLink(link);
  }

  useEffect(() => {
    sendDataToParent(heightSize);
  }, [heightSize]);


  const heightSize1 = 'small'
  const type1 = 'Story'

  return (
    <div
      className={`relative flex items-center
    justify-center  ${showArrows ? "show-arrows" : ""} ${getSize(heightSize, mobileView)} overflow-hidden
    ${type == "HomeBanner" ? "max-h-[550px]" : ""}`}
    >
      {/* start video section */}
      <>
        {video?.videoFileReference ? (
          <Video
            poster={video.poster}
            videoPath={video.s7videoFileReference || video.videoFileReference}
            id={`video${index + 1}`}
            autoPlay={video.autoplay}
            muted={true}
            loop={video.autorerun}
            horizontalPosition={"center center"}
            classNames={"absolute z-0 w-auto min-w-full bottom-0 top-0 w-full object-cover h-full"}
          />
        ) : image ? (
          <Picture
            imageClassNames={`w-full ${imageClassNames} h-full object-cover ${heightSize == "small" ? "md:rounded-2xl" : ""}`}
            containerClassName={"absolute z-0 w-auto min-w-full min-h-full"}
            image={getImage(image)}
            breakpoints={image?.breakpoints}
            alt={image?.alt}
          />
        ) : null}
      </>
      {/* end video section */}

      {/* start promotionalBanner section */}
      {type !== "HomeBanner" ? (
        <>
          <div className="from-black/40  absolute max-h-screen max-w-screen w-full h-auto bottom-0">
            <div
              className={`ltr:left-0 rtl:right-0 absolute w-screen
                        ${heightSize == "small" ? "bottom-[250px]" : "bottom-[250px] md:bottom-[386px]"}
                        h-10 filter blur-[1.5px] backdrop-blur-[0.25px]
                        ${video ? 'hidden' : ''}`}
            />
            <div
              className={`ltr:left-0 rtl:right-0 absolute w-full
                        ${heightSize == "small" ? "md:rounded-2xl h-[250px]" : "h-[250px] md:h-[386px]"}
                        z-[-1px] bottom-0
                        ${video ? 'hidden' : ''}`}
            >
              <div className="backdrop-blur-[2.5px] bg-gradient-to-t from-black/40 w-full h-full md:rounded-2xl" />
            </div>

            <div
              className={`${heightSize == "small" ? "lg:px-[50px]" : "lg:px-[100px]"}
                            text-white w-full ltr:text-left rtl:text-right
                             absolute bottom-0 px-[30px]
                            text-blk ${className} z-10`}
            >
              {logo?.fileReference ? (
                <Picture
                  image={getImage(logo)}
                  breakpoints={logo?.breakpoints}
                  containerClassName="flex items-end"
                  imageClassNames={`${logoSize === "small" ? "h-[150px]" : "h-14 md:h-100"} w-auto`}
                  alt={logo?.alt}
                />
              ) : null}
              {(pageBannerTitle || title) && (!logo || !logo?.fileReference) ? (
                <h4 className=" text-3xl lg:text-5xl font-primary-bold" style={textShadow}>
                  {pageBannerTitle || title}
                </h4>
              ) : null}
              {description || subTitle ? (
                <p
                  className="pt-4 pb-0 text-lg lg:text-1.5xl
                                font-primary-semibold leading-[27px] md:leading-[33px]"
                  style={textShadow}
                >
                  {description || subTitle}
                </p>
              ) : null}

              {/* start price section and button */}
              <>
                {link && link?.copy && link.url ? (
                  <div className="flex">
                    <a href={link?.url}
                      onClick={() => handleButtonClick(link)}
                      className={`m-auto select-none rounded-lg  transition-all duration-300 bg-theme-100 hover:bg-theme-200 border border-theme-100 hover:border-theme-200 font-primary-semibold text-sm md:text-base mx-0 px-4 py-[4.8px]
                  md:py-[9px] md:px-5 ${heightSize !== "small" ? "md:mt-8 mt-6" : " mt-8 md:mt-6"}`}
                    ><span className="px-3 flex items-center gap-2">{link?.copy} {type === "Story" && heightSize == "small" && <Icon name="chevron" svgClass={`w-3 h-3  rtl:rotate-180`} />}</span></a>
                  </div>
                ) : null}
              </>
              {/* end price section and button */}
            </div>
          </div>
        </>
      ) : null}

      {/* end promotionalBanner section */}

      {/* start homeBanner section */}
      {type === "HomeBanner" ? (
        <div
          className="ltr:text-center rtl:text-center ltr:lg:text-left
                                rtl:lg:text-right absolute
                                max-h-[529px] lg:max-h-screen max-w-screen w-full h-auto bottom-6 md:bottom-0"
        // pointer-events-none removed TODO find a better solution
        >
          <div
            className={`text-white w-full ${className} absolute bottom-0
                                    text-blk px-[20px] lg:px-0`}
          >
            <div
              className={`ltr:left-0 rtl:right-0 absolute w-screen bottom-[250px] h-12 filter blur-[1px] backdrop-blur-[0.25px] ${video ? 'hidden' : ''}`}
            />
            <div
              className={`ltr:left-0 rtl:right-0 absolute w-screen bottom-[204px] h-12 filter blur-[1.5px] backdrop-blur-[1.25px] ${video ? 'hidden' : ''}`}
            />
            <div className={`ltr:left-0 rtl:right-0 absolute w-screen h-[204px] z-[-1px] bottom-0`}>
              <div className="backdrop-blur-[2.5px] bg-gradient-to-t from-black/40 w-full h-full" />
            </div>
            <div
              className="relative bottom-[20px] lg:bottom-[60px] z-10 lg:mx-[100px]"
            >
              {pageBannerTitle || title ? (
                <>
                  {logo?.fileReference && replaceTitleWithLogo ? (
                    <div className="pb-2">
                      <Picture
                        image={getImage(logo)}
                        breakpoints={logo?.breakpoints}
                        containerClassName="flex items-end"
                        imageClassNames={`${logoSize === "small" ? "h-[150px]" : "h-14 md:h-100"} w-auto`}
                        alt={logo?.alt}
                      />
                    </div>
                  ) : (
                    <>
                      <h4
                        style={textShadow}
                        className={`text-[13vw] sm:text-5.5xl lg:text-8xl font-secondary-bold mb-0 text-shadow  pb-2
                                        leading-none rtl:leading-[80px] md:rtl:leading-[120px] ltr:lg:!text-left rtl:lg:!text-right break-words ${isMobile ? "!text-center" : ""
                          }`}
                      >
                        {pageBannerTitle || title}
                      </h4>
                      {isMobile && !replaceTitleWithLogo && logo?.fileReference ? (
                        <div className="pb-6 pt-4">
                          <Picture
                            image={logo.s7fileReference + "?fmt=png-alpha"}
                            breakpoints={logo?.breakpoints}
                            containerClassName={"flex justify-center"}
                            imageClassNames={`w-[228px] h-auto`}
                            alt={logo?.alt}
                          />
                        </div>
                      ) : null}
                    </>
                  )}
                </>
              ) : null}
              {description ? (
                <p
                  className={`text-xl lg:text-1.5xl font-primary-semibold ltr:text-left rtl:text-right leading-6
                  ${isMobile ? "!text-center" : ""}`}
                  style={textShadow}
                >
                  {description}
                </p>
              ) : null}

              {!isMobile ? (
                <div className="flex gap-13">
                  {thumbs?.gallery ? (
                    <div className="flex h-24 gap-6 mt-4 w-[750px]">
                      {thumbs.gallery.slice(0, 4).map((item: any, key: number) => (
                        <div
                          key={key}
                          className="cursor-pointer w-1/4 relative h-full"
                          onClick={() => setOpenGallery(key)}
                        >
                          {item?.type === "video" ? (
                            <>
                              {!(thumbs.gallery.length > 4 && key === 3) ? (
                                <div
                                  className="absolute top-1/2 left-1/2 z-10 cursor-pointer
                                                                           -translate-y-1/2 -translate-x-1/2"
                                >
                                  <Icon name="play" />
                                </div>
                              ) : (
                                <></>
                              )}
                              {item?.thumbnail ? (
                                <Picture imageClassNames="h-full w-full rounded-2xl" image={item.thumbnail} />
                              ) : null}
                            </>
                          ) : item?.type === "image" && item.image ? (
                            <Picture
                              imageClassNames="h-full w-full rounded-2xl"
                              image={getImage(item.image, 767)}
                              breakpoints={item.image?.breakpoints}
                            />
                          ) : (
                            <></>
                          )}
                          {thumbs.gallery.length > 4 && key === 3 ? (
                            <ShowMore gallery={thumbs.gallery} moreLabel={thumbs.moreLabel} />
                          ) : (
                            <></>
                          )}
                        </div>
                      ))}
                    </div>
                  ) : (
                    <></>
                  )}

                  {!replaceTitleWithLogo && logo?.fileReference ? (
                    <div>
                      <Picture
                        image={logo.s7fileReference + "?fmt=png-alpha"}
                        breakpoints={logo?.breakpoints}
                        containerClassName="flex items-end"
                        imageClassNames={`${logoSize === "small" ? "h-[150px]" : "h-14 md:h-100"} w-auto`}
                        alt={logo?.alt}
                      />
                    </div>
                  ) : null}
                </div>
              ) : (
                <></>
              )}
            </div>
          </div>
        </div>
      ) : null}

      {/* end homeBanner section */}
    </div>
  );
};

export default Card;
