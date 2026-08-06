import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemorandoComponent } from './memorando.component';

describe('MemorandoComponent', () => {
  let component: MemorandoComponent;
  let fixture: ComponentFixture<MemorandoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemorandoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemorandoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
