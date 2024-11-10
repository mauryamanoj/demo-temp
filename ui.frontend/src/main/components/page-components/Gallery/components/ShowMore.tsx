import React from "react";
import { useMemo } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";

const ShowMore = ({gallery,moreLabel} : any) => {
  const icons = useMemo(() => {
    const icon: any[] = [];
    gallery.map((item: any) => {
      if (item.type === "video") {
        icon.push("simple-play");
      } else if (item.type === "image") {
        icon.push("gallery");
      }
    });

    const uniqueSet = new Set(icon);
    const uniqueArray = Array.from(uniqueSet);
    return uniqueArray;
  }, [gallery]);

  return (
    <div className="absolute top-0 left-0 w-full h-full backdrop-blur-[1px] bg-black/20 rounded-2xl">
    <div
      className="absolute top-1/2 left-1/2 z-10 cursor-pointer -translate-y-1/2
    -translate-x-1/2"
    >
      <div className="items-center flex flex-col">
      <div className="flex gap-2 mb-1">
      {icons?.length > 0 && icons.map((name, index) => (
        <Icon key={index} name={name} />
      ))}
      </div>
      <div className="font-primary-bold text-base text-white">{moreLabel}</div>
      </div>
    </div>
    </div>
  );
};

export default ShowMore;
