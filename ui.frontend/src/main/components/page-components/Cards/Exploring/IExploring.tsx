export interface ExploringInterface {
    proxy: Proxy
    type: string
    title: string
    link?: Link
    cards: Card[]
  }
  
  export interface Proxy {}
  
  export interface Link {
    hideMap: boolean
    url: string
    urlWithExtension: string
    urlSlingExporter: string
    copy: string
    targetInNewWindow: boolean
    title: string
  }
  
  export interface Card {
    title: string
    tagline: string
    image: Image
    cardCtaLink: string
  }
  
  export interface Image {
    fileReference: string
    mobileImageReference: string
    s7fileReference: string
    s7mobileImageReference: string
    desktopImage: string
    mobileImage: string
  }
  