import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, ColDirective, ModalBodyComponent, ModalComponent, ModalHeaderComponent, PlaceholderAnimationDirective, PlaceholderDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilCursor, cilFork } from '@coreui/icons';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { SearchService } from '../../../app/services/search.service';

@Component({
  selector: 'app-layout-search-modal',
  imports: [
    IconDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalBodyComponent,
    ButtonCloseDirective,
    FormsModule,
    PlaceholderDirective,
    PlaceholderAnimationDirective,
    ColDirective
  ],
  templateUrl: './layout-search-modal.component.html',
  styleUrl: './layout-search-modal.component.scss',
})
export class LayoutSearchModalComponent implements OnDestroy, OnChanges {
  @Input() visible!: boolean;
  @Output() closeModal = new EventEmitter<any>();

  protected icons = { cilCursor, cilFork };
  protected resultList: Array<string> = [];
  protected searchInput = "";
  protected loadSeach = true;
  protected showResult = false;
  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private searchService: SearchService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public ngOnChanges(): void {
    if (!this.visible) {
      this.resetForm();
    }
  }

  public onSearchChange(value: string) {
    this.searchSubject.next(value);
  }

  public onSubmit(value: string) {
    this.showResult = true;
    this.loadSeach = true;

    this.searchService.searchProject(value).subscribe({
      next: (value) => {
        this.resultList = value;
        this.loadSeach = false;
        this.cdr.detectChanges();
      },
      error: () => console.log("Erro ao fazer consulta!")
    })
  }

  protected closeSearchModal(): void {
    this.closeModal.emit();
    this.resetForm()
  }

  protected openPdf(projectName: string): void {
    this.searchService.openProject(projectName);
  }

  private resetForm(): void {
    this.resultList = [];
    this.searchInput = "";
    this.loadSeach = false;
    this.showResult = false;
  }
}
