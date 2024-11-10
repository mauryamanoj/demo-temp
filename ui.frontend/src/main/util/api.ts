// Set config for Axios API calls
import axios from "axios";

const header: any = {
    Accept: '*/*',
};
const call = async ({ method, url, data, params, baseURL, headers }: any, type?: string) => {
    baseURL = baseURL ? baseURL :
        window.location.origin === 'http://localhost:9000' ? "https://acc-revamp.visitsaudi.com" : "";
    headers = headers ? { ...header, ...headers } : header;

    //    headers = headers ? { ...headers, ...new Headers(headers) } : headers;
    const response: any = await axios.request({
        baseURL,
        url: url,
        method,
        data,
        params,
        headers,
    });
    return response && response.status >= 200 && response.status < 300 ? response :
        new Error(`API Error!' ${response.statusText}${type ?? ""}`);
};

const post = ({ url, baseURL, params, data, headers }: any, type?: string) => {
    if (!baseURL) { baseURL = ``; }
    return call({ method: 'POST', url, baseURL, params, data, headers }, type);
};

const put = ({ url, baseURL, params, data, headers }: any, type?: string) => {
    if (!baseURL) { baseURL = ``; }
    return call({ method: 'PUT', url, baseURL, params, data, headers }, type);
};

const get = ({ url, baseURL, params, headers }: any, type?: string) => {
    if (!baseURL) { baseURL = ``; }
    return call({ method: 'GET', url, baseURL, params, headers }, type);
};

const patch = ({ url, baseURL, params, data, headers }: any, type?: string) => {
    if (!baseURL) { baseURL = ``; }
    return call({ method: 'PATCH', url, baseURL, params, data, headers }, type);
};

const _delete = ({ url, baseURL, params, data, headers }: any, type?: string) => {
    if (!baseURL) { baseURL = ``; }
    return call({ method: 'DELETE', url, baseURL, params, data, headers }, type);
};

export default {
    call,
    get,
    post,
    put,
    patch,
    delete: _delete
};
