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
import ru.vladkochur.thermalControlApi.dto.MeasurementDTO;
import ru.vladkochur.thermalControlApi.dto.SensorDTO;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.it.testcontainers.AbstractRestControllerBaseTest;
import ru.vladkochur.thermalControlApi.repository.MeasurementRepository;
import ru.vladkochur.thermalControlApi.repository.SensorRepository;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
//@Sql({"testJDBC/sql/initDB.sql"})
@WithMockUser(roles = {"ADMIN"})

class ItMeasurementRestControllerTest extends AbstractRestControllerBaseTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    MeasurementRepository measurementRepository;

    @BeforeEach
    public void setup() {
        measurementRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    @DisplayName("Test get all measurements functionality")
    public void givenThreeMeasurementsInDB_whenGetAllMeasurements_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorTransient();
        List<Measurement> measurements = DataUtils.getMeasurementsTransient(sensor);
        measurementRepository.saveAll(measurements);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].temperature",
                        CoreMatchers.is(measurements.get(0).getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].humidity",
                        CoreMatchers.is(measurements.get(0).getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sensor.serial",
                        CoreMatchers.is(measurements.get(0).getSensor().getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].temperature",
                        CoreMatchers.is(measurements.get(1).getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].humidity",
                        CoreMatchers.is(measurements.get(1).getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].sensor.serial",
                        CoreMatchers.is(measurements.get(1).getSensor().getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[2].temperature",
                        CoreMatchers.is(measurements.get(2).getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].humidity",
                        CoreMatchers.is(measurements.get(2).getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].sensor.serial",
                        CoreMatchers.is(measurements.get(2).getSensor().getSerial())));
    }

    @Test
    @DisplayName("Test get all measurements by sensor serial functionality")
    public void givenThreeMeasurementsInDB_whenFindAllBySensorSerial_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorTransient();
        List<Measurement> measurements = DataUtils.getMeasurementsTransient(sensor);
        measurementRepository.saveAll(measurements);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/" + sensor.getSerial())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].temperature",
                        CoreMatchers.is(measurements.get(0).getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].humidity",
                        CoreMatchers.is(measurements.get(0).getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sensor.serial",
                        CoreMatchers.is(measurements.get(0).getSensor().getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].temperature",
                        CoreMatchers.is(measurements.get(1).getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].humidity",
                        CoreMatchers.is(measurements.get(1).getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].sensor.serial",
                        CoreMatchers.is(measurements.get(1).getSensor().getSerial())))

                .andExpect(MockMvcResultMatchers.jsonPath("$[2].temperature",
                        CoreMatchers.is(measurements.get(2).getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].humidity",
                        CoreMatchers.is(measurements.get(2).getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].sensor.serial",
                        CoreMatchers.is(measurements.get(2).getSensor().getSerial())));
    }

    @Test
    @DisplayName("Test get all measurements by incorrect sensor serial functionality")
    public void givenIncorrectSerial_whenFindAllBySensorSerial_thenErrorResponse() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/" + 1)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test get avg measurement functionality")
    public void givenAvgMeasurement_whenFindAvgMeasurement_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorTransient();
        List<Measurement> measurements = DataUtils.getMeasurementsTransient(sensor);
        measurementRepository.saveAll(measurements);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/avg")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.humidity",
                        CoreMatchers.is(43.3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature",
                        CoreMatchers.is(20.6)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensor.serial",
                        CoreMatchers.is(-1)));
    }

    @Test
    @DisplayName("Test get avg measurement by sensor serial functionality")
    public void givenSerial_whenFindAvgMeasurementBySensorSerial_thenSuccessResponse() throws Exception {
        //given
        Sensor sensor = DataUtils.getKostromaSensorTransient();
        List<Measurement> measurements = DataUtils.getMeasurementsTransient(sensor);
        measurementRepository.saveAll(measurements);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/avg/" + sensor.getSerial())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.humidity",
                        CoreMatchers.is(43.3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature",
                        CoreMatchers.is(20.6)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensor.serial",
                        CoreMatchers.is(sensor.getSerial())));
    }

    @Test
    @DisplayName("Test get avg measurement by incorrect serial functionality")
    public void givenIncorrectSerial_whenFindAvgMeasurementBySensorSerial_thenErrorResponse() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/avg/" + 1)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Sensor is not exist!")));
    }

    @Test
    @DisplayName("Test create measurement functionality")
    @WithMockUser(roles = {"SENSOR"})
    public void givenMeasurementDTO_whenCreateMeasurement_thenSuccessResponse() throws Exception {
        //given
        SensorDTO sensorDTO = DataUtils.getKostromaSensorDto();
        Sensor sensor = DataUtils.getKostromaSensorTransient();
        sensorRepository.save(sensor);
        MeasurementDTO measurementDTO = DataUtils.getFirstMeasurementDTO(sensorDTO);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/measurements/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(measurementDTO)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature",
                        CoreMatchers.is(measurementDTO.getTemperature())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.humidity",
                        CoreMatchers.is(measurementDTO.getHumidity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensor.serial",
                        CoreMatchers.is(sensorDTO.getSerial())));
    }

    @Test
    @DisplayName("Test create measurement with incorrect sensor serial functionality")
    @WithMockUser(roles = {"SENSOR"})
    public void givenMeasurementDTO_whenCreateMeasurement_thenErrorResponse() throws Exception {
        //given
        SensorDTO sensorDTO = DataUtils.getKostromaSensorDto();
        MeasurementDTO measurementDTO = DataUtils.getFirstMeasurementDTO(sensorDTO);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/measurements/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(measurementDTO)));
        //then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("sensor.serial - This sensor is not exist!;")));
    }
}