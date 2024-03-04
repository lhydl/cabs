import dayjs from 'dayjs/esm';

import { IAppointment, NewAppointment } from './appointment.model';

export const sampleWithRequiredData: IAppointment = {
  id: 4352,
  appt_type: 'yum fooey',
  appt_datetime: dayjs('2024-03-03T00:05'),
  patient_id: 1080,
  doctor_id: 13409,
};

export const sampleWithPartialData: IAppointment = {
  id: 3578,
  appt_type: 'boohoo',
  appt_datetime: dayjs('2024-03-02T16:09'),
  patient_id: 12660,
  doctor_id: 19595,
};

export const sampleWithFullData: IAppointment = {
  id: 1889,
  appt_type: 'surface',
  appt_datetime: dayjs('2024-03-02T20:03'),
  remarks: 'amid woot',
  patient_id: 18981,
  doctor_id: 17025,
};

export const sampleWithNewData: NewAppointment = {
  appt_type: 'tensely disclosure tingle',
  appt_datetime: dayjs('2024-03-02T16:24'),
  patient_id: 1882,
  doctor_id: 28289,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
