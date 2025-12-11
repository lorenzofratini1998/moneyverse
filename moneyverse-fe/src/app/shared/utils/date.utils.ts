export function today() {
  const {year, month, day} = getToday();
  return getUTCDate(year, month, day);
}

export function firstDayOfCurrentMonth() {
  const {year, month} = getToday();
  return getUTCDate(year, month, 1);
}

export function lastDayOfCurrentMonth() {
  const {year, month} = getToday();
  return getUTCDate(year, month + 1, 0);
}

export function getUTCDate(year: number, month: number, day: number) {
  return new Date(Date.UTC(year, month, day));
}

function getToday() {
  const now = new Date();
  return {
    year: now.getFullYear(),
    month: now.getMonth(),
    day: now.getDate(),
  };
}
