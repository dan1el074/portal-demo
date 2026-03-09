import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemorandoFormComponent } from './memorando-form.component';

describe('MemorandoFormComponent', () => {
  let component: MemorandoFormComponent;
  let fixture: ComponentFixture<MemorandoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemorandoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemorandoFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
