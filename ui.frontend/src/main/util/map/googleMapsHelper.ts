export const createGoogleHTMLMapMarker = ({ OverlayView = google.maps.OverlayView, ...args }) => {
    class HTMLMapMarker extends OverlayView {
        latlng: any;
        html: any;
        div?: HTMLElement | any;
        markerClass?: any;
        activeClass?: any;

        constructor() {
            super();
            this.latlng = args.latlng;
            this.html = args.html;
            this.setMap(args.map);
            this.markerClass = args.markerClass;
            this.activeClass = args.activeClass;

        }

        createDiv() {
            this.div = document.createElement('div');
            this.div.className = this.markerClass;
            this.div.setAttribute("data-component", "google-marker");
            this.div.style.position = 'absolute';
            this.div.style.cursor = 'pointer';
            if (this.html) {
                this.div.innerHTML = this.html;
            }
            if (args?.index === 0) {
                this.div.classList.toggle(this.activeClass ? this.activeClass : 'is-active', true);
            }
            this.div.addEventListener('click', () => {
                google.maps.event.trigger(this, 'click');
            });
            this.div.addEventListener('mouseover', () => {
                google.maps.event.trigger(this, 'mouseover');
            });
            this.div.addEventListener('mouseout', () => {
                google.maps.event.trigger(this, 'mouseout');
            });
        }

        appendDivToOverlay() {
            const panes = this.getPanes();
            panes?.overlayLayer.appendChild(this.div);
            panes?.overlayMouseTarget.appendChild(this.div);
        }

        positionDiv() {
            const point = this.getProjection()?.fromLatLngToDivPixel(this.latlng);
            if (point) {
                this.div.style.left = `${point.x}px`;
                this.div.style.top = `${point.y}px`;
            }
        }

        draw() {
            if (!this.div) {
                this.createDiv();
                this.appendDivToOverlay();
            }
            this.positionDiv();
        }

        remove() {
            if (this.div) {
                this.div.parentNode?.removeChild(this.div);
                delete this.div;
            }
        }

        getPosition() {
            return this.latlng;
        }

        getDraggable() {
            return false;
        }
    }
    return new HTMLMapMarker();
};

export const getGoogleRegion = () => {
    const lang = window.document.documentElement.lang;
    let region = '';
    switch (lang) {
        case 'en':
            region = 'US';
            break;
        case 'ar':
            region = 'SA';
            break;
        case 'de':
            region = 'DE';
            break;
        case 'es':
            region = 'ES';
            break;
        case 'FR':
            region = 'FR';
            break;
        case 'ja':
            region = 'JP';
            break;
        case 'ru':
            region = 'RU';
            break;
        default:
            region = '';
            break;
    }
    return region;
};
