import {Component, inject, output, signal, ViewChild} from '@angular/core';
import {SvgComponent} from "../../../../../../shared/components/svg/svg.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Category, CategoryFormData} from '../../../../category.model';
import {ColorPickerComponent} from '../../../../../../shared/components/color-picker/color-picker.component';
import {Color} from '../../../../../../shared/models/color.model';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {IconPickerComponent} from '../../../../../../shared/components/icon-picker/icon-picker.component';
import {Dialog} from 'primeng/dialog';
import {CategoryStore} from '../../../../category.store';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';
import {ColorService} from '../../../../../../shared/services/color.service';
import {Chip} from 'primeng/chip';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-category-form-dialog',
  imports: [
    SvgComponent,
    ReactiveFormsModule,
    ColorPickerComponent,
    IconPickerComponent,
    Dialog,
    FloatLabel,
    InputText,
    Message,
    Select,
    Chip,
    Button
  ],
  templateUrl: './category-form-dialog.component.html',
  styleUrl: './category-form-dialog.component.scss'
})
export class CategoryFormDialogComponent {

  private readonly fb = inject(FormBuilder);
  protected readonly Icons = IconsEnum;
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly colorService = inject(ColorService);

  @ViewChild(ColorPickerComponent) colorPicker!: ColorPickerComponent;
  @ViewChild(IconPickerComponent) iconPicker!: IconPickerComponent;

  protected _categoryToEdit = signal<Category | null>(null);
  categoryToEdit = this._categoryToEdit.asReadonly()
  save = output<CategoryFormData>();
  categoryForm: FormGroup;

  protected _isOpen = false;

  constructor() {
    this.categoryForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      categoryName: ['', Validators.required],
      parentCategory: [null],
      description: [''],
      color: ['red'],
      icon: [IconsEnum.CIRCLE_DOLLAR_SIGN]
    })
  }

  private patchForm(category: Category): void {
    this.categoryForm.patchValue({
      categoryName: category.categoryName,
      parentCategory: category.parentCategory ?? null,
      description: category.description,
      color: category.style.color,
      icon: category.style.icon
    })
  }

  get categoryName() {
    return this.categoryForm.get('categoryName');
  }

  get color() {
    return this.categoryForm.controls['color'];
  }

  get icon() {
    return this.categoryForm.controls['icon'];
  }

  open(category?: Category) {
    this._isOpen = true;
    if (category) {
      this._categoryToEdit.set(category);
      this.patchForm(category);
    } else {
      this._categoryToEdit.set(null);
      this.reset();
    }
  }

  close() {
    this._isOpen = false;
    this._categoryToEdit.set(null);
    this.reset();
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
      color: selectedColor.name
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
      this.save.emit({
        categoryName: form.value.categoryName,
        parentCategory: form.value.parentCategory,
        description: form.value.description,
        style: {
          color: form.value.color,
          icon: form.value.icon
        }
      });
      this.close();
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
      color: 'red',
      icon: this.Icons.CIRCLE_DOLLAR_SIGN,
    });
    this.categoryForm.markAsPristine();
    this.categoryForm.markAsUntouched();
  }

  protected cancel(): void {
    this.reset();
    this.close();
  }

}
