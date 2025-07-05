import {Component, computed, EventEmitter, Input, Output, signal} from '@angular/core';

export interface MonthYearSelection {
  month: number;
  year: number;
  monthName: string;
}

@Component({
  selector: 'app-month-year-picker',
  imports: [],
  templateUrl: './month-year-picker.component.html',
  styleUrl: './month-year-picker.component.scss'
})
export class MonthYearPickerComponent {
  @Input() selection = signal<MonthYearSelection | null>(null);
  @Input() minYear: number = 1900;
  @Input() maxYear: number = 2100;
  @Input() minMonth?: number; // 1-12, solo se l'anno è minYear
  @Input() maxMonth?: number; // 1-12, solo se l'anno è maxYear
  @Input() placeholder: string = 'Seleziona mese e anno';
  @Input() disabled: boolean = false;

  @Output() selectionChanged = new EventEmitter<MonthYearSelection>();
  @Output() selectionCleared = new EventEmitter<void>();

  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth() + 1; // JavaScript months are 0-based
  isOpen = signal(false);
  currentView = signal<'year' | 'month'>('year');
  selectedYear = signal<number | null>(null);
  currentDecadeStart = signal(Math.floor(this.currentYear / 10) * 10);

  currentDecadeEnd = computed(() => this.currentDecadeStart() + 9);

  displayedYears = computed(() => {
    const start = this.currentDecadeStart();
    return Array.from({length: 10}, (_, i) => start + i);
  });

  months = [
    {value: 1, name: 'Gennaio', short: 'Gen'},
    {value: 2, name: 'Febbraio', short: 'Feb'},
    {value: 3, name: 'Marzo', short: 'Mar'},
    {value: 4, name: 'Aprile', short: 'Apr'},
    {value: 5, name: 'Maggio', short: 'Mag'},
    {value: 6, name: 'Giugno', short: 'Giu'},
    {value: 7, name: 'Luglio', short: 'Lug'},
    {value: 8, name: 'Agosto', short: 'Ago'},
    {value: 9, name: 'Settembre', short: 'Set'},
    {value: 10, name: 'Ottobre', short: 'Ott'},
    {value: 11, name: 'Novembre', short: 'Nov'},
    {value: 12, name: 'Dicembre', short: 'Dic'}
  ];

  constructor() {
    // Inizializza il componente con la selezione esistente
    if (this.selection()) {
      this.selectedYear.set(this.selection()!.year);
      this.currentDecadeStart.set(Math.floor(this.selection()!.year / 10) * 10);
    } else {
      this.currentDecadeStart.set(Math.floor(this.currentYear / 10) * 10);
    }
  }

  getDisplayText(): string {
    const sel = this.selection();
    if (sel) {
      return `${sel.monthName} ${sel.year}`;
    }
    return this.placeholder;
  }

  navigateDecade(direction: number) {
    const newStart = this.currentDecadeStart() + (direction * 10);
    if (newStart >= this.minYear && newStart <= this.maxYear) {
      this.currentDecadeStart.set(newStart);
    }
  }

  selectYear(year: number) {
    if (!this.isYearDisabled(year)) {
      this.selectedYear.set(year);
      this.currentView.set('month');
      // Non chiudere il dropdown, passa alla vista mesi
    }
  }

  selectMonth(month: number) {
    if (!this.isMonthDisabled(month) && this.selectedYear()) {
      const year = this.selectedYear()!;
      const monthName = this.months.find(m => m.value === month)?.name || '';

      const newSelection: MonthYearSelection = {
        month,
        year,
        monthName
      };

      this.selection.set(newSelection);
      this.selectionChanged.emit(newSelection);
      this.closeDropdown();
    }
  }

  selectCurrentMonth() {
    if (this.isCurrentYearSelected() && !this.isMonthDisabled(this.currentMonth)) {
      this.selectMonth(this.currentMonth);
    }
  }

  goBackToYearSelection() {
    this.currentView.set('year');
    this.selectedYear.set(null);
    // Non chiudere il dropdown
  }

  clearSelection() {
    this.selection.set(null);
    this.selectedYear.set(null);
    this.selectionCleared.emit();
    this.closeDropdown();
  }

  toggleDropdown(event?: Event) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    if (this.disabled) return;

    this.isOpen.set(!this.isOpen());
    if (this.isOpen()) {
      this.currentView.set('year');
      this.selectedYear.set(null);
    }
  }

  closeDropdown() {
    this.isOpen.set(false);
    this.currentView.set('year');
    this.selectedYear.set(null);
  }

  isYearDisabled(year: number): boolean {
    return year < this.minYear || year > this.maxYear || this.disabled;
  }

  isMonthDisabled(month: number): boolean {
    if (this.disabled || !this.selectedYear()) return true;

    const year = this.selectedYear()!;

    // Check min constraints
    if (year === this.minYear && this.minMonth && month < this.minMonth) {
      return true;
    }

    // Check max constraints
    if (year === this.maxYear && this.maxMonth && month > this.maxMonth) {
      return true;
    }

    return false;
  }

  isCurrentYearSelected(): boolean {
    return this.selectedYear() === this.currentYear;
  }

  getYearButtonClass(year: number): string {
    const baseClass = 'hover:btn-primary hover:text-primary-content';

    if (this.selectedYear() === year) {
      return `btn-primary ${baseClass}`;
    }

    if (this.selection()?.year === year) {
      return `btn-secondary ${baseClass}`;
    }

    if (year === this.currentYear) {
      return `btn-outline btn-accent ${baseClass}`;
    }

    if (this.isYearDisabled(year)) {
      return 'btn-disabled opacity-30';
    }

    return `btn-outline ${baseClass}`;
  }

  getMonthButtonClass(month: number): string {
    const baseClass = 'hover:btn-primary hover:text-primary-content';

    if (this.selection()?.month === month && this.selection()?.year === this.selectedYear()) {
      return `btn-primary ${baseClass}`;
    }

    if (month === this.currentMonth && this.isCurrentYearSelected()) {
      return `btn-outline btn-accent ${baseClass}`;
    }

    if (this.isMonthDisabled(month)) {
      return 'btn-disabled opacity-30';
    }

    return `btn-outline ${baseClass}`;
  }
}
