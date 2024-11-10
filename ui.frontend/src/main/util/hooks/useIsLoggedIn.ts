
import { getCookie } from '../handleCookie';

export const useIsLoggedIn = () => {
    const token = getCookie("access_token");
    const user = sessionStorage.getItem('userData');
    if(token && user) {
        return true;
    } else {
        return false;
    }
};
