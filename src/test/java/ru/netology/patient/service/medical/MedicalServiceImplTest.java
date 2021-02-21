package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MedicalServiceImplTest {

  @Test
  void checkMessageBloodPressure() {
    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    PatientInfo patientInfo = new PatientInfo("1","Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("36.65"), new BloodPressure(130, 80)));
    Mockito.when(patientInfoRepository.add(patientInfo)).thenReturn("1");
    Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);
    SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

    MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

    BloodPressure currentPressure = new BloodPressure(120, 80);

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    medicalService.checkBloodPressure("1", currentPressure);
    Mockito.verify(sendAlertService, Mockito.times(1)).send(argumentCaptor.capture());
    Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());
  }

  @Test
  void checkMessageTemperature() {
    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    PatientInfo patientInfo = new PatientInfo("1","Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("38.2"), new BloodPressure(120, 80)));
    Mockito.when(patientInfoRepository.add(patientInfo)).thenReturn("1");
    Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);
    SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

    MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    BigDecimal currentTemperature = new BigDecimal("36.6");
    medicalService.checkTemperature("1", currentTemperature);
    Mockito.verify(sendAlertService).send(argumentCaptor.capture());
    Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());
  }

  @Test
  void checkNoMessage() {
    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    PatientInfo patientInfo = new PatientInfo("1","Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("37.5"), new BloodPressure(120, 80)));
    Mockito.when(patientInfoRepository.add(patientInfo)).thenReturn("1");
    Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);
    SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

    MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

    BloodPressure currentPressure = new BloodPressure(120, 80);
    medicalService.checkBloodPressure("1", currentPressure);
    Mockito.verify(sendAlertService, Mockito.never()).send("Warning, patient with id: 1, need help");

    BigDecimal currentTemperature = new BigDecimal("36.6");
    medicalService.checkTemperature("1", currentTemperature);
    Mockito.verify(sendAlertService, Mockito.never()).send("Warning, patient with id: 1, need help");
  }

}