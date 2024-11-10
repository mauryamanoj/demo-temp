import mapboxgl, { 
  LngLatBoundsLike,
  Map as MapBoxMap,
  Marker as MapBoxMarker,
  setRTLTextPlugin,
  getRTLTextPluginStatus,
  LngLatBounds}
 from 'mapbox-gl';
import bowser, { isIPad } from '../bowser';
import { cssClasses } from '../constants';
import isMobile from '../isMobile';

interface SetupMap {
  token: string;
  container: HTMLElement;
}

export const setupMap = ({ token, container }: SetupMap) => {
  if (getRTLTextPluginStatus() === 'unavailable') {
    mapboxgl.accessToken = token;

    setRTLTextPlugin(
      'https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-rtl-text/v0.2.3/mapbox-gl-rtl-text.js',
      () => null,
      true, // Lazy load the plugin
    );
  }

  return new MapBoxMap({
    container,
    style: 'mapbox://styles/stepann/ck3afwcxm147a1clomn1g96my',
    zoom: 12,
    // interactive: false,
  });
};

interface SetMarkersAndBounce {
  map: MapBoxMap;
  coordinates: Array<[number, number]>;
}

export const setMarkersAndBounce = ({ map, coordinates }: SetMarkersAndBounce) => {
  coordinates.forEach((coordinate, index) => {
    const element = document.createElement('div');
    element.className = 'marker';
    element.setAttribute('data-marker', `${index}`);
    const marker = new MapBoxMarker(element).setLngLat(coordinate);
    marker.addTo(map);
  });

  // TODO: try to find the real type for bounds and coords
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const bounds: LngLatBoundsLike = coordinates.reduce((bounds: any, coord: any) => {
    return bounds.extend(coord);
  }, new LngLatBounds(coordinates[0], coordinates[0]));

  map.fitBounds(bounds, {
    linear: true,
    padding: isMobile(768) ? 50 : { top: 65, bottom: 100, left: 100, right: 100 },
  });
};

interface SetMapActiveItem {
  index: number;
  markers: Array<HTMLElement>;
}

export const setMapActiveItem = ({ index, markers }: SetMapActiveItem): void => {
  (markers || []).forEach((marker, markerIndex) => {
    marker.classList.toggle(cssClasses.isActive, index === markerIndex);
  });
};

export const disableMapDragOnMobile = (map: MapBoxMap) => {
  if (bowser.platform.type === 'mobile' || isIPad()) {
    map.dragPan.disable();
    map.scrollZoom.disable();
    map.on('touchstart', (originalEvent) => {
      const event = originalEvent.originalEvent;
      if (event && 'touches' in event) {
        if (event.touches.length > 1) {
          map.dragPan.enable();
        } else {
          map.dragPan.disable();
        }
      }
    });
  }
};



