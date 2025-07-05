import {Component, computed, effect, inject} from '@angular/core';
import {IconsEnum} from '../../models/icons.model';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SvgComponent} from "../svg/svg.component";
import {DashboardStore} from '../../stores/dashboard.store';
import {AccountStore} from '../../../features/account/account.store';
import {CategoryStore} from '../../../features/category/category.store';
import {MultiSelectComponent} from '../multi-select/multi-select.component';
import {DatePicker} from 'primeng/datepicker';
import {MultiSelect} from 'primeng/multiselect';
import {Checkbox} from 'primeng/checkbox';
import {PrimeTemplate} from 'primeng/api';
import {FloatLabel} from 'primeng/floatlabel';

@Component({
  selector: 'app-dashboard-filter-drawer',
  imports: [
    ReactiveFormsModule,
    SvgComponent,
    MultiSelectComponent,
    DatePicker,
    FormsModule,
    MultiSelect,
    Checkbox,
    PrimeTemplate,
    FloatLabel
  ],
  templateUrl: './dashboard-filter-drawer.component.html',
  styleUrl: './dashboard-filter-drawer.component.scss'
})
export class DashboardFilterDrawerComponent {
  protected readonly Icons = IconsEnum;
  protected readonly dashboardStore = inject(DashboardStore);
  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);

  private readonly fb = inject(FormBuilder);

  accountsSelect = computed(() => this.accountStore.accounts().map(account => account.accountName));
  categoriesSelect = computed(() => this.categoryStore.categories().map(category => category.categoryName));
  date3: Date[] | undefined;

  filterForm: FormGroup = this.createForm();

  private createForm(): FormGroup {
    const filter = this.dashboardStore.filter();
    return this.fb.group({
      accounts: [filter.accounts ?? []],
      categories: [filter.categories ?? []],
    })
  }

  constructor() {
    effect(() => {
      const filter = this.dashboardStore.filter();
      this.filterForm.patchValue({
        accounts: filter.accounts ?? [],
        categories: filter.categories ?? [],
      }, {emitEvent: false});
    })
  }

  reset() {
    this.dashboardStore.resetFilter();
  }

  apply() {
    const {accounts, categories} = this.filterForm.value;
    this.dashboardStore.updateFilter({
      accounts: accounts,
      categories: categories,
    });
    console.log(this.dashboardStore.filter());
  }

}
