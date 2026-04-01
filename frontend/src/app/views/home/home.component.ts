import { Component, OnInit } from '@angular/core';
import { AlertComponent, ContainerComponent } from '@coreui/angular';
import { NgxSpinnerService } from 'ngx-spinner';
import { PostComponent } from './../../../components/cards/post/post.component';
import { TodoComponent } from './../../../components/cards/todo/todo.component';
import { EventComponent } from './../../../components/cards/event/event.component';
import { FilesComponent } from './../../../components/cards/files/files.component';
import { PostCard } from './../../interface/post.interface';
import { FileCard } from './../../interface/file.interface';
import { EventCard } from './../../interface/event.interface';
import { TodoTableCard } from '../../interface/todo.interface';

@Component({
  selector: 'app-home',
  imports: [
    ContainerComponent,
    AlertComponent,
    FilesComponent,
    EventComponent,
    TodoComponent,
    PostComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  protected event: EventCard = {
    title: 'Minuto da Qualidade',
    picture: '.\\assets\\images\\others\\event_03.webp',
    date: '2025-06-21T15:00:00Z',
  };
  protected files: Array<FileCard> = [
    {
      id: 1,
      name: 'Lista de ramais',
      path: 'caminho1',
    },
    {
      id: 2,
      name: 'Dados cadastrais',
      path: 'caminho2',
    },
    {
      id: 3,
      name: 'Solicitação de compras',
      path: 'caminho3',
    },
  ];
  protected todoItems: Array<TodoTableCard> = [
    {
      priority: 'Urgente',
      items: [
        {
          title: 'Validar coisas',
          date: '2025-06-21T15:00:00Z',
          late: false,
        },
        {
          title: 'Fazer coisas',
          date: '2025-06-21T15:00:00Z',
          late: false,
        },
        {
          title: 'Reunião com a engenharia',
          date: '2025-06-21T15:00:00Z',
          late: true,
        },
      ],
    },
    {
      priority: 'Outros',
      items: [
        {
          title: 'Tomar café',
          date: '2025-06-21T15:00:00Z',
          late: false,
        },
        {
          title: 'Acessar servidor',
          date: '2025-06-21T15:00:00Z',
          late: true,
        },
      ],
    },
  ];
  protected feed: Array<PostCard> = [
    {
      id: 3,
      author: 'Aline Moterle',
      authorPictureId: 3,
      position: 'Recursos Humanos',
      instant: '6 horas atrás',
      content:
        'Hoje à tarde o RH juntamente com os gestores da área (Chailan e Diogo), conduziram a primeira etapa do processo seletivo de Recrutamento Interno para a vaga de líder de produção.',
      pictures: [
        {id: 1, uri: '.\\assets\\images\\others\\img_00001.jpg'},
        {id: 2, uri: '.\\assets\\images\\others\\img_00002.jpg'},
        {id: 3, uri: '.\\assets\\images\\others\\img_00003.jpg'},
        {id: 4, uri: '.\\assets\\images\\others\\img_00004.jpg'},
        {id: 5, uri: '.\\assets\\images\\others\\img_00005.jpg'},
      ],
    },
    {
      id: 1,
      author: 'Aline Moterle',
      authorPictureId: 3,
      position: 'Recursos Humanos',
      instant: '2 horas atrás',
      content: '',
      pictures: [{id: 6, uri: '.\\assets\\images\\others\\img_00007.jpeg'}],
    },
    {
      id: 2,
      author: 'Juliano Bortoletti',
      authorPictureId: 5,
      position: 'Marketing',
      instant: '2 horas atrás',
      content:
        'Hoje é o <strong>Dia da Família</strong>, e queremos lembrar que ela vai muito além dos laços de sangue. Família é onde encontramos apoio, afeto, cuidado e verdade.<br><br>Feliz Dia da Família!<br>Equipe Metaro.',
      pictures: [{id: 7, uri: '.\\assets\\images\\others\\img_00006.jpeg'}],
    },
    {
      id: 4,
      author: 'Aline Moterle',
      authorPictureId: 3,
      position: 'Recursos Humanos',
      instant: '2 horas atrás',
      content:
        '<strong>Sobre nossa energia emocional:</strong><br><strong>- Gratidão:</strong> Aprecie as qualidades dos outros e veja o lado bom em cada situação. Seja grato por tudo e todos, sempre fazendo o que é certo!<br><strong>- Ambiente estimulante: </strong> Crie um ambiente leve, positivo e criativo. Para nós, é essencial trabalharmos em um lugar agradável!<br><strong>- Franqueza: </strong> Aja com franqueza e mostre seus sentimentos de maneira aberta e amigável!<br>Nós acreditamos que estamos criando a melhor empresa para se trabalhar! 🚀',
      pictures: [{id: 8, uri: '.\\assets\\images\\others\\img_00008.jpeg'}],
    },
  ];

  constructor(private spinner: NgxSpinnerService) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.spinner.hide("loginSpinner")
    }, 500);
  }
}
