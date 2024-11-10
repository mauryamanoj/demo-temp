import React, { useEffect, useState } from "react";
import { useResize } from "src/main/util/hooks/useResize";
import DesktopGallery from "./Components/Desktop/DesktopGallery";
import MobileGallery from "./Components/Mobile/MobileGallery";

const Gallery = ({ gallery, openGallery, closeGallery }: any) => {
  const {isMobile} = useResize();
  const [selectedItem, setSelectedItem] = useState(null);
  const [indexItem, setIndexItem] = useState(-1);
  const [showAfterSelect, setShowAfterSelect] = useState(false);
  const arrowsSelectorRandNum = Math.floor(Math.random() * 1000) + 1;

  const onSlideChange = (swiper: any) => {
   if(isMobile) {
    setIndexItem(swiper.realIndex);
    setSelectedItem(gallery[swiper.realIndex]);
   }else{
    setSelectedItem(null);
    setShowAfterSelect(false);
    setIndexItem(swiper.realIndex);
    setTimeout(() => setSelectedItem(gallery[swiper.realIndex]), 300);
    setTimeout(() => setShowAfterSelect(true), 500);
  }
  };

  useEffect(() => {
    if (openGallery >= 0) {
      document.body.style.overflow = "hidden";
      onSlideChange({ realIndex: openGallery });
    } else {
      document.body.style.overflow = "auto";
      onSlideChange({ realIndex: -1 });
    }
  }, [openGallery]);

  return (
    <div
      className={`fixed top-0 bottom-0 left-0 w-screen z-50 transition-all ease-in-out duration-500
      ${isMobile && selectedItem? "bg-black" :"bg-gray-100"} md:bg-gray
   ${openGallery >= 0 ? "h-screen pt-6 pb-0 md:py-[4svh] px-0 md:px-100" : "h-0"}`}
    >
      {openGallery >= 0 && indexItem >= 0 ? (
        isMobile ?
        <MobileGallery
          closeGallery={closeGallery}
          selectedItem={selectedItem}
          gallery={gallery}
          onSlideChange={onSlideChange}
          setSelectedItem={setSelectedItem}
          indexItem={indexItem}
        />
        :
        <DesktopGallery
          closeGallery={closeGallery}
          selectedItem={selectedItem}
          showAfterSelect={showAfterSelect}
          arrowsSelectorRandNum={arrowsSelectorRandNum}
          gallery={gallery}
          onSlideChange={onSlideChange}
          indexItem={indexItem}
        />
      ) : (
        <></>
      )}
    </div>
  );
};

export default Gallery;
