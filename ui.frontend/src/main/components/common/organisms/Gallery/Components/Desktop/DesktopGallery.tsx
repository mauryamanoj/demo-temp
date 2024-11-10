import React from "react";
import { getImage } from "src/main/util/getImage";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Scrollbar } from "swiper";
import Icon from "../../../../atoms/Icon/Icon";
import Picture from "../../../../atoms/Picture/Picture";
import SliderArrow from "../../../../atoms/SliderArrow/SliderArrow";
import { Video } from "../../../../atoms/Video/Video";
import ItemLocation from "../ItemLocation";

const DesktopGallery = ({
  closeGallery,
  selectedItem,
  showAfterSelect,
  arrowsSelectorRandNum,
  gallery,
  onSlideChange,
  indexItem,
}: any) => {
  const bigGallery = gallery && gallery.length > 5;
  return (
    <div>
      <div className="flex ltr:justify-end">
        <div className="p-[2svh] bg-white cursor-pointer rounded-full" onClick={closeGallery}>
          <Icon name="close" />
        </div>
      </div>
      <div className="mt-[2svh] mb-[6svh] h-[64svh] flex justify-center">
        <div className={`relative transition-all duration-300 ${selectedItem ? "h-[64svh]" : "h-0"}`}>
          {selectedItem?.type === "video" ? (
            <Video
              videoPath={selectedItem.video.s7videoFileReference || selectedItem.video.videoFileReference}
              poster={selectedItem.video.poster}
              horizontalPosition={"center center"}
              classNames={`w-auto h-full rounded-2xl`}
            />
          ) : selectedItem?.type === "image" ? (
            <Picture
              imageClassNames="min-w-[50vw] w-auto h-full rounded-2xl"
              image={getImage(selectedItem.image, 767)}
              alt={selectedItem.image.alt}
              breakpoints={selectedItem.image?.breakpoints}
              containerClassName="flex justify-center"
            />
          ) : (
            <div></div>
          )}
          <div className={`${selectedItem && showAfterSelect ? "block" : "hidden"}`}>
            <ItemLocation location={selectedItem?.location} />
            {/* {bigGallery ?
            <>
            <SliderArrow
              icon="small-arrow-left"
              className={`navigg-categoryCards-left${arrowsSelectorRandNum}
              ltr:left-8 rtl:right-8 top-1/2 -translate-y-1/2 !bg-black/70 hover:!bg-black`}
            />
            <SliderArrow
              icon="small-arrow-right"
              className={`navigg-categoryCards-right${arrowsSelectorRandNum}
              ltr:right-8 rtl:left-8 top-1/2 -translate-y-1/2 !bg-black/70 hover:!bg-black`}
            />
            </>
            :<></>} */}
          </div>
        </div>
      </div>
      <div className="flex justify-center">
        <div className="h-[10svh] md:w-[80vw] xl:w-[60vw] relative">
          <Swiper
            modules={[Navigation, Pagination, Scrollbar]}
            navigation={{
              nextEl: `.navigg-categoryCards-right${arrowsSelectorRandNum}`,
              prevEl: `.navigg-categoryCards-left${arrowsSelectorRandNum}`,
            }}
            slidesOffsetAfter={20}
            slidesPerView={1.3}
            spaceBetween={20}
            threshold={30}
            initialSlide={indexItem}
            centeredSlides={bigGallery ? true : false}
            loop={bigGallery ? true : false}
            onSlideChange={onSlideChange}
            slideToClickedSlide
            breakpoints={{
              768: {
                slidesPerView: 5,
                spaceBetween: 36,
                slidesOffsetAfter:-100
              },
            }}
          >
            {gallery.map((item: any, key: number) => (
              <SwiperSlide key={key}
              className={`cursor-pointer ${indexItem === key ? "border-2 border-white rounded-2xl":""}`}
              onClick={bigGallery ? undefined :()=> onSlideChange({realIndex: key})}
              >
                {item?.type === "video" ? (
                  <div className="relative">
                    <div
                      className="absolute top-1/2 left-1/2 z-10 cursor-pointer -translate-y-1/2
                    -translate-x-1/2"
                    >
                      <Icon name="play" />
                    </div>
                    <Picture imageClassNames="h-[10vh] w-[16vw] rounded-2xl" image={item?.thumbnail || ""} />
                  </div>
                ) : item?.type === "image" && item?.image ? (
                  <Picture
                    imageClassNames="h-[10vh] w-[16vw] rounded-2xl"
                    image={getImage(item.image, 767)}
                    breakpoints={item.image?.breakpoints}
                  />
                ) : (
                  <div></div>
                )}
              </SwiperSlide>
            ))}
          </Swiper>
          {bigGallery ? (
            <div className="hidden md:block">
              <SliderArrow
                icon="arrow-left"
                className={`navigg-categoryCards-left${arrowsSelectorRandNum}
                ltr:-left-6 rtl:-right-6 top-1/2 -translate-y-1/2`}
              />
              <SliderArrow
                icon="arrow-right"
                className={`navigg-categoryCards-right${arrowsSelectorRandNum}
                ltr:-right-6 rtl:-left-6 top-1/2 -translate-y-1/2`}
              />
            </div>
          ) : (
            <></>
          )}
        </div>
      </div>
    </div>
  );
};

export default DesktopGallery;
