import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostComponent } from './post.component';

describe('PostComponent', () => {
  let component: PostComponent;
  let fixture: ComponentFixture<PostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostComponent);
    component = fixture.componentInstance;
    component.post = {
      id: 1,
      author: { id: 1, name: 'Usuário', position: { id: 1, name: 'Cargo' }, picture: null },
      createdAt: new Date().toISOString(),
      content: null,
      isWarning: false,
      pictures: []
    };
    component.canPost = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
