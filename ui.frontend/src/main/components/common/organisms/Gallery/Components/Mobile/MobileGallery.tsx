import React from "react";
import { getImage } from "src/main/util/getImage";
import { Swiper, SwiperSlide } from "swiper/react";
import Picture from "../../../../atoms/Picture/Picture";
import { Video } from "../../../../atoms/Video/Video";
import ItemLocation from "../ItemLocation";
import { useGalleryColumn } from "./IMobileGallery";

const MobileGallery = ({
  closeGallery,
  selectedItem,
  gallery,
  onSlideChange,
  setSelectedItem,
  indexItem,
}: any) => {
  const { leftList, rightList } = useGalleryColumn(gallery, onSlideChange);

  return (
    <div>
      <div className="flex justify-between items-center px-6 py-3 mb-3">
        <div onClick={selectedItem ? () => setSelectedItem(null) : closeGallery}>
          <svg
            className="w-6 h-6 transform ltr:rotate-90 rtl:-rotate-90"
            fill="none"
            stroke={selectedItem ? "white" : "currentColor"}
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </div>
        {(indexItem === 0 || indexItem) && selectedItem ? (
          <div className="text-white font-primary-regular text-base">
            {`${indexItem + 1}`}/{gallery.length}
          </div>
        ) : (
          <></>
        )}
      </div>
      {!selectedItem ? (
        <div className="flex px-5 gap-4 overflow-y-auto h-[88.5vh]">
          <div className="w-1/2 flex flex-col gap-4">{leftList}</div>
          <div className="w-1/2 flex flex-col gap-4">{rightList}</div>
        </div>
      ) : (
        <div className="flex justify-center items-center h-[88.5vh] relative">
          <ItemLocation location={selectedItem?.location} className="z-10" />
          <Swiper
            slidesPerView={1}
            initialSlide={indexItem}
            loop={gallery.length > 1 ? true : false}
            onSlideChange={onSlideChange}
            className="h-full child:h-full"
          >
            {gallery.map((item: any, key: number) => (
              <SwiperSlide key={key} className="h-full flex items-center">
                {item?.type === "video" ? (
                  <Video
                    videoPath={item.video.s7videoFileReference || item.video.videoFileReference}
                    poster={item.video.poster}
                    horizontalPosition={"center center"}
                    classNames="w-full h-auto"
                  />
                ) : item?.type === "image" && item?.image ? (
                  <Picture
                    imageClassNames="min-w-full w-full h-auto"
                    image={getImage(item.image, 767)}
                    alt={item.image.alt}
                    breakpoints={item.image?.breakpoints}
                    containerClassName="!h-fit"
                  />
                ) : (
                  <div></div>
                )}
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      )}
    </div>
  );
};

export default MobileGallery;
