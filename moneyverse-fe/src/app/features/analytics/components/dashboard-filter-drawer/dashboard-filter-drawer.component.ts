import {Component, signal, viewChild} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Drawer} from 'primeng/drawer';
import {DashboardFilterFormComponent} from '../dashboard-filter-form/dashboard-filter-form.component';
import {CancelButtonComponent} from '../../../../shared/components/forms/cancel-button/cancel-button.component';
import {SubmitButtonComponent} from '../../../../shared/components/forms/submit-button/submit-button.component';

@Component({
  selector: 'app-dashboard-filter-drawer',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    Drawer,
    DashboardFilterFormComponent,
    CancelButtonComponent,
    SubmitButtonComponent
  ],
  templateUrl: './dashboard-filter-drawer.component.html'
})
export class DashboardFilterDrawerComponent {

  form = viewChild.required(DashboardFilterFormComponent);

  protected isOpen = signal<boolean>(false);

  open() {
    this.isOpen.set(true);
  }

  submit() {
    this.form().submit();
    this.isOpen.set(false);
  }

  reset() {
    this.form().reset();
    this.isOpen.set(false);
  }

}

