import api from 'src/main/util/api';

const basePath = process.env.NODE_ENV == 'development' ? 'https://qa-revamp.visitsaudi.com' : window.location.origin;
var path = `${basePath}`;

let offset = 0;
let limit:any = 15;

export function handleSearch(apiUrl:any = "/bin/api/v1/things-to-do",
locale?: string, loadmore?: boolean, objType?: any, type?: string) {
    if (loadmore) {
        offset = offset + parseInt(limit);
    } else {
        offset = 0;
    }
    const url = `${path+apiUrl}?locale=${locale}&limit=${limit}&offset=${offset}${type ? type : ''}`;
    return new Promise((resolve) => {
        
        api.get({ url }).then((data: any) => {
            
            if(data && data.data && data.data.code ==200){
                resolve(data.data.response);
            }
          
        });
    });
}


export function resetOffset() {
    offset = 0;
}

export function getOffset() {
   return offset ;
}

export function changeLimit(number: any) {
    limit = parseInt(number);
}
