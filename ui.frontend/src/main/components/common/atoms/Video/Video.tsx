import React, { useEffect, useRef, useState } from "react";

interface VideoButtonProps {
    videoPath?: string;
    id?: string;
    poster?: string;
    autoPlay?: boolean;
    loop?: boolean;
    muted?: boolean;
    preload?: string;
    horizontalPosition?: string;
    classNames?: string;
}

export const Video = ({
    videoPath,
    id,
    poster,
    autoPlay,
    loop,
    muted,
    preload,
    horizontalPosition,
    classNames,
}: VideoButtonProps) => {
    const videoRef = useRef<HTMLVideoElement>(null);
    const [relaod, setReload] = useState(false);

    useEffect(() => {
        setReload(true);
        setTimeout(() => setReload(false), 50);
    }, [poster, videoPath]);

    return videoPath && !relaod ? (
        <video
            id={id}
            ref={videoRef}
            loop={loop ? true : false}
            muted={muted || autoPlay ? true : false}
            autoPlay={autoPlay}
            playsInline
            style={{ objectPosition: horizontalPosition }}
            className={classNames}
            poster={poster}
            preload={preload}
            controls={autoPlay ? false : true}
            data-video-element
            data-media-element
            data-file={videoPath}
        >
            <source
                src={videoPath}
                type="video/mp4"
            />
            <source
                src={videoPath}
                type="video/mp4"
                media="all and (max-width: 768px)"
            />
        </video>
    ) : (
        <></>
    );
};

