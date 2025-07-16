import {HttpParams} from '@angular/common/http';

export function buildHttpParams(criteria: any): HttpParams {
  let params = new HttpParams();

  function recurse(obj: any, prefix: string = '') {
    Object.entries(obj).forEach(([key, value]) => {
      if (value == null) return;
      const paramName = prefix
        ? `${prefix}.${key}`
        : key;

      if (typeof value === 'object' && !Array.isArray(value)) {
        recurse(value, paramName);
      } else {
        params = params.set(paramName, value.toString());
      }
    });
  }

  recurse(criteria);
  return params;
}
