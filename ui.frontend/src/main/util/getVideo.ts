import isMobile from "./isMobile";
interface VideoSourceType {
    videoFileReference?: string;
    mobileVideoReference?: string;
    s7videoFileReference?: string;
    s7mobileVideoReference?: string;
}
export const getVideoSrc = (videoSrc: VideoSourceType) => {
    const { videoFileReference, mobileVideoReference, s7videoFileReference, s7mobileVideoReference } = videoSrc;
    let videoRef = null;
    if (isMobile(1024)) {
        if(s7mobileVideoReference){
            videoRef=s7mobileVideoReference;
        }
        else if(mobileVideoReference){
            videoRef=mobileVideoReference;
        }
        else{
            videoRef=videoFileReference;
        }
    }
    else{
        if(s7videoFileReference){
            videoRef=s7videoFileReference;
        }
        else if(videoFileReference){
            videoRef=videoFileReference;
        }
        else{
            videoRef=mobileVideoReference;
        }
    }
    return videoRef;
};
