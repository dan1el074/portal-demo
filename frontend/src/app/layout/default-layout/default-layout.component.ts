import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NgScrollbar } from 'ngx-scrollbar';
import { ContainerComponent, INavData, ShadowOnScrollDirective, SidebarBrandComponent, SidebarComponent, SidebarFooterComponent, SidebarHeaderComponent, SidebarNavComponent, SidebarToggleDirective, SidebarTogglerDirective} from '@coreui/angular';
import { Me } from '../../interface/user.interface';
import { UserService } from '../../services/user.service';
import { NotificationService } from '../../services/notification.service';
import { NotificationWebSocketService } from '../../services/websocket.service';
import { DefaultFooterComponent, DefaultHeaderComponent } from './';
import { LayoutAlertModalComponent } from './../../../components/modal/layout-alert-modal/layout-alert-modal.component';
import { LayoutSearchModalComponent } from '../../../components/modal/layout-search-modal/layout-search-modal.component';
import { navItems } from './_nav';

@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html',
  styleUrls: ['./default-layout.component.scss'],
  imports: [
    SidebarComponent,
    SidebarHeaderComponent,
    SidebarBrandComponent,
    SidebarNavComponent,
    SidebarFooterComponent,
    SidebarToggleDirective,
    SidebarTogglerDirective,
    ContainerComponent,
    DefaultFooterComponent,
    DefaultHeaderComponent,
    NgScrollbar,
    RouterOutlet,
    RouterLink,
    ShadowOnScrollDirective,
    LayoutSearchModalComponent,
    LayoutAlertModalComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DefaultLayoutComponent implements OnInit {
  protected showSearchModal = false;
  protected showAlertModal = false;
  public navItems!: Array<INavData>;

  protected user: Me = {
    id: 0,
    name: '',
    email: '',
    position: null,
    birthDate: '',
    pictureId: null,
    activated: true,
    username: '',
    supportToken: null,
    roles: [],
    notifications: [],
    pendingIssues: []
  };

  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private wsService: NotificationWebSocketService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnInit() {
    this.userService.user$.subscribe(user => {
      if (!user) return;

      this.user = user;
      this.updateTools();
      this.connectWebsocket();
      this.checkFirstAccess();
    });

    if (!this.userService.getCurrentUser()) {
      this.userService.refreshUser().subscribe();
    }

  }

  private updateTools(): void {
    const customNav: Array<INavData> = [];

    this.user.roles.forEach(role => {
      if (role.authority == 'ROLE_USER' || role.authority == 'ROLE_ADMIN') return;

      const toolList: INavData = {
        name: role.parent,
        url: role.parentUrl,
        iconComponent: { name: role.parent == 'Administração' ? 'cilCursor' : 'cilFork' },
        children: []
      };

      let index = customNav.findIndex(tool => tool.name == role.parent);

      if (index < 0) {
        customNav.push(toolList);
        index = customNav.length - 1;
      }

      customNav[index].children?.push({
        name: role.title,
        url: role.parentUrl + role.titleUrl,
        icon: 'nav-icon-bullet'
      });
    });

    customNav.sort((a, b) => {
      if ((a.name ?? '') === 'Gestão' && (b.name ?? '') !== 'Gestão') return -1;
      if ((a.name ?? '') !== 'Gestão' && (b.name ?? '') === 'Gestão') return 1;
      return (a.name ?? '').localeCompare((b.name ?? ''), 'pt-BR', { sensitivity: 'base' });
    });

    const tempNavItems = [...navItems];
    tempNavItems.splice(-2, 0, customNav.length > 0 ? { title: true, name: 'Ferramentas' } : {}, ...customNav);

    if (this.user.supportToken && this.user.supportToken != 'null') {
      tempNavItems[tempNavItems.length - 1].url = 'http://suporte.metaro.com.br/autologin.php?token=' + this.user.supportToken;
      tempNavItems[tempNavItems.length - 1].badge = { color: 'info', text: 'LINK' };
    }

    this.navItems = [...tempNavItems];
    this.cdr.detectChanges();
  }

  private connectWebsocket(): void {
    const token = localStorage.getItem('auth-token');
    if (!token) return;

    this.notificationService.getMyNotifications().subscribe(list => {
      this.wsService.setInitialNotifications(list);
    });

    this.notificationService.getUnreadCount().subscribe(res => {
      this.wsService.setInitialUnreadCount(res.unreadCount);
    });

    this.wsService.connect(token);
  }

  private checkFirstAccess(): void {
    if (sessionStorage.getItem('first-access') == 'true') {
      if (!this.user.pendingIssues || this.user.pendingIssues.length == 0) {
        sessionStorage.removeItem('first-access')
        return;
      }

      this.showAlertModal = true;
      this.cdr.detectChanges();
    }
  }

  protected toggleAlertModal(status: boolean): void {
    this.showAlertModal = status;

    if (!status) {
      sessionStorage.removeItem('first-access');
    }

    this.cdr.detectChanges();
  }

  protected toggleSearchModal(status: boolean): void {
    this.showSearchModal = status;
    this.cdr.detectChanges();
  }
}
