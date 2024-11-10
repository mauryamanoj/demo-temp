import React, { useEffect, useState } from "react";
import { useResize } from "src/main/util/hooks/useResize";
import { isRetina } from "src/main/util/isRetina";

export type BrushId = "01" | "02" | "03" | "04" | "05" | "05B" | "06";

export interface OrnamentsBrushes {
  brushId?: BrushId;
  orientation?: "horizontal" | "vertical";
  direction?: "top" | "bottom" | "left" | "right";
  className?: string;
}

const OrnamentsBrushes: React.FC<OrnamentsBrushes> = ({
  brushId = "01",
  orientation = "horizontal",
  direction = "bottom",
  className,
  
}) => {
  const { widthSize, isMobile } = useResize();
  const imacBreakpoint = 2500;
  const isHorizontal: boolean = orientation === "horizontal";
  const id: string = isRetina() && isMobile && isHorizontal ? `${brushId}@2x` : brushId;
  const [viewport, setViewport] = useState<"mobile" | "desktop" | "imac">();
  const [maskUrl, setMaskUrl] = useState<string>("");

  useEffect(() => {
    if (isMobile && isHorizontal) {
      setViewport("mobile");
    } else if (widthSize >= imacBreakpoint && isHorizontal) {
      setViewport("imac");
    } else {
      setViewport("desktop");
    }
  }, [widthSize]);

  useEffect(() => {
    if (viewport && id) {
      const hostName = window.location.origin;
      const brushImage =
        (window.location.hostname == "localhost" ? "https://qa.visitsaudi.com" : hostName) +
        `/content/dam/static-images/resources/images/ornaments-03-brushes/` +
        orientation +
        `/${viewport}/${id}.png`;

      setMaskUrl(`url(${brushImage})`);
    }
  }, [viewport]);

  return (
    <div
      data-component="ornaments-brushes"
      style={{
        WebkitMaskImage: maskUrl,
        WebkitMaskSize: "100%",
        WebkitMaskRepeat: "no-repeat",
        WebkitMaskPositionY: "center",
      }}
      className={`${
        isHorizontal
          ? `w-full h-4 ${direction === "bottom" ? "-scale-y-100 rtl:-scale-100 bottom-0" : ""}`
          : `h-full w-4 ${direction === "right" ? "right-0 -scale-x-100" : ""}`
      }
      z-10 absolute bg-white ${className ?? ""}
`}
    ></div>
  );
};

export default OrnamentsBrushes;
