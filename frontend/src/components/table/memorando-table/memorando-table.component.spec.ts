import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemorandoTableComponent } from './memorando-table.component';

describe('MemorandoTableComponent', () => {
  let component: MemorandoTableComponent;
  let fixture: ComponentFixture<MemorandoTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemorandoTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemorandoTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
