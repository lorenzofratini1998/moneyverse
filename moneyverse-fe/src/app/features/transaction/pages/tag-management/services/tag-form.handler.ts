import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../shared/models/form.model';
import {Tag} from '../../../transaction.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {IconsEnum} from '../../../../../shared/models/icons.model';
import {TagFormData} from "../models/form.model";

@Injectable({
  providedIn: 'root'
})
export class TagFormHandler implements FormHandler<Tag, TagFormData> {
  private readonly fb = inject(FormBuilder);

  create(): FormGroup {
    return this.fb.group({
      tagId: [null],
      tagName: [null, Validators.required],
      description: [null],
      color: ['red'],
      icon: [IconsEnum.CIRCLE_DOLLAR_SIGN],
    });
  }

  patch(form: FormGroup, tag: Tag): void {
    form.patchValue({
      tagId: tag.tagId,
      tagName: tag.tagName,
      description: tag.description,
      color: tag.style.color,
      icon: tag.style.icon,
    });
  }

  reset(form: FormGroup): void {
    form.reset({
      tagId: null,
      tagName: '',
      description: '',
      color: 'red',
      icon: IconsEnum.CIRCLE_DOLLAR_SIGN,
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): TagFormData {
    const value = form.value;
    return {
      tagId: value.tagId,
      tagName: value.tagName,
      description: value.description,
      style: {
        color: value.color,
        icon: value.icon
      }
    };
  }

}
