import { NgTemplateOutlet } from '@angular/common';
import { Component, inject, Input, input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AvatarComponent, BadgeComponent, BreadcrumbRouterComponent, ColorModeService, ContainerComponent, DropdownComponent, DropdownDividerDirective, DropdownItemDirective, DropdownMenuDirective, DropdownToggleDirective, HeaderComponent, HeaderNavComponent, HeaderTogglerDirective, SidebarToggleDirective } from '@coreui/angular';
import { cilBell, cilMenu, cilTask, cilSettings, cilAccountLogout, cilX, cilSun, cilMoon, cilContrast, cilPaperclip } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { Me } from '../../../interface/user.interface';
import { environment } from '../../../../environments/environment';
import { TimeAgoPipe } from './../../../pipes/time-ago.pipe';

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
    DropdownDividerDirective,
    BadgeComponent,
    TimeAgoPipe
  ]
})
export class DefaultHeaderComponent extends HeaderComponent implements OnInit {
  @Input() user!: Me;
  protected apiUrl = environment.apiUrl;
  readonly icons = { cilBell, cilMenu, cilTask, cilSettings, cilAccountLogout, cilX, cilPaperclip };
  readonly sidebarId = input('sidebar1');
  readonly #colorModeService = inject(ColorModeService);
  readonly colorMode = this.#colorModeService.colorMode;
  readonly colorModes = [
    { name: 'light', text: 'Claro', icon: cilSun },
    { name: 'dark', text: 'Escuro', icon: cilMoon },
    { name: 'auto', text: 'Automático', icon: cilContrast }
  ];

  constructor() { super() }

  ngOnInit(): void {
    let theme = this.colorModes.find(mode => localStorage.getItem('theme')?.includes(mode.name))?.name ?? 'light';
    this.colorMode.set(theme);

    if (theme == 'auto') theme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
    document.documentElement.setAttribute('data-coreui-theme', theme);
  }

  public getIcon(): Array<string> {
    const currentMode = this.colorMode();
    return this.colorModes.find(mode => mode.name === currentMode)?.icon ?? cilSun;
  };

}
