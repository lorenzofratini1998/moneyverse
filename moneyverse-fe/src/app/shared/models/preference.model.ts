export enum PreferenceKey {
  LANGUAGE = 'LANGUAGE',
  CURRENCY = 'CURRENCY',
  DATE_FORMAT = 'DATE_FORMAT',
  THEME = 'THEME'
}

export interface DateFormat {
  label: string;
  value: string;
  default: boolean,
  group: string
}

export interface Language {
  languageId: string;
  isoCode: string,
  locale: string,
  country: string,
  icon: string,
  default: boolean,
  enabled: boolean
}

export const DATE_FORMATS: DateFormat[] = [
  {label: 'YYYY-MM-DD', value: 'yyyy-MM-dd', default: true, group: 'ISO'},
  {label: 'MM-DD-YYYY', value: 'MM-dd-yyyy', default: false, group: 'Dash'},
  {label: 'DD-MM-YYYY', value: 'dd-MM-yyyy', default: false, group: 'Dash'},

  {label: 'YYYY/MM/DD', value: 'yyyy/MM/dd', default: false, group: 'Slash'},
  {label: 'MM/DD/YYYY', value: 'MM/dd/yyyy', default: false, group: 'Slash'},
  {label: 'DD/MM/YYYY', value: 'dd/MM/yyyy', default: false, group: 'Slash'},

  {label: 'YYYY.MM.DD', value: 'yyyy.MM.dd', default: false, group: 'Dot'},
  {label: 'DD.MM.YYYY', value: 'dd.MM.yyyy', default: false, group: 'Dot'},

  {label: 'MMM DD, YYYY', value: 'MMM dd, yyyy', default: false, group: 'Text'},
  {label: 'DD MMM YYYY', value: 'dd MMM yyyy', default: false, group: 'Text'},
];

export interface Preference {
  preferenceId: string;
  name: string;
  mandatory: boolean;
  updatable: boolean;
  defaultValue?: string
}

export interface UserPreference {
  userId: string,
  preference: Preference,
  value: string
}

export interface UserPreferenceRequest {
  preferenceId: string,
  value: string
}

export const STORAGE_MISSING_MANDATORY_PREFERENCES = '__moneyverse_missing_mandatory_preferences';

export interface PreferenceFormData {
  key: PreferenceKey,
  value: string
}

export interface UserPreferenceFormData {
  currency?: PreferenceFormData,
  language?: PreferenceFormData,
  dateFormat?: PreferenceFormData
}
