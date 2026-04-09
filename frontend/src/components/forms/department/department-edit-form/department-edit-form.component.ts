import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MultiSelectComponent, MultiSelectOptgroupComponent, MultiSelectOptionComponent } from '@coreui/angular-pro';
import { ButtonDirective, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective } from '@coreui/angular';
import { cilPencil, cilX } from '@coreui/icons';
import { ToastrService } from 'ngx-toastr';
import { UserService } from './../../../../app/services/user.service';
import { Position, PositionFormImput } from '../../../../app/interface/position.interface';
import { UserGroup } from '../../../../app/interface/user.interface';

@Component({
  selector: 'app-department-edit-form',
  imports: [
    ReactiveFormsModule,
    FormFloatingDirective,
    FormLabelDirective,
    FormControlDirective,
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    ButtonDirective,
    MultiSelectComponent,
    MultiSelectOptionComponent,
    MultiSelectOptgroupComponent,
  ],
  templateUrl: './department-edit-form.component.html',
  styleUrl: './department-edit-form.component.scss',
})
export class DepartmentEditFormComponent implements OnInit, OnChanges {
  @Input() departmentData!: Position;
  @Output() exitTask = new EventEmitter<void>();
  @Output() editTask = new EventEmitter<{id: number, data: PositionFormImput}>();

  protected icons = { cilPencil, cilX };
  protected editForm: FormGroup;
  protected valid: boolean | undefined;
  protected showErrors: boolean = false;
  protected users: Array<UserGroup> = []

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef
  ) {
    this.editForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      manangers: [[], [Validators.required]],
      disabled: [false]
    })
  }

  public ngOnInit(): void {
    this.userService.listByPositionName().subscribe({
      next: (data) => {
        this.users = data;
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao buscar lista de usuários!')
    });
  }

  public ngOnChanges(): void {
    this.clearDepartmentData();
    this.editForm.get('name')?.setValue(this.departmentData.name);
    this.editForm.get('disabled')?.setValue(!this.departmentData.activated);

    let manangersId: Array<number> = [];
    this.departmentData.manangers.forEach(mananger => manangersId.push(mananger.id));
    this.editForm.get('manangers')?.setValue(manangersId);
  }

  protected clearDepartmentData(): void {
    this.editForm.reset({
      name: '',
      disabled: false,
      manangers: []
    });
  }

  protected onExit(): void {
    this.clearDepartmentData();
    this.exitTask.emit();
  }

  protected onSubmit(): void {
    if (!this.editForm.valid) {
      this.showErrors = true;
      return;
    }

    let formImput: PositionFormImput = {
      name: this.editForm.get('name')?.value,
      manangers: this.editForm.get('manangers')?.value,
      activated: this.editForm.get('disabled')?.value ? false : true
    }

    this.editTask.emit({id: this.departmentData.id, data: formImput});
  }
}
