import React, { useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import { getLanguage } from "src/main/util/getLanguage";

const HeaderLogo: React.FC<any> = ({ visitSaudiLogo, onMouseEnter, scrolled }) => {
  const [isSuccess, setIsSuccess] = useState(true);

  //get the svg from server and fallback to en logo if not found

  return (
    <a
      href={window.location.origin + window.location.pathname.slice(0, 3)}
      className="w-20 cursor-pointer"
      onMouseEnter={onMouseEnter}
    >
      <Icon
        name={visitSaudiLogo}
        isFetch
        isSuccess={isSuccess}
        setIsSuccess={setIsSuccess}
        wrapperClass={`${scrolled ? undefined : "white-svg"} svg-max-w-full`}
      />
      {!isSuccess && <Icon name={getLanguage() === "ar" ? "ar" : "en"}
        wrapperClass={`${scrolled ? undefined : "white-svg"} svg-max-w-full`} />}
    </a>
  );
};
export default HeaderLogo;
