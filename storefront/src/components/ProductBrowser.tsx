import { useEffect, useMemo, useState } from 'react';
import { formatPrice } from '../lib/format';
import { getProducts, ApiError } from '../lib/api';
import type { CategoryDTO, PagedResponse, ProductSummaryDTO } from '../lib/types';

interface Props {
  categories: CategoryDTO[];
  initialProducts: PagedResponse<ProductSummaryDTO>;
}

type SortOption = 'newest' | 'price-asc' | 'price-desc';

const PAGE_SIZE = 12;

export default function ProductBrowser({ categories, initialProducts }: Props) {
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [sort, setSort] = useState<SortOption>('newest');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PagedResponse<ProductSummaryDTO>>(initialProducts);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Debounce search input (300ms)
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedSearch(search), 300);
    return () => clearTimeout(timer);
  }, [search]);

  // Reset to first page when filters change
  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, selectedCategory, sort]);

  // Fetch when filters or page change
  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    const params: { page: number; size: number; category?: string; q?: string } = {
      page,
      size: PAGE_SIZE,
    };
    if (selectedCategory) params.category = selectedCategory;
    if (debouncedSearch.trim()) params.q = debouncedSearch.trim();

    getProducts(params)
      .then((result) => {
        if (!cancelled) {
          // Apply client-side sort (backend sorts by createdAt desc)
          const sorted = sortProducts(result.content, sort);
          setData({ ...result, content: sorted });
        }
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          const message =
            err instanceof ApiError
              ? err.message
              : err instanceof Error
                ? err.message
                : 'Failed to load products';
          setError(message);
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [debouncedSearch, selectedCategory, page, sort]);

  const products = useMemo(() => data.content, [data.content]);

  return (
    <div>
      {/* Filter bar */}
      <div className="mb-6 flex flex-col gap-3 rounded-lg border border-gray-200 bg-white p-4 sm:flex-row sm:items-center">
        <div className="flex-1">
          <label htmlFor="product-search" className="sr-only">
            Search products
          </label>
          <input
            id="product-search"
            type="search"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search products..."
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm placeholder-gray-400 focus:border-gray-900 focus:ring-1 focus:ring-gray-900 focus:outline-none"
          />
        </div>

        <div className="flex gap-2">
          <label htmlFor="product-category" className="sr-only">
            Category
          </label>
          <select
            id="product-category"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:border-gray-900 focus:ring-1 focus:ring-gray-900 focus:outline-none"
          >
            <option value="">All categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.slug}>
                {c.name}
              </option>
            ))}
          </select>

          <label htmlFor="product-sort" className="sr-only">
            Sort
          </label>
          <select
            id="product-sort"
            value={sort}
            onChange={(e) => setSort(e.target.value as SortOption)}
            className="rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:border-gray-900 focus:ring-1 focus:ring-gray-900 focus:outline-none"
          >
            <option value="newest">Newest</option>
            <option value="price-asc">Price: low to high</option>
            <option value="price-desc">Price: high to low</option>
          </select>
        </div>
      </div>

      {/* Status */}
      <div className="mb-4 flex items-center justify-between text-sm text-gray-600">
        <span>
          {loading
            ? 'Loading...'
            : `${data.totalElements} product${data.totalElements === 1 ? '' : 's'}`}
          {selectedCategory && !loading && (
            <span className="ml-2 text-gray-500">
              in {categories.find((c) => c.slug === selectedCategory)?.name}
            </span>
          )}
        </span>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Grid or empty state */}
      {!error && products.length === 0 && !loading ? (
        <div className="rounded-lg border border-dashed border-gray-300 bg-white p-12 text-center">
          <p className="text-base text-gray-600">No products match your filters.</p>
          <button
            type="button"
            onClick={() => {
              setSearch('');
              setSelectedCategory('');
              setSort('newest');
            }}
            className="mt-4 text-sm font-medium text-gray-900 underline hover:text-gray-700"
          >
            Clear filters
          </button>
        </div>
      ) : (
        <div
          className={`grid grid-cols-1 gap-6 transition-opacity sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 ${loading ? 'opacity-50' : 'opacity-100'}`}
        >
          {products.map((product) => (
            <a
              key={product.id}
              href={`/products/${product.slug}`}
              className="group block overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm transition-all hover:-translate-y-0.5 hover:border-gray-300 hover:shadow-md"
            >
              <div className="aspect-square w-full overflow-hidden bg-gray-100">
                <img
                  src={product.imageUrl ?? 'https://placehold.co/600x600?text=No+image'}
                  alt={product.name}
                  loading="lazy"
                  className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                />
              </div>
              <div className="p-4">
                <p className="text-xs tracking-wide text-gray-500 uppercase">
                  {product.category.name}
                </p>
                <h3 className="mt-1 line-clamp-1 text-base font-medium text-gray-900">
                  {product.name}
                </h3>
                <p className="mt-2 text-lg font-semibold text-gray-900">
                  {formatPrice(product.price)}
                </p>
              </div>
            </a>
          ))}
        </div>
      )}

      {/* Pagination */}
      {data.totalPages > 1 && (
        <div className="mt-8 flex items-center justify-center gap-2">
          <button
            type="button"
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0 || loading}
            className="rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Previous
          </button>
          <span className="px-3 text-sm text-gray-600">
            Page {data.page + 1} of {data.totalPages}
          </span>
          <button
            type="button"
            onClick={() => setPage((p) => p + 1)}
            disabled={!data.hasNext || loading}
            className="rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

function sortProducts(products: ProductSummaryDTO[], sort: SortOption): ProductSummaryDTO[] {
  const copy = [...products];
  switch (sort) {
    case 'price-asc':
      copy.sort((a, b) => a.price - b.price);
      break;
    case 'price-desc':
      copy.sort((a, b) => b.price - a.price);
      break;
    case 'newest':
    default:
      // Backend already returns sorted by createdAt desc
      break;
  }
  return copy;
}
