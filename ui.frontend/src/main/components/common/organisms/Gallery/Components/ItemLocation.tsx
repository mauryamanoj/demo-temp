import React from "react";
import Icon from "../../../atoms/Icon/Icon";

const ItemLocation = ({ location,className }: any) =>
  location ? (
    <div
      className={`flex gap-1 justify-center items-center absolute top-0  md:top-6 ltr:right-6 rtl:left-6
  border-[1px] border-white p-2 rounded-lg text-white ${className ?? ""}`}
    >
      <Icon name="white-location-mark" /> {location}
    </div>
  ) : (
    <></>
  );

export default ItemLocation;
