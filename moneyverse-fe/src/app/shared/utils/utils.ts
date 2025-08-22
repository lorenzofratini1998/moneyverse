import {HttpParams} from '@angular/common/http';

export function buildHttpParams(criteria: any): HttpParams {
  return new HttpParams({
    fromObject: flattenAndStringify(criteria),
  });
}

function flattenAndStringify(obj: any, prefix = ''): Record<string, string> {
  return Object.entries(obj).reduce((acc, [key, value]) => {
    if (value == null) return acc;

    const paramName = prefix ? `${prefix}.${key}` : key;

    if (value instanceof Date) {
      acc[paramName] = value.toISOString().split('T')[0];
    } else if (typeof value === 'object' && !Array.isArray(value)) {
      Object.assign(acc, flattenAndStringify(value, paramName));
    } else if (Array.isArray(value)) {
      acc[paramName] = value.join(',');
    } else {
      acc[paramName] = value.toString();
    }

    return acc;
  }, {} as Record<string, string>);
}
