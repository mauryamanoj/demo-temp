import React, { useEffect, useRef, useState } from "react";
import { LoyaltyEnrollmentProps } from "./ILoyaltyEnrollment";
import { SSIDContext } from "src/main/util/ssid-provider/ssidContext";
import Button from "../../common/atoms/Button/Button";
import Icon from "../../common/atoms/Icon/Icon";
import { loginWithRedirect, updateCookies } from "src/main/util/ssid-provider/ssidAction";
import Text from "../../common/atoms/Text/Text";
import { PopupType } from "../Popup/IPopup";
const basePath = process.env.NODE_ENV == "development" ? "https://qa-revamp.visitsaudi.com" : window.location.origin;

const LoyaltyEnrollmentComp: React.FC<LoyaltyEnrollmentProps> = ({ loginModal, enrollmentModal }) => {
  const { title, paragraph, button } = loginModal;
  const { user,token, loginConfig } = React.useContext(SSIDContext);
  const { userApi } = loginConfig;
  const [message, setMessage] = useState<string>(enrollmentModal.unsuccessfullyEnrolledMessage);
  const [show, setShow]= useState(false);

  useEffect(() => {
    if (user) {
      enrollToLoyalty();
    } else {
      handleLogin();
    }
  }, [user]);

  const handleLogin = () => {
    updateCookies();
    loginWithRedirect(userApi.ssidLoginUrl);
    document.body.style.overflow = "hidden";
    setShow(true)
  };

  const enrollToLoyalty = async () => {
    let tempMessage = message;
    setShow(false)
    try {
      const response = await fetch(basePath + enrollmentModal.apiEndpoint, {
        method: "POST",
        headers: {
          token: token ??"",
          "Access-Control-Allow-Origin": "*",
          "Content-Type": "application/json",
        },
      });
      const res = await response.json();

      if (res.code === 200) {
        switch (res.response.status) {
          case "SUCCESSFULLY_ENROLLED":
            tempMessage = enrollmentModal.successfullyEnrolledMessage;
            break;
          case "ALREADY_ENROLLED":
            tempMessage = enrollmentModal.alreadyEnrolledMessage;
            break;
          default:
            //UNSUCCESSFULLY_ENROLLED
            break;
        }
      }
    } catch (error) {}

    setMessage(tempMessage);
    setShow(true);
    document.body.style.overflow = "hidden";
  };

  const closeModal = () => {
    setShow(false)
    document.body.style.overflow = "auto";
  };

  const iconName = (elementType: PopupType): string => {
    return `popup_${elementType}_icon`;
};

  return (
    <div className={`fixed inset-0 items-center justify-center backdrop-blur-0.5 w-screen z-50
    ${show? "flex": "hidden"}`}
    >
      <div className="w-[353px] md:w-[510px] min-h-fit p-4 md:p-8 bg-white rounded-2xl
                               flex-col justify-center items-center shadow-md">

        <div>
          <div onClick={closeModal}>
                            <Icon
                                name="close"
                                svgClass="ml-auto rtl:mr-auto rtl:ml-0 cursor-pointer"
                            />
                        </div>
        {user ? (
          <><Text
                      styles="text-sm md:text-base text-center text-neutral-500
                                       h-full mt-2 md:mt-4"
                      text={message}
                  />
                 <div className="justify-center items-center inline-flex gap-2 md:gap-6 mt-12 w-full">
              <Button
                  title={enrollmentModal.copy}
                  size={"big"}
                  styles="justify-center items-center inline-flex w-full"
                  arrows={false}
                  onclick={closeModal}
              />
            </div>
          </>
        ) : (
          <>
          <div className="grid gap-4">
              <Icon
                  name={iconName(PopupType.Default)}
                  svgClass="mx-auto"
                  wrapperClass="fill-themed"
              />

              <div
                  className="self-stretch flex-col justify-center items-center
                                       gap-0 md:gap-0 flex"
              >
                  <Text
                      styles="text-xl md:text-3.5xl font-primary-bold capitalize"
                      text={title}
                  />

                  <Text
                      styles="text-sm md:text-base text-center text-neutral-500
                                       h-full mt-2 md:mt-4"
                      text={paragraph}
                  />
              </div>
          </div>

          <div className="justify-center items-center inline-flex gap-2 md:gap-6 mt-12 w-full">
              <Button
                  title={button.copy}
                  size={"big"}
                  styles="justify-center items-center inline-flex w-full"
                  arrows={false}
                  onclick={button.link ?()=> window.open(basePath+button.link,"_self") : undefined}
              />
          </div>
          </>
        )}
        </div>
      </div>
    </div>
  );
};

export default LoyaltyEnrollmentComp;
