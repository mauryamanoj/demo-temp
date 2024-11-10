import React from "react";
import Icon from 'src/main/components/common/atoms/Icon/Icon';
interface SliderArrowProps {
  icon?: "arrow-left" | "arrow-right" | string;
  className?: string;
  disabled?: boolean;
  onClick?: React.MouseEventHandler<HTMLDivElement>;
}
const divStyles = {
  boxShadow: '0px 4px 15px rgba(0, 0, 0, 0.25)'
};
const SliderArrow = ({ icon = "arrow-left", className = "", disabled = false, onClick}: SliderArrowProps) => {
  return (
    <div
      style={divStyles}
      className={`
      arrows
      absolute top-1/2 px-3 py-[14px] ${!disabled && 'cursor-pointer'} bg-white rounded-full
       shadow-lg hover:bg-gray-50 z-10 ${className}`}
      onClick={!disabled ? onClick : undefined}
    >
      <Icon name={icon} svgClass="w-6 w-5 rtl:scale-x-[-1]" />
    </div>
  );
};

export default SliderArrow;
