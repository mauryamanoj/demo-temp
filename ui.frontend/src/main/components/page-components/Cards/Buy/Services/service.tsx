import api from 'src/main/util/api';

const basePath = process.env.NODE_ENV == 'development' ? 'https://qa-revamp.visitsaudi.com' : window.location.origin;



export function fetchLatestStoriesApi(path: string, locale: string, limit: number, filtersQueryString: string) {

    const url = `${basePath + path}?locale=${locale}&limit=${limit}${filtersQueryString && '&' + filtersQueryString}`;
    return new Promise((resolve) => {

        api.get({ url }).then((data: any) => {
            resolve(data?.data?.response);
        });
    });
}




