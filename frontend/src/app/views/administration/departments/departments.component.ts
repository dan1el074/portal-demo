import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ButtonDirective, CardBodyComponent, CardComponent, ContainerComponent, Tabs2Module } from '@coreui/angular';
import { cilPlus, cilX } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { ToastrService } from 'ngx-toastr';
import { PostitionService } from './../../../services/position.service';
import { DepartmentTableComponent } from '../../../../components/table/department-table/department-table.component';
import { DepartmentFormComponent } from '../../../../components/forms/department/department-form/department-form.component';
import { DepartmentEditFormComponent } from '../../../../components/forms/department/department-edit-form/department-edit-form.component';
import { Position, PositionFormImput } from '../../../interface/position.interface';
import { ErrorService } from '../../../services/error.service';
import { BackNavigationService } from '../../../services/back-navigation.service';

@Component({
  selector: 'app-departments',
  imports: [
    ContainerComponent,
    CardComponent,
    CardBodyComponent,
    ButtonDirective,
    IconDirective,
    Tabs2Module,
    DepartmentTableComponent,
    DepartmentFormComponent,
    DepartmentEditFormComponent
  ],
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DepartmentsComponent implements OnInit {
  protected activeDepartments: Array<Position> = [];
  protected inactiveDepartments: Array<Position> = [];
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected icons = { cilPlus, cilX };
  protected activeItemKey = 0;
  protected createDepartmentTab = false;
  protected editDepartmentTab = false;
  protected tableResetKey = 0;
  private formHistoryActive = false;
  private historyCloseTargetTab = 0;
  protected editDepartmentData: Position = {
    id: 0,
    name: '',
    manangers: [],
    activated: false,
    updatedAt: '',
    createdAt: ''
  };

  constructor(
    private departmentService: PostitionService,
    private errorService: ErrorService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef,
    private backNav: BackNavigationService
  ) { }

  public ngOnInit(): void {
    this.departmentService.findAll().subscribe({
      next: (departments: Array<Position>) => {
        this.updateDepartments(departments)
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao obter departamentos!')
    });
  }

  public updateDepartments(allDepartments: Array<Position>) {
    this.activeDepartments = allDepartments.filter(department => department.activated);
    this.inactiveDepartments = allDepartments.filter(department => !department.activated);
  }

  protected onTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const parsedKey = Number(key);
    if (Number.isNaN(parsedKey)) return;

    if (parsedKey <= 1 && (this.createDepartmentTab || this.editDepartmentTab)) {
      this.closeFormTabs(parsedKey);
      return;
    }

    this.activeItemKey = parsedKey;
  }

  protected resetTableFilters(): void {
    if (this.createDepartmentTab || this.editDepartmentTab) {
      this.closeFormTabs(0);
    } else {
      this.activeItemKey = 0;
    }
    this.tableResetKey++;
  }

  protected openUpdateTab(id: number): void {
    this.departmentService.findById(id).subscribe({
      next: (data) => {
        this.editDepartmentData = data;
        this.toggleEditDepartmentTab(true);
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao obter departamento!')
    });
  }

  protected toggleEditDepartmentTab(status: boolean): void {
    if (status) {
      this.createDepartmentTab = false;
      this.editDepartmentTab = true;
      this.activeItemKey = 3;
      this.registerFormHistory();
      return;
    }

    this.closeFormTabs();
  }

  protected onUpdate(form: {id: number, data: PositionFormImput}): void {
    this.departmentService.update(form.id, form.data).subscribe({
      next: (data: Array<Position>) => {
        this.updateDepartments(data);
        this.toasterService.success('Departamento editado com sucesso!');
        this.toggleEditDepartmentTab(false);
        this.cdr.detectChanges();
      },
      error: (error) => this.errorService.showError(error)
    });
  }

  protected openCreateTab(id: number): void {
    this.toggleCreateDepartmentTab(true);
    this.cdr.detectChanges();
  }

  protected toggleCreateDepartmentTab(status: boolean): void {
    if (status) {
      this.editDepartmentTab = false;
      this.createDepartmentTab = true;
      this.activeItemKey = 2;
      this.registerFormHistory();
      return;
    }

    this.closeFormTabs();
  }

  private registerFormHistory(): void {
    if (this.formHistoryActive) return;

    this.formHistoryActive = true;
    this.historyCloseTargetTab = 0;
    this.backNav.register(() => {
      const targetTab = this.historyCloseTargetTab;
      this.formHistoryActive = false;
      this.historyCloseTargetTab = 0;
      this.resetFormTabs(targetTab);
    });
  }

  private closeFormTabs(targetTab = 0): void {
    const shouldRemoveHistory = this.formHistoryActive;
    this.formHistoryActive = false;
    this.historyCloseTargetTab = targetTab;
    this.resetFormTabs(targetTab);

    if (shouldRemoveHistory) {
      this.backNav.unregister();
    } else {
      this.historyCloseTargetTab = 0;
    }
  }

  private resetFormTabs(targetTab = 0): void {
    this.createDepartmentTab = false;
    this.editDepartmentTab = false;
    this.activeItemKey = targetTab;
    this.cdr.detectChanges();
  }

  protected onCreate(data: PositionFormImput): void {
    this.departmentService.insert(data).subscribe({
      next: (data: Array<Position>) => {
        this.updateDepartments(data);
        this.toasterService.success('Departamento criado com sucesso!');
        this.toggleCreateDepartmentTab(false);
        this.cdr.detectChanges();
      },
      error: (error) => this.errorService.showError(error)
    });
  }

  protected onDeactive(id: number): void {
    this.departmentService.deactive(id).subscribe({
      next: (data: Array<Position>) => {
        this.updateDepartments(data);
        this.toasterService.success('Departamento desativado com sucesso!');
        this.toggleCreateDepartmentTab(false);
        this.cdr.detectChanges();
      },
      error: (error) => this.errorService.showError(error)
    });
  }
}
