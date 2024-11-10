import React, { useState, useEffect, useCallback, KeyboardEvent } from "react";
import InputText from "../../common/atoms/InputText/InputText";
import Text from "../../common/atoms/Text/Text";
import Dropdown from "src/main/components/common/atoms/Dropdown/Dropdown";
import Textarea from "../../common/atoms/Textarea/Textarea";
import Button from "../../common/atoms/Button/Button";
import { getImage } from "src/main/util/getImage";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import Icon from "../../common/atoms/Icon/Icon";
import ReactCustomFlagSelect, {
    OptionListItem,
} from "react-custom-flag-select";
import countrycode_en from "src/main/config/countrycode_en.json";
import countrycode_ar from "src/main/config/countrycode_ar.json";
import "react-custom-flag-select/lib/react-custom-flag-select.min.css";
import "./ContactUs.css";

import {
    isValidEmail,
    isValidFirstName,
    isValidLastName,
    isValidMessage,
    isValidPhoneNumber,
} from "src/main/util/validations";
import { getLanguage } from "src/main/util/getLanguage";
const ContactUs: React.FC<any> = ({
    title,
    firstNameText,
    firstNamePlaceholderText,
    firstNameErrorText,
    lastNameText,
    lastNamePlaceholderText,
    lastNameErrorText,
    emailAddressText,
    emailAddressPlaceholderText,
    emailAddressErrorText,
    phoneNumberText,
    phoneNumberPlaceholderText,
    phoneNumberErrorText,
    messageTypeText,
    messageText,
    messagePlaceholderText,
    messageErrorText,
    ctaButtonText,
    messageSuccessText,
    messageFailureText,
    messageTypeServlet,
    // recaptchaValidationServlet,
    submitFormServlet,
    // phoneCard,
    phoneValidationErrorText,
    emailValidationErrorText,
    // executeRecaptcha,
    image,
    firstNameValidationErrorText,
}) => {
    const [email, setEmail] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [areaCode, setAreaCode] = useState("966");
    const lang = getLanguage();
    const [flagList, setFlagList] = useState<Array<OptionListItem>>();
    const [phoneNumber, setPhoneNumber] = useState("");
    const [message, setMessage] = useState("");
    const [lastNameError, setLastNameError] = useState("");
    const [emailError, setEmailError] = useState("");
    const [messageError, setMessageError] = useState("");
    const [phoneError, setPhoneError] = useState("");
    const [key, setKey]: any = useState("");
    const [isValid, setIsValid] = useState(true);
    const [firstNameError, setFirstNameError] = useState("");
    const [isSuccess, setIsSuccess] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isError, setIsError] = useState(false);
    const [token] = useState<any>();
    const [selectedMsgType, setSelectedMsgType] = useState<any>();
    const [messageTypeOptions, setMessageTypeOptions] = useState<any>();

    const countrycode = lang == "ar" ? countrycode_ar : countrycode_en;
    const FLAG_SELECTOR_OPTION_LIST: Array<OptionListItem> = countrycode.data;
    const focusInput = `focus-within:border-theme-100 focus-within:border-2
  focus-within:rounded-md`;

    function handleFirstNameChange(event: any) {
        validateFields("firstName", event?.target?.value);
    }

    function handleLastNameChange(event: any) {
        validateFields("lastName", event?.target?.value);
    }

    function handleEmailChange(event: any) {
        validateFields("email", event?.target?.value);
    }

    function handlePhoneNumberChange(event: any) {
        validateFields("phoneNumber", event?.target?.value);
    }

    function handleMessageChange(event: any) {
        validateFields("message", event.target.value);
    }

    function setThePlus() {
        setTimeout(() => {
            const numberBox: any = document.querySelector(
                ".react-custom-flag-select__select__dropdown-name div"
            );
            if (numberBox) {
                numberBox.innerHTML = "+" + numberBox.innerHTML;
            }
        }, 1000);
    }

    const find = (arr: any, obj: any) => {
        return arr.filter((o: any) => {
            return Object.keys(obj).every((key) => obj[key] === o[key]);
        });
    };

    const currentItem = find(FLAG_SELECTOR_OPTION_LIST, { id: areaCode })[0];

    const getFlagList = () => {
        const updatedFlagList = countrycode.data.map((element: any) => {
            return {
                ...element,
                flag: `${window.location.origin}/content/dam/static-images/resources/flags/png/${element.flag}`,
            };
        });

        setFlagList(updatedFlagList);
        setThePlus();
    };

    const getDropDownList = async () => {
        const apiUrl = `${messageTypeServlet}?language=${lang}`;

        setIsLoading(true);
        fetch(apiUrl)
            .then((response) => response.json())
            .then((json) => {
                setIsLoading(false);
                if (json?.response?.caseTypeDataObj) {
                    for (
                        let index = 0;
                        index < json.response.caseTypeDataObj.length;
                        index++
                    ) {
                        const element = json.response.caseTypeDataObj[index];
                        element.id = element.CaseTypeGuid;
                        element.title =
                            lang == "ar"
                                ? element.ArabicName
                                : lang == "zh"
                                ? element.ChineseName
                                : element.Name;
                    }
                    setMessageTypeOptions(json.response.caseTypeDataObj);
                    setSelectedMsgType(
                        json.response.caseTypeDataObj[0].CaseTypeGuid
                    );
                }
            })
            .catch((error) => {
                setIsLoading(false);
                console.error("failed to get messageTypeServlet", error);
            });
    };

    const isNumber = (event: KeyboardEvent<HTMLInputElement>): boolean => {
        const key = event.key;
        // Check if the pressed key is a number (0-9)
        return /^[0-9]$/.test(key);
    };
    const handleKeyPress = (event: KeyboardEvent<HTMLInputElement>) => {
        if (!isNumber(event)) {
            event.preventDefault(); // Prevent non-numeric input
        }
    };
    function isNumberKey(e: any) {
        setPhoneNumber(e?.target?.value);
    }

    function handleSelect(e: any) {
        setSelectedMsgType(e);
    }
    const clearForm = () => {
        setFirstName("");
        setLastName("");
        setPhoneNumber("");
        setEmail("");
        setMessage("");
        selectId();
        setKey(new Date().getTime());
    };

    function selectId() {
        return messageTypeOptions && messageTypeOptions.length > 0
            ? messageTypeOptions[0].id
            : "";
    }
    function validateFields(fieldName: any, value: any) {
        switch (fieldName) {
            case "firstName":
                const res = isValidFirstName(value, true);
                if (res.isValid) {
                    setFirstNameError("none");
                } else {
                    setFirstNameError(res.errorKey ? res.errorKey : "");
                }
                return res.isValid;
            case "lastName":
                const lastNameRes = isValidLastName(value, true);

                if (lastNameRes.isValid) {
                    setLastNameError("none");
                } else {
                    setLastNameError(
                        lastNameRes.errorKey ? lastNameRes.errorKey : ""
                    );
                }

                return lastNameRes.isValid;
            case "email":
                const emailResponse = isValidEmail(value, true);

                if (emailResponse.isValid) {
                    setEmailError("none");
                } else {
                    setEmailError(
                        emailResponse.errorKey ? emailResponse.errorKey : ""
                    );
                }
                return emailResponse.isValid;
            case "phoneNumber":
                const phoneNumberResponse = isValidPhoneNumber(value, true);

                if (phoneNumberResponse.isValid) {
                    setPhoneError("none");
                } else {
                    setPhoneError(
                        phoneNumberResponse.errorKey
                            ? phoneNumberResponse.errorKey
                            : ""
                    );
                }
                return phoneNumberResponse.isValid;
            case "message":
                const messageResponse = isValidMessage(value, true);
                setIsValid(messageResponse.isValid && isValid);
                if (messageResponse.isValid) {
                    setMessageError("none");
                } else {
                    setMessageError(
                        messageResponse.errorKey ? messageResponse.errorKey : ""
                    );
                }
                return messageResponse.isValid;
            default:
                break;
        }
    }

    useEffect(() => {
        getFlagList();
        getDropDownList();
    }, []);

    const handleSubmit = useCallback(
        async (e) => {
            e.preventDefault();

            try {
                const isValidFirstNameField = validateFields(
                    "firstName",
                    firstName
                );
                const isValidLastNameField = validateFields(
                    "lastName",
                    lastName
                );
                const isValidEmailField = validateFields("email", email);
                const isValidPhoneNumberField = validateFields(
                    "phoneNumber",
                    phoneNumber
                );

                const isValidMessageField = validateFields("message", message);
                //if all the fields are valid
                if (
                    isValidFirstNameField &&
                    isValidLastNameField &&
                    isValidEmailField &&
                    isValidPhoneNumberField &&
                    isValidMessageField
                ) {
                    setIsSuccess(false);
                    setIsError(false);

                    const phone = "+" + areaCode + phoneNumber;
                    const createConversation = {
                        firstName: firstName,
                        lastName: lastName,
                        emailAddress: email,
                        mobileNumber: phone,
                        caseType: selectedMsgType ? selectedMsgType : "",
                        incomingMessage: message,
                    };
                    setIsSuccess(false);
                    setIsError(false);

                    setIsLoading(true);
                    const responseData = await fetch(submitFormServlet, {
                        method: "POST",
                        body: JSON.stringify(createConversation),
                        headers: {
                            "Access-Control-Allow-Origin": "*",
                            "Content-Type": "application/json",
                        },
                    });
                    setIsLoading(false);
                    if (responseData.status === 200) {
                        setIsSuccess(true);
                        setIsError(false);
                        clearForm();
                    } else {
                        setIsSuccess(false);
                        setIsError(true);
                    }
                }
            } catch (ex) {
                setIsLoading(false);
                setIsError(true);
            }
        },
        [
            validateFields,
            firstName,
            areaCode,
            phoneNumber,
            lastName,
            email,
            selectedMsgType,
            message,
            token,
        ]
    );

    return (
        <div
            className="lg:px-100 lg:flex lg:gap-[36px] lg:justify-center lg:items-flex-start
                       lg:content-center  m-auto  px-[20px] pb-0 lg:mb-14"
        >
            <div className="block p-6 bg-white rounded-[16px] w-full">
                {title ? (
                    <Text
                        text={title}
                        type="h1"
                        styles="lg:text-3.5xl text-3xl font-primary-bold"
                    />
                ) : null}

                <form
                    className="relative"
                    onSubmit={(e) => {
                        e.preventDefault();
                        handleSubmit(e);
                    }}
                >
                    {isLoading && (
                        <div
                            className="absolute top-0 left-0 right-0 bottom-0 z-10 bg-white bg-opacity-50
          flex justify-center items-center"
                        >
                            <Icon
                                name="loader-ios"
                                svgClass={`w-5 h-5 animate-spin`}
                            />
                        </div>
                    )}

                    <div className="grid lg:grid-cols-2 gap-4 relative">
                        <div>
                            <InputText
                                type={"text"}
                                labelStyle={"lg:text-base font-primary-regular"}
                                style={`rounded-lg  w-full border h-[40px]
                  px-[16px] mb-1 mt-2 border-[#AAAAAA] text-[#4B4B4B]
                  ${
                      firstNameError !== "" && firstNameError !== "none"
                          ? "border-danger"
                          : ""
                  }`}
                                label={firstNameText}
                                placeHolder={firstNamePlaceholderText}
                                value={firstName}
                                onChange={(event: any) =>
                                    setFirstName(event?.target?.value)
                                }
                                onBlur={(event: any) =>
                                    handleFirstNameChange(event)
                                }
                                htmlFor={firstNameText}
                                id={firstNameText}
                            />
                            {firstNameError !== "" &&
                                firstNameError !== "none" &&
                                (firstNameError === "firstnameempty" ? (
                                    <p className="text-danger capitalize">
                                        {firstNameErrorText}
                                    </p>
                                ) : (
                                    <p className="text-danger capitalize">
                                        {firstNameValidationErrorText}
                                    </p>
                                ))}
                        </div>
                        {/* end firstName block */}

                        {/* start lastName block */}
                        <div>
                            <InputText
                                type={"text"}
                                labelStyle={"lg:text-base font-primary-regular"}
                                style={`rounded-lg w-full border h-[40px] px-[16px] mb-1 mt-2 border-[#AAAAAA]
                text-[#4B4B4B] ${
                    lastNameError !== "" && lastNameError !== "none"
                        ? "border-danger	"
                        : ""
                }`}
                                label={lastNameText}
                                placeHolder={lastNamePlaceholderText}
                                value={lastName}
                                onChange={(event: any) =>
                                    setLastName(event?.target?.value)
                                }
                                onBlur={(event: any) =>
                                    handleLastNameChange(event)
                                }
                                htmlFor={lastNameText}
                                id={lastNameText}
                            />
                            {lastNameError !== "" &&
                                lastNameError !== "none" && (
                                    <p className="text-danger capitalize">
                                        {lastNameErrorText}
                                    </p>
                                )}
                        </div>
                        {/* end lastName block */}
                    </div>
                    {/* start email block */}
                    <div className="mt-4">
                        <InputText
                            type={"email"}
                            labelStyle={"lg:text-base font-primary-regular"}
                            style={`rounded-lg w-full border h-[40px] px-[16px] mb-1 mt-2 border-[#AAAAAA]
              text-[#4B4B4B] ${
                  emailError && emailError !== "none" ? "border-danger	" : ""
              }`}
                            label={emailAddressText}
                            placeHolder={emailAddressPlaceholderText}
                            value={email}
                            maxLength={100}
                            onChange={(event: any) =>
                                setEmail(event?.target?.value)
                            }
                            onBlur={(event: any) => handleEmailChange(event)}
                            htmlFor={emailAddressText}
                            id={emailAddressText}
                        />

                        {emailError !== "" && emailError !== "none" ? (
                            <p className="text-danger capitalize">
                                {emailError === "emailaddressempty"
                                    ? emailAddressErrorText
                                    : emailValidationErrorText}
                            </p>
                        ) : (
                            <></>
                        )}
                    </div>
                    {/* end email block */}

                    {/* start phone block */}
                    <div className="pt-4">
                        <label htmlFor="" className="lg:text-base font-primary-regular">
                            {phoneNumberText}
                        </label>
                        <div
                            dir="ltr"
                            className={`flex justify-between align-center items-center
            border border-[#AAAAAA] rounded-lg my-2 ${focusInput}
            ${phoneError && phoneError !== "none" ? "border-danger	" : ""}`}
                        >
                            <div>
                                {flagList && flagList.length > 0 && (
                                    <ReactCustomFlagSelect
                                        value={currentItem.id}
                                        disabled={false}
                                        showSearch={true}
                                        showArrow={true}
                                        animate={true}
                                        fields={["name", "locale"]}
                                        optionList={flagList}
                                        customStyleSelect={{
                                            width: "99px",
                                            height: "40px",
                                        }}
                                        customStyleContainer={{
                                            fontSize: "16px",
                                            borderRadius: "8px",
                                        }}
                                        customStyleOptionListContainer={{
                                            maxHeight: "100px",
                                            overflow: "auto",
                                            width: "130px",
                                            background: "#fff",
                                        }}
                                        onChange={(code: any) => {
                                            setAreaCode(code);
                                            setThePlus();
                                        }}
                                    />
                                )}
                            </div>
                            <InputText
                                type={"text"}
                                labelStyle={"lg:text-base font-primary-regular"}
                                style={`!rounded-md w-full  h-[40px] px-[16px] outline-none text-[#4B4B4B] rtl:text-right focus-within:!border-none`}
                                placeHolder={phoneNumberPlaceholderText}
                                containerStyle={"w-full"}
                                value={phoneNumber}
                                onKeyPress={handleKeyPress}
                                onChange={(event: any) => isNumberKey(event)}
                                onBlur={(event: any) =>
                                    handlePhoneNumberChange(event)
                                }
                                minLength={9}
                                maxLength={9}
                                htmlFor={phoneNumberText}
                                id={phoneNumberText}
                            />
                        </div>
                        {phoneError !== "" &&
                            phoneError !== "none" &&
                            (phoneError === "phonenumberempty" ? (
                                <p className="text-danger capitalize">
                                    {phoneNumberErrorText}
                                </p>
                            ) : (
                                <p className="text-danger capitalize">
                                    {phoneValidationErrorText}
                                </p>
                            ))}
                    </div>
                    {/* end phone block */}

                    {/* start message block */}
                    <div className="mt-4">
                        {messageTypeOptions &&
                            messageTypeOptions.length > 0 && (
                                <Dropdown
                                    key={key}
                                    className={
                                        "mt-2  border-[#AAAAAA] text-[#000]"
                                    }
                                    labelText={messageTypeText}
                                    labelStyle={
                                        "lg:text-base font-primary-regular block"
                                    }
                                    options={messageTypeOptions}
                                    onChange={handleSelect}
                                    selectedOption={selectId()}
                                />
                            )}
                    </div>
                    {/* end message block */}

                    <div className="mt-4">
                        <Textarea
                            id={messageText}
                            textareaStyle={`rounded-lg w-full border px-4 mt-2 border-[#AAAAAA] text-[#4B4B4B]
                h-[133px] py-2 ${
                    messageError && messageError !== "none"
                        ? "border-danger	"
                        : ""
                }`}
                            labelStyle={"lg:text-base font-primary-regular"}
                            htmlFor={messageText}
                            placeholder={messagePlaceholderText}
                            label={messageText}
                            value={message}
                            onChange={(event: any) => setMessage(event)}
                            onBlur={handleMessageChange}
                        />
                    </div>
                    {messageError !== "" && messageError !== "none" && (
                        <p className="text-danger capitalize">
                            {messageErrorText}
                        </p>
                    )}

                    {ctaButtonText ? (
                        <div className="flex">
                            <Button
                                title={ctaButtonText}
                                buttonType="type1"
                                arrows={false}
                                spanStyle={"px-0 relative top-[1px]"}
                                styles={`justify-center text-sm py-[9px]
                lg:text-base font-primary-semibold
                rounded-lg text-theme-100 border
                ltr:ml-0 rtl:mr-0 rtl:ml-0 mt-6`}
                            />
                        </div>
                    ) : null}

                    {isError === true && (
                        <p className="mt-6 font-primary-regular text-sm text-danger capitalize">
                            {messageFailureText}
                        </p>
                    )}
                    {isSuccess && messageSuccessText ? (
                        <div className="flex content-center align-center relative">
                            <p className="text-success mt-6 flex content-center align-center">
                                <Icon
                                    name="check"
                                    svgClass="relative top-[3px] ltr:mr-2 rtl:ml-2"
                                />
                                <span className="font-primary-regular text-sm">
                                    {messageSuccessText}
                                </span>
                            </p>
                        </div>
                    ) : null}
                </form>
            </div>
            <div className="w-fit lg:block hidden relative">
                {image ? (
                    <Picture
                        imageClassNames="!min-w-[496px] h-full rounded-2xl"
                        containerClassName="lg:block hidden"
                        image={getImage(image)}
                        breakpoints={image?.breakpoints}
                        alt={image?.alt}
                    />
                ) : null}
                <div className="absolute bottom-7 left-0">
                    <Icon name="ornament-h-m-133" svgClass="w-full" />
                </div>
            </div>
        </div>
    );
};

export default ContactUs;