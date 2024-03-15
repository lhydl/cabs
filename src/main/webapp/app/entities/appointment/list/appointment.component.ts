import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpParams } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { IAppointment } from '../appointment.model';
import { EntityArrayResponseType, AppointmentService } from '../service/appointment.service';
import { AppointmentDeleteDialogComponent } from '../delete/appointment-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import dayjs from 'dayjs/esm';

@Component({
  standalone: true,
  selector: 'jhi-appointment',
  templateUrl: './appointment.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    ItemCountComponent,
    HasAnyAuthorityDirective,
    MatChipsModule,
  ],
})
export class AppointmentComponent implements OnInit {
  appointments?: IAppointment[];
  filteredAppointments: IAppointment[] = [];
  state: 'upcoming' | 'past' = 'upcoming';
  isLoading = false;

  predicate = 'id';
  ascending = true;

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;
  account: Account | null = null;
  isAdmin: boolean = this.accountService.hasAnyAuthority('ROLE_ADMIN');
  displayTitle: string | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(
    protected appointmentService: AppointmentService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected modalService: NgbModal,
    private accountService: AccountService,
  ) {}

  trackId = (_index: number, item: IAppointment): number => this.appointmentService.getAppointmentIdentifier(item);

  ngOnInit(): void {
    this.setUserRoleContent();
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  setUserRoleContent(): void {
    this.accountService.identity().subscribe(account => {
      if (account) {
        if (this.accountService.hasAnyAuthority(['ROLE_ADMIN'])) {
          this.displayTitle = 'Manage Appointments';
        } else {
          this.displayTitle = 'Manage Your Appointments';
        }
      }
    });
  }

  toggleState(state: 'upcoming' | 'past') {
    this.state = state;
    this.filterAppointments();
  }

  filterAppointments() {
    const today = dayjs();
    if (this.appointments != null) {
      if (this.state === 'upcoming') {
        this.filteredAppointments = this.appointments.filter(appointment => dayjs(appointment.apptDatetime).isAfter(today));
      } else {
        this.filteredAppointments = this.appointments.filter(appointment => dayjs(appointment.apptDatetime).isBefore(today));
      }
    }
  }

  delete(appointment: IAppointment): void {
    const modalRef = this.modalService.open(AppointmentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.appointment = appointment;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations()),
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.load();
        },
      });
  }

  load(): void {
    if (this.isAdmin) {
      this.loadFromBackendWithRouteInformations().subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
    } else {
      this.loadUserAppt();
    }
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending);
  }

  loadUserAppt(): void {
    const userId = this.account?.id;
    let params = new HttpParams();
    if (userId !== null && userId !== undefined) {
      params = new HttpParams().set('userId', userId);
    }
    this.appointmentService.getUserAppt(params).subscribe(res => {
      this.onResponseSuccess(res);
    });
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.appointments = dataFromBody;
    this.filterAppointments();
  }

  protected fillComponentAttributesFromResponseBody(data: IAppointment[] | null): IAppointment[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(page?: number, predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.appointmentService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
