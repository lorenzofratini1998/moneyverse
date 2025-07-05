export interface MenuItem {
  path: string;
  icon: any;
  translationKey: string;
  children?: MenuItem[];
}
