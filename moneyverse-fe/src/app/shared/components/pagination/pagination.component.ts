import {Component, computed, input, output} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-pagination',
  imports: [
    FormsModule
  ],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.scss'
})
export class PaginationComponent {
  currentPage = input(1);
  itemsPerPage = input(10);
  totalItems = input(0);
  pageSizeOptions = [5, 10, 25, 50, 100];

  pageChange = output<number>();
  itemsPerPageChange = output<number>();

  totalPages = computed(() => Math.ceil(this.totalItems() / this.itemsPerPage()));
  startItem = computed(() => (this.currentPage() - 1) * this.itemsPerPage() + 1);
  endItem = computed(() => Math.min(this.currentPage() * this.itemsPerPage(), this.totalItems()));

  visiblePages = computed(() => {
    const pages = [];
    const maxVisible = 5;
    let start = Math.max(1, this.currentPage() - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages(), start + maxVisible - 1);

    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  });

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages()) {
      this.pageChange.emit(page);
    }
  }
}
