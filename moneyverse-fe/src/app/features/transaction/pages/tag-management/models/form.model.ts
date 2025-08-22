import {Style} from '../../../../../shared/models/common.model';

export interface TagFormData {
  tagId?: string,
  tagName: string,
  description?: string,
  style: Style
}
