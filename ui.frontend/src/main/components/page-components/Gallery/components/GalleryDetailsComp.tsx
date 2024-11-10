import React, { useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import Gallery from "src/main/components/common/organisms/Gallery/Gallery";
import { getImage } from "src/main/util/getImage";
import { useResize } from "src/main/util/hooks/useResize";
import ShowMore from "./ShowMore";

const GalleryDetailsComp = ({ gallery, moreLabel, story }: any) => {
  const [openGallery, setOpenGallery] = useState(-1);
  const {isMobile }= useResize();
  const item1 = gallery[0];
  const item2 = gallery[1];
  const item3 = gallery[2];
  const ShownIcon = (item: any) =>
    item.type === "video" ? (
      <Icon
        name="play"
        wrapperClass="absolute top-1/2 left-1/2 z-10 cursor-pointer -translate-y-1/2
  -translate-x-1/2"
      />
    ) : (
      <></>
    );

  const getItem = (item: any) => {
    if (item.type === "video") {
      return item.thumbnail;
    } else {
      return getImage(item.image, 767);
    }
  };

  return (
    <div
      className={`flex gap-4 md:gap-[3vw]
    ${item3 ? "flex-col md:flex-row md:h-[464px]" : item2 ? "mx-5 md:mx-0 h-24 md:h-[300px]" : "h-auto"}`}
    >
      <div
        className={`h-full relative cursor-pointer ${item3 ? "md:w-[calc(70vw-200px)]" : item2 ? "w-1/2" : "w-full"}`}
        onClick={() => setOpenGallery(0)}
      >
        <Picture
          imageClassNames={`w-full object-cover
          ${story ? "rounded-2xl" : "md:rounded-2xl"}
          ${item2 && !item3 ? "rounded-2xl" : ""}
          ${item2 || item3 ? "h-full": "max-h-[600px]"}
          `}
          image={getItem(item1)}
          alt={item1.image?.alt}
          breakpoints={item1.image?.breakpoints}
        />
        {ShownIcon(item1)}
      </div>
      {item2 ? (
        <div
          className={`flex md:flex-col gap-2 md:gap-0 h-24 md:h-full
        ${item3 ? `${story ? "mx-0" :"mx-5"} md:mx-0 md:w-[27vw]` : "w-1/2"}`}
        >
          <div
            className={`w-full relative cursor-pointer ${item3 ? "md:h-1/2 md:pb-[17px]" : "md:h-full"}`}
            onClick={() => setOpenGallery(1)}
          >
            <Picture
              imageClassNames="w-full h-full rounded-2xl object-cover"
              image={getItem(item2)}
              alt={item2.image?.alt}
              breakpoints={item2.image?.breakpoints}
            />
            {ShownIcon(item2)}
          </div>
          {item3 ? (
            <div className="w-full md:h-1/2 relative cursor-pointer"
            onClick={() => setOpenGallery(isMobile? 9999 : 2)}>
              <Picture
                imageClassNames="w-full h-full rounded-2xl object-cover"
                image={getItem(item3)}
                alt={item3.image?.alt}
                breakpoints={item3.image?.breakpoints}
              />
              {gallery.length > 3 ? <ShowMore gallery={gallery} moreLabel={moreLabel} /> : ShownIcon(item3)}
            </div>
          ) : (
            <></>
          )}
        </div>
      ) : (
        <></>
      )}
      {gallery.length > 0 ? (
        <Gallery gallery={gallery} openGallery={openGallery} closeGallery={() => setOpenGallery(-1)} />
      ) : (
        <></>
      )}
    </div>
  );
};

export default GalleryDetailsComp;
