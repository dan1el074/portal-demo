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
  {
    name: 'Dashboard',
    url: '/dashboard',
    iconComponent: { name: 'cilChartPie' },
    badge: {
      color: 'info',
      text: 'NOVO'
    }
  },
  {
    name: 'Jornal Digital',
    url: '/newspaper',
    iconComponent: { name: 'cilNewspaper' }
  },
  {
    name: 'Eventos',
    url: '/events',
    iconComponent: { name: 'cilCalendar' }
  },
  {
    name: 'Configurações',
    url: '/config',
    iconComponent: { name: 'cilSettings' }
  },
  {
    title: true,
    name: 'Ferramentas'
  },
  {
    name: 'Gestão',
    url: '/administration',
    iconComponent: { name: 'cilCursor' },
    children: [
      {
        name: 'Usuários',
        url: '/administration/users',
        icon: 'nav-icon-bullet'
      }
    ]
  },
  {
    name: 'Geral',
    url: '/general',
    iconComponent: { name: 'cilFork' },
    children: [
      // {
      //   name: 'Controle Interno',
      //   url: '/general/internal-control',
      //   icon: 'nav-icon-bullet'
      // },
      {
        name: 'Matérias primas',
        url: '/general/raw-materials',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'Para Fazer',
        url: '/general/todo',
        icon: 'nav-icon-bullet'
      }
    ]
  },
  {
    name: 'Qualidade',
    url: '/quality',
    iconComponent: { name: 'cilFork' },
    children: [
      {
        name: 'Checklist',
        url: '/quality/checklist',
        icon: 'nav-icon-bullet'
      }
    ]
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
    attributes: { target: '_blank' }
  }
];
