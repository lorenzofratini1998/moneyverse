import {PreferenceDto} from './preference.dto';

export interface UserPreferenceDto {
  userId: string,
  preference: PreferenceDto
  value: string
}
