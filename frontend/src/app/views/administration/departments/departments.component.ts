import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonDirective, CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { cilPlus, cilX } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { ToastrService } from 'ngx-toastr';
import { PostitionService } from './../../../services/position.service';
import { DepartmentTableComponent } from '../../../../components/table/department-table/department-table.component';
import { DepartmentFormComponent } from '../../../../components/forms/department/department-form/department-form.component';
import { DepartmentEditFormComponent } from '../../../../components/forms/department/department-edit-form/department-edit-form.component';
import { Position, PositionFormImput } from '../../../interface/position.interface';

@Component({
  selector: 'app-departments',
  imports: [
    ColComponent,
    RowComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    ButtonDirective,
    IconDirective,
    Tabs2Module,
    DepartmentTableComponent,
    DepartmentFormComponent,
    DepartmentEditFormComponent
  ],
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.scss',
})
export class DepartmentsComponent implements OnInit {
  protected activeDepartments!: Array<Position>;
  protected inactiveDepartments!: Array<Position>;
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected icons = { cilPlus, cilX };
  protected activeItemKey = 0;
  protected createDepartmentTab = false;
  protected editDepartmentTab = false;
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
    private toasterService: ToastrService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  public ngOnInit(): void {
    this.departmentService.findAll().subscribe({
      next: (departments: Array<Position>) => {
        this.updateDepartments(departments)
        this.cdr.detectChanges();
      },
      error: (error) => {
        if (error.status == 401) {
          this.router.navigate(['login']);
          this.toasterService.error('Sessão expirada!');
          return;
        }

        this.toasterService.error('Erro ao obter departamentos!', error)
      }
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
    this.activeItemKey = parsedKey;
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
    this.editDepartmentTab = status;
    status ? this.activeItemKey = 3 : this.activeItemKey = 0;
  }

  protected onUpdate(form: {id: number, data: PositionFormImput}): void {
    this.departmentService.update(form.id, form.data).subscribe({
      next: (data: Array<Position>) => {
        this.updateDepartments(data);
        this.toasterService.success('Departamento editado com sucesso!');
        this.toggleEditDepartmentTab(false);
        this.cdr.detectChanges();
      },
      error: error => {
        this.toasterService.error('Erro ao editar departamento!')
      }
    });
  }

  protected openCreateTab(id: number): void {
    this.toggleCreateDepartmentTab(true);
    this.cdr.detectChanges();
  }

  protected toggleCreateDepartmentTab(status: boolean): void {
    this.createDepartmentTab = status;
    status ? this.activeItemKey = 2 : this.activeItemKey = 0;
  }

  protected onCreate(data: PositionFormImput): void {
    this.departmentService.insert(data).subscribe({
      next: (data: Array<Position>) => {
        this.updateDepartments(data);
        this.toasterService.success('Departamento criado com sucesso!');
        this.toggleCreateDepartmentTab(false);
        this.cdr.detectChanges();
      },
      error: error => {
        this.toasterService.error('Erro ao criar departamento!')
      }
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
      error: () => this.toasterService.error('Erro ao desativar departamento!')
    });
  }
}
