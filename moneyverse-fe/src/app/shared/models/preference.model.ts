import {CurrencyDto} from './currencyDto';

export enum PreferenceKey {
  LANGUAGE = 'LANGUAGE',
  CURRENCY = 'CURRENCY',
  DATE_FORMAT = 'DATE_FORMAT'
}

type PreferenceMapping = {
  [PreferenceKey.LANGUAGE]: LanguageDto;
  [PreferenceKey.CURRENCY]: CurrencyDto;
  [PreferenceKey.DATE_FORMAT]: DateFormat;
};

export interface PreferenceField<T> {
  key: string;
  value: T | undefined;
  editable: boolean;
}

export type MandatoryPreferences = {
  [K in PreferenceKey]: PreferenceField<PreferenceMapping[K]>;
};

export interface PreferenceConfig {
  key: PreferenceKey;
  inputId: string;
  label: string;
  optionLabel: string;
  options: any;
}

export interface DateFormat {
  label: string;
  value: string;
  default: boolean,
  group: string
}

export interface LanguageDto {
  languageId: string;
  isoCode: string,
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

export interface PreferenceDto {
  preferenceId: string;
  name: string;
  mandatory: boolean;
  updatable: boolean;
  defaultValue?: string
}

export interface UserPreferenceDto {
  userId: string,
  preference: PreferenceDto,
  value: string
}

export interface UserPreferenceRequestDto {
  preferenceId: string,
  value: string
}
