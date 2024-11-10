/* eslint-disable max-len */
import React, { useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import { FragmentBranding } from "./IFooter";
import { getLanguage } from "src/main/util/getLanguage";

const FooterLogo: React.FC<FragmentBranding> = ({
    visitSaudiLogoLink,
    visitSaudiLogo,
}) => {
    const [isSuccess, setIsSuccess] = useState(true);
    //get the svg from server and fallback to en logo if not found
    return (
        <>
            <a href={visitSaudiLogoLink} className={`relative block w-[125px]`}>
                <Icon
                    wrapperClass="svg-max-w-full"
                    name={visitSaudiLogo}
                    isFetch
                    isSuccess={isSuccess}
                    setIsSuccess={setIsSuccess}
                />
                {!isSuccess && <Icon name={getLanguage() === "ar" ? "ar" : "en"} wrapperClass="svg-max-w-full" />}
            </a>
        </>
    );
};
export default FooterLogo;

