import { AuthGuard } from '../..//config/authGuard';
import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NgScrollbar } from 'ngx-scrollbar';
import {
  ContainerComponent,
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

function isOverflown(element: HTMLElement) {
  return (
    element.scrollHeight > element.clientHeight ||
    element.scrollWidth > element.clientWidth
  );
}

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
  public navItems = [...navItems];
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

  constructor(private authGuardService: AuthGuard) {}

  ngOnInit() {
    this.authGuardService.getUser().subscribe(user => {
      this.user = user;
    });
  }
}
