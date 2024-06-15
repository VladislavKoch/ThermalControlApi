package ru.vladkochur.thermalControlApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.SensorDTO;
import ru.vladkochur.thermalControlApi.dto.SensorSetupDTO;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorSetup;
import ru.vladkochur.thermalControlApi.exception.DataIsNotCorrectException;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSetupService;
import ru.vladkochur.thermalControlApi.utils.SensorValidator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = SensorRestController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class SensorRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SensorService sensorService;
    @MockBean
    private SensorSetupService setupService;
    @MockBean
    private SensorValidator sensorValidator;
    @SpyBean
    private DtoConverter converter;

    @Test
    @DisplayName("Test get all sensors functionality")
    public void givenThreeSensorsInDB_whenGetAllSensors_thenSuccessResponse() throws Exception {
        //given
        List<Sensor> sensors = getSensorsPersisted();
        List<SensorDTO> sensorDTOs = getSensorDTOs();
        BDDMockito.given(sensorService.findAll()).willReturn(sensors);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name",
                        CoreMatchers.is(sensorDTOs.get(0).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].serial",
                        CoreMatchers.is(sensorDTOs.get(0).getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name",
                        CoreMatchers.is(sensorDTOs.get(1).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].serial",
                        CoreMatchers.is(sensorDTOs.get(1).getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name",
                        CoreMatchers.is(sensorDTOs.get(2).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].serial",
                        CoreMatchers.is(sensorDTOs.get(2).getSerial())));
    }

    @Test
    @DisplayName("Test get sensor data functionality")
    public void givenSensorSerial_whenGetSensorData_thenSuccessResponse() throws Exception {
        //given
        SensorSetup setup = getSensorSetup();
        SensorSetupDTO dto = getSensorSetupDTO();

        BDDMockito.given(setupService.getSetupBySerial(anyInt())).willReturn(setup);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors/156000")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.startAt", CoreMatchers.is(dto.getStartAt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endAt", CoreMatchers.is(dto.getEndAt())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.minimalTemperature",
                        CoreMatchers.is(dto.getMinimalTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optimalTemperature",
                        CoreMatchers.is(dto.getOptimalTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.serial", CoreMatchers.is(dto.getSerial())));
    }

    @Test
    @DisplayName("Test get sensor data with incorrect serial functionality")
    public void givenIncorrectSensorSerial_whenGetSensorData_thenErrorResponse() throws Exception {
        //given
        BDDMockito.given(setupService.getSetupBySerial(anyInt())).willThrow(new SensorNotFoundException());
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors/156000")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test create sensor functionality")
    public void givenSensorDTO_whenCreateSensor_thenSuccessResponse() throws Exception {
        //given
        SensorDTO dto = getKostromaSensorDto();
        Sensor sensor = getKostromaSensorPersisted();
        BDDMockito.given(sensorService.save(any(Sensor.class)))
                .willReturn(sensor);
        BDDMockito.doNothing().when(sensorValidator).validate(any(), any());

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(sensor.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.serial", CoreMatchers.is(sensor.getSerial())));
    }

    @Test
    @DisplayName("Test create sensor with duplicate serial functionality")
    public void givenSensorDtoWithDuplicateSerial_whenCreateSensor_thenErrorResponse() throws Exception {
        //given
        SensorDTO dto = getKostromaSensorDto();
        BDDMockito.doNothing().when(sensorValidator).validate(any(), any());
        BDDMockito.given(sensorService.save(any(Sensor.class)))
                .willThrow(new DataIsNotCorrectException("This serial is already used"));
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("This serial is already used")));
    }

    @Test
    @DisplayName("Test delete sensor by serial functionality")
    public void givenSensorSerial_whenDeleteSensorBySerial_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.doNothing().when(sensorService).deleteBySerial(anyInt());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/sensors/156000")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        BDDMockito.verify(sensorService, Mockito.times(1)).deleteBySerial(anyInt());
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test delete sensor by incorrect serial functionality")
    public void givenIncorrectSensorSerial_whenDeleteSensorBySerial_thenErrorResponse() throws Exception {
        //given
        BDDMockito.doThrow(new SensorNotFoundException()).when(sensorService).deleteBySerial(anyInt());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/sensors/156000")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        BDDMockito.verify(sensorService, Mockito.times(1)).deleteBySerial(anyInt());
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test update sensor functionality")
    public void givenSensorDTO_whenUpdateSensor_thenSuccessResponse() throws Exception {
        //given
        SensorDTO dto = getKostromaSensorDto();
        Sensor sensor = getKostromaSensorPersisted();
        BDDMockito.given(sensorService.update(any(Sensor.class)))
                .willReturn(sensor);
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(sensor.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.serial", CoreMatchers.is(sensor.getSerial())));
    }

    @Test
    @DisplayName("Test update sensor with incorrect serial functionality")
    public void givenSensorDTOWithIncorrectSerial_whenUpdateSensor_thenErrorResponse() throws Exception {
        //given
        SensorDTO dto = getKostromaSensorDto();
        BDDMockito.given(sensorService.update(any(Sensor.class)))
                .willThrow(new SensorNotFoundException());
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test make the sensor wanted to interact functionality")
    public void givenSensorSerial_whenMakeSensorWantedToInteract_thenSuccessResponse() throws Exception {
        //given
        SensorDTO dto = getIvanovoSensorDto();
        BDDMockito.doNothing().when(sensorService).makeSensorWantedToInteract(any(Sensor.class));
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/sensors/interaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test make the sensor with incorrect serial wanted to interact functionality")
    public void givenIncorrectSerial_whenSensorWantInteraction_thenErrorResponse() throws Exception {
        //given
        SensorDTO dto = getKostromaSensorDto();
        BDDMockito.doThrow(new SensorNotFoundException()).when(sensorService)
                .makeSensorWantedToInteract(any(Sensor.class));

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/sensors/interaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test get all sensors that want to interact functionality")
    public void givenThreeSensorsInDB_whenGetAllSensorsThatWantInteract_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = getIvanovoSensorPersisted();
        SensorDTO dto = getIvanovoSensorDto();
        BDDMockito.given(sensorService.findAllSensorsWantedForInteraction()).willReturn(List.of(sensor));
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors/interaction")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name",
                        CoreMatchers.is(dto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].serial",
                        CoreMatchers.is(dto.getSerial())));
    }

    @Test
    @DisplayName("Test make all sensors unwanted to interact functionality")
    public void givenHttpRequest_whenMakeAllSensorsUnwantedToInteract_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.doNothing().when(sensorService).makeAllSensorsUnwantedToInteract();
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/sensors/interaction")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}