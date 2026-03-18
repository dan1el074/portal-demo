import { AuthGuard } from '../..//config/authGuard';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NgScrollbar } from 'ngx-scrollbar';
import {
  ContainerComponent,
  INavData,
  ShadowOnScrollDirective,
  SidebarBrandComponent,
  SidebarComponent,
  SidebarFooterComponent,
  SidebarHeaderComponent,
  SidebarNavComponent,
  SidebarToggleDirective,
  SidebarTogglerDirective,
} from '@coreui/angular';
import { DefaultFooterComponent, DefaultHeaderComponent } from './';
import { navItems } from './_nav';
import { Me } from '../../interface/user.interface';

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
    ShadowOnScrollDirective
  ]
})
export class DefaultLayoutComponent implements OnInit {
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
    notifications: []
  };

  constructor(
    private authGuardService: AuthGuard,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnInit() {
    this.authGuardService.getUser().subscribe(user => {
      this.user = user;
      this.updateTools();
    });
  }

  private updateTools(): void {
    const customNav: Array<INavData> = [];

    this.user.roles.forEach(role => {
      if (role.authority == 'ROLE_USER' || role.authority == 'ROLE_ADMIN') return;

      const toolList = {
        name: role.parent,
        url: role.parentUrl,
        iconComponent: { name: role.parent == 'Administração' ? 'cilCursor' : 'cilFork' },
        children: []
      }

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
    })

    customNav.sort((a, b) => {
      if ((a.name ?? "") === "Gestão" && (b.name ?? "") !== "Gestão") return -1;
      if ((a.name ?? "") !== "Gestão" && (b.name ?? "") === "Gestão") return 1;
      return (a.name ?? "").localeCompare((b.name ?? ""), "pt-BR", { sensitivity: "base" });
    });

    const tempNavItems = [...navItems];
    tempNavItems.splice(6, 0, customNav.length > 0 ? {title: true, name: 'Ferramentas'} : {}, ...customNav);

    if (this.user.supportToken && this.user.supportToken != "null") {
      tempNavItems[tempNavItems.length - 1].url = "http://suporte.metaro.com.br/autologin.php?token=" + this.user.supportToken;
      tempNavItems[tempNavItems.length - 1].badge = { color: 'info', text: 'LINK' };
    }

    this.navItems = [...tempNavItems];
    this.cdr.detectChanges();
  }
}
