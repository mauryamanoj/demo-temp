export type Data = Record<string, any>[] | []

const takeLast2HashTag = (url: string)=>{
  if(url){
  const parts = url.split("/")
  if(parts.length > 2){
    const value = parts.slice(-2)?.join('/')?.replace(".html","")
    return value
  }
}
  return ""
}

const filterDataByUrl = (pathname:string,data:Data)=>{
  const parts = pathname?.split("/")
  if(parts.length > 2){
    const value = takeLast2HashTag(pathname)
   if(value) return data.filter((item: any )=> takeLast2HashTag(item?.pageLink?.url) !== value)
  }
  return data
}

export default filterDataByUrl