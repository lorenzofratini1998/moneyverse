import {Component, computed, effect, inject, input, output, ViewChild} from '@angular/core';
import {SvgComponent} from "../../../../../../shared/components/svg/svg.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Category} from '../../../../category.model';
import {ColorPickerComponent} from '../../../../../../shared/components/color-picker/color-picker.component';
import {Color, COLORS} from '../../../../../../shared/models/color.model';
import {NgStyle} from '@angular/common';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {
  ICON_PICKER_ICONS,
  IconPickerComponent
} from '../../../../../../shared/components/icon-picker/icon-picker.component';

@Component({
  selector: 'app-category-form',
  imports: [
    SvgComponent,
    ReactiveFormsModule,
    ColorPickerComponent,
    NgStyle,
    IconPickerComponent
  ],
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.scss'
})
export class CategoryFormComponent {

  private readonly fb = inject(FormBuilder);
  protected readonly Icons = IconsEnum;

  @ViewChild(ColorPickerComponent) colorPicker!: ColorPickerComponent;
  @ViewChild(IconPickerComponent) iconPicker!: IconPickerComponent;

  categories = input.required<Category[]>();
  category = input<Category | null>(null);
  cancel = output<any>();
  save = output<any>();
  categoryForm: FormGroup = this.createForm();

  protected readonly categoryColor = computed(() => {
    const cat = this.category();
    const match = COLORS.find(c =>
      c.background === cat?.style.backgroundColor &&
      c.text === cat?.style.textColor
    ) ?? COLORS.find(color => color.selected) ?? COLORS[0];
    COLORS.forEach(c => c.selected = false);
    match.selected = true;
    return match;
  })

  protected readonly categoryIcon = computed(() => {
    const key = this.category()?.style.icon;
    const match = ICON_PICKER_ICONS.find(i => i === key);
    return match ?? ICON_PICKER_ICONS[0];
  })

  constructor() {
    effect(() => {
      const currentCategory = this.category();
      if (currentCategory) {
        this.patchForm(currentCategory)
      }
    });
  }

  private createForm(): FormGroup {
    return this.fb.group({
      categoryName: ['', Validators.required],
      parentCategory: [null],
      description: [''],
      backgroundColor: ['#FEE2E2'],
      textColor: ['#EF4444'],
      icon: [IconsEnum.CIRCLE_DOLLAR_SIGN]
    })
  }

  private patchForm(category: Category): void {
    this.categoryForm.patchValue({
      categoryName: category.categoryName,
      parentCategory: category.parentCategory ?? null,
      description: category.description,
      backgroundColor: category.style.backgroundColor,
      textColor: category.style.textColor,
      icon: category.style.icon
    })
  }

  get categoryName() {
    return this.categoryForm.get('categoryName');
  }

  get backgroundColor() {
    return this.categoryForm.controls['backgroundColor'];
  }

  get textColor() {
    return this.categoryForm.controls['textColor'];
  }

  get icon() {
    return this.categoryForm.controls['icon'];
  }

  preview() {
    const categoryName: string = this.categoryName?.value;
    if (categoryName === '') {
      return 'Example';
    }
    return categoryName;
  }

  updateColor(selectedColor: Color): void {
    this.categoryForm.patchValue({
      backgroundColor: selectedColor.background,
      textColor: selectedColor.text
    })
  }

  updateIcon(icon: IconsEnum): void {
    this.categoryForm.patchValue({
      icon: icon
    })
  }

  saveCategory() {
    const form = this.categoryForm;
    if (form.valid) {
      this.save.emit(form.value);
    } else {
      Object.keys(form.controls).forEach(key => {
        form.get(key)?.markAsTouched();
        form.get(key)?.markAsDirty();
      })
    }

  }

  reset() {
    this.categoryForm.reset({
      categoryName: '',
      parentCategory: null,
      description: '',
      backgroundColor: '#FEE2E2',
      textColor: '#EF4444',
      icon: this.Icons.CIRCLE_DOLLAR_SIGN,
    });
    this.colorPicker.reset();
    this.iconPicker.reset();
  }

  cancelForm(): void {
    this.cancel.emit({});
    this.reset();
  }


}
