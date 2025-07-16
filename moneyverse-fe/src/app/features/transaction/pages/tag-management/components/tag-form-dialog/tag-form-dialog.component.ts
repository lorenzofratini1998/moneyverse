import {Component, inject, output, signal, ViewChild} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {Chip} from 'primeng/chip';
import {InputText} from 'primeng/inputtext';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {ColorPickerComponent} from '../../../../../../shared/components/color-picker/color-picker.component';
import {IconPickerComponent} from '../../../../../../shared/components/icon-picker/icon-picker.component';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {ColorService} from '../../../../../../shared/services/color.service';
import {Tag, TagFormData} from '../../../../transaction.model';
import {Color} from '../../../../../../shared/models/color.model';

@Component({
  selector: 'app-tag-form-dialog',
  imports: [
    Dialog,
    ReactiveFormsModule,
    Chip,
    SvgComponent,
    ColorPickerComponent,
    IconPickerComponent,
    InputText,
    FloatLabel,
    Message,
    Button
  ],
  templateUrl: './tag-form-dialog.component.html',
  styleUrl: './tag-form-dialog.component.scss'
})
export class TagFormDialogComponent {

  protected readonly Icons = IconsEnum;
  protected readonly colorService = inject(ColorService);
  private readonly fb = inject(FormBuilder);

  @ViewChild(ColorPickerComponent) colorPicker!: ColorPickerComponent;
  @ViewChild(IconPickerComponent) iconPicker!: IconPickerComponent;

  protected _isOpen = false;
  protected _tagToEdit = signal<Tag | null>(null);
  tagToEdit = this._tagToEdit.asReadonly();
  save = output<TagFormData>();
  tagForm: FormGroup;

  constructor() {
    this.tagForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      tagName: [''],
      description: [''],
      color: ['red'],
      icon: [IconsEnum.CIRCLE_DOLLAR_SIGN],
    });
  }

  private patchForm(tag: Tag): void {
    this.tagForm.patchValue({
      tagName: tag.tagName,
      description: tag.description,
      color: tag.style.color,
      icon: tag.style.icon,
    });
  }

  get color() {
    return this.tagForm.controls['color'];
  }

  get icon() {
    return this.tagForm.controls['icon'];
  }

  open(tag?: Tag) {
    this._isOpen = true;
    if (tag) {
      this._tagToEdit.set(tag);
      this.patchForm(tag);
    } else {
      this._tagToEdit.set(null);
      this.tagForm.reset();
    }
  }

  close() {
    this._isOpen = false;
    this._tagToEdit.set(null);
    this.tagForm.reset();
  }

  preview() {
    return this.tagName.value ?? 'Example';
  }

  get tagName() {
    return this.tagForm.controls['tagName'];
  }

  updateColor(color: Color) {
    this.tagForm.patchValue({
      color: color.name
    });
  }

  updateIcon(icon: IconsEnum) {
    this.tagForm.patchValue({
      icon: icon
    });
  }

  saveTag() {
    const form = this.tagForm;
    if (form.valid) {
      this.save.emit({
        tagName: form.value.tagName,
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
    this.tagForm.reset({
      tagName: '',
      description: '',
      color: 'red',
      icon: this.Icons.CIRCLE_DOLLAR_SIGN,
    });
    this.tagForm.markAsPristine();
    this.tagForm.markAsUntouched();
  }

  protected cancel(): void {
    this.reset();
    this.close();
  }
}
