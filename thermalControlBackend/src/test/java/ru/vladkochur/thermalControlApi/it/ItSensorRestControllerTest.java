package ru.vladkochur.thermalControlApi.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.dao.SensorInteractionDAO;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.SensorDTO;
import ru.vladkochur.thermalControlApi.dto.SensorSetupDTO;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;
import ru.vladkochur.thermalControlApi.entity.SensorSetup;
import ru.vladkochur.thermalControlApi.it.testcontainers.AbstractRestControllerBaseTest;
import ru.vladkochur.thermalControlApi.repository.MeasurementRepository;
import ru.vladkochur.thermalControlApi.repository.SensorPeriodRepository;
import ru.vladkochur.thermalControlApi.repository.SensorRepository;
import ru.vladkochur.thermalControlApi.repository.SensorSettingsRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSetupService;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@WithMockUser(roles = {"ADMIN"})
class ItSensorRestControllerTest extends AbstractRestControllerBaseTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private DtoConverter converter;
    @Autowired
    private SensorSetupService setupService;
    @Autowired
    private SensorPeriodRepository periodRepository;
    @Autowired
    private SensorSettingsRepository settingsRepository;
    @Autowired
    SensorInteractionDAO sensorInteractionDAO;

    @BeforeEach
    public void setup() {
        sensorInteractionDAO.deleteAll();
        measurementRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all sensors functionality")
    public void givenThreeSensorsInDB_whenGetAllSensors_thenSuccessResponse() throws Exception {
        //given
        List<Sensor> sensors = DataUtils.getSensorsTransient();
        List<Sensor> obtainedSensors = sensorRepository.saveAll(sensors);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name",
                        CoreMatchers.is(obtainedSensors.get(0).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].serial",
                        CoreMatchers.is(obtainedSensors.get(0).getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name",
                        CoreMatchers.is(obtainedSensors.get(1).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].serial",
                        CoreMatchers.is(obtainedSensors.get(1).getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name",
                        CoreMatchers.is(obtainedSensors.get(2).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].serial",
                        CoreMatchers.is(obtainedSensors.get(2).getSerial())));
    }

    @Test
    @DisplayName("Test get sensor data functionality")
    @WithMockUser(roles = {"SENSOR"})
    public void givenSensorSerial_whenGetSensorData_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        SensorSettings settings = DataUtils.getFirstSensorSettingsPersisted(sensor);
        Weekday weekday = Weekday.values()[LocalDate.now().getDayOfWeek().getValue() - 1];
        SensorPeriod period = DataUtils.getFirstSensorPeriodTransient(weekday);
        sensorRepository.save(sensor);
        settingsRepository.save(settings);
        periodRepository.save(period);
        SensorSetup setup = setupService.getSetupBySerial(sensor.getSerial());
        SensorSetupDTO dto = converter.sensorSetupToDto(setup);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors/" + sensor.getSerial())
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
    @WithMockUser(roles = {"SENSOR"})
    public void givenIncorrectSensorSerial_whenGetSensorData_thenErrorResponse() throws Exception {
        //given

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
    @WithMockUser(roles = {"SENSOR"})
    public void givenSensorDTO_whenCreateSensor_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        SensorDTO dto = converter.sensorToDto(sensor);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/sensors/registration")
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
    @WithMockUser(roles = {"SENSOR"})
    public void givenSensorDtoWithDuplicateSerial_whenCreateSensor_thenErrorResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        sensorRepository.save(sensor);
        SensorDTO dto = converter.sensorToDto(sensor);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/sensors/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("serial - This serial is already used;")));
    }

    @Test
    @DisplayName("Test delete sensor by serial functionality")
    public void givenSensorSerial_whenDeleteSensorBySerial_thenSuccessResponse() throws Exception {
        //given
        sensorRepository.save(DataUtils.getKostromaSensorPersisted());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/sensors/156000")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test delete sensor by incorrect serial functionality")
    public void givenIncorrectSensorSerial_whenDeleteSensorBySerial_thenErrorResponse() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/sensors/156000")
                .contentType(MediaType.APPLICATION_JSON));
        //then
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
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        SensorDTO dto = converter.sensorToDto(sensor);
        sensorRepository.save(sensor);
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
        SensorDTO dto = DataUtils.getKostromaSensorDto();
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
    @WithMockUser(roles = {"SENSOR"})
    public void givenSensorSerial_whenMakeSensorWantedToInteract_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        SensorDTO dto = converter.sensorToDto(sensor);
        sensorRepository.save(sensor);
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
    @WithMockUser(roles = {"SENSOR"})
    public void givenIncorrectSerial_whenSensorWantInteraction_thenErrorResponse() throws Exception {
        //given
        SensorDTO dto = DataUtils.getKostromaSensorDto();

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
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        SensorDTO dto = converter.sensorToDto(sensor);
        sensorRepository.save(sensor);
        Sensor sensor2 = DataUtils.getYaroslavlSensorPersisted();
        SensorDTO dto2 = converter.sensorToDto(sensor2);
        sensorRepository.save(sensor2);
        sensorInteractionDAO.makeSensorWantedToInteract(sensor.getSerial());
        sensorInteractionDAO.makeSensorWantedToInteract(sensor2.getSerial());
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/sensors/interaction")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].serial",
                        CoreMatchers.is(dto.getSerial())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].serial",
                        CoreMatchers.is(dto2.getSerial())));
    }

    @Test
    @DisplayName("Test make all sensors unwanted to interact functionality")
    public void givenHttpRequest_whenMakeAllSensorsUnwantedToInteract_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorPersisted();
        sensorRepository.save(sensor);
        Sensor sensor2 = DataUtils.getYaroslavlSensorPersisted();
        sensorRepository.save(sensor2);
        sensorInteractionDAO.makeSensorWantedToInteract(sensor.getSerial());
        sensorInteractionDAO.makeSensorWantedToInteract(sensor2.getSerial());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/sensors/interaction")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertThat(sensorInteractionDAO.findAllSensorsThatWantInteract().size()).isEqualTo(0);
    }
}