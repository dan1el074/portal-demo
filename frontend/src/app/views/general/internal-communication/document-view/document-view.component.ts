import { ButtonDirective, ColComponent, ContainerComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { InternalCommunicationService } from './../../../../services/internal-communication.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { InternalCommunication } from '../../../../interface/internal-communication.interface';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-document-view',
  imports: [
    CommonModule,
    ContainerComponent,
    RowComponent,
    ColComponent,
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    ButtonDirective
  ],
  templateUrl: './document-view.component.html',
  styleUrl: './document-view.component.scss',
})
export class DocumentViewComponent implements OnInit {
  protected item: InternalCommunication = {
    id: 0,
    number: 0,
    request: 0,
    client: '',
    item: '',
    title: '',
    description: '',
    reason: '',
    createAt: '',
    user: {
      id: 0,
      name: '',
      position: {
        id: 0,
        name: ''
      },
      picture: null
    },
    interactions: [],
    fromDepartments: [],
    status: '',
    logs: []
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ciService: InternalCommunicationService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef
  ) { }

  public ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.ciService.findById(id).subscribe({
      next: (data: InternalCommunication) => {
        this.item = data;
        this.cdr.detectChanges();

        console.log(data);
      },
      error: () => {
        this.toasterService.error('Registro não encontrado!');
        this.router.navigate(['general/internal-communication']);
        return;
      }
    });
  }

  protected onEdit(): void {
  }

  protected onSign(): void {
  }

}
