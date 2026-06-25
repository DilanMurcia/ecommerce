import { useState } from 'react';

export default function HelloReact() {
  const [count, setCount] = useState(0);

  return (
    <div className="flex items-center gap-4">
      <button
        type="button"
        onClick={() => setCount((c) => c + 1)}
        className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
      >
        Click aquí
      </button>
      <span className="text-gray-700">
        Has hecho click <strong className="text-blue-600">{count}</strong> veces
      </span>
    </div>
  );
}
