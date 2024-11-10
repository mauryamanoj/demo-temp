import React from "react";

const PulseAnimation = ({ className }: any) => {
  return (
    <div
      className={`w-full h-full absolute top-0 left-0 animate-pulse
  bg-gradient-to-r bg-gray-100 from-gray-50 via-gray-100 to-gray-300${className ? +" " + className : ""}`}
    ></div>
  );
};

export default PulseAnimation;
