import type {
  CategoryDTO,
  PagedResponse,
  ProductDetailDTO,
  ProductQueryParams,
  ProductSummaryDTO,
} from './types';

const API_BASE = import.meta.env.PUBLIC_API_URL ?? 'http://localhost:8080';

class ApiError extends Error {
  status: number;
  body: unknown;
  constructor(status: number, message: string, body: unknown) {
    super(message);
    this.status = status;
    this.body = body;
    this.name = 'ApiError';
  }
}

async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const url = `${API_BASE}${path}`;
  const response = await fetch(url, {
    headers: { Accept: 'application/json' },
    ...init,
  });

  if (!response.ok) {
    let body: unknown;
    try {
      body = await response.json();
    } catch {
      body = await response.text();
    }
    const message =
      typeof body === 'object' && body !== null && 'message' in body
        ? String((body as { message: unknown }).message)
        : `Request failed: ${response.status}`;
    throw new ApiError(response.status, message, body);
  }

  return (await response.json()) as T;
}

function buildQueryString(params: Record<string, string | number | undefined>): string {
  const filtered = Object.entries(params).filter(
    ([, value]) => value !== undefined && value !== null && value !== '',
  );
  if (filtered.length === 0) return '';
  const search = new URLSearchParams();
  for (const [key, value] of filtered) {
    search.set(key, String(value));
  }
  return `?${search.toString()}`;
}

export async function getCategories(): Promise<CategoryDTO[]> {
  return apiFetch<CategoryDTO[]>('/api/categories');
}

export async function getCategoryBySlug(slug: string): Promise<CategoryDTO> {
  return apiFetch<CategoryDTO>(`/api/categories/${encodeURIComponent(slug)}`);
}

export async function getProducts(
  params: ProductQueryParams = {},
): Promise<PagedResponse<ProductSummaryDTO>> {
  const query = buildQueryString({
    page: params.page,
    size: params.size,
    category: params.category,
    q: params.q,
  });
  return apiFetch<PagedResponse<ProductSummaryDTO>>(`/api/products${query}`);
}

export async function getProductBySlug(slug: string): Promise<ProductDetailDTO> {
  return apiFetch<ProductDetailDTO>(`/api/products/${encodeURIComponent(slug)}`);
}

export { ApiError };
