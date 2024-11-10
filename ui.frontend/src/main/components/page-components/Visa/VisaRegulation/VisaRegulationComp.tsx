import React, { useCallback, useEffect, useState } from "react";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { useResize } from "src/main/util/hooks/useResize";
import MainComp from "./Components/MainComp";
import VisaTypes from "./Components/VisaTypes";
import VisaTypesMobile from "./Components/VisaTypesMobile";
import Icon from "src/main/components/common/atoms/Icon/Icon";

// Note:you see the icon locally and on QA
const basePath =
    process.env.NODE_ENV == "development"
        ? "https://qa-revamp.visitsaudi.com"
        : window.location.origin;

const VisaRegulationComp: React.FC<any> = () => {
    const [visaTypes, setVisaTypes] = useState<any>();
    const { isMobile } = useResize(1023);
    const [mobileView, setMobileView] = useState(window.innerWidth < 768);
    const [country, setCountry] = useState<any>();

    useEffect(() => {
        let resizeTimeout: NodeJS.Timeout;
        const handleResize = () => {
            if (window.innerWidth >= 768) {
                setMobileView(false);
            } else {
                setMobileView(true);
            }
        };

        handleResize();
        window.addEventListener("resize", handleResize);

        return () => {
            clearTimeout(resizeTimeout);
            window.removeEventListener("resize", handleResize);
        };
    }, []);

    function selectedCountry(country:string){
        setCountry(country);
    }

    const showVisaTypes = useCallback((sections: any) => {
        setVisaTypes(undefined);
        setTimeout(() => setVisaTypes(sections), 10);
    }, []);
    return (
        <div>
            <MainComp showVisaTypes={showVisaTypes} selectedCountry={selectedCountry} />
            {visaTypes ? (
                visaTypes.visaGroup === "gcc" ? (
                    <div
                        className="flex flex-col md:flex-row gap-8 md:gap-13 bg-theme-100
           px-6 md:px-8 py-8 mt-8 md:mt-10 mx-5 lg:mx-100 mb-16
          md:min-h-[219px] items-center rounded-2xl relative"
                    >
                        <div className={`absolute top-0 ${mobileView ? "right-0 left-0" :
                        "bottom-0 ltr:left-16 rtl:right-16 w-20"}`}>
                            <Icon
                                name={mobileView ? "ornament-h-p1-dark-80" : "ornament-v-p1-dark-80"}
                                svgClass={`w-full md:h-full h-20${mobileView ? " rounded-t-2xl": ""}`}
                            />
                        </div>
                        <Picture
                            containerClassName="!w-fit"
                            imageClassNames="h-[155px] w-auto"
                            image={basePath + visaTypes.sections[0].icon}
                        />
                        <div className="flex flex-col gap-4 text-white font-primary-semibold">
                            <div className="text-1.5xl leading-7">
                                {visaTypes.sections[0].title}
                            </div>
                            <div
                                className="text-3.5xl leading-10 richTextContainer"
                                dangerouslySetInnerHTML={{
                                    __html: visaTypes.sections[0].description,
                                }}
                            ></div>
                        </div>
                    </div>
                ) : isMobile ? (
                    <VisaTypesMobile {...visaTypes} selectedCountry={country?.title}/>
                ) : (
                    <VisaTypes {...visaTypes} selectedCountry={country?.title} />
                )
            ) : (
                <></>
            )}
            {visaTypes && visaTypes?.visaGroup && (
                        <Icon
                            name={
                                isMobile
                                    ? "ornament-h-m3-48"
                                    : "ornament-h-m3-80"
                            }
                            svgClass="w-full h-12 lg:h-20"
                        />
            )}
        </div>
    );
};

export default VisaRegulationComp;

