import api from 'src/main/util/api';

const basePath = process.env.NODE_ENV == 'development' ? 'https://www.visitsaudi.com' : window.location.origin;
const path = `${basePath}/bin/api/solr/search`;
let offset = 0;
let limit = 15;

export function handleSearch(query: string, locale?: string, loadmore?: boolean, objType?: any, type?: string) {
    if (loadmore) {
        offset = offset + limit;
    } else {
        offset = 0;
    }
    const url = `${path}?source=web&locale=${locale}&limit=${limit}&offset=${offset}&query=${query || '*:*'}${type ? type : ''}`;
    return new Promise((resolve) => {
        
        api.get({ url }).then((data: any) => {
          
            if(data.data.data || data.data.status =='error'){
                data.method = objType;
                resolve(data);
            }
          
        });
    });
}


export function handleSearchSugesstions(query?: string, locale?: string) {
    const url = `${path}?source=web&locale=${locale}&limit=30&offset=0&suggestion=${query}`;
    return new Promise((resolve) => {
        api.get({ url }).then((data: any) => {
            resolve(data);
        });
    });
}



export function resetOffset() {
    offset = 0;
}

export function getOffset() {
   return offset ;
}

export function changeLimit(number: number) {
    limit = number;
}
