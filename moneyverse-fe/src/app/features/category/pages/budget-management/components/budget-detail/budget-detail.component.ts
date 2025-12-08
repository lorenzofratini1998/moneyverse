import {Component, inject, input, output} from '@angular/core';
import {BudgetGaugeComponent} from "../budget-gauge/budget-gauge.component";
import {ButtonDirective} from "primeng/button";
import {Card} from "primeng/card";
import {ProgressBar} from "primeng/progressbar";
import {SvgComponent} from "../../../../../../shared/components/svg/svg.component";
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {Budget} from '../../../../category.model';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {TranslationService} from '../../../../../../shared/services/translation.service';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-budget-detail',
  imports: [
    BudgetGaugeComponent,
    ButtonDirective,
    Card,
    ProgressBar,
    SvgComponent,
    CurrencyPipe,
    TranslatePipe
  ],
  templateUrl: './budget-detail.component.html'
})
export class BudgetDetailComponent {
  budget = input.required<Budget>();

  onEdit = output<Budget>();
  onDelete = output<Budget>();

  protected readonly icons = IconsEnum;
  private readonly confirmationService = inject(AppConfirmationService);
  private readonly translateService = inject(TranslationService);

  protected confirmDelete(budget: Budget) {
    this.confirmationService.confirmDelete({
      header: this.translateService.translate('app.dialog.budget.delete'),
      message: this.translateService.translate('app.dialog.budget.confirmDelete'),
      accept: () => this.onDelete.emit(budget),
    })
  }
}
