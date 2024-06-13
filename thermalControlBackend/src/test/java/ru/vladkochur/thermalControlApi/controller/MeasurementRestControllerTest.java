package ru.vladkochur.thermalControlApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.MeasurementDTO;
import ru.vladkochur.thermalControlApi.dto.SensorDTO;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.service.serviceInterface.MeasurementService;
import ru.vladkochur.thermalControlApi.util.DataUtils;
import ru.vladkochur.thermalControlApi.utils.MeasurementValidator;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = MeasurementRestController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class MeasurementRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MeasurementService measurementService;
    @MockBean
    private MeasurementValidator measurementValidator;
    @SpyBean
    private DtoConverter converter;


    @Test
    @DisplayName("Test get all measurements functionality")
    public void givenThreeMeasurementsInDB_whenGetAllMeasurements_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(measurementService.findAll()).willReturn(measurements);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))

                .andExpect(jsonPath("$[0].temperature",
                        CoreMatchers.is(measurements.get(0).getTemperature())))
                .andExpect(jsonPath("$[0].humidity",
                        CoreMatchers.is(measurements.get(0).getHumidity())))
                .andExpect(jsonPath("$[0].sensor.serial",
                        CoreMatchers.is(measurements.get(0).getSensor().getSerial())))

                .andExpect(jsonPath("$[1].temperature",
                        CoreMatchers.is(measurements.get(1).getTemperature())))
                .andExpect(jsonPath("$[1].humidity",
                        CoreMatchers.is(measurements.get(1).getHumidity())))
                .andExpect(jsonPath("$[1].sensor.serial",
                        CoreMatchers.is(measurements.get(1).getSensor().getSerial())))

                .andExpect(jsonPath("$[2].temperature",
                        CoreMatchers.is(measurements.get(2).getTemperature())))
                .andExpect(jsonPath("$[2].humidity",
                        CoreMatchers.is(measurements.get(2).getHumidity())))
                .andExpect(jsonPath("$[2].sensor.serial",
                        CoreMatchers.is(measurements.get(2).getSensor().getSerial())));
    }


    @Test
    @DisplayName("Test get all measurements by sensor serial functionality")
    public void givenThreeMeasurementsInDB_whenFindAllBySensorSerial_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(measurementService.findAllMeasurementsBySensorSerial(anyInt())).willReturn(measurements);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/" + sensor.getSerial())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))

                .andExpect(jsonPath("$[0].temperature",
                        CoreMatchers.is(measurements.get(0).getTemperature())))
                .andExpect(jsonPath("$[0].humidity",
                        CoreMatchers.is(measurements.get(0).getHumidity())))
                .andExpect(jsonPath("$[0].sensor.serial",
                        CoreMatchers.is(measurements.get(0).getSensor().getSerial())))

                .andExpect(jsonPath("$[1].temperature",
                        CoreMatchers.is(measurements.get(1).getTemperature())))
                .andExpect(jsonPath("$[1].humidity",
                        CoreMatchers.is(measurements.get(1).getHumidity())))
                .andExpect(jsonPath("$[1].sensor.serial",
                        CoreMatchers.is(measurements.get(1).getSensor().getSerial())))

                .andExpect(jsonPath("$[2].temperature",
                        CoreMatchers.is(measurements.get(2).getTemperature())))
                .andExpect(jsonPath("$[2].humidity",
                        CoreMatchers.is(measurements.get(2).getHumidity())))
                .andExpect(jsonPath("$[2].sensor.serial",
                        CoreMatchers.is(measurements.get(2).getSensor().getSerial())));
    }

    @Test
    @DisplayName("Test get all measurements by incorrect sensor serial functionality")
    public void givenIncorrectSerial_whenFindAllBySensorSerial_thenErrorResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        BDDMockito.given(measurementService.findAllMeasurementsBySensorSerial(anyInt()))
                .willThrow(new SensorNotFoundException());
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/" + sensor.getSerial())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test get avg measurement functionality")
    public void givenAvgMeasurement_whenFindAvgMeasurement_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        Measurement measurement = DataUtils.getFirstMeasurementPersisted(sensor);
        BDDMockito.given(measurementService.findAvgMeasurement()).willReturn(Optional.ofNullable(measurement));
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/avg")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature",
                        CoreMatchers.is(measurement.getTemperature())))
                .andExpect(jsonPath("$.humidity",
                        CoreMatchers.is(measurement.getHumidity())))
                .andExpect(jsonPath("$.sensor.serial",
                        CoreMatchers.is(sensor.getSerial())));
    }

    @Test
    @DisplayName("Test get avg measurement by sensor serial functionality")
    public void givenSerial_whenFindAvgMeasurementBySensorSerial_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        Measurement measurement = DataUtils.getFirstMeasurementPersisted(sensor);
        BDDMockito.given(measurementService.findAvgMeasurementBySensorSerial(anyInt()))
                .willReturn(Optional.ofNullable(measurement));

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/avg/" + sensor.getSerial())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature",
                        CoreMatchers.is(measurement.getTemperature())))
                .andExpect(jsonPath("$.humidity",
                        CoreMatchers.is(measurement.getHumidity())))
                .andExpect(jsonPath("$.sensor.serial",
                        CoreMatchers.is(sensor.getSerial())));
    }

    @Test
    @DisplayName("Test get avg measurement by incorrect serial functionality")
    public void givenIncorrectSerial_whenFindAvgMeasurementBySensorSerial_thenErrorResponse() throws Exception {
        //given
        BDDMockito.given(measurementService.findAvgMeasurementBySensorSerial(anyInt())).willThrow(new SensorNotFoundException());
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/avg/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test create measurement functionality")
    public void givenMeasurementDTO_whenCreateMeasurement_thenSuccessResponse() throws Exception {
        //given
        SensorDTO sensorDTO = DataUtils.getKostromaSensorDto();
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        MeasurementDTO measurementDTO = DataUtils.getFirstMeasurementDTO(sensorDTO);
        Measurement measurement = DataUtils.getThirdMeasurementPersisted(sensor);

        BDDMockito.given(measurementService.save(any(Measurement.class)))
                .willReturn(measurement);
        BDDMockito.doNothing().when(measurementValidator).validate(any(), any());


        //when
        ResultActions result = mockMvc.perform(post("/api/v1/measurements/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(measurementDTO)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature",
                        CoreMatchers.is(measurement.getTemperature())))
                .andExpect(jsonPath("$.humidity",
                        CoreMatchers.is(measurement.getHumidity())))
                .andExpect(jsonPath("$.sensor.serial",
                        CoreMatchers.is(sensor.getSerial())));
    }

    @Test
    @DisplayName("Test create measurement functionality")
    public void givenPanicMeasurementDTO_whenSendCriticalMeasurementToAllUsers_thenSuccessResponse() throws Exception {
        //given
        SensorDTO sensorDTO = DataUtils.getKostromaSensorDto();
        MeasurementDTO measurementDTO = DataUtils.getPanicMeasurementDTO(sensorDTO);

        BDDMockito.doNothing().when(measurementService).sendCriticalMeasurementToAllUsers(any(Measurement.class));
        BDDMockito.doNothing().when(measurementValidator).validate(any(), any());

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/measurements/panic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(measurementDTO)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

}