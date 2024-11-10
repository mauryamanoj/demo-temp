import React, { useCallback, useState, useEffect } from "react";
import Dropdown from "src/main/components/common/atoms/Dropdown/Dropdown";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { getImage } from "src/main/util/getImage";
import { ICountry, useCountries, useEVisaConfig } from "../IVisaRegulation";
import Icon from "src/main/components/common/atoms/Icon/Icon";
const MainComp = (props:any) => {
    const { showVisaTypes, selectedCountry } = props;
    const [country, setCountry] = useState<ICountry>();
    const countries = useCountries();
    const config = useEVisaConfig();
    const [mobileView, setMobileView] = useState(window.innerWidth < 768);

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
    useEffect(() => {
        // Select the event cards element
        const breadcrumbComponents = document.querySelector(
            "#breadcrumb-container"
        );

        if (breadcrumbComponents) {
            // Add classes to adjust the styling of breadcrumb Components
            breadcrumbComponents.classList.add(
                "lg:!mt-[108px]",
                "!mt-[84px]",
                "!mb-[16px]",
                "lg:!mb-[26px]",
                "md:px-8",
                "lg:px-8",
                "xl:px-16",
                "2xl:px-[100px]"
            );

            breadcrumbComponents.classList.remove("md:px-100");
        }
    }, []);

    const handleSelectCountry = useCallback(
        (value: any) => {
         
            setCountry(undefined);
            setTimeout(() => {
                selectedCountry(value);
                return setCountry(value);
            }, 10);
            if (value.visaGroup !== "noneligible") {
                const arr: any = config.visaTypes?.filter(
                    (vt: any) => vt.visaGroup === value.visaGroup
                )[0];

                if (arr) {
                    showVisaTypes({
                        sections: arr.sections,
                        visaGroup: value.visaGroup,
                        visaTypesTitle: arr.visaTypesTitle,
                        informationsLabel: config.informationsLabel,
                        requirementsLabel: config.requirementsLabel,
                        eligibleForLabel: config.eligibleForLabel,
                        viewMoreMobileLabel: config.viewMoreMobileLabel,
                    });
                }
            } else {
                showVisaTypes();
            }
        },
        [config.visaTypes]
    );

    const handleSelectGroup = useCallback(
        (value: any) => {
            const arr: any = config.visaTypes?.filter(
                (vt: any) => vt.questions && vt.questions[0].code === value.id
            )[0];

            if (arr) {
                showVisaTypes({
                    sections: arr.sections,
                    visaGroup: country?.visaGroup,
                    visaTypesTitle: arr.visaTypesTitle,
                    informationsLabel: config.informationsLabel,
                    requirementsLabel: config.requirementsLabel,
                    eligibleForLabel: config.eligibleForLabel,
                    viewMoreMobileLabel: config.viewMoreMobileLabel,
                });
            }
        },
        [config.visaTypes, country?.visaGroup]
    );

    return (
        <div className="flex flex-col md:flex-row gap-9 mx-5 lg:mx-100 md:!h-[433px] md:rtl:!h-[470px]">
            <div className="md:w-1/2">
                {config?.image ? (
                    <div className="h-full md:w-full rounded-2xl relative">
                        <Picture
                            imageClassNames="h-full h-auto md:w-full rounded-2xl object-cover"
                            image={getImage(config.image, 767)}
                            breakpoints={config.image?.breakpoints}
                        />
                        <div className="absolute bottom-7 left-0 w-30 md:w-full">
                        <Icon
                            name={mobileView ? "ornament-h-m-133-resp" :"ornament-h-m-133"}
                            svgClass="w-full"
                        />
                        </div>
                    </div>
                ) : (
                    <></>
                )}
            </div>
            <div className="bg-white md:w-1/2 rounded-2xl px-6 py-8 md:p-8 drop-shadow-lg z-10">
                {config.title ? (
                    <div
                        className="font-primary-bold text-3xl md:text-5xl mb-6
          lg:leading-[60px]  md:leading-[50px] md:rtl:leading-[75px] leading-[40px] line-clamp-3"
                    >
                        {config.title}
                    </div>
                ) : (
                    <></>
                )}
                {config.selectCountryLabel ? (
                    <div className="font-primary-regular text-base mb-2 line-clamp-1">
                        {config.selectCountryLabel}
                    </div>
                ) : (
                    <></>
                )}
                {countries.length > 0 ? (
                    <Dropdown
                        isWithIconImg
                        className="rtl:!pr-0"
                        wrapperClassName="ltr:pr-9 rtl:pl-9"
                        options={countries}
                        placeHolder={config.selectCountryPlaceholder}
                        inputClassName="placeholder:text-base placeholder:font-primary-regular"
                        handleChange={handleSelectCountry}
                    />
                ) : (
                    <></>
                )}

                {country?.visaGroup === "noneligible" ? (
                    <>
                        {config.selectQuestionLabel ? (
                            <div className="font-primary-regular text-base mb-2 mt-6 line-clamp-1">
                                {config.selectQuestionLabel}
                            </div>
                        ) : (
                            <></>
                        )}
                        {config.questionsByVisaGroup ? (
                            <Dropdown
                                className="rtl:!pr-0"
                                wrapperClassName="ltr:pr-9 rtl:pl-9"
                                options={config.questionsByVisaGroup}
                                placeHolder={config.selectQuestionPlaceholder}
                                inputClassName="placeholder:text-base placeholder:font-primary-regular"
                                handleChange={handleSelectGroup}
                            />
                        ) : (
                            <></>
                        )}
                    </>
                ) : (
                    <></>
                )}
            </div>
        </div>
    );
};

export default MainComp;
