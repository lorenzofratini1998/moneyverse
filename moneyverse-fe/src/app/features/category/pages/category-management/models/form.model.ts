import {Style} from '../../../../../shared/models/common.model';

export interface CategoryFormData {
  categoryId?: string,
  categoryName: string,
  parentId?: string,
  description?: string,
  style: Style
}

export interface CategoryFilterFormData {
  name?: string,
  parentCategories?: string[],
}
