import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RuntaskComponent } from './runtask.component';

describe('RuntaskComponent', () => {
  let component: RuntaskComponent;
  let fixture: ComponentFixture<RuntaskComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RuntaskComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RuntaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
