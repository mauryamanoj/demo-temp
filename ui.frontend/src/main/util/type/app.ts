export type Locale = 'en' | 'zh' | 'ar-SA' | 'ar';

export interface Disposable {
  dispose: () => void;
}
