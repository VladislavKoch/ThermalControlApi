package ru.vladkochur.thermalControlApi.telegramBot.telegramUtils;

import org.springframework.stereotype.Component;
import ru.vladkochur.thermalControlApi.entity.Measurement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class GraphUrlMaker {

    public String makeMeasuresGraph(List<Measurement> measurements) {
        String temperatures = measurements.stream().map(x -> Math.floor(x.getTemperature()  * 10) / 10).toList()
                .toString().replace(" ", "");
        String humidities = measurements.stream().map(x -> Math.floor(x.getHumidity()  * 10) / 10).toList()
                .toString().replace(" ", "");

        String dateTimes = measurements.stream().map(
                x -> "'" + x.getTime().format(DateTimeFormatter.ofPattern("HH:mm-dd")) + "'"
        ).toList().toString().replace(" ", "");

        StringBuilder sb = new StringBuilder()
                .append('\n')
                .append(LocalDateTime.now().format((DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"))))
                .append("\n\nГрафик температуры : ")
                .append("https://quickchart.io/chart?c={type:'line',data:{labels:")
                .append(dateTimes)
                .append(",datasets:[{label:'Температура',data:")
                .append(temperatures)
                .append("}]}}\n\n")
                .append("График влажности : ")
                .append("https://quickchart.io/chart?c={type:'line',data:{labels:")
                .append(dateTimes)
                .append(",datasets:[{label:'Влажность',data:")
                .append(humidities)
                .append("}]}}");
        return sb.toString();
    }

}

