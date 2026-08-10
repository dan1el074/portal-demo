import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { EditMemorandoComponent } from './edit-memorando.component';

describe('EditMemorandoComponent', () => {
  let component: EditMemorandoComponent;
  let fixture: ComponentFixture<EditMemorandoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditMemorandoComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditMemorandoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
