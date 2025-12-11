export interface BoundCriteria {
  lower?: number,
  upper?: number
}

export interface DateCriteria {
  start?: Date,
  end?: Date
}

export interface PageCriteria {
  offset: number,
  limit: number
}

export interface SortCriteria {
  attribute: string,
  direction: string
}

export enum Direction {
  ASC = 'ASC',
  DESC = 'DESC'
}
