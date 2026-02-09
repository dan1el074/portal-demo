import { ButtonDirective, ColComponent, ContainerComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { InternalCommunicationService } from './../../../../services/internal-communication.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Interaction, InteractionList, InternalCommunication } from '../../../../interface/internal-communication.interface';
import { CommonModule } from '@angular/common';
import { Position } from '../../../../interface/position.interface';

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
    interactionsSummary: [],
    fromDepartments: [],
    status: '',
    logs: []
  };
  protected interactions: Array<InteractionList> = [];

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
        this.updateInteractions();
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Registro não encontrado!');
        this.router.navigate(['general/internal-communication']);
        return;
      }
    });
  }

  protected updateInteractions(): void {
    this.item.fromDepartments.forEach((department: Position) =>{
      let findInteraction = false;

      this.item.interactions.forEach((interaction: Interaction) => {
        if (department.id == interaction.departmentSigned.id) {
          findInteraction = true;

          this.interactions.push({
            check: true,
            position: department.name,
            signedBy: interaction.user.name
          });
          return;
        }
      })

      if (!findInteraction) {
        this.interactions.push({
          check: false,
          position: department.name,
          signedBy: null
        });
      }
    })
  }

  protected onEdit(): void {
  }

  protected onSign(): void {
  }

}
