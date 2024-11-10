export type BlockData = DetailHero | Card | Title | EventSlider;

// TODO: this should be derived from blocks, not the other way around like it is now
export type BlockName = 'detail-hero' | 'card' | 'title' | 'event-slider';

interface Block<Name extends BlockName, Data> {
  name: Name;
  data: Data;
}

interface Link {
  copy: string;
  href: string;
}

export type DetailHero = Block<
  'detail-hero',
  {
    title: string;
    link: Link | null;
    picture: string | null;
    info: Array<{
      title: string;
      text: string;
    }>;
  }
>;

export type Card = Block<
  'card',
  {
    title: string | null;
    'over-title-text': string | null;
    description: string;
    link: Link | null;
    picture: null;
  }
>;

export type Title = Block<
  'title',
  {
    title: string;
    link: Link | null;
  }
>;

export type EventSlider = Block<
  'event-slider',
  Array<{
    title: string;
    'over-title-text': string | null;
    description: string | null;
    link: Link | null;
    picture: string | null;
    'from-to': {
      from: string;
      to: string;
    };
  }>
>;
