import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppointment, NewAppointment } from '../appointment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppointment for edit and NewAppointmentFormGroupInput for create.
 */
type AppointmentFormGroupInput = IAppointment | PartialWithRequiredKeyOf<NewAppointment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppointment | NewAppointment> = Omit<T, 'appt_datetime'> & {
  appt_datetime?: string | null;
};

type AppointmentFormRawValue = FormValueOf<IAppointment>;

type NewAppointmentFormRawValue = FormValueOf<NewAppointment>;

type AppointmentFormDefaults = Pick<NewAppointment, 'id' | 'appt_datetime'>;

type AppointmentFormGroupContent = {
  id: FormControl<AppointmentFormRawValue['id'] | NewAppointment['id']>;
  appt_type: FormControl<AppointmentFormRawValue['appt_type']>;
  appt_datetime: FormControl<AppointmentFormRawValue['appt_datetime']>;
  remarks: FormControl<AppointmentFormRawValue['remarks']>;
  patient_id: FormControl<AppointmentFormRawValue['patient_id']>;
  firstName: FormControl<AppointmentFormRawValue['firstName']>;
  lastName: FormControl<AppointmentFormRawValue['lastName']>;
  email: FormControl<AppointmentFormRawValue['email']>;
  phoneNumber: FormControl<AppointmentFormRawValue['phoneNumber']>;
  // doctor_id: FormControl<AppointmentFormRawValue['doctor_id']>;
};

export type AppointmentFormGroup = FormGroup<AppointmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppointmentFormService {
  createAppointmentFormGroup(appointment: AppointmentFormGroupInput = { id: null }): AppointmentFormGroup {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({
      ...this.getFormDefaults(),
      ...appointment,
    });
    return new FormGroup<AppointmentFormGroupContent>({
      id: new FormControl(
        { value: appointmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      appt_type: new FormControl(appointmentRawValue.appt_type, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      appt_datetime: new FormControl(appointmentRawValue.appt_datetime, {
        validators: [Validators.required],
      }),
      remarks: new FormControl(appointmentRawValue.remarks, {
        validators: [Validators.maxLength(200)],
      }),
      patient_id: new FormControl(appointmentRawValue.patient_id, {
        // validators: [Validators.required],
      }),
      firstName: new FormControl(appointmentRawValue.firstName, {
        // validators: [Validators.required],
      }),
      lastName: new FormControl(appointmentRawValue.lastName, {
        // validators: [Validators.required],
      }),
      email: new FormControl(appointmentRawValue.email, {
        // validators: [Validators.required],
      }),
      phoneNumber: new FormControl(appointmentRawValue.phoneNumber, {
        // validators: [Validators.required],
      }),
      // doctor_id: new FormControl(appointmentRawValue.doctor_id, {
      //   validators: [Validators.required],
      // }),
    });
  }

  getAppointment(form: AppointmentFormGroup): IAppointment | NewAppointment {
    return this.convertAppointmentRawValueToAppointment(form.getRawValue() as AppointmentFormRawValue | NewAppointmentFormRawValue);
  }

  resetForm(form: AppointmentFormGroup, appointment: AppointmentFormGroupInput): void {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({ ...this.getFormDefaults(), ...appointment });
    form.reset(
      {
        ...appointmentRawValue,
        id: { value: appointmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppointmentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      appt_datetime: currentTime,
    };
  }

  private convertAppointmentRawValueToAppointment(
    rawAppointment: AppointmentFormRawValue | NewAppointmentFormRawValue,
  ): IAppointment | NewAppointment {
    return {
      ...rawAppointment,
      appt_datetime: dayjs(rawAppointment.appt_datetime, DATE_TIME_FORMAT),
    };
  }

  private convertAppointmentToAppointmentRawValue(
    appointment: IAppointment | (Partial<NewAppointment> & AppointmentFormDefaults),
  ): AppointmentFormRawValue | PartialWithRequiredKeyOf<NewAppointmentFormRawValue> {
    return {
      ...appointment,
      appt_datetime: appointment.appt_datetime ? appointment.appt_datetime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
