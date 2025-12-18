import { AuthGuard } from '../../../config/authGuard';
import { NgTemplateOutlet } from '@angular/common';
import { Component, inject, input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AvatarComponent, BadgeComponent, BreadcrumbRouterComponent, ColorModeService, ContainerComponent, DropdownComponent, DropdownItemDirective, DropdownMenuDirective, DropdownToggleDirective, HeaderComponent, HeaderNavComponent, HeaderTogglerDirective, SidebarToggleDirective } from '@coreui/angular';
import { cilBell, cilMenu, cilTask, cilSettings, cilAccountLogout, cilSun, cilMoon, cilContrast } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { Me } from 'src/app/interface/user.interface';

@Component({
  selector: 'app-default-header',
  templateUrl: './default-header.component.html',
  imports: [
    ContainerComponent,
    HeaderTogglerDirective,
    SidebarToggleDirective,
    IconDirective,
    HeaderNavComponent,
    RouterLink,
    NgTemplateOutlet,
    BreadcrumbRouterComponent,
    DropdownComponent,
    DropdownToggleDirective,
    AvatarComponent,
    DropdownMenuDirective,
    DropdownItemDirective,
    BadgeComponent
  ]
})
export class DefaultHeaderComponent extends HeaderComponent implements OnInit {
  readonly #colorModeService = inject(ColorModeService);
  readonly colorMode = this.#colorModeService.colorMode;
  readonly colorModes = [
    { name: 'light', text: 'Claro', icon: cilSun },
    { name: 'dark', text: 'Escuro', icon: cilMoon },
    { name: 'auto', text: 'Automático', icon: cilContrast }
  ];

  protected icons = { cilBell, cilMenu, cilTask, cilSettings, cilAccountLogout };
  public sidebarId = input('sidebar1');

  /*
    public newNotifications = [
      { id: 0, title: 'New user registered', icon: 'cilUserFollow', color: 'success' },
      { id: 1, title: 'User deleted', icon: 'cilUserUnfollow', color: 'danger' },
      { id: 2, title: 'Sales report is ready', icon: 'cilChartPie', color: 'info' },
      { id: 3, title: 'New client', icon: 'cilBasket', color: 'primary' },
      { id: 4, title: 'Server overloaded', icon: 'cilSpeedometer', color: 'warning' }
    ];

    public newTasks = [
      { id: 0, title: 'Upgrade NPM', value: 0, color: 'info' },
      { id: 1, title: 'ReactJS Version', value: 25, color: 'danger' },
      { id: 2, title: 'VueJS Version', value: 50, color: 'warning' },
      { id: 3, title: 'Add new layouts', value: 75, color: 'info' },
      { id: 4, title: 'Angular Version', value: 100, color: 'success' }
    ];

    TODO: editar informações de "Me"
      -> remover: theme, notifications
      -> adicionar: picture, tasks, notifications

    TODO: levar essa lógica para o componente pai

    TODO: ver como mudar o tema, deixar a cargo do locastorage fazer isso, e mudar sempre para o "light" na página de login
  */
  protected user: Me = {
    id: 0,
    name: '',
    username: '',
    email: '',
    birthDate: '',
    theme: '',
    activated: true,
    notifications: 0,
    roles: [],
  };

  constructor(
    private authGuardService: AuthGuard
  ) { super() }

  ngOnInit(): void {
    this.authGuardService.getUser().subscribe(user => {
      this.user = user;
    });
  }

  public getIcon(): Array<string> {
    const currentMode = this.colorMode();
    return this.colorModes.find(mode => mode.name === currentMode)?.icon ?? cilSun;
  };

}
