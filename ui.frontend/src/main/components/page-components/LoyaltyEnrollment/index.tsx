import React, { Suspense, useLayoutEffect, useState } from "react";
import { getQueryParamsFromUrl } from "src/main/util/getLanguage";
import { getSSID } from "src/main/util/ssid-provider/ssidAction";
import { SSIDProvider } from "src/main/util/ssid-provider/ssidContext";
import { LoyaltyEnrollmentProps } from "./ILoyaltyEnrollment";
const LoyaltyEnrollmentComp = React.lazy(() => import("./LoyaltyEnrollmentComp"));
const basePath = (process.env.NODE_ENV == "development" ? "https://qa-revamp.visitsaudi.com" : window.location.origin)+"/bin/api/v1/ssid/userInfo";

const LoyaltyEnrollment = (props: LoyaltyEnrollmentProps) => {
  const [user, setUser] = useState<any>();
  const [show, setShow] = useState<boolean>(false);
  const queryParams: any = getQueryParamsFromUrl();

  useLayoutEffect(() => {
    fetchUser();
}, []);

async function fetchUser() {
  const user = await getSSID(basePath);
  if(user) setUser(JSON.parse(user));
  setShow(true)
}

  return (
    <Suspense fallback={null}>
      {queryParams &&
      props.queryParameter?.name &&
      queryParams[props.queryParameter.name] === props.queryParameter.value ? (
        <SSIDProvider user={user} loginConfig={{userApi: {ssidUserInfo: basePath}}}>
        {show ? <LoyaltyEnrollmentComp {...props} /> : <></>}
        </SSIDProvider>
      ) : (
        <></>
      )}
    </Suspense>
  );
};

export default LoyaltyEnrollment;
