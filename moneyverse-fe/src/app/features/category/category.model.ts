import {Style} from '../../shared/models/common.model';

export interface Category {
  categoryId: string,
  userId: string,
  categoryName: string,
  description: string,
  parentCategory?: string,
  children?: Category[],
  style: Style
}

export interface CategoryRequest {
  userId?: string,
  categoryName?: string,
  description?: string,
  parentId?: Category,
  style?: Style
}
