import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { AlertComponent, ButtonDirective, ContainerComponent } from '@coreui/angular';
import { HomeService } from './../../services/home.service';
import { UserService } from './../../services/user.service';
import { BirthdaysComponent } from './../../../components/cards/birthdays/birthdays.component';
import { PostComponent } from './../../../components/cards/post/post.component';
import { EventComponent } from './../../../components/cards/event/event.component';
import { FilesComponent } from './../../../components/cards/files/files.component';
import { HelloComponent } from '../../../components/cards/hello/hello.component';
import { HomeInfo } from '../../interface/home.interface';
import { Me } from '../../interface/user.interface';

@Component({
  selector: 'app-home',
  imports: [
    ContainerComponent,
    AlertComponent,
    FilesComponent,
    EventComponent,
    PostComponent,
    HelloComponent,
    BirthdaysComponent,
    ButtonDirective
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeComponent implements OnInit {
  protected user!: Me;
  protected fatalError = false;
  protected homeInfo!: HomeInfo;
  protected isAdmin = false;

  constructor(
    private homeService: HomeService,
    private toasterService: ToastrService,
    private userService: UserService,
    private spinner: NgxSpinnerService,
    private cdf: ChangeDetectorRef
  ) {}

  public ngOnInit(): void {
    this.homeService.getHomeInfo().subscribe({
      next: (data) => {
        this.homeInfo = data;
        this.cdf.detectChanges();
      },
      error: () => {
        this.toasterService.error("Erro ao obter informações!");
        this.fatalError = true;
      }
    });

    this.userService.user$.subscribe(user => {
      if (!user) return;

      if (user.roles.findIndex(role => role.authority == 'ROLE_ADMIN') >= 0) {
        this.isAdmin = true;
      }

      this.user = user;
    });

    setTimeout(() => {
      this.spinner.hide("loginSpinner")
    }, 500);
  }

  public clearCache(): void {
    this.homeService.clearAllCache().subscribe({
      next: () => this.toasterService.success("Cache limpo com sucesso!"),
      error: () => this.toasterService.error("Erro ao limpar cache!")
    });
  }
}
