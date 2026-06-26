// Tipos sincronizados con los DTOs del backend (Java records).
// Si cambias un campo en el backend, cámbialo acá también.

export interface CategoryDTO {
  id: number;
  name: string;
  slug: string;
  description: string | null;
}

export interface ProductSummaryDTO {
  id: number;
  name: string;
  slug: string;
  price: number;
  imageUrl: string | null;
  category: CategoryDTO;
}

export interface ProductDetailDTO {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  price: number;
  imageUrl: string | null;
  category: CategoryDTO;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export interface ProductQueryParams {
  page?: number;
  size?: number;
  category?: string;
  q?: string;
}
