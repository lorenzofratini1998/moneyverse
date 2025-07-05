import {Component, computed, EventEmitter, Input, Output, signal} from '@angular/core';

@Component({
  selector: 'app-year-picker',
  imports: [],
  templateUrl: './year-picker.component.html',
  styleUrl: './year-picker.component.scss'
})
export class YearPickerComponent {
  @Input() selectedYear = signal<number | null>(null);
  @Input() minYear: number = 1900;
  @Input() maxYear: number = 2100;
  @Input() placeholder: string = 'Seleziona anno';
  @Input() disabled: boolean = false;

  @Output() yearSelected = new EventEmitter<number>();
  @Output() yearCleared = new EventEmitter<void>();

  currentYear = new Date().getFullYear();
  isOpen = signal(false);
  currentDecadeStart = signal(Math.floor(this.currentYear / 10) * 10);

  currentDecadeEnd = computed(() => this.currentDecadeStart() + 9);

  displayedYears = computed(() => {
    const start = this.currentDecadeStart();
    return Array.from({length: 10}, (_, i) => start + i);
  });

  constructor() {
    // Se non c'è un anno selezionato, mostra il decennio corrente
    if (!this.selectedYear()) {
      this.currentDecadeStart.set(Math.floor(this.currentYear / 10) * 10);
    } else {
      // Altrimenti mostra il decennio dell'anno selezionato
      this.currentDecadeStart.set(Math.floor(this.selectedYear()! / 10) * 10);
    }
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
      this.yearSelected.emit(year);
      this.isOpen.set(false);
    }
  }

  clearSelection() {
    this.selectedYear.set(null);
    this.yearCleared.emit();
    this.isOpen.set(false);
  }

  isYearDisabled(year: number): boolean {
    return year < this.minYear || year > this.maxYear || this.disabled;
  }

  getYearButtonClass(year: number): string {
    const baseClass = 'hover:btn-primary hover:text-primary-content';

    if (this.selectedYear() === year) {
      return `btn-primary ${baseClass}`;
    }

    if (year === this.currentYear) {
      return `btn-outline btn-secondary ${baseClass}`;
    }

    if (this.isYearDisabled(year)) {
      return 'btn-disabled opacity-30';
    }

    return `btn-outline ${baseClass}`;
  }
}
