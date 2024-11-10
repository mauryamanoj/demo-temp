import bowser from './bowser';

// TODO: ask Vene if she remember for what this is for this.restartInlineVideo(video);
// TODO: and refactor parallaxCarousel to use it if we can remove this.restartInlineVideo(video);

/* eslint no-use-before-define: 0 */  // --> OFF

export const handleVideoExitFullScreenMode = (event: Event): void => {
  const video :HTMLVideoElement= event.target as HTMLVideoElement;
  const fullScreenElement = document.fullscreenElement;
  if (!fullScreenElement) {
    video.classList.remove('video-playing');
    video.muted = true;
    video.removeAttribute('controls');

    if (bowser.browser.name !== 'Microsoft Edge') {
      video.removeAttribute('controls');
    }
    video.controls = false;
    window.scrollTo(0, video.scrollTop);
    video.classList.add('d-none');
  }
};

export const endFullScreenVideo = (event: Event): void => {
  const video:any= event.target as HTMLVideoElement;
  if (video.webkitExitFullScreen) {
    video.webkitExitFullScreen();
  }
  document.exitFullscreen();

  // TODO: check why is not working if you put handleVideoExitFullScreenMode here
  video.classList.remove('video-playing');
  video.muted = true;
  video.removeAttribute('controls');

  if (bowser.browser.name !== 'Microsoft Edge') {
    video.removeAttribute('controls');
  }
  video.controls = false;
  window.scrollTo(0, video.scrollTop);
  video.loop = true;
  video.play();

  video.removeEventListener('ended', endFullScreenVideo);
  video.removeEventListener('webkitfullscreenchange', handleVideoExitFullScreenMode);
  video.removeEventListener('mozfullscreenchange', handleVideoExitFullScreenMode);
  video.removeEventListener('fullscreenchange', handleVideoExitFullScreenMode);
  video.removeEventListener('webkitendfullscreen', handleVideoExitFullScreenMode);
};

export const handleFullScreenVideo = (video: HTMLVideoElement) => {
  video.classList.add('video-playing');
  setTimeout(() => {
    // TODO: check why enterFullscreen util is not working well on IOs X
    try {
      video.requestFullscreen();
    } catch (error) {
      // video.webkitEnterFullScreen();
    }
    video.pause();
    video.currentTime = 0;
    video.muted = false;
    video.loop = false;
    video.controls = true;
    video.play();
    video.addEventListener('ended', endFullScreenVideo);
    video.addEventListener('webkitfullscreenchange', handleVideoExitFullScreenMode);
    video.addEventListener('mozfullscreenchange', handleVideoExitFullScreenMode);
    video.addEventListener('fullscreenchange', handleVideoExitFullScreenMode);
    video.addEventListener('webkitendfullscreen', handleVideoExitFullScreenMode);
  }, 0);
};
