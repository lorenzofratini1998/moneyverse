import {Component, ElementRef, forwardRef, HostListener, input} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {SvgComponent} from '../svg/svg.component';
import {IconsEnum} from '../../models/icons.model';

@Component({
  selector: 'app-multi-select',
  imports: [
    SvgComponent
  ],
  templateUrl: './multi-select.component.html',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => MultiSelectComponent),
    multi: true
  }]
})
export class MultiSelectComponent implements ControlValueAccessor {
  options = input<string[]>([]);
  label = input<string>('');

  selected: string[] = [];
  isOpen = false;

  private onChange: any = () => {
  };
  private onTouched: any = () => {
  };

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }

  constructor(private readonly elementRef: ElementRef) {
  }

  writeValue(value: string[]): void {
    this.selected = value || [];
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  toggleOption(option: string) {
    if (this.selected.includes(option)) {
      this.selected = this.selected.filter(o => o !== option);
    } else {
      this.selected = [...this.selected, option];
    }
    this.onChange(this.selected);
    this.onTouched();
  }

  isSelected(option: string): boolean {
    return this.selected.includes(option);
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  protected readonly IconsEnum = IconsEnum;
}
