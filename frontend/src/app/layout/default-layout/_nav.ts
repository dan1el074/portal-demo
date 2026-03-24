import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [
  {
    title: true,
    name: 'Menu'
  },
  {
    name: 'Home',
    url: '/home',
    iconComponent: { name: 'cilHome' }
  },
  // {
  //   name: 'Dashboard',
  //   url: '/dashboard',
  //   iconComponent: { name: 'cilChartPie' },
  // },
  // {
  //   name: 'Jornal Digital',
  //   url: '/newspaper',
  //   iconComponent: { name: 'cilNewspaper' }
  // },
  // {
  //   name: 'Eventos',
  //   url: '/events',
  //   iconComponent: { name: 'cilCalendar' }
  // },
  {
    name: 'Configurações',
    url: '/config',
    iconComponent: { name: 'cilSettings' }
  },
  {
    title: true,
    name: 'Links',
    class: 'mt-auto'
  },
  {
    name: 'Suporte',
    url: 'http://suporte.metaro.com.br/',
    iconComponent: { name: 'cilCommentBubble' },
    attributes: { target: '_blank' },
  }
];
