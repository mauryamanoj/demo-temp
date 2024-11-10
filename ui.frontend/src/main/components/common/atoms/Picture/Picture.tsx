import React, { useState } from "react";
import "lazysizes";
import "lazysizes/plugins/parent-fit/ls.parent-fit";
import PulseAnimation from "../../HOC/LoadingAnimation/PulseAnimation";

export interface PictureProps {
  containerClassName?: string;
  alt?: string;
  image?: string;
  breakpoints?: {
    srcset: string;
    media?: string;
    sizes?: string;
  }[];
  pictureOverlay?: React.ReactNode;
  hasRoundedCorners?: boolean;
  isHorizontal?: boolean;
  imageClassNames?: string;
  pictureClassNames?: string;
}

const Picture: React.FC<PictureProps> = ({
  containerClassName = "",
  alt,
  image,
  breakpoints,
  pictureOverlay,
  hasRoundedCorners = true,
  isHorizontal,
  imageClassNames,
  pictureClassNames
}) => {
  const [imageLoaded, setImageLoaded] = useState(false);

  const handleImageLoad = () => {
    setImageLoaded(true);
  };

  return (
    <div
      id="picture"
      className={`w-full h-full relative top-0 bottom-0 left-0 right-0
      transition duration-300 ease-in-out
      ${isHorizontal ? "mr-4 xl:max-w-none" : ""}
      ${hasRoundedCorners ? "rounded-s-lg rounded-e-lg" : ""}
      ${containerClassName}`}
    >
      {breakpoints && breakpoints.length > 0 ? (
        <picture className={pictureClassNames}>
          {breakpoints &&
            breakpoints.map((breakpoint, key) => (
              <source srcSet={breakpoint.srcset} media={breakpoint.media} sizes={breakpoint.sizes} key={key} />
            ))}
          <img data-src={image} className={`lazyload ${imageClassNames}`} alt={alt} onLoad={handleImageLoad} />
        </picture>
      ) : (
        <picture className={pictureClassNames}>
          <img data-src={image} className={`lazyload ${imageClassNames}`} alt={alt} onLoad={handleImageLoad} />
        </picture>
      )}
      {imageLoaded ? null : <PulseAnimation className={imageClassNames} />}
      {pictureOverlay}
    </div>
  );
};

export default Picture;
