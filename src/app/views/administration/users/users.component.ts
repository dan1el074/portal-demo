import { Component, OnInit } from '@angular/core';
import { CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { UserTableComponent } from '../../../../components/table/user-table/user-table.component';
import { UserTable } from '../../../interface/user.interface';

@Component({
  selector: 'app-users',
  imports: [
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    Tabs2Module,
    UserTableComponent
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected activeUsers: Array<UserTable> = [];
  protected inactiveUsers: Array<UserTable> = [];
  protected users: Array<UserTable> = [
    {
      id: 0,
      picture: null,
      name: 'Daniel Vargas',
      username: 'daniel-vargas',
      position: 'TI',
      email: 'daniel.vargas@metaro.com.br',
      updateAt: '2024-06-21T15:00:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Carlos Fronza',
      username: 'carlos-fronza',
      position: 'Compras',
      email: 'carlos.fronza@metaro.com.br',
      updateAt: '2025-07-15T12:30:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\10.jpg',
      name: 'Aline Moterle',
      username: 'aline-moterle',
      position: 'Recursos Humanos',
      email: 'aline.moterle@metaro.com.br',
      updateAt: '2025-07-12T16:25:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Luana Kohlausch',
      username: 'luana-kohlausch',
      position: 'Recursos Humanos',
      email: 'luana.kohlausch@metaro.com.br',
      updateAt: '2025-07-12T16:28:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\11.jpg',
      name: 'Juliano Bortoletti',
      username: 'juliano-bortoletti',
      position: 'Marketing',
      email: 'juliano.bortoletti@metaro.com.br',
      updateAt: '2025-08-08T10:32:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\1.jpg',
      name: 'Mariana Diniz',
      username: 'mariana-diniz',
      position: 'Financeiro',
      email: 'mariana.diniz@gmail.com',
      updateAt: '2025-08-12T09:10:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\2.jpg',
      name: 'Rafael Silveira',
      username: 'rafael-silveira',
      position: 'TI',
      email: 'rafael.silveira@gmail.com',
      updateAt: '2025-08-14T14:18:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\3.jpg',
      name: 'Bruna Fagundes',
      username: 'bruna-fagundes',
      position: 'Marketing',
      email: 'bruna.fagundes@gmail.com',
      updateAt: '2025-09-01T11:05:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\4.jpg',
      name: 'Eduardo Fraga',
      username: 'eduardo-fraga',
      position: 'Compras',
      email: 'eduardo.fraga@gmail.com',
      updateAt: '2025-09-03T08:40:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\5.jpg',
      name: 'Fernanda Castro',
      username: 'fernanda-castro',
      position: 'Jurídico',
      email: 'fernanda.castro@gmail.com',
      updateAt: '2025-09-10T16:22:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\6.jpg',
      name: 'Lucas Mendonça',
      username: 'lucas-mendonca',
      position: 'TI',
      email: 'lucas.mendonca@gmail.com',
      updateAt: '2025-10-02T13:45:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\7.jpg',
      name: 'Patrícia Almeida',
      username: 'patricia-almeida',
      position: 'Recursos Humanos',
      email: 'patricia.almeida@gmail.com',
      updateAt: '2026-01-01T14:12:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\8.jpg',
      name: 'Vinícius Rabelo',
      username: 'vinicius-rabelo',
      position: 'Financeiro',
      email: 'vinicius.rabelo@gmail.com',
      updateAt: '2025-10-08T10:11:00Z',
      activated: true
    },
    {
      id: 0,
      picture: '.\\assets\\images\\avatars\\9.jpg',
      name: 'Sabrina Meller',
      username: 'sabrina-meller',
      position: 'Marketing',
      email: 'sabrina.meller@gmail.com',
      updateAt: '2025-10-09T12:25:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Henrique Duarte',
      username: 'henrique-duarte',
      position: 'Logística',
      email: 'henrique.duarte@gmail.com',
      updateAt: '2025-10-12T11:50:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Jéssica Ramos',
      username: 'jessica-ramos',
      position: 'Compras',
      email: 'jessica.ramos@gmail.com',
      updateAt: '2025-11-02T15:04:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Maurício Pires',
      username: 'mauricio-pires',
      position: 'Financeiro',
      email: 'mauricio.pires@gmail.com',
      updateAt: '2025-11-06T09:33:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Tatiana Beltrão',
      username: 'tatiana-beltrao',
      position: 'Recursos Humanos',
      email: 'tatiana.beltrao@gmail.com',
      updateAt: '2025-11-10T10:05:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Rodrigo Klein',
      username: 'rodrigo-klein',
      position: 'TI',
      email: 'rodrigo.klein@gmail.com',
      updateAt: '2025-11-12T12:12:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Gabriela Souza',
      username: 'gabriela-souza',
      position: 'Marketing',
      email: 'gabriela.souza@gmail.com',
      updateAt: '2025-11-14T08:20:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Thiago Mota',
      username: 'thiago-mota',
      position: 'Logística',
      email: 'thiago.mota@gmail.com',
      updateAt: '2025-11-18T11:15:00Z',
      activated: true
    },
    {
      id: 0,
      picture: null,
      name: 'Camila Ferreira',
      username: 'camila-ferreira',
      position: 'Compras',
      email: 'camila.ferreira@gmail.com',
      updateAt: '2025-11-20T16:44:00Z',
      activated: false
    },
    {
      id: 0,
      picture: null,
      name: 'Gustavo Neri',
      username: 'gustavo-neri',
      position: 'TI',
      email: 'gustavo.neri@gmail.com',
      updateAt: '2025-11-22T09:02:00Z',
      activated: false
    },
    {
      id: 0,
      picture: null,
      name: 'Letícia Marin',
      username: 'leticia-marin',
      position: 'Financeiro',
      email: 'leticia.marin@gmail.com',
      updateAt: '2025-11-25T14:30:00Z',
      activated: false
    },
  ];

  ngOnInit(): void {
    this.activeUsers = this.users.filter(user => user.activated)
    this.inactiveUsers = this.users.filter(user => !user.activated)
  }
}
