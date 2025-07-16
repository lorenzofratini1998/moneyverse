export interface Style {
  color: string
  icon: string
}

export interface PageMetadata {
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface PageResponse<T> {
  content: T[]
  metadata: PageMetadata
}
