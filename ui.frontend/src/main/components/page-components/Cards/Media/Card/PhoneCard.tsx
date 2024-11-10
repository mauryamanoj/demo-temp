import React, { useState, useEffect } from "react";
import Text from "src/main/components/common/atoms/Text/Text";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Button from "../../../../common/atoms/Button/Button";
import Dropdown from "src/main/components/common/atoms/Dropdown/Dropdown";

const PhoneCard: React.FC<any> = (props) => {
    const { PhoneCard, title, variation } = props;
    const { contactItems, phoneNumber } = PhoneCard;
    const [phone, setPhone]: any = useState("");
    const [showCountries, setShowCountries] = useState(false);
    const [selectedCountry, setSelectedCountry]: any = useState();

    function handleCatsChange(optionId: any) {
        if (contactItems && contactItems.length != 0) {
            var selected = contactItems.filter(
                (e: any) => e.id == optionId
            );
            setSelectedCountry(selected[0]);
        }
    }

    useEffect(() => {
        if (contactItems && contactItems.length > 0) {
            for (let index = 0; index < contactItems.length; index++) {
                const element = contactItems[index];
                element.title = element.contactName;
                element.id = element.contactName;
                element.src = element.countryFlag;
            }

            if (contactItems[0] && contactItems[0].phoneNumber) {
                var selected: any = contactItems.filter((e: any) =>
                    e.phoneNumber.includes("930")
                );
                if (selected && selected.length != 0) {
                    setPhone(selected[0].phone);
                    setSelectedCountry(selected[0]);
                } else {
                    setPhone(contactItems[0].phone);
                    setSelectedCountry(contactItems[0]);
                }
            }
        }
        setShowCountries(true);
    }, [contactItems]);

    return (
        <div className="w-full max-h-[229px] lg:max-h-[275px] relative">
            {title && PhoneCard ? (
                <Text
                    text={title}
                    styles={`text-xl lg:text-1.5xl font-primary-bold
                    ${variation == "SMALL"
                            ? "overflow-hidden line-clamp-4 lg:h-[96px]"
                            : ""
                        }`}
                />
            ) : null}

            <div className={variation == "SMALL" ? "" : "pt-6"}>
                {PhoneCard && contactItems && showCountries ? (
                    <Dropdown
                        options={contactItems}
                        onChange={handleCatsChange}
                        selectedOption={selectedCountry.id}
                        isWithIconImg={PhoneCard.isWithDropdown ? false : true}
                    />
                ) : null}

                <hr className="h-px my-8 border-[#D2D2D2]" />
                <div className="lg:flex lg:mt-10 mt-2 border-top items-center content-center h-[83]">
                    {PhoneCard?.liveLabel &&
                        (selectedCountry?.phoneNumber || phoneNumber) ? (
                        <div className="flex mb-3 items-center content-center ltr:pl-[10px] ltr:mr-2 rtl:ml-2">
                            <div className="ltr:mr-4 rtl:ml-4 max-w-[30px]">
                                <Icon name="mobile-icon" />
                            </div>

                            <div>
                                <Text
                                    text={
                                        PhoneCard
                                            ? selectedCountry?.phoneNumber ||
                                            phoneNumber
                                            : ""
                                    }
                                    styles={
                                        "text-lg lg:text-1.5xl font-primary-semibold ltr:mr-2 rtl:ml-2"
                                    }
                                />
                                {/* {PhoneCard?.liveLabel ? (
                                    <div className="flex items-center content-center">
                                        <Icon name="circle" />
                                        <Text
                                            text={PhoneCard?.liveLabel}
                                            styles={`text-sm lg:text-base font-primary-semibold
                                        text-[#51C041] ltr:ml-2 rtl:mr-2`}
                                        />
                                    </div>
                                ) : null} */}
                            </div>
                        </div>
                    ) : null}

                    {(contactItems && contactItems.length != 0) ||
                        phoneNumber ? (
                        <div className="ltr:lg:ml-auto rtl:lg:mr-auto flex ltr:justify-end rtl:justify-start">
                            <a
                                className={`w-full px-0 justify-center ${variation == "SMALL" ? "lg:w-[78px]" : ""
                                    } py-[9px]
                                !text-base font-primary-semibold justify-items-center
                                rounded-lg text-theme-100 hover:text-theme-200 border lg:mt-[-17px] lg:w-[99px]
                                block text-center ltr:ml-auto rtl:mr-auto`}
                                href={
                                    (contactItems &&
                                        contactItems.length != 0) ||
                                        phoneNumber
                                        ? `tel:${selectedCountry?.phoneNumber ||
                                        phoneNumber
                                        }`
                                        : ""
                                }
                            >
                                {PhoneCard?.callUsCta?.substring(0, 30)}
                            </a>
                        </div>
                    ) : (
                        ""
                    )}
                </div>
            </div>
        </div>
    );
};

export default PhoneCard;

