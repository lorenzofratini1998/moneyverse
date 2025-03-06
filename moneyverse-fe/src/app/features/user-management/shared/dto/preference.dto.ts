export interface PreferenceDto {
  preferenceId: string;
  name: string;
  mandatory: boolean;
  updatable: boolean;
  defaultValue?: string
}
