import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { MemorandoEditFormComponent } from './memorando-edit-form.component';

describe('MemorandoEditFormComponent', () => {
  let component: MemorandoEditFormComponent;
  let fixture: ComponentFixture<MemorandoEditFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemorandoEditFormComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemorandoEditFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
