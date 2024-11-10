import { getCookie } from "./handleCookie";
import { UserType } from "./interfaceType";

export const getAuthHeaders = () => {
  const token = getCookie("access_token");
  const user: UserType = JSON.parse(sessionStorage.getItem('userData') as string);

  return {
    token: token,
    userid: user.sid,
  };
};
