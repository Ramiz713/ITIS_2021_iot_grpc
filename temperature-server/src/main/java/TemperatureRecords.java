import com.itis.grpc.Messages;

import java.util.ArrayList;
import java.util.Random;

public class TemperatureRecords extends ArrayList<Messages.TemperatureRecord> {
    private static TemperatureRecords temperatureRecords;

    public static TemperatureRecords getInstance() {
        if (temperatureRecords == null) {
            temperatureRecords = new TemperatureRecords();
        }
        return temperatureRecords;
    }

    public TemperatureRecords() {
        Random rand = new Random();
        float min = -50f;
        float max = 50f;
        Character[] data = {'F', 'C', 'K'};
        for (int i = 0; i < 100; i++) {
            float randomValue = min + rand.nextFloat() * (max - min);
            int scaleIndex = rand.nextInt(data.length);
            this.add(Messages.TemperatureRecord
                    .newBuilder()
                    .setId(i)
                    .setTemperature(randomValue)
                    .setScale(data[scaleIndex].toString())
                    .build());
        }
    }
}
