import { getCookie } from "src/main/util/handleCookie";

export function isAuthorView() {
  return window.Granite && window.Granite.author ? true : false;
}

// save referral QueryParams to cookie so it can be passed to login url
export const saveReferralQueryParamsIntoCookies = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const cookieValue = urlParams.get("referral");
  if (cookieValue) {
    document.cookie = `referralQueryParams=${cookieValue}; expires=0; path=/`;
  }
}

// get referral QueryParams if exist
export const getReferralQueryParamsFromCookies = () => {
  const urlParams = getCookie('referralQueryParams');
  if (urlParams) {
    return `&referral=${urlParams}`
  }
  return '';
}