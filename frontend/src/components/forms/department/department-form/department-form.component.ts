import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MultiSelectComponent, MultiSelectOptgroupComponent, MultiSelectOptionComponent } from '@coreui/angular-pro';
import { ButtonDirective, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective } from '@coreui/angular';
import { cilPencil, cilX } from '@coreui/icons';
import { ToastrService } from 'ngx-toastr';
import { UserService } from './../../../../app/services/user.service';
import { PositionFormImput } from '../../../../app/interface/position.interface';
import { UserGroup } from '../../../../app/interface/user.interface';

@Component({
  selector: 'app-department-form',
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
  templateUrl: './department-form.component.html',
  styleUrl: './department-form.component.scss',
})
export class DepartmentFormComponent implements OnInit{
  @Output() exitTask = new EventEmitter<void>();
  @Output() createTask = new EventEmitter<PositionFormImput>();

  protected icons = { cilPencil, cilX };
  protected createForm: FormGroup;
  protected valid: boolean | undefined;
  protected showErrors: boolean = false;
  protected users: Array<UserGroup> = []

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef
  ) {
    this.createForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      manangers: [[]],
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

  protected clearDepartmentData(): void {
    this.createForm.reset({
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
    if (!this.createForm.valid) {
      this.showErrors = true;
      return;
    }

    let formImput: PositionFormImput = {
      name: this.createForm.get('name')?.value,
      manangers: this.createForm.get('manangers')?.value,
      activated: this.createForm.get('disabled')?.value ? false : true
    }

    this.createTask.emit(formImput);
    this.onExit();
  }
}
