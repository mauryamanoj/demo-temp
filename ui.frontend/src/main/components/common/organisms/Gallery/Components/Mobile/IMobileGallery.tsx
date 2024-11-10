import React, { useMemo } from "react";
import { getImage } from "src/main/util/getImage";
import Icon from "../../../../atoms/Icon/Icon";
import Picture from "../../../../atoms/Picture/Picture";

export const useGalleryColumn = (gallery: any, onSlideChange: any) => {

  return useMemo(() => {
    const left: any[] = [];
    const right: any[] = [];

    gallery.map((item: any, key: number) => {
      const htmlItem = (
        <div key={key} onClick={() => onSlideChange({ realIndex: key })}>
          {item?.type === "video" ? (
            <div className="relative">
              <div className="absolute top-1/2 left-1/2 z-10 cursor-pointer -translate-y-1/2 -translate-x-1/2">
                <Icon name="play" />
              </div>
              <Picture imageClassNames="w-full h-full min-h-[100px] rounded-2xl" image={item?.thumbnail || ""} />
            </div>
          ) : item?.type === "image" && item?.image ? (
            <Picture
              imageClassNames="h-auto w-full rounded-2xl"
              image={getImage(item.image, 767)}
              breakpoints={item.image?.breakpoints}
            />
          ) : (
            <div></div>
          )}
        </div>
      );
      if (key % 2 === 0) {
        left.push(htmlItem);
      } else {
        right.push(htmlItem);
      }
    });

    return { leftList: left, rightList: right };
  }, [gallery]);
};
